package com.blade.mvc.ui.template;

public class UncheckedTemplateException extends RuntimeException {

    private static final String INVALID_NUMBER_OF_ARGUMENTS = "Invalid number of arguments: ${argsNum}. Every argument needs to have a pair.";

    private static final String ARGUMENT_ALREADY_DEFINED = "Argument '${arg}' is already defined in the arguments list.";

    private static final String INVALID_CHARACTER_IN_PARAM_NAME = "Invalid character '${char}' used in param name (affected index: ${idx}).";

    private static final String IO_EXCEPTION_READING_FROM_FILE = "Error accessing ${strPath}. Exception:";

    private static final String INVALID_ARGUMENT_NAME_NULL_OR_EMPTY = "Invalid argument name: '${arg}'. Argument should not be null or empty";

    private static final String INVALID_STATE_EXCEPTION = "Invalid state: '${state}'. No code coverage for this new state.";

    public UncheckedTemplateException(String message) {
        super(message);
    }

    public UncheckedTemplateException(String message, Throwable cause) {
        super(message, cause);
    }

    public UncheckedTemplateException(Throwable cause) {
        super(cause);
    }

    public static UncheckedTemplateException invalidNumberOfArguments(int argsNum) {
        String msg = BladeTemplate.template(INVALID_NUMBER_OF_ARGUMENTS).arg("argsNum", argsNum).fmt();
        return new UncheckedTemplateException(msg);
    }

    public static UncheckedTemplateException argumentAlreadyExist(String arg) {
        String msg = BladeTemplate.template(ARGUMENT_ALREADY_DEFINED).arg("arg", arg).fmt();
        return new UncheckedTemplateException(msg);
    }

    public static UncheckedTemplateException invalidCharacterInParam(char c, int idx) {
        String msg = BladeTemplate.template(INVALID_CHARACTER_IN_PARAM_NAME).args("char", c, "idx", idx).fmt();
        return new UncheckedTemplateException(msg);
    }

    public static UncheckedTemplateException ioExceptionReadingFromFile(String strPath, Throwable t) {
        String msg = BladeTemplate.template(IO_EXCEPTION_READING_FROM_FILE).arg("strPath", strPath).fmt();
        return new UncheckedTemplateException(msg, t);
    }

    public static UncheckedTemplateException invalidArgumentName(Object argName) {
        String msg = BladeTemplate.template(INVALID_ARGUMENT_NAME_NULL_OR_EMPTY, "arg", argName).fmt();
        return new UncheckedTemplateException(msg);
    }

    public static UncheckedTemplateException invalidStateException(BladeTemplate.State state) {
        String msg = BladeTemplate.template(INVALID_STATE_EXCEPTION, "state", state).fmt();
        return new UncheckedTemplateException(msg);
    }

}