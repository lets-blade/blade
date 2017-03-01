/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.config;

import com.blade.Const;
import com.blade.kit.Assert;
import com.blade.kit.CollectionKit;
import com.blade.kit.StringKit;
import com.blade.kit.base.Config;
import com.blade.mvc.context.DynamicContext;
import com.blade.mvc.view.ViewSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static com.blade.Blade.$;

/**
 * Blade Application Config Class
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.6.6
 */
public class BConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(BConfig.class);

    private Set<String> packages;
    private Set<String> statics;
    // Encoding
    private String encoding = Const.DEFAULT_ENCODING;

    // Is dev mode
    private boolean isDev = true;
    private boolean isInit = false;
    private String webRoot;
    private Class<?> applicationClass;
    private String classPath = "config";
    private Config config = new Config();
    private String basePackage;

    public BConfig() {
        this.packages = CollectionKit.newHashSet();
        this.statics = new HashSet<>(CollectionKit.asList("/public/", "/assets/", "/static/"));
    }

    public void setEnv(Config config) {
        if (null != config && !isInit) {
            this.isDev = config.getBoolean(Const.APP_DEV, true);
            this.basePackage = config.get(Const.APP_BASE_PKG);

            // config scan ioc packages
            String pkgs = config.get(Const.APP_SCAN);
            if (StringKit.isNotBlank(pkgs)) {
                String[] pkArr = StringKit.split(pkgs, ",");
                packages.addAll(CollectionKit.asList(pkArr));
            }

            // get view 404, 500 page
            ViewSettings.$().setView500(config.get(Const.MVC_VIEW_500));
            ViewSettings.$().setView404(config.get(Const.MVC_VIEW_404));

            this.encoding = config.get(Const.HTTP_ENCODING, Const.DEFAULT_ENCODING);

            // get mvc static folders
            String statics = config.get(Const.MVC_STATICS);

            this.classPath = config.get(Const.APP_CLASSPATH, "config");

            // get server start port
            int port = config.getInt(Const.SERVER_PORT, Const.DEFAULT_PORT);

            $().listen(port);

            if (StringKit.isNotBlank(statics)) {
                this.addStatic(StringKit.split(statics, ','));
            }

            if (StringKit.isNotBlank(basePackage)) {
                this.setBasePackage(basePackage);
            }
            isInit = true;
        }
    }

    public boolean isDev() {
        return isDev;
    }

    public String getEncoding() {
        return encoding;
    }

    public boolean isInit() {
        return this.isInit;
    }

    public String webRoot() {
        return this.webRoot;
    }

    public Class<?> getApplicationClass() {
        return applicationClass;
    }

    public void setApplicationClass(Class<?> applicationClass) {
        this.applicationClass = applicationClass;
        DynamicContext.init(applicationClass);
    }

    public void setBasePackage(String basePackage) {
        Assert.notBlank(basePackage);
        packages.add(basePackage + ".controller");
        packages.add(basePackage + ".service");
        packages.add(basePackage + ".config");
        packages.add(basePackage + ".context");
        packages.add(basePackage + ".plugins");
        packages.add(basePackage + ".init");
        packages.add(basePackage + ".interceptor");
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setWebRoot(String webRoot) {
        this.webRoot = webRoot;
    }

    public void setDev(boolean isDev) {
        this.isDev = isDev;
    }

    public String getClassPath() {
        return classPath;
    }

    public void load(String location) {
        try {
            config.add(location);
        } catch (Exception e) {
            LOGGER.warn("[load config] " + e.getMessage());
        }
    }

    public Config config() {
        return config;
    }

    public void addScanPackage(String... packages) {
        if (null != packages && packages.length > 0) {
            this.packages.addAll(CollectionKit.asList(packages));
        }
    }

    public Set<String> getStatics() {
        return statics;
    }

    public void addStatic(String[] statics) {
        if (null != statics && statics.length > 0) {
            for (String s : statics) {
                if (s.endsWith("/*")) {
                    s = s.replace("/*", "");
                }
                if (!s.endsWith("/")) {
                    s += "/";
                }
                this.statics.add(s);
            }
        }
    }

    public Set<String> getPackages() {
        return packages;
    }
}
