package com.blade.mvc.ui;

import com.blade.mvc.Const;

import java.util.List;

public class HtmlCreator {

    private static final String HTML_FOOTER = "<br/><p><center>Copyright © <a href='https://github.com/biezhi/blade' target='_blank'>Blade-" + Const.VERSION + "</a></center></p><br/>";

    private StringBuilder html = new StringBuilder();

    public HtmlCreator() {
        html.append("<!DOCTYPE html>");
        html.append("<head>");
        html.append("<meta charset=\"utf-8\">");
        html.append("<style type='text/css'>.version{padding:10px;text-decoration-line: none;}.message-header{" +
                "background-color: #900C3F;\n" +
                "color: #fff;\n" +
                "-webkit-box-align: center;\n" +
                "-ms-flex-align: center;\n" +
                "align-items: center;\n" +
                "border-radius: 3px 3px 0 0;\n" +
                "color: #fff;\n" +
                "display: -webkit-box;\n" +
                "display: -ms-flexbox;\n" +
                "display: flex;\n" +
                "-webkit-box-pack: justify;\n" +
                "-ms-flex-pack: justify;\n" +
                "justify-content: space-between;\n" +
                "line-height: 1.25;\n" +
                "padding: 0.5em 0.75em;\n" +
                "position: relative;}" +
                ".message-body{" +
                "background-color: #fff5f7;" +
                "font-size:1rem;" +
                "border-color: #ff3860;\n" +
                "color: #900C3F;\n" +
                "border-top-left-radius: 0;\n" +
                "border-top-right-radius: 0;\n" +
                "border-top: none;\n" +
                "border: 1px solid #dbdbdb;\n" +
                "border-radius: 3px;\n" +
                "padding: 1em 1.25em;" +
                "}</style>");
    }

    public HtmlCreator title(String title) {
        html.append("<title>").append(title).append("</title>");
        return this;
    }

    public HtmlCreator h1(String heading) {
        html.append("</head>");
        html.append("<body>");
        html.append("<h1>").append(heading).append("</h1>");
        return this;
    }

    public HtmlCreator h2(String heading) {
        html.append("<h2>").append(heading).append("</h2>");
        return this;
    }

    public HtmlCreator paragraph(String paragraph) {
        html.append("<p>").append(paragraph).append("</p>");
        return this;
    }

    public HtmlCreator startP() {
        html.append("<p>");
        return this;
    }

    public HtmlCreator startP(String className) {
        html.append("<p class='" + className + "'>");
        return this;
    }

    public HtmlCreator addBold(String text) {
        html.append("<b>").append(text).append("</b>");
        return this;
    }

    public HtmlCreator endP() {
        html.append("</p>");
        return this;
    }

    public HtmlCreator hr() {
        html.append("<hr/>");
        return this;
    }

    public HtmlCreator br() {
        html.append("<br/>");
        return this;
    }

    public HtmlCreator addTableWithHeaders(List<String> headers) {
        html.append("<table>");
        html.append("<tr>");
        headers.forEach((h) -> html.append("<th>").append(h).append("</th>"));
        html.append("</tr>");
        return this;
    }

    public HtmlCreator addRowToTable(List<String> rowElements) {
        html.append("<tr>");
        rowElements.forEach((re) -> html.append("<td>").append(re).append("</td>"));
        html.append("</tr>");
        return this;
    }

    public HtmlCreator endTable() {
        html.append("</table>");
        return this;
    }

    public HtmlCreator startStyle() {
        html.append("<style type='text/css'>");
        return this;
    }

    public HtmlCreator endStyle() {
        html.append("</style>");
        return this;
    }

    public HtmlCreator centerHeadings() {
        html.append("h1, h2 { text-align: center; }");
        return this;
    }

    public HtmlCreator add(String text) {
        html.append(text);
        return this;
    }

    public HtmlCreator styleTables() {
        html.append("table, th, td { margin: 0 auto; border: 1px solid black; border-collapse: collapse; text-align: center; }");
        return this;
    }

    public HtmlCreator center(String text) {
        html.append("<center>").append(text).append("</center>");
        return this;
    }

    public String toString() {
        return html.toString();
    }

    public String html() {
        html.append(HTML_FOOTER);
        html.append("</body>");
        return html.toString();
    }

}
