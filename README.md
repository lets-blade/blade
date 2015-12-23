
[![](https://dn-biezhi.qbox.me/LOGO_BIG.png)](http://bladejava.com)

[![Build Status](https://img.shields.io/travis/biezhi/blade.svg?style=flat-square)](https://travis-ci.org/biezhi/blade)
[![maven-central](https://img.shields.io/maven-central/v/com.bladejava/blade-core.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.bladejava%22)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Gitter](https://badges.gitter.im/biezhi/blade.svg)](https://gitter.im/biezhi/blade?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

[中文说明](https://github.com/biezhi/blade/blob/master/README_CN.md)

## What Is Blade?

Blade is a lightweight MVC framework. It is based on the principles of simplicity and elegance. 
If you like it, please [star and fork it](https://github.com/biezhi/blade). Thank you!

## Features

* [x] Lightweight: the code is simple and the structure is clear
* [x] Modular (you can choose which components to use)
* [x] Supports plug-in extension mechanism
* [x] RESTful style routing interface
* [x] Supports multiple configuration files (currently properties, json and coding)
* [x] Embedded jetty server and template engine support
* [x] Supports JDK 1.6 and up

## Overview

* Simplicity: The design is simple, easy to understand and doesn't introduce many layers between you and the standard library. The goal of this project is that the users should be able to understand the whole framework in a single day.
* Elegance: `blade` supports the RESTful style routing interface, has no invasive interceptors and provides the writing of DSL grammar.

## Get Start

To get started, first [include the Blade library](http://bladejava.com/docs/intro/getting_start) :

Grab via `Maven`：

```sh
<dependency>
	<groupId>com.bladejava</groupId>
	<artifactId>blade-core</artifactId>
	<version>1.5.1-alpha</version>
</dependency>
<dependency>
        <groupId>com.bladejava</groupId>
        <artifactId>blade-startup</artifactId>
        <version>1.0.1</version>
</dependency>
```
or `Gradle`:
```
compile 'com.bladejava:blade-core:1.5.1-alpha'
compile 'com.bladejava:blade-startup:1.0.1'
```

Create `Main` method like this：

```java
public class App {
	
	public static void main(String[] args) {
		Blade blade = me();
		blade.get("/", (request, response) -> {
			response.html("<h1>Hello blade!</h1>");
		});
		blade.listen(9001).start();
	}
}
```

Run it and point your browser to [http://localhost:9001](http://localhost:9001). There you go, you've just created your first Blade app!

## API Example

```java
public static void main(String[] args) {
	Blade blade = me();
	blade.get("/user/21", getxxx);
	blade.post("/save", postxxx);
	blade.delete("/del/21", deletexxx);
	blade.put("/put", putxxx);
	blade.listen(9001).start();
}
```

## REST URL Parameters

```java
public static void main(String[] args) {
	Blade blade = me();
	blade.get("/user/:uid", (request, response) -> {
		Integer uid = request.paramAsInt("uid");
		response.text("uid : " + uid);
	});
	
	blade.get("/users/:uid/post/:pid", (request, response) -> {
		Integer uid = request.paramAsInt("uid");
		Integer pid = request.paramAsInt("pid");
		String msg = "uid = " + uid + ", pid = " + pid;
		response.text(msg);
	});
	
	blade.listen(9001).start();
}
```

## Form URL Parameters

```java
public static void main(String[] args) {
	Blade blade = me();
	blade.get("/user", (request, response) -> {
		Integer uid = request.queryAsInt("uid");
		response.text("uid : " + uid);
	});
	blade.listen(9001).start();
}
```

## Upload File

```java
public void upload_img(Request request, Response response){
		
	JsonObject jsonObject = new JsonObject();

	FileItem[] fileItems = request.files();
	if(null != fileItems && fileItems.length > 0){
		
		FileItem fileItem = fileItems[0];
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
	Blade blade = me();
	blade.before("/.*", (request, response) -> {
		System.out.println("before...");
	});
	blade.listen(9001).start();
}
```

## DSL DB Operation

```java
// save
public boolean save(Integer cid, Integer tid, Integer fuid, Integer tuid) {
    return model.insert().param("cid", cid)
    .param("tid", tid)
    .param("fuid", fuid)
    .param("tuid", tuid)
    .param("addtime", new Date())
    .param("ntype", 0).executeAndCommit() > 0;
}

// signin
public User signin(String username, String password) {
    String pwd = EncrypKit.md5(username + password);
    return model.select().eq("username", username)
    .eq("password", pwd).fetchOne();
}

// search count
public Long getUserCount(String email){
    return model.count().eq("email", email).fetchCount();
}
```

You may refer to these examples for additional guidance:

+ [Hello](https://github.com/blade-samples/hello)
+ [API Doc](http://bladejava.com/apidocs)
+ [User Guide](http://bladejava.com/docs)
+ [Examples](https://github.com/blade-samples)
+ [Version](LAST_VERSION.md)

## Plan

- 1. Add the test code
- 2. Optimize the code base
- 3. Optimization of concurrent ability
	
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
