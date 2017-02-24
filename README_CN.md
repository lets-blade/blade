
[![](https://dn-biezhi.qbox.me/LOGO_BIG.png)](http://bladejava.com)

[开始使用](https://bladejava.com/docs)&nbsp; | &nbsp;[示例项目](https://github.com/blade-samples)&nbsp; | &nbsp;[贡献代码](https://bladejava.com/docs/appendix/contribute)&nbsp; | &nbsp;[捐赠](donate.md)&nbsp; | &nbsp;[FAQ](https://bladejava.com/docs/faqs) | &nbsp;[English](https://github.com/biezhi/blade/blob/master/README.md)

[![Build Status](https://img.shields.io/travis/biezhi/blade.svg?style=flat-square)](https://travis-ci.org/biezhi/blade)
[![maven-central](https://img.shields.io/maven-central/v/com.bladejava/blade-core.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.bladejava%22)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![@biezhi on weibo](https://img.shields.io/badge/weibo-%40biezhi-red.svg?style=flat-square)](http://weibo.com/u/5238733773)

## Blade是什么?

Blade 是一款轻量级的MVC框架, 重新定义JavaWeb开发,它拥有简洁的代码，优雅的设计。

如果你觉得不错, 欢迎 [Star](https://github.com/biezhi/blade/stargazers) / [Fork](https://github.com/biezhi/blade), 谢谢 :blush:

## 特性

* [x] 轻量级, 代码简洁,结构清晰,更容易开发
* [x] 模块化(你可以选择使用哪些组件)
* [x] Restful风格的路由接口
* [x] 多种模板引擎支持
* [x] 支持以jar文件发布运行
* [x] JDK8以上

## 概述

* 简洁的：框架设计简单,容易理解,不依赖于更多第三方库。Blade框架目标让用户在一天内理解并使用。
* 优雅的：`Blade` 支持 REST 风格路由接口, 提供 DSL 语法编写，无侵入式的拦截器。
* 易部署：支持 `maven` 打成 `jar` 包直接运行。

## 快速入门

开始之前,首先 [引入Blade的库文件](http://bladejava.com/docs/intro/getting_start) ：

`Maven` 配置：

```xml
<dependency>
	<groupId>com.bladejava</groupId>
	<artifactId>blade-core</artifactId>
	<version>1.7.1-alpha</version>
</dependency>
<dependency>
	<groupId>com.bladejava</groupId>
	<artifactId>blade-embed-jetty</artifactId>
	<version>0.1.1</version>
</dependency>
```

或者  `Gradle`:

```sh
compile 'com.bladejava:blade-core:1.7.1-alpha'
compile 'com.bladejava:blade-embed-jetty:0.1.1'
```

编写 `Main`函数：

```java
public static void main(String[] args) {
	$().get("/", (request, response) -> {
		response.html("<h1>Hello blade!</h1>");
	}).start(Application.class);
}
```

用浏览器打开 http://localhost:9000 这样就可以看到第一个Blade应用了！

## API示例

```java
public static void main(String[] args) {
	$().get("/user/21", getxxx);
	$().post("/save", postxxx);
	$().delete("/del/21", deletexxx);
	$().put("/put", putxxx);
}
```

## REST URL参数获取

```java
public static void main(String[] args) {
	$().get("/user/:uid", (request, response) -> {
		Integer uid = request.paramAsInt("uid");
		response.text("uid : " + uid);
	});
	
	$().get("/users/:uid/post/:pid", (request, response) -> {
		Integer uid = request.paramAsInt("uid");
		Integer pid = request.paramAsInt("pid");
		String msg = "uid = " + uid + ", pid = " + pid;
		response.text(msg);
	});
	
	$().start(Application.class);
}
```

## 表单参数获取

```java
public static void main(String[] args) {
	$().get("/user", (request, response) -> {
		Integer uid = request.queryAsInt("uid");
		response.text("uid : " + uid);
	}).start(Application.class);
}
```

## 上传文件

```java
public void upload_img(@MultipartParam FileItem fileItem){
	if(null != fileItem){
		File file = fileItem.getFile();
		String fileRealPath = "your upload file path!";
		nioTransferCopy(file, fileRealPath);
	}
}
```

## 配置文件路由

`route.conf`

```sh
GET		/					IndexRoute.home
GET		/signin				IndexRoute.show_signin
POST	/signin				IndexRoute.signin
GET		/signout			IndexRoute.signout
POST	/upload_img			UploadRoute.upload_img
```

## 路由拦截

```java
public static void main(String[] args) {
	$().before("/.*", (request, response) -> {
		System.out.println("before...");
	}).start(Application.class);
}
```


这一切看起来多么的简单，不过上面的功能可是冰山一角，查看文档和示例项目有更多惊喜:

+ [hello工程](https://github.com/blade-samples/hello)
+ [文档服务](https://github.com/biezhi/grice)
+ [更多例子](https://github.com/blade-samples)

## 使用Blade的站点

+ 论坛程序：https://java-china.org
+ 图片社交：https://nice.biezhi.me
+ SS面板：https://github.com/biezhi/ss-panel
+ 文档站点：https://bladejava.com

## 更新日志

[更新日志](https://github.com/biezhi/blade/blob/master/UPDATE_LOG.md)

## 联系我

- Blog:[http://biezhi.me](http://biezhi.me)
- Mail: biezhi.me#gmail.com
- Java交流群: [1013565](http://shang.qq.com/wpa/qunwpa?idkey=932642920a5c0ef5f1ae902723c4f168c58ea63f3cef1139e30d68145d3b5b2f)

## 贡献

非常感谢下面的开发者朋友对本项目的帮助，如果你也愿意提交PR，欢迎你！

- [mfarid](https://github.com/mfarid)
- [daimajia](https://github.com/daimajia)
- [shenjie1993](https://github.com/shenjie1993)
- [sumory](https://github.com/sumory)
- [udaykadaboina](https://github.com/udaykadaboina)
- [SyedWasiHaider](https://github.com/SyedWasiHaider)
- [Awakens](https://github.com/Awakens)
- [shellac](https://github.com/shellac)
- [SudarAbisheck](https://github.com/SudarAbisheck)

## 开源协议

请查看 [Apache License](LICENSE)
