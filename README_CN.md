<p align="center">
    <a href="https://lets-blade.com"><img src="http://7xls9k.dl1.z0.glb.clouddn.com/blade-logo.png" width="650"/></a>
</p>
<p align="center">基于 <code>Java8</code> + <code>Netty4</code> 创造的轻量级、高性能、简洁优雅的Web框架 😋</p>
<p align="center">花 <b>1小时</b> 学会它做点有趣的项目，一款除了Spring系框架的不二之选。</p>
<p align="center">
    🐾 <a href="" target="_blank">快速开始</a> | 
    📘 <a href="https://biezhi.gitbooks.io/blade-in-action" target="_blank">BladeInAction</a> | 
    🎬 <a href="http://v.qq.com/vplus/56171fe5fc0541ba6f356522325b0902/foldervideos/w6g0008012ruo91" target="_blank">视频教程</a> | 
    🌚 <a href="" target="_blank">参与贡献</a> | 
    💰 <a href="https://lets-blade.com/donate" target="_blank">捐赠我们</a> |
    🌾 <a href="README.md">English</a>
</p>
<p align="center">
    <a href="https://travis-ci.org/biezhi/blade"><img src="https://img.shields.io/travis/biezhi/blade.svg?style=flat-square"></a>
    <a href="http://codecov.io/github/biezhi/blade?branch=dev"><img src="https://img.shields.io/codecov/c/github/biezhi/blade/dev.svg?style=flat-square"></a>
    <a href="http://search.maven.org/#search%7Cga%7C1%7Cblade-mvc"><img src="https://img.shields.io/maven-central/v/com.bladejava/blade-mvc.svg?style=flat-square"></a>
    <a href="LICENSE"><img src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square"></a>
    <a href="https://gitter.im/biezhi/blade"><img src="https://badges.gitter.im/biezhi/blade.svg?style=flat-square"></a>
</p>

***

## Blade是什么？

`Blade` 是一款追求简约、高效的 Web 框架，让 `JavaWeb` 开发如虎添翼，在性能与灵活性上同时兼顾。
如果你喜欢尝试有趣的事物，相信你会爱上它。
如果觉得这个项目不错可以 [star](https://github.com/biezhi/blade/stargazers) 支持或者 [捐赠](https://lets-blade.com/donate) 它 :blush:

## 功能特性

* [x] 新一代MVC框架，不依赖更多的库
* [x] 摆脱SSH的臃肿，模块化设计
* [x] 源码不到 `500kb`，学习也简单
* [x] Restful风格路由设计
* [x] 模板引擎支持，视图开发更灵活
* [x] 高性能，100并发下tps 6w/s
* [x] 运行 `JAR` 包即可开启 web 服务
* [x] 流式API风格
* [x] 支持插件扩展
* [x] 支持 webjars 资源
* [x] 内置多种常用中间件
* [x] 内置JSON输出
* [x] JDK8+

## 框架概述

» 简洁的：框架设计简单,容易理解,不依赖于更多第三方库。Blade框架目标让用户在一天内理解并使用。<br/>
» 优雅的：`Blade` 支持 REST 风格路由接口, 提供 DSL 语法编写，无侵入式的拦截器。<br/>
» 易部署：支持 `maven` 打成 `jar` 包直接运行。<br/>

## 快速入门

`Maven` 配置：

```xml
<dependency>
	<groupId>com.bladejava</groupId>
	<artifactId>blade-mvc</artifactId>
	<version>2.0.3-alpha</version>
</dependency>
```

或者  `Gradle`:

```sh
compile 'com.bladejava:blade-mvc:2.0.3-alpha'
```

编写 `main` 函数写一个 `Hello World`：

```java
public static void main(String[] args) {
    Blade.me().get("/", (req, res) -> {
        res.text("Hello Blade");
    }).start();
}
```

用浏览器打开 http://localhost:9000 这样就可以看到第一个 `Blade` 应用了！

## API示例

```java
public static void main(String[] args) {
    // 创建 Blade 对象，完成 GET、POST、PUT、DELETE 请求
    Blade.me()
        .get("/user/21", getting)
        .post("/save", posting)
        .delete("/remove", deleting)
        .put("/putValue", putting)
        .start();
}
```

## REST URL参数获取

```java
public static void main(String[] args) {
    Blade blade = Blade.me();
    // 创建一个 /user/:uid 的路由
    blade.get("/user/:uid", (request, response) -> {
		Integer uid = request.pathInt("uid");
		response.text("uid : " + uid);
	});
	
    // 创建有2个参数的 REST 风格路由
    blade.get("/users/:uid/post/:pid", (request, response) -> {
		Integer uid = request.pathInt("uid");
		Integer pid = request.pathInt("pid");
		String msg = "uid = " + uid + ", pid = " + pid;
		response.text(msg);
	});
	
    // 启动应用
    blade.start();
}
```

## 表单参数

```java
public static void main(String[] args) {
    Blade.me().get("/user", ((request, response) -> {
         Optional<Integer> ageOptional = request.queryInt("age");
         ageOptional.ifPresent(age -> System.out.println("年龄是:" + age));
     })).start();
}
```

## 上传文件

```java
public void upload(@MultipartParam FileItem fileItem){
    byte[] data = fileItem.getData();
    // 将临时文件保存到指定路径
    Files.write(Paths.get(filePath), data);
}
```

或者

```java
public void upload(Request request){
    request.fileItem("img").ifPresent(fileItem -> {
        byte[] data = fileItem.getData();
        // 将临时文件保存到指定路径
        Files.write(Paths.get(filePath), data);              
    });
}
```

## 路由钩子

```java
public static void main(String[] args) {
    // 所有请求执行前都会输出 before
    Blade.me().before("/*", (request, response) -> {
        System.out.println("before...");
    }).start();
}
```

这一切看起来多么的简单，不过上面的功能可是冰山一角，查看文档和示例项目有更多惊喜:

+ [第一个例子](https://github.com/bladejava/first-blade-app)
+ [文档服务](https://github.com/biezhi/grice)
+ [更多例子](https://github.com/bladejava/blade-demos)

## 使用Blade的站点

+ 博客系统：https://github.com/otale/tale
+ 社区程序：https://github.com/junicorn/roo
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