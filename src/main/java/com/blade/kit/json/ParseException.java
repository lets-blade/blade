package com.blade.kit.json;

public class ParseException extends RuntimeException {

    private int position = 0;
    private String json = "";

    public ParseException(String json, int position, String message) {
        super(message);
        this.json = json;
        this.position = position;
    }

    @Override
    public String getMessage() {
        final int maxTipLength = 10;
        int end = position + 1;
        int start = end - maxTipLength;
        if (start < 0) start = 0;
        if (end > json.length()) end = json.length();
        return String.format("%s  (%d):%s", json.substring(start, end), position, super.getMessage());
    }

    public String getJson() {
        return this.json;
    }

    public int getPosition() {
        return this.position;
    }

}
