package com.blade.kit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.blade.kit.BladeKit.isWindows;

/**
 * Usage:
 * <li>String msg = Ansi.Red.and(Ansi.BgYellow).format("Hello %s", name)</li>
 * <li>String msg = Ansi.Blink.colorize("BOOM!")</li>
 * <p>
 * Or, if you are adverse to that, you can use the constants directly:
 * <li>String msg = new Ansi(Ansi.ITALIC, Ansi.GREEN).format("Green money")</li>
 * Or, even:
 * <li>String msg = Ansi.BLUE + "scientific"</li>
 * <p>
 * NOTE: Nothing stops you from combining multiple FG colors or BG colors,
 * but only the last one will display.
 *
 * @author dain
 */
public final class Ansi {

    // Color code strings from:
    // http://www.topmudsites.com/forums/mud-coding/413-java-ansi.html
    public static final String SANE = "\u001B[0m";

    public static final String HIGH_INTENSITY = "\u001B[1m";
    public static final String LOW_INTENSITY  = "\u001B[2m";

    public static final String ITALIC         = "\u001B[3m";
    public static final String UNDERLINE      = "\u001B[4m";
    public static final String BLINK          = "\u001B[5m";
    public static final String RAPID_BLINK    = "\u001B[6m";
    public static final String REVERSE_VIDEO  = "\u001B[7m";
    public static final String INVISIBLE_TEXT = "\u001B[8m";

    public static final String BLACK   = "\u001B[30m";
    public static final String RED     = "\u001B[31m";
    public static final String GREEN   = "\u001B[32m";
    public static final String YELLOW  = "\u001B[33m";
    public static final String BLUE    = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN    = "\u001B[36m";
    public static final String WHITE   = "\u001B[37m";

    public static final String BACKGROUND_BLACK   = "\u001B[40m";
    public static final String BACKGROUND_RED     = "\u001B[41m";
    public static final String BACKGROUND_GREEN   = "\u001B[42m";
    public static final String BACKGROUND_YELLOW  = "\u001B[43m";
    public static final String BACKGROUND_BLUE    = "\u001B[44m";
    public static final String BACKGROUND_MAGENTA = "\u001B[45m";
    public static final String BACKGROUND_CYAN    = "\u001B[46m";
    public static final String BACKGROUND_WHITE   = "\u001B[47m";

    public static final Ansi HighIntensity = new Ansi(HIGH_INTENSITY);
    public static final Ansi Bold          = HighIntensity;
    public static final Ansi LowIntensity  = new Ansi(LOW_INTENSITY);
    public static final Ansi Normal        = LowIntensity;

    public static final Ansi Italic     = new Ansi(ITALIC);
    public static final Ansi Underline  = new Ansi(UNDERLINE);
    public static final Ansi Blink      = new Ansi(BLINK);
    public static final Ansi RapidBlink = new Ansi(RAPID_BLINK);

    public static final Ansi Black   = new Ansi(BLACK);
    public static final Ansi Red     = new Ansi(RED);
    public static final Ansi Green   = new Ansi(GREEN);
    public static final Ansi Yellow  = new Ansi(YELLOW);
    public static final Ansi Blue    = new Ansi(BLUE);
    public static final Ansi Magenta = new Ansi(MAGENTA);
    public static final Ansi Cyan    = new Ansi(CYAN);
    public static final Ansi White   = new Ansi(WHITE);

    public static final Ansi BgBlack   = new Ansi(BACKGROUND_BLACK);
    public static final Ansi BgRed     = new Ansi(BACKGROUND_RED);
    public static final Ansi BgGreen   = new Ansi(BACKGROUND_GREEN);
    public static final Ansi BgYellow  = new Ansi(BACKGROUND_YELLOW);
    public static final Ansi BgBlue    = new Ansi(BACKGROUND_BLUE);
    public static final Ansi BgMagenta = new Ansi(BACKGROUND_MAGENTA);
    public static final Ansi BgCyan    = new Ansi(BACKGROUND_CYAN);
    public static final Ansi BgWhite   = new Ansi(BACKGROUND_WHITE);

    final private String[] codes;
    final private String   codes_str;

    public Ansi(String... codes) {
        this.codes = codes;
        String _codes_str = "";
        for (String code : codes) {
            _codes_str += code;
        }
        codes_str = _codes_str;
    }

    public Ansi and(Ansi other) {
        List<String> both = new ArrayList<String>();
        Collections.addAll(both, codes);
        Collections.addAll(both, other.codes);
        return new Ansi(both.toArray(new String[]{}));
    }

    public String colorize(String original) {
        return codes_str + original + SANE;
    }

    public String format(String template, Object... args) {
        if (isWindows()) {
            if (null == args || args.length == 0) {
                return template;
            }
            return String.format(template, args);
        }
        String text = (null == args || args.length == 0) ? template : String.format(template, args);
        return colorize(text);
    }

}