package com.blade.mvc.ui.template;

import com.blade.exception.TemplateException;
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
 * default template implment
 *
 * @author biezhi
 * 2017/5/31
 */
public class DefaultEngine implements TemplateEngine {

    public static String TEMPLATE_PATH = "templates";

    @Override
    public void render(ModelAndView modelAndView, Writer writer) throws TemplateException {
        String view     = modelAndView.getView();
        String viewPath = Const.CLASSPATH + File.separator + TEMPLATE_PATH + File.separator + view;
        viewPath = viewPath.replace("//", "/");
        try {
            Request request = WebContext.request();
            String  body    = IOKit.readToString(viewPath);

            Map<String, Object> attributes = new HashMap<>();
            attributes.putAll(request.attributes());
            attributes.putAll(modelAndView.getModel());

            Session session = request.session();
            if (null != session) {
                attributes.putAll(session.attributes());
            }
            String result = BladeTemplate.template(body, attributes).fmt();
            writer.write(result);
        } catch (Exception e) {
            throw new TemplateException(e);
        } finally {
            IOKit.closeQuietly(writer);
        }
    }
}
