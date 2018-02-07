package com.blade.kit;

import lombok.NoArgsConstructor;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:chenchen_839@126.com" target="_blank">ccqy66</a>
 * @Date: 2018/1/8
 *
 * file name format：i18n_{language}_{country}.properties
 * for example:i18n_zh_CN.properties
 * <p>How to use</p>
 * i18n_zh_CN.properties:
 * name=ccqy66
 * ...
 *
 * public String getConfig(String key) {
 *     return I18nUtils.getInstance("i18n_zh_CN.properties").get(key);
 * }
 * or
 * public String getConfig(String key) {
 *     return I18nUtils.getInstance(new Locale("zh","CN")).get(key);
 * }
 *
 */
@NoArgsConstructor
public class I18nUtils {
    private static Map<String,ResourceHolder> CACHE = new ConcurrentHashMap<>();
    private static Pattern pattern = Pattern.compile("_");

    public static synchronized ResourceHolder getInstance(String baseName){
        return createResourceHolder(baseName,null);
    }
    public static synchronized ResourceHolder getInstance(Locale locale) {
        return createResourceHolder(null,locale);
    }
    private static ResourceHolder createResourceHolder(String baseName,Locale locale) {
        Tuple2<String,Locale> localeModel = toLocaleModel(baseName, locale);
        ResourceHolder holder = CACHE.get(localeModel._1());
        if (null != holder) {
            return holder;
        }
        holder = new ResourceHolder(ResourceBundle.getBundle(localeModel._1(),localeModel._2()));
        CACHE.putIfAbsent(localeModel._1(),holder);
        return holder;
    }
    public static Tuple2<String,Locale> toLocaleModel(String baseName,Locale locale) {
        if (StringKit.isBlank(baseName)) {
            return new Tuple2<>("i18n_"+locale.getLanguage()+"_"+locale.getCountry(),locale);
        }else {
            String[] baseNames = pattern.split(baseName);
            if (baseNames != null && baseNames.length == 3) {
                return new Tuple2<>(baseName,new Locale(baseNames[1],baseNames[2]));
            }
            throw new IllegalArgumentException("baseName illegal,name format is :i18n_{language}_{country}.properties," +
                    "for example:i18n_zh_CN.properties");
        }
    }

    public static class ResourceHolder {
        private ResourceBundle resourceBundle;
        public ResourceHolder(ResourceBundle resourceBundle) {
            this.resourceBundle = resourceBundle;
        }
        public String get(String key) {
            return resourceBundle.getString(key);
        }
        public Object getObject(String key) {
            return resourceBundle.getObject(key);
        }
        public boolean containsKey(String key) {
            return resourceBundle.containsKey(key);
        }
        public String format(String key,String ... params) {
            return MessageFormat.format(resourceBundle.getString(key),params);
        }
    }
}
