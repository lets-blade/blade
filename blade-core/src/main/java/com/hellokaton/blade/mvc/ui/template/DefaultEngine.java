package com.hellokaton.blade.mvc.ui.template;

import com.hellokaton.blade.exception.TemplateException;
import com.hellokaton.blade.kit.BladeKit;
import com.hellokaton.blade.kit.IOKit;
import com.hellokaton.blade.mvc.BladeConst;
import com.hellokaton.blade.mvc.WebContext;
import com.hellokaton.blade.mvc.http.Request;
import com.hellokaton.blade.mvc.http.Session;
import com.hellokaton.blade.mvc.ui.ModelAndView;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * default template implment
 *
 * @author biezhi
 * 2017/5/31
 */
@Slf4j
public class DefaultEngine implements TemplateEngine {

    public  static       String TEMPLATE_PATH = "templates";
    private static final String PATH_SEPARATOR = "/";

    @Override
    public void render(ModelAndView modelAndView, Writer writer) throws TemplateException {
        String view = modelAndView.getView();
        String body;

        String viewPath;
        if (BladeConst.CLASSPATH.endsWith(PATH_SEPARATOR)) {
            viewPath = BladeConst.CLASSPATH + TEMPLATE_PATH + PATH_SEPARATOR + view;
        } else {
            viewPath = BladeConst.CLASSPATH + PATH_SEPARATOR + TEMPLATE_PATH + PATH_SEPARATOR + view;
        }

        try {
            if (view.startsWith("jar:")) {
                String         jarPath = view.substring(4);
                InputStream    input   = DefaultEngine.class.getResourceAsStream(jarPath);
                BufferedReader reader  = new BufferedReader(new InputStreamReader(input));
                body = IOKit.readToString(reader);
            } else {
                if (BladeKit.runtimeIsJAR()) {
                    viewPath = PATH_SEPARATOR + TEMPLATE_PATH + PATH_SEPARATOR + view;

                    InputStream    in     = getClass().getResourceAsStream(viewPath);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    body = IOKit.readToString(reader);
                } else {
                    body = IOKit.readToString(viewPath);
                }
            }

            Request request = WebContext.request();

            Map<String, Object> attributes = new HashMap<>();
            Map<String, Object> reqAttrs   = request.attributes();
            attributes.putAll(reqAttrs);
            attributes.putAll(modelAndView.getModel());

            Session session = request.session();
            if (null != session) {
                attributes.putAll(session.attributes());
            }
            String result = BladeTemplate.template(body, attributes).fmt();
            writer.write(result);
        } catch (Exception e) {
            log.warn("View path is: {}", viewPath);
            throw new TemplateException(e);
        } finally {
            IOKit.closeQuietly(writer);
        }
    }

}
