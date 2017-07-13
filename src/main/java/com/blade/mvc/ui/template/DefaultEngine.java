package com.blade.mvc.ui.template;

import com.blade.exception.BladeException;
import com.blade.kit.IOKit;
import com.blade.mvc.Const;
import com.blade.mvc.WebContext;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Session;
import com.blade.mvc.ui.ModelAndView;

import java.io.File;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * default template implement
 *
 * @author biezhi
 *         2017/5/31
 */
public class DefaultEngine implements TemplateEngine {

    public static String TEMPLATE_PATH = "templates";

    @Override
    public void render(ModelAndView modelAndView, Writer writer) throws BladeException {
        String view = modelAndView.getView();
        String viewPath = Const.CLASSPATH + TEMPLATE_PATH + File.separator + view;
        viewPath = viewPath.replace("//", "/");
        try {
            Request request = WebContext.request();
            String body = IOKit.readToString(viewPath);

            Map<String, Object> attrs = new HashMap<>();
            attrs.putAll(request.attributes());
            Session session = request.session();
            if (null != session) {
                attrs.putAll(session.attributes());
            }
            String result = BladeTemplate.template(body, attrs).fmt();
            writer.write(result);
        } catch (Exception e) {
            throw new BladeException(e);
        } finally {
            IOKit.closeQuietly(writer);
        }
    }
}
