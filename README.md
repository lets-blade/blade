<p align="center">
    <a href="https://lets-blade.com"><img src="https://static.biezhi.me/blade-logo.png" width="650"/></a>
</p>
<p align="center">Based on <code>Java8</code> + <code>Netty4</code> to create lightweight, high-performance, simple and elegant Web framework 😋</p>
<p align="center">Spend <b>1 hour</b> to learn it to do something interesting, a Spring in addition to the framework of the best choice.</p>
<p align="center">
    🐾 <a href="#quick-start" target="_blank">Quick Start</a> |
    📘 <a href="https://dev-cheats.com/topics/blade-in-action.html" target="_blank">Blade In Action</a> |
    🎬 <a href="https://www.youtube.com/playlist?list=PLK2w-tGRdrj5TV2lxHFj8hcg4mbmRmnWX" target="_blank">Video Tutorial</a> |
    🌚 <a href="" target="_blank">Contribution</a> |
    💰 <a href="https://lets-blade.com/donate" target="_blank">Donate</a> |
    🇨🇳 <a href="README_CN.md">简体中文</a>
</p>
<p align="center">
    <a href="https://travis-ci.org/lets-blade/blade"><img src="https://img.shields.io/travis/lets-blade/blade.svg?style=flat-square"></a>
    <a href="http://codecov.io/github/lets-blade/blade?branch=dev"><img src="https://img.shields.io/codecov/c/github/lets-blade/blade/dev.svg?style=flat-square"></a>
    <a href="http://search.maven.org/#search%7Cga%7C1%7Cblade-mvc"><img src="https://img.shields.io/maven-central/v/com.bladejava/blade-mvc.svg?style=flat-square"></a>
    <a href="LICENSE"><img src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square"></a>
    <a class="badge-align" href="https://www.codacy.com/app/lets-blade/blade"><img src="https://api.codacy.com/project/badge/Grade/5f5fb55f38614f04823372db3a3c1d1b"/></a>
    <a href="https://gitter.im/biezhi/blade"><img src="https://badges.gitter.im/biezhi/blade.svg?style=flat-square"></a>
    <a href="https://www.codetriage.com/biezhi/blade"><img src="https://www.codetriage.com/biezhi/blade/badges/users.svg"></a>
</p>

***

## What Is Blade?

`Blade` is a pursuit of simple, efficient Web framework, so that `JavaWeb` development even more powerful, both in performance and flexibility.
If you like to try something interesting, I believe you will love it.
If you think this item is good can [star](https://github.com/biezhi/blade/stargazers) support or [donate](https://lets-blade.com/donate) it :blush:

## Features

* [x] A new generation of MVC frameworks that do not depend on more libraries
* [x] Get rid of SSH's bloated, modular design
* [x] Source less than `500kb`, learning is also simple
* [x] Restful style routing design
* [x] Template engine support, view development more flexible
* [x] High performance, 100 concurrent qps 14w/s
* [x] Run the `JAR` package to open the web service
* [x] Streaming API style
* [x] Supports plug-in extensions
* [x] Support webjars resources
* [x] Built-in a variety of commonly used middleware
* [x] Built-in JSON output
* [x] JDK8 +

## Overview

» Simplicity: The design is simple, easy to understand and doesn't introduce many layers between you and the standard library. The goal of this project is that the users should be able to understand the whole framework in a single day.<br/>
» Elegance: `blade` supports the RESTful style routing interface, has no invasive interceptors and provides the writing of DSL grammar.<br/>
» Easy deploy: support `maven` package `jar` file running.<br/>

## Quick Start

Run with `Maven`：

Create a basic `Maven` project

```xml
<dependency>
    <groupId>com.bladejava</groupId>
    <artifactId>blade-mvc</artifactId>
    <version>2.0.7-R2</version>
</dependency>
```

> Do not create a `webapp` project, blade is not so much trouble.

or `Gradle`:

```sh
compile 'com.bladejava:blade-mvc:2.0.7-R2'
```

Write `main` method, try `Hello World`：

```java
public static void main(String[] args) {
    Blade.me().get("/", (req, res) -> {
        res.text("Hello Blade");
    }).start();
}
```

Using browser open http://localhost:9000 so you can see the first `Blade` application!

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
    Blade.me()
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
    public RestResponse doSignin(Request request){
        // do something
        return RestResponse.ok();
    }

}
```

## Get Request Parameters

### Form Parameters

Here is an example:

**By Request**

```java
public static void main(String[] args) {
    Blade.me().get("/user", ((request, response) -> {
         Optional<Integer> ageOptional = request.queryInt("age");
         ageOptional.ifPresent(age -> System.out.println("age is:" + age));
     })).start();
}
```

**By Annotation**

```java
@PostRoute("/save")
public void savePerson(@Param String username, @Param Integer age){
  System.out.println("username is:" + username + ", age is:" + age)
}
```

The terminal sends a data test

```bash
curl -X GET http://127.0.0.1:9000/user?age=25
```

```bash
curl -X POST http://127.0.0.1:9000/save -F username=jack -F age=16
```

### Path Parameters

**By Request**

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

**By Annotation**

```java
@GetRoute("/users/:username/:page")
public void userTopics(@PathParam String username, @PathParam Integer page){
  System.out.println("username is:" + usernam + ", page is:" + page)
}
```

The terminal sends a data test

```bash
curl -X GET http://127.0.0.1:9000/users/biezhi/2
```

### Body Parameters

```java
public static void main(String[] args) {
    Blade.me().post("/body", ((request, response) -> {
      System.out.println("body string is:" + request.bodyToString())
    }).start();
}
```

The terminal sends a data test

```bash
curl -X POST http://127.0.0.1:9000/body -d '{"username":"biezhi","age":22}'
```

### Parse To Model

This is `User` model.

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

The terminal sends a data test

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

The terminal sends a data test

```bash
curl -X POST http://127.0.0.1:9000/users -F u[username]=jack -F u[age]=16
```

**Body Parameter To Model**

```java
public void getUser(@BodyParam User user){
    System.out.println("user => " + user);
}
```

The terminal sends a data test

```bash
curl -X POST http://127.0.0.1:9000/body -d '{"username":"biezhi","age":22}'
```

## Get Environment

```java
Environment environment = WebContext.blade().environment();
String version = environment.get("app.version", "0.0.1");;
```

## Get Header

**By Request**

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

**By Annotation**

```java
@GetRoute("header")
public void getHeader(@HeaderParam String Host){
  System.out.println("Host => " + Host);
}
```

## Get Cookie

**By Request**

```java
@GetRoute("cookie")
public void getCookie(Request request){
  System.out.println("UID => " + request.cookie("UID").get());
  request.cookie("UID").ifPresent(System.out::println);
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

Blade built a few static resource catalog, as long as you will save the resource file in the static directory under the classpath, and then browse http://127.0.0.1:9000/static/style.css

If you want to customize the static resource URL.

```java
Blade.me().addStatics("/mydir");
```

Of course you can also specify in the configuration file. `app.properties` (location in classpath)

```bash
mvc.statics=/mydir
```

## Upload File

**By Request**

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

**By Annotation**

```java
@PostRoute("upload")
public void upload(@MultipartParam FileItem fileItem){
    byte[] data = fileItem.getData();
    // Save the temporary file to the specified path
    Files.write(Paths.get(filePath), data);
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

**By Response**

```java
@GetRoute("users/json")
public void printJSON(Response response){
  User user = new User("biezhi", 18);
  response.json(user);
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
public void printText(Response response){
  response.text("I Love Blade!");
}
```

### Render Html

```java
@GetRoute("html")
public void printHtml(Response response){
  response.html("<center><h1>I Love Blade!</h1></center>");
}
```

## Render Template

By default all template files are in the templates directory, most of the cases you do not need to change it.

### Default Template

By default, the Blade uses the built-in template engine, which is very simple if you really do a web project can try several other extensions.

```java
public static void main(String[] args) {
    Blade.me().get("/hello", ((request, response) -> {
                request.attribute("name", "biezhi");
                response.render("hello.html");
            }))
            .start(Hello.class, args);
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

Create a `BeanProcessor` class

```java
@Bean
public class TemplateConfig implements BeanProcessor {

    @Override
    public void processor(Blade blade) {
        blade.templateEngine(new JetbrickTemplateEngine());
    }

}
```

Write some data for the template engine to render

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
public void redirectToGithub(Response response){

  response.redirect("https://github.com/biezhi");

}
```

[Redirect API](http://static.javadoc.io/com.bladejava/blade-mvc/2.0.3/com/blade/mvc/http/Response.html#redirect-java.lang.String-)

## Write Cookie

```java
@GetRoute("write-cookie")
public void writeCookie(Response response){

  response.cookie("hello", "world");
  response.cookie("UID", "22", 3600);

}
```

[Cookie API](http://static.javadoc.io/com.bladejava/blade-mvc/2.0.3/com/blade/mvc/http/Response.html#cookie-java.lang.String-java.lang.String-)

## Web Hook

`WebHook` is the interface in the Blade framework that can be intercepted before and after the execution of the route.

```java
public static void main(String[] args) {
    // All requests are exported before execution before
    Blade.me().before("/*", (request, response) -> {
        System.out.println("before...");
    }).start();
}
```

## Logging

Blade using slf4-api as a log interface, the default implementation of a simple log package (modified from simple-logger), if you need complex logging you can also use custom, you only need to exclude the `blade-log` in dependencies.

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

Blade built a few middleware, when you need Basic certification can be used, of course, can also be customized to achieve.

```java
public static void main(String[] args) {
  Blade.me().use(new BasicAuthMiddleware()).start();
}
```

Specify the user name and password in the `app.properties` configuration file.

```bash
http.auth.username=admin
http.auth.password=123456
```

## Change Server Port

There are three ways to modify the port, hard coding, configuration files, start the command line parameters.

**Hard Coding**

```java
Blade.me().listen(9001).start();
```

**Configuration For `app.properties`**

```bash
server.port=9001
```

**Command Line**

```bash
java -jar blade-app.jar --server.port=9001
```

## Configuration SSL

**Configuration For `app.properties`**

```bash
server.ssl.enable=true
server.ssl.cert-path=cert.pem
server.ssl.private-key-path=private_key.pem
server.ssl.private-key-pass=123456
```

## Custom Exception Handler

Blade has already implemented an exception handler by default, and sometimes you need to deal with custom exceptions, so you can do it.

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

How easy it all looks, but the features above are the tip of the iceberg, and there are more surprises to see in the documentation and sample projects:

+ [FirstBladeApp](https://github.com/lets-blade/first-blade-app)
+ [Blade Demos](https://github.com/lets-blade/blade-demos)
+ [Awesome Blade](https://github.com/lets-blade/awesome-blade)

## Change Logs

[See Here](https://lets-blade.com/about/change-logs)

## Contact

- Findor:[https://findor.me/biezhi](https://findor.me/biezhi)
- Twitter: [biezhi](https://twitter.com/biezhii)
- Mail: biezhi.me@gmail.com

## Contributors

Thanks goes to these wonderful people

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore -->
| [<img src="https://avatars2.githubusercontent.com/u/3849072?s=460&v=4" width="100px;"/><br /><sub><b>王爵nice</b></sub>](https://findor.me/biezhi) | [<img src="https://avatars2.githubusercontent.com/u/9401233?s=460&v=4" width="100px;"/><br /><sub><b>ccqy66</b></sub>](https://github.com/ccqy66) | [<img src="https://avatars0.githubusercontent.com/u/9024855?s=460&v=4" width="100px;"/><br /><sub><b>王晓辉(Eddie)</b></sub>](https://github.com/eddie-wang) | [<img src="https://avatars2.githubusercontent.com/u/2503423?s=460&v=4" width="100px;"/><br /><sub><b>代码家</b></sub>](https://github.com/daimajia) | [<img src="https://avatars2.githubusercontent.com/u/9032795?s=460&v=4" width="100px;"/><br /><sub><b>David Dong</b></sub>](https://github.com/dongm2ez) | [<img src="https://avatars1.githubusercontent.com/u/10883521?s=460&v=4" width="100px;"/><br /><sub><b>José Vieira Neto</b></sub>](https://github.com/NetoDevel) | [<img src="https://avatars0.githubusercontent.com/u/59744?s=460&v=4" width="100px;"/><br /><sub><b>Schneeman</b></sub>](https://github.com/schneems) |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| [<img src="https://avatars1.githubusercontent.com/u/497803?s=460&v=4" width="100px;"/><br /><sub><b>Mohd Farid</b></sub>](https://github.com/mfarid) | [<img src="https://avatars3.githubusercontent.com/u/1326893?s=460&v=4" width="100px;"/><br /><sub><b>sumory</b></sub>](https://github.com/sumory) | [<img src="https://avatars3.githubusercontent.com/u/463602?s=460&v=4" width="100px;"/><br /><sub><b>Uday K</b></sub>](https://github.com/udaykadaboina) | [<img src="https://avatars0.githubusercontent.com/u/11169857?s=460&v=4" width="100px;"/><br /><sub><b>Antony Kwok</b></sub>](https://github.com/Awakens) | &nbsp; | &nbsp; | &nbsp; |
<!-- ALL-CONTRIBUTORS-LIST:END -->

Contributions of any kind are welcome!

## Licenses

Please see [Apache License](LICENSE)
