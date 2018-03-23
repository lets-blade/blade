package com.blade.mvc.ui.template;

import com.blade.exception.TemplateException;
import com.blade.kit.BladeKit;
import com.blade.kit.IOKit;
import com.blade.mvc.Const;
import com.blade.mvc.WebContext;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Session;
import com.blade.mvc.ui.ModelAndView;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * default template implment
 *
 * @author biezhi
 * 2017/5/31
 */
@Slf4j
public class DefaultEngine implements TemplateEngine {

    public static String TEMPLATE_PATH = "templates";

    @Override
    public void render(ModelAndView modelAndView, Writer writer) throws TemplateException {
        String view = modelAndView.getView();

        String viewPath = Const.CLASSPATH + File.separator + TEMPLATE_PATH + File.separator + view;
        viewPath = viewPath.replace("//", "/");

        try {
            String body;
            if (BladeKit.isInJar()) {
                viewPath = File.separator + TEMPLATE_PATH + File.separator + view;
                viewPath = viewPath.replace("//", "/");
                InputStream    in     = getClass().getResourceAsStream(viewPath);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                body = IOKit.readToString(reader);
            } else {
                body = IOKit.readToString(viewPath);
            }

            Request request = WebContext.request();

            Map<String, Object> attributes = new HashMap<>();
            Map<String, Object> reqAttrs = request.attributes();
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
