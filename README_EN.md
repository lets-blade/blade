#Blade

[![a concise and powerful web development framework](http://i1.tietuku.com/0c4b9726253b6268.png "a concise and powerful web development framework")](http://bladejava.com)

[中文](https://github.com/biezhi/blade/blob/master/README.md)

[![@biezhi on weibo](https://img.shields.io/badge/weibo-%40biezhi-red.svg)](http://weibo.com/u/5238733773)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Build Status](https://api.travis-ci.org/biezhi/blade.svg?branch=master)](https://travis-ci.org/biezhi/blade)
[![Circle CI](https://circleci.com/gh/biezhi/blade/tree/master.svg?style=svg)](https://circleci.com/gh/biezhi/blade/tree/master)
[![release](https://img.shields.io/maven-central/v/com.bladejava/blade.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.bladejava%22)

## Introduction

**blade** Is a concise and powerful web development framework, it is built into the `IOC` administration, the interceptor configuration, `REST API` development and so on many mainstream web features, integrate the template engine, a cache plug-in, database operations, commonly used functions such as email, concise source deserves your reading. If you like it, can be `Star or Fork`, thanks!

## Features

* Simple MVC & Interceptor
* REST API
* Annotation way development
* The microkernel IOC container
* Utility class
* A template engine support
* Support JDK1.6 +
* Jetty is started
* Plug-in extension mechanism
* ...

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
		// Set up routing and interceptor bag in bag
		Blade.defaultRoute("blade.sample");
	}
	
}
```

	
```java
@Path("/")
public class Hello {
	
	@Route("hello")
	public String hello() {
		System.out.println("hello");
		return "hello.jsp";
	}
		
	@Route(value = "post", method = HttpMethod.POST)
	public void post(Request request) {
		String name = request.query("name");
		System.out.println("name = " + name);
	}
	
	@Route("users/:name")
	public ModelAndView users(Request request, Response response) {
		System.out.println("users");
		String name = request.pathParam(":name");
		
		ModelAndView modelAndView = new ModelAndView("users");
		modelAndView.add("name", name);
		return modelAndView;
	}

	@Route("index")
	public String index(Request request) {
		request.attribute("name", "jack");
		return "index.jsp";
	}
	
}
```
	
OK, all this may seem simple, refer to the guidelines for use more ready-made examples for your reference:

+ [API Docs](http://bladejava.com/apidocs/)
+ [Use Guide](http://bladejava.com/doc/cn/index.html) (The ongoing...)
+ [Some Examples](https://github.com/bladejava)

### Plan
	1. Perfect the documents
	2. Complete with `blade` single-user blog system development
	3. Write English document
	4. Optimize the code performance
	
### Update

### v1.1.3
	1. Update Bug fix sql2o
	2. Remove the blade-kit useless class
	3. Add email support
	4. Add a program timing
	5. Add the HTTP request support network
	6. Optimization of the built-in logging output
	7. Add tasks to support regularly
	
### v1.1.0
	1. Remove excess public methods
	2. Add the `Blade.run()` run jetty
	3. Add the `Blade.register()` method register bean object
	4. Optimize the ioc object management
	5. Add initialization to monitor the context
	6. Optimize the underlying IO
	7. Simplify the plug-in extension
	8. Matching vehicle routing separation
	9. Repair jetty in running maven environment more bugs
	10. Optimize the file upload
	11. Optimized matching routing
	12. Add methods perform monitoring
	13. Add cache support

### v1.0.0
	The first stable release
			
## licenses
Blade Framework based on the [Apache2 License](http://www.apache.org/licenses/LICENSE-2.0.html)

## Contact
Mail: biezhi.me#gmail.com

QQ Group: [1013565](http://shang.qq.com/wpa/qunwpa?idkey=932642920a5c0ef5f1ae902723c4f168c58ea63f3cef1139e30d68145d3b5b2f)