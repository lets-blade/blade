<p align="center">
    <a href="https://lets-blade.com"><img src="http://7xls9k.dl1.z0.glb.clouddn.com/blade-logo.png" width="650"/></a>
</p>
<p align="center">Based on <code>Java8</code> + <code>Netty4</code> to create lightweight, high-performance, simple and elegant Web framework 😋</p>
<p align="center">Spend <b>1 hour</b> to learn it to do something interesting, a Spring in addition to the framework of the best choice.</p>
<p align="center">
    🐾 <a href="" target="_blank">Quick Start</a> | 
    📘 <a href="https://biezhi.gitbooks.io/blade-in-action" target="_blank">Blade In Action</a> | 
    🎬 <a href="https://www.youtube.com/playlist?list=PLK2w-tGRdrj5TV2lxHFj8hcg4mbmRmnWX" target="_blank">Video Tutorial</a> | 
    🌚 <a href="" target="_blank">Contribution</a> | 
    💰 <a href="https://lets-blade.com/donate" target="_blank">Donate</a> |
    🇨🇳 <a href="README_CN.md">简体中文</a>
</p>
<p align="center">
    <a href="https://travis-ci.org/biezhi/blade"><img src="https://img.shields.io/travis/biezhi/blade.svg?style=flat-square"></a>
    <a href="http://codecov.io/github/biezhi/blade?branch=dev"><img src="https://img.shields.io/codecov/c/github/biezhi/blade/dev.svg?style=flat-square"></a>
    <a href="http://search.maven.org/#search%7Cga%7C1%7Cblade-mvc"><img src="https://img.shields.io/maven-central/v/com.bladejava/blade-mvc.svg?style=flat-square"></a>
    <a href="LICENSE"><img src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square"></a>
    <a href="https://gitter.im/biezhi/blade"><img src="https://badges.gitter.im/biezhi/blade.svg?style=flat-square"></a>
</p>

***

## What Is Blade?

`Blade` is a pursuit of simple, efficient Web framework, so that `JavaWeb` development even more powerful, both in performance and flexibility.
If you like to try something interesting, I believe you will love it.
If you think this item is good can [star](https://github.com/biezhi/blade/stargazers) support or [donate](https://lets-blade.com/donate) it :blush:

## Features

* [x] A new generation of MVC frameworks that do not depend on more libraries
* [x] Get rid of SSH's bloated, modular design
* [x] source less than `500kb`, learning is also simple
* [x] Restful style routing design
* [x] template engine support, view development more flexible
* [x] high performance, 100 concurrent tps 6w/s
* [x] Run the `JAR` package to open the web service
* [x] Streaming API style
* [x] supports plug-in extensions
* [x] support webjars resources
* [x] built-in a variety of commonly used middleware
* [x] Built-in JSON output
* [x] JDK8 +

## Overview

» Simplicity: The design is simple, easy to understand and doesn't introduce many layers between you and the standard library. The goal of this project is that the users should be able to understand the whole framework in a single day.<br/>
» Elegance: `blade` supports the RESTful style routing interface, has no invasive interceptors and provides the writing of DSL grammar.<br/>
» Easy deploy: support `maven` package `jar` file running.<br/>

## Get Start

Grab via `Maven`：

```xml
<dependency>
	<groupId>com.bladejava</groupId>
	<artifactId>blade-mvc</artifactId>
	<version>2.0.3-alpha</version>
</dependency>
```

or `Gradle`:

```sh
compile 'com.bladejava:blade-mvc:2.0.3-alpha'
```

Write `main` method, lets `Hello World`：

```java
public static void main(String[] args) {
    Blade.me().get("/", (req, res) -> {
        res.text("Hello Blade");
    }).start();
}
```

Using browser open http://localhost:9000 so you can see the first `Blade` application!

## API Example

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

## REST URL Parameters

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

## Form Parameters

```java
public static void main(String[] args) {
    Blade.me().get("/user", ((request, response) -> {
         Optional<Integer> ageOptional = request.queryInt("age");
         ageOptional.ifPresent(age -> System.out.println("age is:" + age));
     })).start();
}
```

## Upload File

```java
public void upload(@MultipartParam FileItem fileItem){
    byte[] data = fileItem.getData();
    // Save the temporary file to the specified path
    Files.write(Paths.get(filePath), data);
}
```

Or

```java
public void upload(Request request){
    request.fileItem("img").ifPresent(fileItem -> {
        byte[] data = fileItem.getData();
        // Save the temporary file to the specified path
        Files.write(Paths.get(filePath), data);              
    });
}
```

## Before hook

```java
public static void main(String[] args) {
    // All requests are exported before execution before
    Blade.me().before("/*", (request, response) -> {
        System.out.println("before...");
    }).start();
}
```

How easy it all looks, but the features above are the tip of the iceberg, and there are more surprises to see in the documentation and sample projects:

+ [FirstBladeApp](https://github.com/bladejava/first-blade-app)
+ [Doc Service](https://github.com/biezhi/grice)
+ [More Examples](https://github.com/bladejava/blade-demos)

## Use the Blade site

+ Blog System：https://github.com/otale/tale
+ Community Application：https://github.com/junicorn/roo
+ Pictures social：https://github.com/biezhi/nice
+ SS Panel：https://github.com/biezhi/ss-panel

## Update

[update log](https://github.com/biezhi/blade/blob/master/UPDATE_LOG.md)

## Contact

- Blog:[http://biezhi.me](http://biezhi.me)
- Mail: biezhi.me@gmail.com

## Contributor

Thank you very much for the developers to help in the project, if you are willing to contribute, welcome!

- [mfarid](https://github.com/mfarid)
- [daimajia](https://github.com/daimajia)
- [shenjie1993](https://github.com/shenjie1993)
- [sumory](https://github.com/sumory)
- [udaykadaboina](https://github.com/udaykadaboina)
- [SyedWasiHaider](https://github.com/SyedWasiHaider)
- [Awakens](https://github.com/Awakens)
- [shellac](https://github.com/shellac)
- [SudarAbisheck](https://github.com/SudarAbisheck)

## Licenses

Please see [Apache License](LICENSE)
