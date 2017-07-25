
package com.blade.mvc.ui.template;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;
import static java.lang.reflect.Array.get;
import static java.lang.reflect.Array.getLength;
import static java.nio.file.Files.readAllBytes;

public class BladeTemplate {

    protected enum State {
        // <'here'> ${} <'here'>
        FREE_TEXT,
        // ${'<here>'}
        PARAM,
        // '${'
        PARAM_START,
        // '}'
        PARAM_END,
        // Escape char
        ESCAPE_CHAR
    }

    private final String str;
    private final Map<String, Object> arguments = new HashMap<>();

    private BladeTemplate(String str) {
        this.str = str;
    }

    public static BladeTemplate template(String str) {
        return new BladeTemplate(str);
    }

    public static BladeTemplate template(String str, Object... args) {
        return template(str).args(args);
    }

    public static BladeTemplate template(String str, Map<String, Object> args) {
        return template(str).args(args);
    }

    public static BladeTemplate fromFile(String strPath) {
        return template(readFromFile(strPath));
    }

    public static BladeTemplate fromFile(String strPath, Charset encoding) {
        return template(readFromFile(strPath, encoding));
    }

    public static BladeTemplate fromFile(String strPath, Object... args) {
        return template(readFromFile(strPath), args);
    }

    public static BladeTemplate fromFile(String strPath, Charset encoding, Object... args) {
        return template(readFromFile(strPath, encoding), args);
    }

    public static BladeTemplate fromFile(String strPath, Map<String, Object> args) {
        return template(readFromFile(strPath), args);
    }

    public static BladeTemplate fromFile(String strPath, Charset encoding, Map<String, Object> args) {
        return template(readFromFile(strPath, encoding), args);
    }

    private void failIfArgExists(String argName) {
        if (arguments.containsKey(argName))
            throw UncheckedTemplateException.argumentAlreadyExist(argName);
    }

    private static String readFromFile(String strPath, Charset encoding) {
        try {
            byte[] encodedBytes = readAllBytes(Paths.get(strPath));
            return new String(encodedBytes, encoding);
        } catch (IOException e) {
            throw UncheckedTemplateException.ioExceptionReadingFromFile(strPath, e);
        }
    }

    private static String readFromFile(String strPath) {
        return readFromFile(strPath, Charset.forName("UTF8"));
    }

    public BladeTemplate arg(String argName, Object object) {
        failIfArgExists(argName);
        this.arguments.put(argName, object);
        return this;
    }

    public BladeTemplate args(Map<String, Object> args) {
        for (Map.Entry<String, Object> entry : args.entrySet()) {
            failIfArgExists(entry.getKey());
            this.arguments.put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public BladeTemplate args(Object... args) {
        if (args.length % 2 == 1)
            throw UncheckedTemplateException.invalidNumberOfArguments(args.length);
        String key;
        for (int i = 0; i < args.length; i += 2) {
            key = (String) args[i];
            failIfArgExists(key);
            this.arguments.put(key, args[i + 1]);
        }
        return this;
    }

    /**
     */
    public String fmt() {

        final StringBuilder result = new StringBuilder(str.length());
        final StringBuilder param  = new StringBuilder(16);

        State state = State.FREE_TEXT;

        int  i = 0;
        char chr;
        while (i < str.length()) {
            chr = str.charAt(i);
            state = nextState(state, i);
            switch (state) {
                // In this state we just add the character to the
                // resulting buffer. No need to perform any processing.
                case FREE_TEXT: {
                    result.append(chr);
                    break;
                }
                // We identify '$'. We skip the following '{'.
                case PARAM_START: {
                    i++;
                    break;
                }
                // We append the character to the param chain buffer
                case PARAM: {
                    validateParamChar(chr, i);
                    param.append(chr);
                    break;
                }
                // We append and replace the param with the correct value
                case PARAM_END: {
                    appendParamValue(param, result);
                    break;
                }
                // Escape character
                case ESCAPE_CHAR:
                    break;
            }
            i++;
        }

        return result.toString();
    }

    // The method that is used to change the states depending on the index
    // in the string and the current value of the character
    private State nextState(State currentState, int i) {
        switch (currentState) {
            case FREE_TEXT:
                return jumpFromFreeText(str, i);
            case PARAM_START:
                return jumpFromParamStart(str, i);
            case PARAM:
                return jumpFromParam(str, i);
            case PARAM_END:
                return jumpFromParamEnd(str, i);
            case ESCAPE_CHAR:
                return State.FREE_TEXT;
            // Should never go here
            default:
                throw UncheckedTemplateException.invalidStateException(currentState);
        }
    }

    // This methods gets called when we want to obtain the value of the parameter
    //
    // - The parameter can be a simple argument "#{intVal}" and in this case
    // it is obtained directly from the arguments map.
    //
    // - The parameter can be a method chain argument: "#{address.getLine1.getNumber}"
    // in this case it is obtained by calling recursively the methods on the last obtained object
    private void appendParamValue(StringBuilder param, StringBuilder result) {
        if (param == null)
            throw UncheckedTemplateException.invalidArgumentName(param);

        // Object name is the parameter that should be found in the map.
        // If it's followed by points, the points remain in the "param" buffer.
        final String objectName  = takeUntilDotOrEnd(param);
        final Object objectValue = arguments.get(objectName);


        Object toAppend;
        if (param.length() != 0) {
            // If this is a chain object.method1.method2.method3
            // we recurse
            toAppend = valueInChain(objectValue, param);
        } else {
            // We evaluate if the obejct is an array
            // If it's an array we print it nicely
            toAppend = evaluateIfArray(objectValue);
        }

        result.append(toAppend);
    }

    private static Object evaluateIfArray(Object o) {
        if (null != o && o.getClass().isArray())
            return arrayToString(o);
        return o;
    }

    private static String arrayToString(Object array) {
        final StringBuilder buff = new StringBuilder("[");

        for (int i = 0; i < getLength(array); ++i)
            buff.append(get(array, i)).append(", ");

        return clearLastComma(buff).append("]").toString();
    }

    private static StringBuilder clearLastComma(StringBuilder buff) {
        int lastComma = buff.lastIndexOf(", ");

        // No comma found, take everything
        if (-1 != lastComma)
            buff.delete(lastComma, buff.length());

        return buff;
    }

    // This method takes the section until the end of the buff or
    // until it finds the first dot ".".
    private static String takeUntilDotOrEnd(StringBuilder buff) {

        final int firstPointIdx = buff.indexOf(".");
        String    result;

        if (-1 == firstPointIdx) {
            result = buff.toString();
            buff.setLength(0);
        } else {
            result = buff.substring(0, firstPointIdx);
            buff.delete(0, firstPointIdx + 1);
        }

        return result;
    }

    // Recursively obtain the value from the method chain by invoking the methods
    // using reflection on the last object obtained.
    private static Object valueInChain(Object object, StringBuilder paramBuffer) {

        // When last obtained is null or when there are no more methods in the chain
        // we stop
        if (object == null || paramBuffer.length() == 0) {
            return evaluateIfArray(object);
        }

        final String methodName = takeUntilDotOrEnd(paramBuffer);

        Object newObject;
        try {
            // Try with the given method or with the getter as a fallback
            Method method = getMethodOrGetter(object, methodName);

            if (null == method)
                return null;

            newObject = method.invoke(object);
            return valueInChain(newObject, paramBuffer);
        } catch (IllegalAccessException | InvocationTargetException e) {
            // Couldn't invoke the method
            return null;
        }
    }

    private static Method getMethodOrGetter(Object object, String methodName) {
        Method method;
        try {
            method = object.getClass().getMethod(methodName);
        } catch (NoSuchMethodException e) {
            try {
                // Maybe improve this
                final String capital         = methodName.substring(0, 1).toUpperCase();
                final String nameCapitalized = "get" + capital + methodName.substring(1);
                method = object.getClass().getMethod(nameCapitalized);
            } catch (NoSuchMethodException e1) {
                return null;
            }
        }
        return method;
    }

    private static State jumpFromFreeText(String fmt, int idx) {
        if (isEscapeChar(fmt, idx))
            return State.ESCAPE_CHAR;
        if (isParamStart(fmt, idx))
            return State.PARAM_START;
        return State.FREE_TEXT;
    }

    private static State jumpFromParamStart(String fmt, int idx) {
        if (isParamEnd(fmt, idx))
            return State.PARAM_END;
        return State.PARAM;
    }

    private static State jumpFromParam(String fmt, int idx) {
        if (isParamEnd(fmt, idx))
            return State.PARAM_END;
        return State.PARAM;
    }

    private static State jumpFromParamEnd(String fmt, int idx) {
        if (isEscapeChar(fmt, idx))
            return State.ESCAPE_CHAR;
        if (isParamStart(fmt, idx))
            return State.PARAM_START;
        return State.FREE_TEXT;
    }

    private static boolean isParamStart(String fmt, int idx) {
        return ('$' == fmt.charAt(idx)) &&
                (idx + 1 < fmt.length() && ('{' == fmt.charAt(idx + 1)));
    }

    private static boolean isParamEnd(String fmt, int idx) {
        return '}' == fmt.charAt(idx);
    }

    private static boolean isEscapeChar(String fmt, int idx) {
        return '`' == fmt.charAt(idx);
    }

    private static void validateParamChar(char cc, int idx) {
        if (!(isDigit(cc) || isLetter(cc) || '.' == cc))
            throw UncheckedTemplateException.invalidCharacterInParam(cc, idx);
    }
}