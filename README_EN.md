#Blade
*The Swiss army knife in javaweb*

[中文](https://github.com/biezhi/blade/blob/master/README.md)

[![@biezhi on weibo](https://img.shields.io/badge/weibo-%40biezhi-red.svg)](http://weibo.com/u/5238733773)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/biezhi/blade/blob/master/license.txt)
[![Build Status](https://api.travis-ci.org/biezhi/blade.svg?branch=master)](https://travis-ci.org/biezhi/blade)
[![JDK](http://img.shields.io/badge/JDK-v1.6+-blue.svg)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
[![maven](https://img.shields.io/maven-central/v/com.bladejava/blade.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.bladejava%22)

**blade** Is a lightweight rapid development of the web application framework, it integrates the IOC object management, vehicle configuration, REST API development and so on many mainstream web features.

## Blade Features

+ Simple MVC
+ REST API
+ Note the interceptor
+ The microkernel IOC container
+ Utility class
+ A template engine support
+ Support JDK1.6 +
+ Built-in jetty start
+ Plug-in extension mechanism
+ ...

## Quick start
First. Use maven to build a webapp, join dependency on the blade,Recommended for the [latest version](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.bladejava%22)

```xml
<dependency>
	<groupId>com.bladejava</groupId>
	<artifactId>blade</artifactId>
	<version>x.x.x</version>
</dependency>
```
	
Second. Configuration in the `web.xml` Blade core filter initialization and set your class, and you can also not configuration(using jetty start)
	
```xml
<web-app>
	<display-name>Archetype Created Web Application</display-name>
	<filter>
		<filter-name>BladeFilter</filter-name>
		<filter-class>blade.BladeFilter</filter-class>
		<init-param>
			<param-name>applicationClass</param-name>
			<param-value>blade.sample.App</param-value>
		</init-param>
	</filter>
	
	<filter-mapping>
		<filter-name>BladeFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
	
</web-app>
```

Third. Write App.java and routing file, here is an example

```java
public class App extends BladeApplication{

	Logger logger = Logger.getLogger(App.class);
	@Override
	public void init() {
		// 设置路由、拦截器包所在包
		Blade.defaultRoute("blade.sample");
	}
	
}
```

	
```java
@Path
public class Hello {
	
	@Route("/hello")
	public String hello() {
		System.out.println("hello");
		return "hello.jsp";
	}
		
	@Route(value = "/post", method = HttpMethod.POST)
	public void post(Request request) {
		String name = request.query("name");
		System.out.println("name = " + name);
	}
	
	@Route("/users/:name")
	public ModelAndView users(Request request, Response response) {
		System.out.println("users");
		String name = request.pathParam(":name");
		
		ModelAndView modelAndView = new ModelAndView("users");
		modelAndView.add("name", name);
		return modelAndView;
	}

	@Route("/index")
	public String index(Request request) {
		request.attribute("name", "jack");
		return "index.jsp";
	}
	
}
```
	
OK, all this may seem simple, refer to the guidelines for use more ready-made examples for your reference:

[Blade Guide](http://bladejava.com/doc/en)


## Update Log

### v1.0.8
	1. Optimize the file upload
	2. Optimized matching routing
	3. Add methods perform monitoring
	
### v1.0.4
	1. Optimize the underlying IO
	2. Simplify the plug-in extension
	3. Matching vehicle routing separation
	4. Repair jetty in running maven environment more bugs
	
### v1.0.1
	1. Remove excess public methods
	2. Add the `Blade.run()` run jetty
	3. Add the `Blade.register()` method register bean object
	4. Optimize the ioc object management
	5. Add initialization to monitor the context

### v1.0.0
	The first stable release
			
## Open source licenses
Blade Framework based on the[Apache2 License](https://github.com/biezhi/blade/blob/master/license.txt)

## Contact me
Mail: biezhi.me#gmail.com

QQ Group: [1013565](http://shang.qq.com/wpa/qunwpa?idkey=932642920a5c0ef5f1ae902723c4f168c58ea63f3cef1139e30d68145d3b5b2f)
