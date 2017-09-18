package com.blade.mvc.ui;

import com.blade.mvc.Const;

/**
 * @author biezhi
 *         2017/6/2
 */
public interface DefaultUI {

    String HTML_FOOTER = "<hr/><br/><p><center><a href='https://github.com/biezhi/blade' target='_blank'>Blade-" + Const.VERSION + "</a></center></p>";

    /**
     * server 404
     */
    String VIEW_404 = new HtmlCreator().title("404 Not Found").center("<h1>[ %s ] Not Found</h1>").html();

    String[] banner = {
            " __, _,   _, __, __,",
            " |_) |   /_\\ | \\ |_",
            " |_) | , | | |_/ |",
            " ~   ~~~ ~ ~ ~   ~~~"
    };

    static void printBanner() {
        StringBuffer text  = new StringBuffer();
        String       space = "\t\t\t\t\t\t\t   ";
        for (String s : banner) {
            text.append("\r\n").append(space).append(s);
        }
        text.append("\r\n")
                .append(space)
                .append(" :: Blade :: (v")
                .append(Const.VERSION + ") \r\n");
        System.out.println(text.toString());
    }

}
