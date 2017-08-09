
[![](https://dn-biezhi.qbox.me/LOGO_BIG.png)](http://bladejava.com)

[Quick Start](https://bladejava.com/docs)&nbsp; | &nbsp;[Demo Project](https://github.com/blade-samples)&nbsp; | &nbsp;[Contribute](https://bladejava.com/docs/appendix/contribute)&nbsp; | &nbsp;[Donate](donate.md)&nbsp; | &nbsp;[FAQ](https://bladejava.com/docs/faqs) | &nbsp;[中文说明](https://github.com/biezhi/blade/blob/master/README_CN.md)

[![Build Status](https://img.shields.io/travis/biezhi/blade.svg?style=flat-square)](https://travis-ci.org/biezhi/blade)
[![maven-central](https://img.shields.io/maven-central/v/com.bladejava/blade-mvc.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cblade-mvc)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Gitter](https://badges.gitter.im/biezhi/blade.svg)](https://gitter.im/biezhi/blade?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)


## Blade是什么？

Blade是一款简洁优雅的轻量级MVC框架。如果你喜欢它请为它 [点赞](https://github.com/biezhi/blade/stargazers)/[Fork](https://github.com/biezhi/blade) 谢谢 :blush:

## 特性

* [x] 新一代MVC框架，不依赖更多的库，摆脱SSH的臃肿，模块化设计
* [x] Restful风格路由设计
* [x] 模板引擎支持
* [x] 高性能，100并发下qps 6w/s
* [x] 运行Jar包即可开启web服务
* [x] 流式API风格
* [x] 支持插件扩展
* [x] 支持webjars
* [x] 内置多种常用中间件
* [x] 内置JSON输出，可替换
* [x] JDK8+
* [x] 事件驱动

## 概述

* 简洁的：框架设计简单,容易理解,不依赖于更多第三方库。Blade框架目标让用户在一天内理解并使用。
* 优雅的：`Blade` 支持 REST 风格路由接口, 提供 DSL 语法编写，无侵入式的拦截器。
* 易部署：支持 `maven` 打成 `jar` 包直接运行。

## 快速入门

`Maven` 配置：

```xml
<dependency>
	<groupId>com.bladejava</groupId>
	<artifactId>blade-mvc</artifactId>
	<version>2.0.1-beta2</version>
</dependency>
```

或者  `Gradle`:

```sh
compile 'com.bladejava:blade-mvc:2.0.1-beta2'
```

编写 `Main`函数：

```java
public static void main(String[] args) {
    Blade blade = Blade.me();
    blade.get("/", (req, res) -> {
        res.text("Hello Blade");
    }).start();
}
```

用浏览器打开 http://localhost:9000 这样就可以看到第一个Blade应用了！

## API示例

```java
public static void main(String[] args) {
    Blade blade = Blade.of();
    blade.get("/user/21", getxxx);
    blade.post("/save", postxxx);
    blade.delete("/del/21", deletexxx);
    blade.put("/put", putxxx);
}
```

## REST URL参数获取

```java
public static void main(String[] args) {
    Blade blade = Blade.of();
    blade.get("/user/:uid", (request, response) -> {
		Integer uid = request.queryInt("uid").get();
		response.text("uid : " + uid);
	});
	
    blade.get("/users/:uid/post/:pid", (request, response) -> {
		Integer uid = request.queryInt("uid").get();
		Integer pid = request.queryInt("pid").get();
		String msg = "uid = " + uid + ", pid = " + pid;
		response.text(msg);
	});
	
    blade.start();
}
```

## 表单参数

```java
public static void main(String[] args) {
    Blade blade = Blade.of();
    blade.get("/user", (request, response) -> {
		Integer uid = request.queryInt("uid").get();
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

## 路由钩子

```java
public static void main(String[] args) {
    Blade blade = Blade.of();
    blade.before("/.*", (request, response) -> {
        System.out.println("before...");
    }).start();
}
```

这一切看起来多么的简单，不过上面的功能可是冰山一角，查看文档和示例项目有更多惊喜:

+ [hello工程](https://github.com/blade-samples/hello)
+ [文档服务](https://github.com/biezhi/grice)
+ [更多例子](https://github.com/blade-samples)

## 使用Blade的站点

+ 博客系统：https://github.com/otale/tale
+ 论坛程序：https://java-china.org
+ 图片社交：https://github.com/biezhi/nice
+ SS面板：https://github.com/biezhi/ss-panel

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