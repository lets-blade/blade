#Blade

[![简洁强大的JavaWeb框架](http://i1.tietuku.com/0c4b9726253b6268.png "简洁强大的JavaWeb框架")](http://bladejava.com)

[English](https://github.com/biezhi/blade/blob/master/README.md)

[![@biezhi on weibo](https://img.shields.io/badge/weibo-%40biezhi-red.svg)](http://weibo.com/u/5238733773)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Build Status](https://api.travis-ci.org/biezhi/blade.svg?branch=master)](https://travis-ci.org/biezhi/blade)
[![Circle CI](https://circleci.com/gh/biezhi/blade/tree/master.svg?style=svg)](https://circleci.com/gh/biezhi/blade/tree/master)
[![release](https://img.shields.io/maven-central/v/com.bladejava/blade-core.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.bladejava%22)

**blade** 是一个简洁强大的web框架，它内置了`IOC`管理，拦截器配置，`REST API`开发等众多主流web特性，集成了模板引擎，缓存插件，数据库操作，邮件发送等常用功能，简洁的源码值得你阅读和学习。如果你喜欢，欢迎[Star and Fork](https://github.com/biezhi/blade) ！

## Blade特性

* 简洁的MVC
* RESTful
* 多种路由配置方式
* 微内核IOC容器
* 实用工具类
* 编码/JSON/配置文件
* 模板引擎支持
* 支持JDK1.6+
* 插件扩展机制
* 缓存支持
* 不到100K的源码

## 快速入门
第一步、用maven构建一个webapp，加入blade的依赖，推荐获取[最新版本](LAST_VERSION.md)

```xml
<dependency>
	<groupId>com.bladejava</groupId>
	<artifactId>blade-core</artifactId>
	<version>1.2.8-alpha</version>
</dependency>
```
	
第二步、在`web.xml`中配置Blade核心过滤器并设置你的初始化类
	
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

第三步、编写App.java和路由文件，下面是一个示例

```java
public class App extends Bootstrap{

	Logger logger = Logger.getLogger(App.class);
	@Override
	public void init() {
		// 注册函数式路由
		Blade.regRoute("/hello", SayHi.class, "hello");
		
		// 匿名路由，java8方式更简化
		Blade.get("/get", new Router() {
			@Override
			public String handler(Request request, Response response) {
				System.out.println("进入get!!");
				System.out.println(request.query("name"));
				return "get";
			}
		});
	}
}
```
	
#### 函数式路由
```java
public class SayHi {
	
	public String hello(Request request, Response response){
		System.out.println("进入hello~");
		request.attribute("name", "rose baby");
		return "hi";
	}
}
```

#### 注解路由
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
	
OK，这一切看起来多么的简单，查阅使用指南更多现成的例子供你参考:

+ [hello](https://github.com/bladejava/hello)
+ [api docs](http://bladejava.com/apidocs/)
+ [使用指南](https://github.com/biezhi/blade/wiki)
+ [更多例子](https://github.com/bladejava)

### 计划

	1. 完善文档
	2. 单用户博客系统
	3. web聊天系统
	4. 优化代码性能

## 更新日志

[更新日志](https://github.com/biezhi/blade/blob/master/UPDATE_LOG.md)

## 开源协议

Blade框架基于 [Apache2 License](http://www.apache.org/licenses/LICENSE-2.0.html)

## 联系我

OSC Blog:[http://my.oschina.net/biezhi](http://my.oschina.net/biezhi)

Mail: biezhi.me#gmail.com

Java交流群: [1013565](http://shang.qq.com/wpa/qunwpa?idkey=932642920a5c0ef5f1ae902723c4f168c58ea63f3cef1139e30d68145d3b5b2f)
