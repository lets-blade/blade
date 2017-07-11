package com.blade.mvc.ui;

import com.blade.mvc.Const;

/**
 * @author biezhi
 *         2017/6/2
 */
public interface DefaultUI {

    String HTML_FOOTER = "<hr/><br/><p><center><a href='https://github.com/biezhi/blade' target='_blank'>Blade-" + Const.VERSION + "</a></center></p>";

    /**
     * server 500
     */
    String VIEW_500 = new HtmlCreator().title("500 Internal Error").center("500 Internal Error").html();

    /**
     * server 404
     */
    String VIEW_404 = new HtmlCreator().title("404 Not Found").center("<h1>URL [ %s ] Not Found</h1>").html();

    String ERROR_START = new HtmlCreator().title("500 Internal Error").startStyle()
            .add("*{margin:0;padding:0;font-weight:400;}.info{margin:0;padding:10px;color:#000;background-color:#fff;height:60px;line-height:60px;border-bottom:5px solid #15557a}.isa_error{margin:0;padding:10px;font-size:14px;font-weight:bold;background-color:#e9eff1;border-bottom:1px solid #000}")
            .endStyle()
            .add("<div class='info'><h3>%s</h3></div><div class='isa_error'><pre>").toString();

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
