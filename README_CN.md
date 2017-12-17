<p align="center">
    <a href="https://lets-blade.com"><img src="https://static.biezhi.me/blade-logo.png" width="650"/></a>
</p>
<p align="center">基于 <code>Java8</code> + <code>Netty4</code> 创造的轻量级、高性能、简洁优雅的Web框架 😋</p>
<p align="center">花 <b>1小时</b> 学会它做点有趣的项目，一款除了Spring系框架的不二之选。</p>
<p align="center">
    🐾 <a href="#快速入门" target="_blank">快速开始</a> | 
    📘 <a href="https://dev-cheats.com/topics/blade-in-action.html" target="_blank">BladeInAction</a> | 
    🎬 <a href="https://www.bilibili.com/video/av15572599" target="_blank">视频教程</a> | 
    🌚 <a href="" target="_blank">参与贡献</a> | 
    💰 <a href="https://lets-blade.com/zh-cn/donate" target="_blank">捐赠我们</a> |
    🌾 <a href="README.md">English</a>
</p>
<p align="center">
    <a href="https://travis-ci.org/biezhi/blade"><img src="https://img.shields.io/travis/biezhi/blade.svg?style=flat-square"></a>
    <a href="http://codecov.io/github/biezhi/blade?branch=dev"><img src="https://img.shields.io/codecov/c/github/biezhi/blade/dev.svg?style=flat-square"></a>
    <a href="http://search.maven.org/#search%7Cga%7C1%7Cblade-mvc"><img src="https://img.shields.io/maven-central/v/com.bladejava/blade-mvc.svg?style=flat-square"></a>
    <a href="LICENSE"><img src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square"></a>
    <a class="badge-align" href="https://www.codacy.com/app/biezhi/blade?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=biezhi/blade&amp;utm_campaign=Badge_Grade"><img src="https://api.codacy.com/project/badge/Grade/5f5fb55f38614f04823372db3a3c1d1b"/></a>
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
* [x] 高性能，100并发下qps 14w/s
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

创建一个基础的 `Maven` 工程

```xml
<dependency>
	<groupId>com.bladejava</groupId>
	<artifactId>blade-mvc</artifactId>
	<version>2.0.6-Alpha1</version>
</dependency>
```

> 不需要创建 webapp 项目骨架, Blade 没这么麻烦。

或者  `Gradle`:

```sh
compile 'com.bladejava:blade-mvc:2.0.6-Alpha1'
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


## Contents

- [**`注册路由`**](#注册路由)
    - [**`硬编码方式`**](#硬编码方式)
    - [**`控制器方式`**](#控制器方式)
- [**`获取请求参数`**](#获取请求参数)
    - [**`表单参数`**](#表单参数)
    - [**`Restful参数`**](#Restful参数)
    - [**`Body参数`**](#Body参数)
    - [**`参数转为对象`**](#参数转为对象)
- [**`获取环境配置`**](#获取环境配置)
- [**`获取Header`**](#获取Header)
- [**`获取Cookie`**](#获取Cookie)
- [**`静态资源`**](#静态资源)
- [**`上传文件`**](#上传文件)
- [**`设置会话`**](#设置会话)
- [**`渲染到浏览器`**](#渲染到浏览器)
    - [**`渲染JSON`**](#渲染JSON)
    - [**`渲染文本`**](#渲染文本)
    - [**`渲染Html`**](#渲染Html)
- [**`模板渲染`**](#模板渲染)
    - [**`默认模板`**](#默认模板)
    - [**`Jetbrick模板`**](#Jetbrick模板)
- [**`重定向`**](#重定向)
- [**`写入Cookie`**](#写入Cookie)
- [**`路由拦截`**](#路由拦截)
- [**`日志输出`**](#日志输出)
- [**`Basic认证`**](#Basic认证)
- [**`修改服务端口`**](#修改服务端口)
- [**`配置SSL`**](#配置SSL)
- [**`自定义异常处理`**](#自定义异常处理)

## 注册路由

### 硬编码方式

```java
public static void main(String[] args) {
    // Create Blade，using GET、POST、PUT、DELETE
    Blade.me()
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
    
    @GetRoute("signin")
    public String signin(){
        return "signin.html";
    }
    
    @PostRoute("signin")
    @JSON
    public RestResponse doSignin(Request request){
        // do something
        return RestResponse.ok();
    }
    
}
```

## 获取请求参数

### 表单参数

下面是个例子:

**使用Request获取**

```java
public static void main(String[] args) {
    Blade.me().get("/user", ((request, response) -> {
         Optional<Integer> ageOptional = request.queryInt("age");
         ageOptional.ifPresent(age -> System.out.println("age is:" + age));
     })).start();
}
```

**使用注解获取**

```java
@PostRoute("/save")
public void savePerson(@Param String username, @Parma Integer age){
  System.out.println("username is:" + usernam + ", age is:" + age)
}
```

在终端下发送数据测试

```bash
curl -X GET http://127.0.0.1:9000/user?age=25
```

```bash
curl -X POST http://127.0.0.1:9000/save -F username=jack -F age=16
```

### Restful参数

**使用Request获取**

```java
public static void main(String[] args) {
    Blade blade = Blade.me();
    // Create a route: /user/:uid
    blade.get("/user/:uid", (request, response) -> {
		Integer uid = request.pathInt("uid");
		response.text("uid : " + uid);
	});
	
    // Create two parameters route
    blade.get("/users/:uid/post/:pid", (request, response) -> {
		Integer uid = request.pathInt("uid");
		Integer pid = request.pathInt("pid");
		String msg = "uid = " + uid + ", pid = " + pid;
		response.text(msg);
	});
	
    // Start blade
    blade.start();
}
```

**使用注解获取**

```java
@GetRoute("/users/:username/:page")
public void userTopics(@PathParam String username, @PathParam Integer page){
  System.out.println("username is:" + usernam + ", page is:" + page)
}
```

在终端下发送数据测试

```bash
curl -X GET http://127.0.0.1:9000/users/biezhi/2
```

### Body参数

```java
public static void main(String[] args) {
    Blade.me().post("/body", ((request, response) -> {
      System.out.println("body string is:" + body)
    }).start();
}
```

在终端下发送数据测试

```bash
curl -X POST http://127.0.0.1:9000/body -d '{"username":"biezhi","age":22}'
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
@PostRoute("/users")
public void saveUser(@Param User user){
    System.out.println("user => " + user);
}
```

在终端下发送数据测试

```bash
curl -X POST http://127.0.0.1:9000/users -F username=jack -F age=16
```

**自定义 `model` 名称**

```java
@PostRoute("/users")
public void saveUser(@Param(name="u") User user){
    System.out.println("user => " + user);
}
```

在终端下发送数据测试

```bash
curl -X POST http://127.0.0.1:9000/users -F u[username]=jack -F u[age]=16
```

**Body参数转对象**

```java
public void getUser(@BodyParam User user){
    System.out.println("user => " + user);
}
```

在终端下发送数据测试

```bash
curl -X POST http://127.0.0.1:9000/body -d '{"username":"biezhi","age":22}'
```

## 获取环境配置

```java
Environment environment = WebContext.blade().environment();
String version = environment.get("app.version", "0.0.1");;
```

## 获取Header

**使用Request获取**

```java
@GetRoute("header")
public void getHeader(Request request){
  System.out.println("Host => " + request.header("Host"));
  // get useragent
  System.out.println("UserAgent => " + request.userAgent());
  // get client ip
  System.out.println("Client Address => " + request.address());
}
```

**使用注解获取**

```java
@GetRoute("header")
public void getHeader(@HeaderParam String Host){
  System.out.println("Host => " + Host);
}
```

## 获取Cookie

**使用Request获取**

```java
@GetRoute("cookie")
public void getCookie(Request request){
  System.out.println("UID => " + request.cookie("UID").get());
  request.cookie("UID").ifPresent(System.out::println);
}
```

**使用注解获取**

```java
@GetRoute("cookie")
public void getCookie(@CookieParam String UID){
  System.out.println("Cookie UID => " + UID);
}
```

## 静态资源

Blade 内置了一些静态资源目录，只要将资源文件保存在 `classpath` 下的 `static` 目录中，然后浏览 http://127.0.0.1:9000/static/style.css

如果要自定义静态资源URL，可以使用下面的代码

```java
Blade.me().addStatics("/mydir");
```

当然你也可以在配置文件中指定 `app.properties` (位于classpath之下)

```bash
mvc.statics=/mydir
```

## 上传文件

**使用Request获取**

```java
@PostRoute("upload")
public void upload(Request request){
    request.fileItem("img").ifPresent(fileItem -> {
        byte[] data = fileItem.getData();
        // Save the temporary file to the specified path
        Files.write(Paths.get(filePath), data);              
    });
}
```

**使用注解获取**

```java
@PostRoute("upload")
public void upload(@MultipartParam FileItem fileItem){
    byte[] data = fileItem.getData();
    // Save the temporary file to the specified path
    Files.write(Paths.get(filePath), data);
}
```

## 设置会话

```java
public void login(Session session){
  // if login success
  session.attribute("login_key", SOME_MODEL);
}
```

## 渲染到浏览器

### 渲染JSON

**使用Request获取**

```java
@GetRoute("users/json")
public void printJSON(Response response){
  User user = new User("biezhi", 18);
  response.json(user);
}
```

**使用注解获取**

这种形式看起来更简洁 😶

```java
@GetRoute("users/json")
@JSON
public User printJSON(){
  return new User("biezhi", 18);
}
```

### 渲染文本

```java
@GetRoute("text")
public void printText(Response response){
  response.text("I Love Blade!");
}
```

### 渲染Html

```java
@GetRoute("html")
public void printHtml(Response response){
  response.html("<center><h1>I Love Blade!</h1></center>");
}
```

## 模板渲染

默认情况下，所有模板文件都 `在templates` 目录中，大多数情况下你不需要更改它。

### 默认模板

默认情况下，Blade使用内置的模板引擎，如果你真的做一个Web项目可以尝试其他几个扩展，这很简单。

```java
public static void main(String[] args) {
    Blade.me().get("/hello", ((request, response) -> {
                request.attribute("name", "biezhi");
                response.render("hello.html");
            }))
            .start(Hello.class, args);
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

创建一个 `BeanProcessor` 配置文件

```java
@Bean
public class TemplateConfig implements BeanProcessor {
    
    @Override
    public void processor(Blade blade) {
        blade.templateEngine(new JetbrickTemplateEngine());
    }
    
}
```

写一点数据让模板渲染

```java
public static void main(String[] args) {
    Blade.me().get("/hello", ((request, response) -> {
                User user = new User("biezhi", 50);
                request.attribute("user", user);
                response.render("hello.html");
            }))
            .start(Hello.class, args);
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

[Render API](http://static.javadoc.io/com.bladejava/blade-mvc/2.0.3/com/blade/mvc/http/Response.html#render-com.blade.mvc.ui.ModelAndView-)

## 重定向

```java
@GetRoute("redirect")
public void redirectToGithub(Response response){
  
  response.redirect("https://github.com/biezhi");
  
}
```

[Redirect API](http://static.javadoc.io/com.bladejava/blade-mvc/2.0.3/com/blade/mvc/http/Response.html#redirect-java.lang.String-)

## 写入Cookie

```java
@GetRoute("write-cookie")
public void writeCookie(Response response){
  
  response.cookie("hello", "world");
  response.cookie("UID", "22", 3600);
  
}
```

[Cookie API](http://static.javadoc.io/com.bladejava/blade-mvc/2.0.3/com/blade/mvc/http/Response.html#cookie-java.lang.String-java.lang.String-)

## 路由拦截

`WebHook` 是Blade框架中可以在执行路由之前和之后拦截的接口。

```java
public static void main(String[] args) {
    // All requests are exported before execution before
    Blade.me().before("/*", (request, response) -> {
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
  Blade.me().use(new BasicAuthMiddleware()).start();
}
```

在 `app.properties` 配置文件中指定用户名和密码。

```bash
http.auth.username=admin
http.auth.password=123456
```

## 修改服务端口

有三种方式修改端口，硬编码，配置文件，启动命令行参数。

**硬编码**

```java
Blade.me().listen(9001).start();
```

**配置文件 `app.properties`**

```bash
server.port=9001
```

**命令行**

```bash
java -jar blade-app.jar --server.port=9001
```

## 配置SSL

**配置文件 `app.properties`**

```bash
server.ssl.enable=true
server.ssl.cert-path=cert.pem
server.ssl.private-key-path=private_key.pem
server.ssl.private-key-pass=123456
```

## 自定义异常处理

默认情况下，Blade 已经实现了一个异常处理器，有时你需要处理自定义异常，因此器可以像下面这样使用。

```java
@Bean
public class GolbalExceptionHandler extends DefaultExceptionHandler {
    
    @Override
    public void handle(Exception e) {
        if (e instanceof ValidateException) {
            ValidateException validateException = (ValidateException) e;
            String msg = validateException.getErrMsg();
            WebContext.response().json(RestResponse.fail(msg));
        } else {
            super.handle(e);
        }
    }
  
}
```

这一切看起来多么的简单，不过上面的功能可是冰山一角，查看文档和示例项目有更多惊喜:

+ [第一个Blade程序](https://github.com/lets-blade/first-blade-app)
+ [Blade Demos](https://github.com/lets-blade/blade-demos)
+ [Blade 资源列表](https://github.com/lets-blade/awesome-blade)

## 联系我

- Blog:[https://biezhi.me](http://biezhi.me)
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
