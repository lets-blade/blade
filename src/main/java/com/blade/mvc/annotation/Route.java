package com.blade.mvc.annotation;

import com.blade.mvc.http.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods defined on the route notes
 * <p>
 * Restful routes:
 * <pre>
 * ==========================================================================================
 * verb    path                   action          used for
 * ==========================================================================================
 * GET     /users                 index 	       display a list of all books
 * GET     /users/new_form        new_form        return an HTML form for creating a new book
 * POST    /users                 create 	       create a new book
 * GET     /users/id              show            display a specific book
 * GET     /users/id/edit_form    edit_form       return an HTML form for editing a books
 * PUT     /users/id              update          update a specific book
 * DELETE 	/users/id              destroy         delete a specific book
 * </pre>
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Route {

    /**
     * @return Request url
     */
    String[] value() default "/";

    /**
     * @return Request HttpMethod
     */
    HttpMethod method() default HttpMethod.ALL;

    /**
     * @return Route description
     */
    String description() default "";
}