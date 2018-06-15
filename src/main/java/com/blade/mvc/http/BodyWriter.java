package com.blade.mvc.http;

public interface BodyWriter<T> {

    T onText(StringBody body);

    T onStream(StreamBody body);

    T onView(ViewBody body);

    T onEmpty(EmptyBody emptyBody);

    T onRawBody(RawBody body);

}