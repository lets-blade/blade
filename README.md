
[![](https://dn-biezhi.qbox.me/LOGO_BIG.png)](https://biezhi.gitbooks.io/blade-in-action)

[Quick Start](https://biezhi.gitbooks.io/blade-in-action/chapter1/1.1-create-blade-application.html)&nbsp; | &nbsp;[Video](https://www.youtube.com/playlist?list=PLK2w-tGRdrj5TV2lxHFj8hcg4mbmRmnWX)&nbsp; | &nbsp;[Contribute](https://bladejava.com/docs/appendix/contribute)&nbsp; | &nbsp;[Donate](donate.md)&nbsp; | &nbsp;[FAQ](https://bladejava.com/docs/faqs) | &nbsp;[中文说明](https://github.com/biezhi/blade/blob/master/README_CN.md)

[![Build Status](https://img.shields.io/travis/biezhi/blade.svg?style=flat-square)](https://travis-ci.org/biezhi/blade)
[![codecov.io](https://img.shields.io/codecov/c/github/biezhi/blade/dev.svg?style=flat-square)](http://codecov.io/github/biezhi/blade?branch=dev)
[![maven-central](https://img.shields.io/maven-central/v/com.bladejava/blade-mvc.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cblade-mvc)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Gitter](https://badges.gitter.im/biezhi/blade.svg)](https://gitter.im/biezhi/blade?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)


## What Is Blade?

Blade is a lightweight MVC framework. It is based on the principles of simplicity and elegance. 
If you like it, give me a [star](https://github.com/biezhi/blade/stargazers) / [fork](https://github.com/biezhi/blade). Thx :blush:

## Features

* [x] Lightweight: the code is simple and the structure is clear
* [x] RESTful style routing interface
* [x] Template engine support
* [x] High performance
* [x] Run with jar file
* [x] Fluent interface
* [x] Support plugin extension
* [x] Support webjars
* [x] JDK8+
* [x] Event mechanism

## Overview

* Simplicity: The design is simple, easy to understand and doesn't introduce many layers between you and the standard library. The goal of this project is that the users should be able to understand the whole framework in a single day.
* Elegance: `blade` supports the RESTful style routing interface, has no invasive interceptors and provides the writing of DSL grammar.

## Get Start

Grab via `Maven`：

```xml
<dependency>
	<groupId>com.bladejava</groupId>
	<artifactId>blade-mvc</artifactId>
	<version>2.0.2-beta</version>
</dependency>
```

or `Gradle`:

```sh
compile 'com.bladejava:blade-mvc:2.0.2-beta'
```

Create `Main` method like this：

```java
public static void main(String[] args) {
    Blade blade = Blade.me();
    blade.get("/", (req, res) -> {
        res.text("Hello Blade");
    }).start();
}
```

Run it and point your browser to [http://localhost:9000](http://localhost:9000). There you go, you've just created your first Blade app!

## API Example

```java
public static void main(String[] args) {
    Blade blade = Blade.of();
    blade.get("/user/21", getxxx);
    blade.post("/save", postxxx);
    blade.delete("/del/21", deletexxx);
    blade.put("/put", putxxx);
}
```

## REST URL Parameters

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

## Form URL Parameters

```java
public static void main(String[] args) {
    Blade blade = Blade.of();
    blade.get("/user", (request, response) -> {
		Integer uid = request.queryInt("uid").get();
		response.text("uid : " + uid);
	}).start(Application.class);
}
```

## Upload File

```java
public void upload_img(@MultipartParam FileItem fileItem){
    if(null != fileItem){
        File file = fileItem.getFile();
        String fileRealPath = "your upload file path!";
        nioTransferCopy(file, fileRealPath);
    }
}
```

## Route Web Hook

```java
public static void main(String[] args) {
    Blade blade = Blade.of();
    blade.before("/.*", (request, response) -> {
        System.out.println("before...");
    }).start();
}
```

You may refer to these examples for additional guidance:

+ [Hello Blade](https://github.com/bladejava/blade-demos/tree/master/helloworld)
+ [Doc Service](https://github.com/biezhi/grice)
+ [More Examples](https://github.com/bladejava)

## Used Blade WebSite

+ Blog：https://github.com/otale/tale
+ BBS：https://java-china.org
+ Gallery：https://github.com/biezhi/nice
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
