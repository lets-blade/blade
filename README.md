#Blade

[![a concise and powerful web development framework](http://i1.tietuku.com/0c4b9726253b6268.png "a concise and powerful web development framework")](http://bladejava.com)

[中文](https://github.com/biezhi/blade/blob/master/README_CN.md)

[![@biezhi on weibo](https://img.shields.io/badge/weibo-%40biezhi-red.svg)](http://weibo.com/u/5238733773)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Build Status](https://api.travis-ci.org/biezhi/blade.svg?branch=master)](https://travis-ci.org/biezhi/blade)
[![Circle CI](https://circleci.com/gh/biezhi/blade/tree/master.svg?style=svg)](https://circleci.com/gh/biezhi/blade/tree/master)
[![release](https://img.shields.io/maven-central/v/com.bladejava/blade-core.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.bladejava%22)

## Introduction

**blade** Is a concise and powerful web development framework, it is built into the `IOC` administration, the interceptor configuration, `REST API` development and so on many mainstream web features, integrate the template engine, a cache plug-in, database operations, commonly used functions such as email, concise source deserves your reading. If you like it, can be `Star or Fork`, thanks!

## Features

* Simple MVC & interceptor
* RESTful
* Multiple routing configuration
* Micro kernel IOC container
* Practical tools
* Coding/JSON/configuration file
* JDK1.6 +
* Plug-in extension mechanism
* Template engine Plugin
* Cache Plugin
* ...

## Quick start
First. Use maven to build a webapp, join dependency on the blade,Recommended for the [latest version](LAST_VERSION.md)

```xml
<dependency>
	<groupId>com.bladejava</groupId>
	<artifactId>blade-core</artifactId>
	<version>1.2.6-alpha</version>
</dependency>
```
	
Second. Configuration in the `web.xml` Blade core filter initialization and set your class, and you can also not configuration(using jetty start)
	
```xml
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
```

Third. Write App.java and routing file, here is an example

```java
public class App extends BladeApplication{

	Logger logger = Logger.getLogger(App.class);
	@Override
	public void init() {
		// register router
		Blade.regRoute("/hello", SayHi.class, "hello");
		
		// anonymous router，java8 so simple
		Blade.get("/get", new RouteHandler() {
			@Override
			public String run(Request request, Response response) {
				System.out.println("come get!!");
				System.out.println(request.query("name"));
				return "get";
			}
		});
	}
	
}
```
	
#### Functional router
```java
public class SayHi {
	
	public String hello(Request request, Response response){
		System.out.println("come hello~");
		request.attribute("name", "rose baby");
		return "hi";
	}
}
```

#### Annotations router
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

+ [hello project](https://github.com/bladejava/hello)
+ [api docs](http://bladejava.com/apidocs/)
+ [user guide](https://github.com/biezhi/blade/wiki)
+ [some examples](https://github.com/bladejava)

## Plan

	1. Improve the document
	2. Single user blog system development
	3. web chat system
	4. Optimize the code performance
	
## Update

[update log](https://github.com/biezhi/blade/blob/master/UPDATE_LOG.md)
			
## licenses

Blade Framework based on the [Apache2 License](http://www.apache.org/licenses/LICENSE-2.0.html)

## Contact

OSC Blog:[http://my.oschina.net/biezhi](http://my.oschina.net/biezhi)

Mail: biezhi.me#gmail.com

QQ Group: [1013565](http://shang.qq.com/wpa/qunwpa?idkey=932642920a5c0ef5f1ae902723c4f168c58ea63f3cef1139e30d68145d3b5b2f)