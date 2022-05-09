<p align="center">
    <a href="https://lets-blade.github.io/"><img src="https://i.loli.net/2018/09/18/5ba0cd93c710e.png" width="650"/></a>
</p>
<p align="center">基于 <code>Java8</code> + <code>Netty4</code> 创造的轻量级、高性能、简洁优雅的Web框架 😋</p>
<p align="center">花 <b>1小时</b> 学会它做点有趣的项目，一款除了 Spring 系框架的不二之选。</p>
<p align="center">
    🐾 <a href="#quick-start" target="_blank">快速开始</a> | 
    🌚 <a href="https://lets-blade.github.io/" target="_blank">官方文档</a> | 
    💰 <a href="https://lets-blade.github.io/donate.html" target="_blank">捐赠我们</a> |
    🌾 <a href="README.md">English</a>
</p>
<p align="center">
    <a href="https://travis-ci.org/lets-blade/blade"><img src="https://img.shields.io/travis/lets-blade/blade.svg?style=flat-square"></a>
    <a href="http://search.maven.org/#search%7Cga%7C1%7Cblade-core"><img src="https://img.shields.io/maven-central/v/com.hellokaton/blade-core.svg?style=flat-square"></a>
    <a href="LICENSE"><img src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square"></a>
    <a class="badge-align" href="https://www.codacy.com/gh/lets-blade/blade/dashboard"><img src="https://app.codacy.com/project/badge/Grade/1eff0e30bf694402ac1b0ebe587bfa5a"/></a>
    <a href="https://gitter.im/hellokaton/blade"><img src="https://badges.gitter.im/hellokaton/blade.svg?style=flat-square"></a>
    <a href="https://www.codetriage.com/lets-blade/blade"><img src="https://www.codetriage.com/lets-blade/blade/badges/users.svg"></a>
</p>

***

## Blade是什么？

`Blade` 是一款追求简约、高效的 Web 框架，让 `JavaWeb` 开发如虎添翼，在性能与灵活性上同时兼顾。
如果你喜欢尝试有趣的事物，相信你会爱上它。
如果觉得这个项目不错可以 [star](https://github.com/hellokaton/blade/stargazers) 支持或者 [捐赠](https://dun.mianbaoduo.com/@hellokaton) 它 :blush:

## 功能特性

* [x] 新一代MVC框架，不依赖更多的库
* [x] 摆脱SSH的臃肿，模块化设计
* [x] 源码不到 `500kb`，学习也简单
* [x] Restful风格路由设计
* [x] 模板引擎支持，视图开发更灵活
* [x] 高性能，100 并发下qps 20w/s
* [x] 运行 `JAR` 包即可开启 web 服务
* [x] 支持 `CSRF` 和 `XSS` 防御
* [x] 支持 `BasicAuth` 和权限管理
* [x] 流式API风格
* [x] 支持插件扩展
* [x] 支持 webjars 资源
* [x] `cron` 表达式的定时任务
* [x] 内置多种常用中间件
* [x] 内置JSON输出
* [x] JDK8+

## 框架概述

» 简洁的：框架设计简单,容易理解,不依赖于更多第三方库。Blade框架目标让用户在一天内理解并使用。<br/>
» 优雅的：`Blade` 支持 REST 风格路由接口, 提供 DSL 语法编写，无侵入式的拦截器。<br/>
» 易部署：支持 `maven` 打成 `jar` 包直接运行。<br/>

## 快速入门

`Maven` 配置：

创建一个基础的 `Maven` 工程

```xml
<dependency>
	<groupId>com.hellokaton</groupId>
	<artifactId>blade-core</artifactId>
	<version>2.1.2.RELEASE</version>
</dependency>
```

> 不需要创建 `webapp` 项目骨架, Blade 没这么麻烦。

或者  `Gradle`:

```sh
compile 'com.hellokaton:blade-core:2.1.2.RELEASE'
```

编写 `main` 函数写一个 `Hello World`：

```java
public static void main(String[] args) {
    Blade.create().get("/", ctx -> ctx.text("Hello Blade")).start();
}
```

用浏览器打开 http://localhost:9000 这样就可以看到第一个 `Blade` 应用了！


## Contents

- [**`注册路由`**](#注册路由)
    - [**`硬编码方式`**](#硬编码方式)
    - [**`控制器方式`**](#控制器方式)
- [**`请求参数`**](#获取请求参数)
    - [**`URL参数`**](#URL参数)
    - [**`表单参数`**](#表单参数)
    - [**`Restful参数`**](#restful参数)
    - [**`Body参数`**](#body参数)
    - [**`参数转为对象`**](#参数转为对象)
- [**`获取环境配置`**](#获取环境配置)
- [**`获取Header`**](#获取header)
- [**`获取Cookie`**](#获取cookie)
- [**`静态资源`**](#静态资源)
- [**`上传文件`**](#上传文件)
- [**`下载文件`**](#下载文件)
- [**`设置会话`**](#设置会话)
- [**`渲染到浏览器`**](#渲染到浏览器)
    - [**`渲染JSON`**](#渲染json)
    - [**`渲染文本`**](#渲染文本)
    - [**`渲染Html`**](#渲染html)
- [**`模板渲染`**](#模板渲染)
    - [**`默认模板`**](#默认模板)
    - [**`Jetbrick模板`**](#jetbrick模板)
- [**`重定向`**](#重定向)
- [**`写入Cookie`**](#写入cookie)
- [**`路由拦截`**](#路由拦截)
- [**`日志输出`**](#日志输出)
- [**`Basic认证`**](#basic认证)
- [**`修改服务端口`**](#修改服务端口)
- [**`配置SSL`**](#配置ssl)
- [**`自定义异常处理`**](#自定义异常处理)

## 注册路由

### 硬编码方式

```java
public static void main(String[] args) {
    // 使用 Blade 实例创建多种路由 GET、POST、PUT、DELETE
    Blade.create()
        .get("/user/21", getting)
        .post("/save", posting)
        .delete("/remove", deleting)
        .put("/putValue", putting)
        .start();
}
```

### 控制器方式

```java
@Path
public class IndexController {

    @GET("/login")
    public String login(){
      return "login.html";
    }
    
    @POST(value = "/login", responseType = ResponseType.JSON)
    public RestResponse doLogin(RouteContext ctx){
      // do something
      return RestResponse.ok();
    }
    
}
```

## 请求参数

### URL参数

下面是个例子:

**使用 RouteContext 获取**

```java
public static void main(String[] args) {
    Blade.create().get("/user", ctx -> {
        Integer age = ctx.queryInt("age");
        System.out.println("age is:" + age);
    }).start();
}
```

**使用 `@Query` 注解获取**

```java
@GET("/user")
public void savePerson(@Query Integer age){
  System.out.println("age is:" + age);
}
```

在命令行下发送数据测试

```bash
curl -X GET http://127.0.0.1:9000/user?age=25
```

### 表单参数

下面是个例子:

**使用 RouteContext 获取**

```java
public static void main(String[] args) {
    Blade.create().get("/user", ctx -> {
        Integer age = ctx.fromInt("age");
        System.out.println("age is:" + age);
    }).start();
}
```

**使用 `@Form` 注解获取**

```java
@POST("/save")
public void savePerson(@Form String username, @Form Integer age){
  System.out.println("username is:" + username + ", age is:" + age);
}
```

在终端下发送数据测试

```bash
curl -X POST http://127.0.0.1:9000/save -F username=jack -F age=16
```

### Restful 参数

**使用 RouteContext 获取**

```java
public static void main(String[] args) {
    Blade blade = Blade.create();
    // Create a route: /user/:uid
    blade.get("/user/:uid", ctx -> {
        Integer uid = ctx.pathInt("uid");
        ctx.text("uid : " + uid);
    });

    // Create two parameters route
    blade.get("/users/:uid/post/:pid", ctx -> {
        Integer uid = ctx.pathInt("uid");
        Integer pid = ctx.pathInt("pid");
        String msg = "uid = " + uid + ", pid = " + pid;
        ctx.text(msg);
    });
    
    // Start blade
    blade.start();
}
```

**使用注解获取**

```java
@GET("/users/:username/:page")
public void userTopics(@PathParam String username, @PathParam Integer page){
  System.out.println("username is:" + username + ", page is:" + page);
}
```

在终端下发送数据测试

```bash
curl -X GET http://127.0.0.1:9000/users/hellokaton/2
```

### Body 参数

```java
public static void main(String[] args) {
    Blade.create().post("/body", ctx -> {
        System.out.println("body string is:" + ctx.bodyToString());
    }).start();
}
```

**使用 `@Body` 注解**

```java
@POST("/body")
public void readBody(@Body String data){
    System.out.println("data is:" + data);
}
```

在终端下发送数据测试

```bash
curl -X POST http://127.0.0.1:9000/body -d '{"username":"hellokaton","age":22}'
```

### 参数转为对象

这是 `User` 类结构

```java
public class User {
  private String username;
  private Integer age;
  // getter and setter
}
```

**使用注解获取**

```java
@POST("/users")
public void saveUser(@Form User user) {
    System.out.println("user => " + user);
}
```

在终端下发送数据测试

```bash
curl -X POST http://127.0.0.1:9000/users -F username=jack -F age=16
```

**自定义 `model` 名称**

```java
@POST("/users")
public void saveUser(@Form(name="u") User user) {
    System.out.println("user => " + user);
}
```

在终端下发送数据测试

```bash
curl -X POST http://127.0.0.1:9000/users -F u[username]=jack -F u[age]=16
```

**Body 参数转对象**

```java
@POST("/body")
public void body(@Body User user) {
    System.out.println("user => " + user);
}
```

在终端下发送数据测试

```bash
curl -X POST http://127.0.0.1:9000/body -d '{"username":"hellokaton","age":22}'
```

## 获取环境配置

```java
Environment environment = WebContext.blade().environment();
String version = environment.get("app.version", "0.0.1");
```

## 获取 Header

**使用 RouteContext 获取**

```java
@GET("header")
public void readHeader(RouteContext ctx){
    System.out.println("Host => " + ctx.header("Host"));
    // get useragent
    System.out.println("UserAgent => " + ctx.userAgent());
    // get client ip
    System.out.println("Client Address => " + ctx.address());
}
```

**使用注解获取**

```java
@GET("header")
public void readHeader(@Header String host){
  System.out.println("Host => " + host);
}
```

## 获取 Cookie

**使用 RouteContext 获取**

```java
@GET("cookie")
public void readCookie(RouteContext ctx){
    System.out.println("UID => " + ctx.cookie("UID"));
}
```

**使用注解获取**

```java
@GET("cookie")
public void readCookie(@Cookie String UID){
  System.out.println("Cookie UID => " + UID);
}
```

## 静态资源

Blade 内置了一些静态资源目录，只要将资源文件保存在 `classpath` 下的 `static` 目录中，然后浏览 http://127.0.0.1:9000/static/style.css

如果要自定义静态资源URL，可以使用下面的代码

```java
Blade.create().addStatics("/mydir");
```

当然你也可以在配置文件中指定 `application.properties` (位于classpath之下)

```bash
mvc.statics=/mydir
```

## 上传文件

**使用Request获取**

```java
@POST("upload")
public void upload(Request request){
    request.fileItem("img").ifPresent(fileItem -> {
        fileItem.moveTo(new File(fileItem.getFileName()));              
    });
}
```

**使用注解获取**

```java
@POST("upload")
public void upload(@Multipart FileItem fileItem){
    // 保存到新位置
    fileItem.moveTo(new File(fileItem.getFileName()));
}
```

## 下载文件

```java
@GET(value = "/download", responseType = ResponseType.STREAM)
public void download(Response response) throws IOException {
    response.write("abcd.pdf", new File("146373013842336153820220427172437.pdf"));
}
```

**如果你想在浏览器预览某些文件**

```java
@GET(value = "/preview", responseType = ResponseType.PREVIEW)
public void preview(Response response) throws IOException {
    response.write(new File("146373013842336153820220427172437.pdf"));
}
```

## 设置会话

默认情况不开启会话功能，首先要开启会话

```java
Blade.create()
     .http(HttpOptions::enableSession)
     .start(Application.class, args);
```

> 💡 也可以使用配置文件开启，`http.session.enabled=true` 

```java
public void login(Session session){
  // if login success
  session.attribute("login_key", SOME_MODEL);
}
```

## 渲染到浏览器

### 渲染JSON

**使用 RouteContext 渲染**

```java
@GET("users/json")
public void printJSON(RouteContext ctx){
    User user = new User("hellokaton", 18);
    ctx.json(user);
}
```

**使用注解获取**

这种形式看起来更简洁 😶

```java
@GET(value = "/users/json", responseType = ResponseType.JSON)
public User printJSON(){
  return new User("hellokaton", 18);
}
```

### 渲染文本

```java
@GET("text")
public void printText(RouteContext ctx){
    ctx.text("I Love Blade!");
}
```

or

```java
@GET(value = "/text", responseType = ResponseType.TEXT)
public String printText(RouteContext ctx){
    return "I Love Blade!";
}
```

### 渲染Html

```java
@GET("html")
public void printHtml(RouteContext ctx){
    ctx.html("<center><h1>I Love Blade!</h1></center>");
}
```

or

```java
@GET(value = "/html", responseType = ResponseType.HTML)
public String printHtml(RouteContext ctx){
    return "<center><h1>I Love Blade!</h1></center>";
}
```

## 模板渲染

默认情况下，所有模板文件都 `在templates` 目录中，大多数情况下你不需要更改它。

### 默认模板

默认情况下，Blade使用内置的模板引擎，如果你真的做一个Web项目可以尝试其他几个扩展，这很简单。

```java
public static void main(String[] args) {
    Blade.create().get("/hello", ctx -> {
        ctx.attribute("name", "hellokaton");
        ctx.render("hello.html");
    }).start(Hello.class, args);
}
```

`hello.html` 模板

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Hello Page</title>
</head>
<body>

  <h1>Hello, ${name}</h1>

</body>
</html>
```

### Jetbrick模板

**配置 Jetbrick 模板引擎**

实现一个 `BladeLoader` 加载初始化的操作

```java
@Bean
public class TemplateConfig implements BladeLoader {
    
    @Override
    public void load(Blade blade) {
        blade.templateEngine(new JetbrickTemplateEngine());
    }
    
}
```

写一点数据让模板渲染

```java
public static void main(String[] args) {
    Blade.create().get("/hello", ctx -> {
        User user = new User("hellokaton", 50);
        ctx.attribute("user", user);
        ctx.render("hello.html");
    }).start(Hello.class, args);
}
```

`hello.html` 模板

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Hello Page</title>
</head>
<body>

  <h1>Hello, ${user.username}</h1>
  
  #if(user.age > 18)
    <p>Good Boy!</p>
  #else
    <p>Gooood Baby!</p>
  #end
  
</body>
</html>
```

[Render API](http://static.javadoc.io/com.hellokaton/blade-core/2.1.2.RELEASE/com/hellokaton/blade/mvc/http/Response.html#render-com.ModelAndView-)

## 重定向

```java
@GET("redirect")
public void redirectToGithub(RouteContext ctx){
    ctx.redirect("https://github.com/hellokaton");
}
```

[Redirect API](http://static.javadoc.io/com.hellokaton/blade-core/2.1.2.RELEASE/com/hellokaton/blade/mvc/http/Response.html#redirect-java.lang.String-)

## 写入Cookie

```java
@GET("write-cookie")
public void writeCookie(RouteContext ctx){
    ctx.cookie("hello", "world");
    ctx.cookie("UID", "22", 3600);
}
```

[Cookie API](http://static.javadoc.io/com.hellokaton/blade-core/2.1.2.RELEASE/com/hellokaton/blade/mvc/http/Response.html#cookie-java.lang.String-java.lang.String-)

## 路由拦截

`WebHook` 是Blade框架中可以在执行路由之前和之后拦截的接口。

```java
public static void main(String[] args) {
    // All requests are exported before execution before
    Blade.create().before("/*", ctx -> {
        System.out.println("before...");
    }).start();
}
```

## 日志输出

Blade 使用 `slf4-api` 作为日志接口，默认实现一个简单的日志（从simple-logger修改），如果你需要复杂的日志记录你也可以使用其他的日志框架，你只需要在依赖关系中排除 `blade-log` 然后添加你喜欢的。

```java
private static final Logger log = LoggerFactory.getLogger(Hello.class);

public static void main(String[] args) {
  log.info("Hello Info, {}", "2017");
  log.warn("Hello Warn");
  log.debug("Hello Debug");
  log.error("Hello Error");
}
```

## Basic认证

Blade 内置了几个中间件，当你需要Basic认证时可以使用如下代码，当然也可以定制来实现。

```java
public static void main(String[] args) {
  Blade.create().use(new BasicAuthMiddleware()).start();
}
```

在 `application.properties` 配置文件中指定用户名和密码。

```bash
http.auth.username=admin
http.auth.password=123456
```

## 修改服务端口

有三种方式修改端口，硬编码，配置文件，启动命令行参数。

**硬编码**

```java
Blade.create().listen(9001).start();
```

**配置文件 `application.properties`**

```bash
server.port=9001
```

**命令行**

```bash
java -jar blade-app.jar --server.port=9001
```

## 配置SSL

**配置文件 `application.properties`**

```bash
server.ssl.enable=true
server.ssl.cert-path=cert.pem
server.ssl.private-key-path=private_key.pem
server.ssl.private-key-pass=123456
```

## 自定义异常处理

默认情况下，Blade 已经实现了一个异常处理器，有时你需要处理自定义异常，因此你可以尝试像下面这样使用。

```java
@Bean
public class GolbalExceptionHandler extends DefaultExceptionHandler {
    
    @Override
    public void handle(Exception e) {
        if (e instanceof CustomException) {
            CustomException customException = (CustomException) e;
            String code = customException.getCode();
            // do something
        } else {
            super.handle(e);
        }
    }
  
}
```

这一切看起来多么的简单，不过上面的功能可是冰山一角，查看文档和示例项目有更多惊喜:

+ [Blade Demos](https://github.com/lets-blade/blade-demos)
+ [Blade 资源列表](https://github.com/lets-blade/awesome-blade)

## 联系我们

- Twitter: [hellokaton](https://twitter.com/hellokaton)
- Mail: hellokaton@gmail.com

## 贡献者们

非常感谢下面的开发者朋友对本项目的帮助，如果你也愿意提交PR，非常欢迎！

![contributors.svg](https://opencollective.com/blade/contributors.svg?width=890&button=false)

## 开源协议

请查看 [Apache License](LICENSE)
