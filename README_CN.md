
[![](https://i.imgur.com/8I289mA.png)](http://bladejava.com)

[![@biezhi on weibo](https://img.shields.io/badge/weibo-%40biezhi-red.svg)](http://weibo.com/u/5238733773)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Build Status](https://api.travis-ci.org/biezhi/blade.svg?branch=master)](https://travis-ci.org/biezhi/blade)
[![release](https://img.shields.io/maven-central/v/com.bladejava/blade-core.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.bladejava%22)

[English](https://github.com/biezhi/blade/blob/master/README.md)

# Blade是什么?
`blade` 是一个轻量级的MVC框架. 它拥有简洁的代码，优雅的设计。
如果你喜欢,欢迎 [Star and Fork](https://github.com/biezhi/blade), 谢谢!

# 特性
* [x] 轻量级。代码简洁,结构清晰,更容易开发
* [x] 模块化(你可以选择使用哪些组件)
* [x] 插件扩展机制
* [x] Restful风格的路由接口
* [x] 多种配置文件支持(当前支持properties、json和硬编码)
* [x] 内置Jetty服务,模板引擎支持
* [x] 支持JDK1.6或者更高版本

# 概述

* 简洁的：框架设计简单,容易理解,不依赖于更多第三方库。Blade框架目标让用户在一天内理解并使用。

* Relevance. `blade` doesn't assume anything. We focus on things that matter, this way we are able to ensure easy maintenance and keep the system well-organized, well-planned and sweet.

* Elegance. `blade` uses golang best practises. We are not afraid of heights, it's just that we need a parachute in our backpack. The source code is heavily documented, any functionality should be well explained and well tested.

# Getting started
To get started, first [include the Blade library](http://bladejava.com) and then create a class with a main method like this:
```java
public class App extends Bootstrap {
	
	@Override
	public void init() {}
	
	public static void main(String[] args) throws Exception {
		Blade blade = Blade.me();
		blade.get("/").run(request, response) -> {
			response.html("<h1>Hello blade!</h1>");
			return null;
		});
		blade.app(App.class).listen(9001).start();
	}
}
```
Run it and point your browser to http://localhost:9001. There you go, you've just created your first Blade app!

OK, all this may seem simple, refer to the guidelines for use more ready-made examples for your reference:

+ [hello project](https://github.com/bladejava/hello)
+ [api docs](http://bladejava.com/apidocs/)
+ [user guide](https://github.com/biezhi/blade/wiki)
+ [some examples](https://github.com/bladejava)

## Plan

	1. Improve the document
	2. Add configurable log
	3. Complete the Java China BBS
	4. Maintain and optimize the code
	
## Update

[update log](https://github.com/biezhi/blade/blob/master/UPDATE_LOG.md)

## licenses

Blade Framework based on the [Apache2 License](http://www.apache.org/licenses/LICENSE-2.0.html)

## Contact

Blog:[https://biezhi.me](https://biezhi.me)

Mail: biezhi.me#gmail.com

QQ Group: [1013565](http://shang.qq.com/wpa/qunwpa?idkey=932642920a5c0ef5f1ae902723c4f168c58ea63f3cef1139e30d68145d3b5b2f)



[![简洁强大的JavaWeb框架](http://i1.tietuku.com/0c4b9726253b6268.png "简洁强大的JavaWeb框架")](http://bladejava.com)

[![@biezhi on weibo](https://img.shields.io/badge/weibo-%40biezhi-red.svg)](http://weibo.com/u/5238733773)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Build Status](https://api.travis-ci.org/biezhi/blade.svg?branch=master)](https://travis-ci.org/biezhi/blade)
[![release](https://img.shields.io/maven-central/v/com.bladejava/blade-core.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.bladejava%22)

[English](https://github.com/biezhi/blade/blob/master/README.md)

## Blade是什么?

**blade** 是一个简洁强大的web框架，简洁的源码值得你阅读和学习。如果你喜欢，欢迎[Star and Fork](https://github.com/biezhi/blade) ！

- __简洁的MVC__  
使用java语言完成mvc更加简洁

- __RESTful__  
提供Restful风格的路由接口

- __多种路由配置方式__  
更多方式进行路由的配置，函数式路由，注解路由，反射方式路由

- __编码/JSON/配置文件__  
blade提供多种配置方式，包括JSON、Properties文件，硬编码

- __插件扩展机制__  
blade支持你使用第三方的组件进行扩展，更方便的积木式开发

- __模板引擎支持__  
支持主流模板引擎接入，目前已经有beetl、jetbrick、velocity引擎

- __支持JDK1.6+__  
支持jdk1.6或者更高版本

- __不到100K的源码__  
目前blade框架的源代码不到100kb，学习简单，上手快速，代码简洁

## 示例

```java
public class App extends Bootstrap{

	Logger logger = Logger.getLogger(App.class);
	@Override
	public void init() {
		// 注册函数式路由
		Blade.register("/hello", SayHi.class, "hello");
		
		// 匿名路由
		Blade.get("/get", new Router() {
			@Override
			public String handler(Request request, Response response) {
				System.out.println("进入get!!");
				System.out.println(request.query("name"));
				return "get";
			}
		});
		
		// 多个路由，java8语法
		Blade.get("/", "/index").run(request, response) -> {
			System.out.println("come index!!");
			return "index";
		});
	}
}
```
	
OK，这一切看起来多么的简单，查阅使用指南更多现成的例子供你参考:

+ [hello](https://github.com/bladejava/hello)
+ [api docs](http://bladejava.com/apidocs/)
+ [使用指南](https://github.com/biezhi/blade/wiki)
+ [更多例子](https://github.com/bladejava)

### 计划

	1. 完善文档
	2. 添加可配置日志
	3. 完成java中国论坛
	4. 维护和优化代码

## 更新日志

[更新日志](https://github.com/biezhi/blade/blob/master/UPDATE_LOG.md)

## 开源协议

Blade框架基于 [Apache2 License](http://www.apache.org/licenses/LICENSE-2.0.html)

## 联系我

Blog:[https://biezhi.me](https://biezhi.me)

Mail: biezhi.me#gmail.com

Java交流群: [1013565](http://shang.qq.com/wpa/qunwpa?idkey=932642920a5c0ef5f1ae902723c4f168c58ea63f3cef1139e30d68145d3b5b2f)
