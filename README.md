
[![](https://dn-biezhi.qbox.me/LOGO_BIG.png)](http://bladejava.com)

[Quick Start](https://bladejava.com/docs)&nbsp; | &nbsp;[Demo Project](https://github.com/blade-samples)&nbsp; | &nbsp;[Contribute](https://bladejava.com/docs/appendix/contribute)&nbsp; | &nbsp;[Donate]()&nbsp; | &nbsp;[FAQ](https://bladejava.com/docs/faqs) | &nbsp;[中文说明](https://github.com/biezhi/blade/blob/master/README_CN.md)

[![Build Status](https://img.shields.io/travis/biezhi/blade.svg?style=flat-square)](https://travis-ci.org/biezhi/blade)
[![maven-central](https://img.shields.io/maven-central/v/com.bladejava/blade-core.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.bladejava%22)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Gitter](https://badges.gitter.im/biezhi/blade.svg)](https://gitter.im/biezhi/blade?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)


## What Is Blade?

Blade is a lightweight MVC framework. It is based on the principles of simplicity and elegance. 
If you like it, please [star](https://github.com/biezhi/blade/stargazers) / [fork](https://github.com/biezhi/blade). Thx :blush:

## Features

* [x] Lightweight: the code is simple and the structure is clear
* [x] Modular (you can choose which components to use)
* [x] RESTful style routing interface
* [x] Template engine support
* [x] Run with jar file
* [x] Supports JDK 1.6 and up (java8 is cool)

## Overview

* Simplicity: The design is simple, easy to understand and doesn't introduce many layers between you and the standard library. The goal of this project is that the users should be able to understand the whole framework in a single day.
* Elegance: `blade` supports the RESTful style routing interface, has no invasive interceptors and provides the writing of DSL grammar.

## Get Start

To get started, first [include the Blade library](http://bladejava.com/docs/intro/getting_start) :

Grab via `Maven`：

```xml
<dependency>
	<groupId>com.bladejava</groupId>
	<artifactId>blade-core</artifactId>
	<version>1.6.7-alpha</version>
</dependency>
<dependency>
	<groupId>com.bladejava</groupId>
	<artifactId>blade-embed-jetty</artifactId>
	<version>0.0.5</version>
</dependency>
```
or `Gradle`:
```sh
compile 'com.bladejava:blade-core:1.6.7-alpha'
compile 'com.bladejava:blade-embed-jetty:0.0.5'
```

Create `Main` method like this：

```java
public static void main(String[] args) {
	$().get("/", (request, response) -> {
		response.html("<h1>Hello blade!</h1>");
	}).start(Application.class);
}
```

Run it and point your browser to [http://localhost:9000](http://localhost:9000). There you go, you've just created your first Blade app!

## API Example

```java
public static void main(String[] args) {
	$().get("/user/21", getxxx);
	$().post("/save", postxxx);
	$().delete("/del/21", deletexxx);
	$().put("/put", putxxx);
}
```

## REST URL Parameters

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

## Form URL Parameters

```java
public static void main(String[] args) {
	$().get("/user", (request, response) -> {
		Integer uid = request.queryAsInt("uid");
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

## Route Config File

`route.conf`

```sh
GET		/					IndexRoute.home
GET		/signin				IndexRoute.show_signin
POST	/signin				IndexRoute.signin
GET		/signout			IndexRoute.signout
POST	/upload_img			UploadRoute.upload_img
```

## Route Intercept

```java
public static void main(String[] args) {
	$().before("/.*", (request, response) -> {
		System.out.println("before...");
	}).start(Application.class);
}
```

You may refer to these examples for additional guidance:

+ [Hello Blade](https://github.com/blade-samples/hello)
+ [BBS WebSite](https://github.com/junicorn/java-china)
+ [Doc Service](https://github.com/biezhi/grice)
+ [More Examples](https://github.com/blade-samples)

	
## Update

[update log](https://github.com/biezhi/blade/blob/master/UPDATE_LOG.md)

## Contact

- Blog:[https://biezhi.me](https://biezhi.me)
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
