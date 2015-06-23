#Blade
*javaweb中的瑞士军刀*

[English](https://github.com/biezhi/blade/blob/master/README_EN.md)

[![@biezhi on weibo](https://img.shields.io/badge/weibo-%40biezhi-red.svg)](http://weibo.com/u/5238733773)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/biezhi/blade/blob/master/license.txt)
[![Build Status](https://api.travis-ci.org/biezhi/blade.svg?branch=master)](https://travis-ci.org/biezhi/blade)
[![JDK](http://img.shields.io/badge/JDK-v1.6+-blue.svg)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
[![maven](https://img.shields.io/maven-central/v/com.bladejava/blade.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.bladejava%22)

**blade** 是一个轻量级快速开发web应用程序的框架，它集成了IOC对象管理，拦截器配置，REST API开发等众多主流web特性。

## Blade特性

+ 简洁的MVC
+ REST风格API
+ 注解+正则拦截器
+ 微内核IOC容器
+ 实用工具类
+ 模板引擎支持
+ 支持JDK1.6+
+ 内置Jetty启动
+ 插件扩展机制
+ ...

## 快速入门
第一步、用maven构建一个webapp，加入blade的依赖，推荐获取[最新版本](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.bladejava%22)

```xml
<dependency>
	<groupId>com.bladejava</groupId>
	<artifactId>blade</artifactId>
	<version>x.x.x</version>
</dependency>
```
	
第二步、在`web.xml`中配置Blade核心过滤器并设置你的初始化类，你也可以不配置(使用jetty启动)
	
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

第三步、编写App.java和路由文件，下面是一个示例

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
	
OK，这一切看起来多么的简单，查阅使用指南更多现成的例子供你参考:
  
[Blade使用指南](http://bladejava.com/doc/cn)
 
## 更新日志

### v1.0.8
	1. 优化文件上传
	2. 优化路由匹配
	3. 添加方法执行监测
	
### v1.0.4
	1. 优化底层IO
	2. 简化插件扩展
	3. 拦截器路由匹配分离
	4. 修复jetty在多maven环境下运行bug 
	5. 添加初始化监听context
		
### v1.0.1
	1. 去除对外公开的多余方法展示
	2. 添加`Blade.run()`方式运行jetty
	3. 添加`Blade.register()`方法注册bean对象
	4. 优化IOC对象管理

### v1.0.0
	第一个稳定版本发布

## 开源协议
Blade框架基于[Apache2 License](https://github.com/biezhi/blade/blob/master/license.txt)

## 联系我
Mail: biezhi.me#gmail.com

Java交流群: [1013565](http://shang.qq.com/wpa/qunwpa?idkey=932642920a5c0ef5f1ae902723c4f168c58ea63f3cef1139e30d68145d3b5b2f)


