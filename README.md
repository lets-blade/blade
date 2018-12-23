<p align="center">
    <a href="https://lets-blade.com"><img src="https://i.loli.net/2018/09/18/5ba0cd93c710e.png" width="650"/></a>
</p>
<p align="center">Based on <code>Java8</code> + <code>Netty4</code> to create a lightweight, high-performance, simple and elegant Web framework 😋</p>
<p align="center">Spend <b>1 hour</b> to learn it to do something interesting, a tool in addition to the other available frameworks.</p>
<p align="center">
    🐾 <a href="#quick-start" target="_blank">Quick Start</a> |
    🎬 <a href="https://www.youtube.com/playlist?list=PLK2w-tGRdrj5TV2lxHFj8hcg4mbmRmnWX" target="_blank">Video Tutorial</a> |
    🌚 <a href="https://lets-blade.com" target="_blank">Document</a> |
    💰 <a href="https://lets-blade.com/donate" target="_blank">Donate</a> |
    🇨🇳 <a href="README_CN.md">简体中文</a>
</p>
<p align="center">
    <a href="https://travis-ci.org/lets-blade/blade"><img src="https://img.shields.io/travis/lets-blade/blade.svg?style=flat-square"></a>
    <a href="http://search.maven.org/#search%7Cga%7C1%7Cblade-mvc"><img src="https://img.shields.io/maven-central/v/com.bladejava/blade-mvc.svg?style=flat-square"></a>
    <a href="LICENSE"><img src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square"></a>
    <a class="badge-align" href="https://www.codacy.com/app/lets-blade/blade"><img src="https://api.codacy.com/project/badge/Grade/5f5fb55f38614f04823372db3a3c1d1b"/></a>
    <a href="https://gitter.im/biezhi/blade"><img src="https://badges.gitter.im/biezhi/blade.svg?style=flat-square"></a>
    <a href="https://www.codetriage.com/biezhi/blade"><img src="https://www.codetriage.com/biezhi/blade/badges/users.svg"></a>
</p>

***

## What Is Blade?

`Blade` is a pursuit of simple, efficient Web framework, so that `JavaWeb` development becomes even more powerful, both in performance and flexibility.
If you like to try something interesting, I believe you will love it.
If you think it's good, you can support it with a [star](https://github.com/biezhi/blade/stargazers) or by [donating](https://lets-blade.com/donate) :blush:

## Features

* [x] A new generation MVC framework that doesn't depend on other libraries
* [x] Get rid of SSH's bloated, modular design
* [x] Source is less than `500kb`, learning it is also simple
* [x] RESTful-style routing design
* [x] Template engine support, view development more flexible
* [x] High performance, 100 concurrent qps 14w/s
* [x] Run the `JAR` package to open the web service
* [x] Streams-style API
* [x] `CSRF` and `XSS` defense
* [x] `Basic Auth` and `Authorization`
* [x] Supports plug-in extensions
* [x] Support webjars resources
* [x] Tasks based on `cron` expressions
* [x] Built-in a variety of commonly used middleware
* [x] Built-in JSON output
* [x] JDK8 +

## Overview

» Simplicity: The design is simple, easy to understand and doesn't introduce many layers between you and the standard library. The goal of this project is that the users should be able to understand the whole framework in a single day.<br/>
» Elegance: `blade` supports the RESTful style routing interface, has no invasive interceptors and provides the writing of a DSL grammar.<br/>
» Easy deploy: supports `maven` package `jar` file running.<br/>

## Quick Start

Create a basic `Maven` or `Gradle` project.

> Do not create a `webapp` project, Blade does not require much trouble.

Run with `Maven`:

```xml
<dependency>
    <groupId>com.bladejava</groupId>
    <artifactId>blade-mvc</artifactId>
    <version>2.0.13.ALPHA</version>
</dependency>
```

or `Gradle`:

```sh
compile 'com.bladejava:blade-mvc:2.0.13.ALPHA'
```

Write the `main` method and the `Hello World`:

```java
public static void main(String[] args) {
    Blade.of().get("/", ctx -> ctx.text("Hello Blade")).start();
}
```

Open http://localhost:9000 in your browser to see your first `Blade` application!


## Contents

- [**`Register Route`**](#register-route)
    - [**`HardCode`**](#hardCode)
    - [**`Controller`**](#controller)
- [**`Get Request Parameters`**](#get-request-parameters)
    - [**`Form Parameters`**](#form-parameters)
    - [**`Path Parameters`**](#path-parameters)
    - [**`Body Parameters`**](#body-parameters)
    - [**`Parse To Model`**](#parse-to-model)
- [**`Get Environment`**](#get-environment)
- [**`Get Header`**](#get-header)
- [**`Get Cookie`**](#get-cookie)
- [**`Static Resource`**](#static-resource)
- [**`Upload File`**](#upload-file)
- [**`Set Session`**](#set-session)
- [**`Render To Browser`**](#render-to-broser)
    - [**`Render JSON`**](#render-json)
    - [**`Render Text`**](#render-text)
    - [**`Render Html`**](#render-html)
- [**`Render Template`**](#render-template)
    - [**`Default Template`**](#default-template)
    - [**`Jetbrick Template`**](#jetbrick-template)
- [**`Redirects`**](#redirects)
- [**`Write Cookie`**](#write-cookie)
- [**`Web Hook`**](#web-hook)
- [**`Logging`**](#logging)
- [**`Basic Auth`**](#basic-auth)
- [**`Change Server Port`**](#change-server-port)
- [**`Configuration SSL`**](#configuration-ssl)
- [**`Custom Exception Handler`**](#custom-exception-handler)

## Register Route

### HardCode

```java
public static void main(String[] args) {
    // Create Blade，using GET、POST、PUT、DELETE
    Blade.of()
        .get("/user/21", getting)
        .post("/save", posting)
        .delete("/remove", deleting)
        .put("/putValue", putting)
        .start();
}
```

### `Controller`

```java
@Path
public class IndexController {

    @GetRoute("signin")
    public String signin(){
        return "signin.html";
    }

    @PostRoute("signin")
    @JSON
    public RestResponse doSignin(RouteContext ctx){
        // do something
        return RestResponse.ok();
    }

}
```

## Get Request Parameters

### Form Parameters

Here is an example:

**By Context**

```java
public static void main(String[] args) {
    Blade.of().get("/user", ctx -> {
        Integer age = ctx.fromInt("age");
        System.out.println("age is:" + age);
    }).start();
}
```

**By Annotation**

```java
@PostRoute("/save")
public void savePerson(@Param String username, @Param Integer age){
    System.out.println("username is:" + username + ", age is:" + age)
}
```

Test it with sample data from the terminal

```bash
curl -X GET http://127.0.0.1:9000/user?age=25
```

```bash
curl -X POST http://127.0.0.1:9000/save -F username=jack -F age=16
```

### Path Parameters

**By RouteContext**

```java
public static void main(String[] args) {
    Blade blade = Blade.of();
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

**By Annotation**

```java
@GetRoute("/users/:username/:page")
public void userTopics(@PathParam String username, @PathParam Integer page){
    System.out.println("username is:" + usernam + ", page is:" + page)
}
```

Test it with sample data from the terminal

```bash
curl -X GET http://127.0.0.1:9000/users/biezhi/2
```

### Body Parameters

```java
public static void main(String[] args) {
    Blade.of().post("/body", ctx -> {
        System.out.println("body string is:" + ctx.bodyToString())
    }).start();
}
```

Test it with sample data from the terminal

```bash
curl -X POST http://127.0.0.1:9000/body -d '{"username":"biezhi","age":22}'
```

### Parse To Model

This is the `User` model.

```java
public class User {
    private String username;
    private Integer age;
    // getter and setter
}
```

**By Annotation**

```java
@PostRoute("/users")
public void saveUser(@Param User user){
    System.out.println("user => " + user);
}
```

Test it with sample data from the terminal

```bash
curl -X POST http://127.0.0.1:9000/users -F username=jack -F age=16
```

**Custom model identification**

```java
@PostRoute("/users")
public void saveUser(@Param(name="u") User user){
    System.out.println("user => " + user);
}
```

Test it with sample data from the terminal

```bash
curl -X POST http://127.0.0.1:9000/users -F u[username]=jack -F u[age]=16
```

**Body Parameter To Model**

```java
public void getUser(@BodyParam User user){
    System.out.println("user => " + user);
}
```

Test it with sample data from the terminal

```bash
curl -X POST http://127.0.0.1:9000/body -d '{"username":"biezhi","age":22}'
```

## Get Environment

```java
Environment environment = WebContext.blade().environment();
String version = environment.get("app.version", "0.0.1");
```

## Get Header

**By Context**

```java
@GetRoute("header")
public void getHeader(RouteContext ctx){
    System.out.println("Host => " + ctx.header("Host"));
    // get useragent
    System.out.println("UserAgent => " + ctx.userAgent());
    // get client ip
    System.out.println("Client Address => " + ctx.address());
}
```

**By Annotation**

```java
@GetRoute("header")
public void getHeader(@HeaderParam String Host){
    System.out.println("Host => " + Host);
}
```

## Get Cookie

**By Context**

```java
@GetRoute("cookie")
public void getCookie(RouteContext ctx){
    System.out.println("UID => " + ctx.cookie("UID"));
}
```

**By Annotation**

```java
@GetRoute("cookie")
public void getCookie(@CookieParam String UID){
    System.out.println("Cookie UID => " + UID);
}
```

## Static Resource

Blade builds a few static resource catalog, as long as you will save the resource file in the static directory under the classpath, and then browse http://127.0.0.1:9000/static/style.css

If you want to customize the static resource URL

```java
Blade.of().addStatics("/mydir");
```

Of course you can also specify it in the configuration file. `application.properties` (location in classpath)

```bash
mvc.statics=/mydir
```

## Upload File

**By Request**

```java
@PostRoute("upload")
public void upload(Request request){
    request.fileItem("img").ifPresent(fileItem -> {
        fileItem.moveTo(new File(fileItem.getFileName()));
    });
}
```

**By Annotation**

```java
@PostRoute("upload")
public void upload(@MultipartParam FileItem fileItem){
    // Save to new path
    fileItem.moveTo(new File(fileItem.getFileName()));
}
```

## Set Session

```java
public void login(Session session){
    // if login success
    session.attribute("login_key", SOME_MODEL);
}
```

## Render To Browser

### Render JSON

**By Context**

```java
@GetRoute("users/json")
public void printJSON(RouteContext ctx){
    User user = new User("biezhi", 18);
    ctx.json(user);
}
```

**By Annotation**

This form looks more concise 😶

```java
@GetRoute("users/json")
@JSON
public User printJSON(){
    return new User("biezhi", 18);
}
```

### Render Text

```java
@GetRoute("text")
public void printText(RouteContext ctx){
    ctx.text("I Love Blade!");
}
```

### Render Html

```java
@GetRoute("html")
public void printHtml(RouteContext ctx){
    ctx.html("<center><h1>I Love Blade!</h1></center>");
}
```

## Render Template

By default all template files are in the templates directory; in most of the cases you do not need to change it.

### Default Template

By default, Blade uses the built-in template engine, which is very simple. In a real-world web project, you can try several other extensions.

```java
public static void main(String[] args) {
    Blade.of().get("/hello", ctx -> {
        ctx.attribute("name", "biezhi");
        ctx.render("hello.html");
    }).start(Hello.class, args);
}
```

The `hello.html` template

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

### Jetbrick Template

**Config Jetbrick Template**

Create a `BladeLoader` class and load some config

```java
@Bean
public class TemplateConfig implements BladeLoader {

    @Override
    public void load(Blade blade) {
        blade.templateEngine(new JetbrickTemplateEngine());
    }

}
```

Write some data for the template engine to render

```java
public static void main(String[] args) {
    Blade.of().get("/hello", ctx -> {
        User user = new User("biezhi", 50);
        ctx.attribute("user", user);
        ctx.render("hello.html");
    }).start(Hello.class, args);
}
```

The `hello.html` template

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

## Redirects

```java
@GetRoute("redirect")
public void redirectToGithub(RouteContext ctx){
    ctx.redirect("https://github.com/biezhi");
}
```

[Redirect API](http://static.javadoc.io/com.bladejava/blade-mvc/2.0.3/com/blade/mvc/http/Response.html#redirect-java.lang.String-)

## Write Cookie

```java
@GetRoute("write-cookie")
public void writeCookie(RouteContext ctx){
    ctx.cookie("hello", "world");
    ctx.cookie("UID", "22", 3600);
}
```

[Cookie API](http://static.javadoc.io/com.bladejava/blade-mvc/2.0.3/com/blade/mvc/http/Response.html#cookie-java.lang.String-java.lang.String-)

## Web Hook

`WebHook` is the interface in the Blade framework that can be intercepted before and after the execution of the route.

```java
public static void main(String[] args) {
    // All requests are exported before execution before
    Blade.of().before("/*", ctx -> {
        System.out.println("before...");
    }).start();
}
```

## Logging

Blade uses slf4j-api as logging interface, the default implementation of a simple log package (modified from simple-logger); if you need complex logging you can also use a custom library, you only need to exclude the `blade-log` from the dependencies.

```java
private static final Logger log = LoggerFactory.getLogger(Hello.class);

public static void main(String[] args) {
    log.info("Hello Info, {}", "2017");
    log.warn("Hello Warn");
    log.debug("Hello Debug");
    log.error("Hello Error");
}
```

## Basic Auth

Blade includes a few middleware, like Basic Authentication; of course, it can also be customized to achieve more complex goals.

```java
public static void main(String[] args) {
    Blade.of().use(new BasicAuthMiddleware()).start();
}
```

Specify the user name and password in the `application.properties` configuration file.

```bash
http.auth.username=admin
http.auth.password=123456
```

## Change Server Port

There are three ways to modify the port: hard coding it, in a configuration file, and through a command line parameter.

**Hard Coding**

```java
Blade.of().listen(9001).start();
```

**Configuration For `application.properties`**

```bash
server.port=9001
```

**Command Line**

```bash
java -jar blade-app.jar --server.port=9001
```

## Configuration SSL

**Configuration For `application.properties`**

```bash
server.ssl.enable=true
server.ssl.cert-path=cert.pem
server.ssl.private-key-path=private_key.pem
server.ssl.private-key-pass=123456
```

## Custom Exception Handler

Blade has an exception handler already implemented by default; if you need to deal with custom exceptions, you can do it like follows.

```java
@Bean
public class GlobalExceptionHandler extends DefaultExceptionHandler {
    
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

Besides looking easy, the features above are only the tip of the iceberg, and there are more surprises to see in the documentation and sample projects:

+ [FirstBladeApp](https://github.com/lets-blade/first-blade-app)
+ [Blade Demos](https://github.com/lets-blade/blade-demos)
+ [Awesome Blade](https://github.com/lets-blade/awesome-blade)

## Change Logs

[See Here](https://lets-blade.com/about/change-logs)

## Contact

- Twitter: [biezhi](https://twitter.com/biezhii)
- Mail: biezhi.me@gmail.com

## Contributors

Thanks goes to these wonderful people

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore -->
| [<img src="https://avatars2.githubusercontent.com/u/3849072?s=460&v=4" width="100px;"/><br /><sub><b>王爵nice</b></sub>](https://twitter.com/biezhii) | [<img src="https://avatars2.githubusercontent.com/u/9401233?s=460&v=4" width="100px;"/><br /><sub><b>ccqy66</b></sub>](https://github.com/ccqy66) | [<img src="https://avatars0.githubusercontent.com/u/9024855?s=460&v=4" width="100px;"/><br /><sub><b>王晓辉(Eddie)</b></sub>](https://github.com/eddie-wang) | [<img src="https://avatars2.githubusercontent.com/u/2503423?s=460&v=4" width="100px;"/><br /><sub><b>代码家</b></sub>](https://github.com/daimajia) | [<img src="https://avatars2.githubusercontent.com/u/9032795?s=460&v=4" width="100px;"/><br /><sub><b>David Dong</b></sub>](https://github.com/dongm2ez) | [<img src="https://avatars1.githubusercontent.com/u/10883521?s=460&v=4" width="100px;"/><br /><sub><b>José Vieira Neto</b></sub>](https://github.com/NetoDevel) | [<img src="https://avatars0.githubusercontent.com/u/59744?s=460&v=4" width="100px;"/><br /><sub><b>Schneeman</b></sub>](https://github.com/schneems) |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| [<img src="https://avatars1.githubusercontent.com/u/497803?s=460&v=4" width="100px;"/><br /><sub><b>Mohd Farid</b></sub>](https://github.com/mfarid) | [<img src="https://avatars3.githubusercontent.com/u/1326893?s=460&v=4" width="100px;"/><br /><sub><b>sumory</b></sub>](https://github.com/sumory) | [<img src="https://avatars3.githubusercontent.com/u/463602?s=460&v=4" width="100px;"/><br /><sub><b>Uday K</b></sub>](https://github.com/udaykadaboina) | [<img src="https://avatars0.githubusercontent.com/u/11169857?s=460&v=4" width="100px;"/><br /><sub><b>Antony Kwok</b></sub>](https://github.com/Awakens) | &nbsp; | &nbsp; | &nbsp; |
<!-- ALL-CONTRIBUTORS-LIST:END -->

Contributions of any kind are welcome!

## Licenses

Please see [Apache License](LICENSE)
