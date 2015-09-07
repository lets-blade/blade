[![a concise and powerful web development framework](http://i1.tietuku.com/0c4b9726253b6268.png "a concise and powerful web development framework")](http://bladejava.com)

[![@biezhi on weibo](https://img.shields.io/badge/weibo-%40biezhi-red.svg)](http://weibo.com/u/5238733773)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Build Status](https://api.travis-ci.org/biezhi/blade.svg?branch=master)](https://travis-ci.org/biezhi/blade)
[![release](https://img.shields.io/maven-central/v/com.bladejava/blade-core.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.bladejava%22)

[中文](https://github.com/biezhi/blade/blob/master/README_CN.md)

## What Is Blade?

**blade** Is a concise and powerful web development framework, it is built into the `IOC` administration, the interceptor configuration, `REST API` development and so on many mainstream web features, integrate the template engine, a cache plug-in, database operations, commonly used functions such as email, concise source deserves your reading. If you like it, can be [Star and Fork](https://github.com/biezhi/blade), thanks!

- __Simple MVC__
use Java language to complete the MVC more concise

- __Restful__
provide a Restful style routing interface

- __Multiple routing configuration__
routing configuration in the form of more functional routing, annotations routing, routing reflection way

- __Coding/JSON/configuration file__
blade offers a variety of configurations, including JSON, the Properties file, hard coding

- __Plug-in extension mechanism__
blade extensions support you use third party components, modular development of more convenient

- __Template engine Plugin__
support access to mainstream template engine, there are beetl, jetbrick, velocity engine

- __Support JDK1.6 +__
Support jdk1.6 or higher version

- __The source code of less than 100kb__
the source code of the blade framework is less than 100 KB, learn easy, get started quickly, the code is simple

## Quick start
First. Use maven to build a webapp, join dependency on the blade,Recommended for the [latest version](LAST_VERSION.md)

```xml
<dependency>
	<groupId>com.bladejava</groupId>
	<artifactId>blade-core</artifactId>
	<version>1.2.9-beta</version>
</dependency>
```
	
Second. Configuration in the `web.xml` Blade core filter initialization and set your class
	
```xml
<filter>
    <filter-name>BladeFilter</filter-name>
    <filter-class>blade.BladeFilter</filter-class>
    <init-param>
        <param-name>bootstrapClass</param-name>
        <param-value>blade.hello.App</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>BladeFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

Third. Write App.java and routing file, here is an example

```java
public class App extends Bootstrap{

	Logger logger = Logger.getLogger(App.class);
	@Override
	public void init() {
		// register router
		Blade.regRoute("/hello", SayHi.class, "hello");
		
		// anonymous router，java8 so simple
		Blade.get("/get", new Router() {
			@Override
			public String handler(Request request, Response response) {
				System.out.println("come get!!");
				System.out.println(request.query("name"));
				return "get";
			}
		});
		
		// multiple routing
		Blade.get("/", "/index").run(new Router() {
			@Override
			public String handler(Request request, Response response) {
				System.out.println("come index!!");
				return "index";
			}
		});
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
	2. Add configurable log
	3. To optimize the database query, connection pool
	4. Complete the Java China BBS
	
## Update

[update log](https://github.com/biezhi/blade/blob/master/UPDATE_LOG.md)
			
## licenses

Blade Framework based on the [Apache2 License](http://www.apache.org/licenses/LICENSE-2.0.html)

## Contact

OSC Blog:[http://my.oschina.net/biezhi](http://my.oschina.net/biezhi)

Mail: biezhi.me#gmail.com

QQ Group: [1013565](http://shang.qq.com/wpa/qunwpa?idkey=932642920a5c0ef5f1ae902723c4f168c58ea63f3cef1139e30d68145d3b5b2f)