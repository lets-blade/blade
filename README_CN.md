
[![](https://dn-biezhi.qbox.me/LOGO_BIG.png)](http://bladejava.com)

[![Build Status](https://img.shields.io/travis/biezhi/blade.svg?style=flat-square)](https://travis-ci.org/biezhi/blade)
[![maven-central](https://img.shields.io/maven-central/v/com.bladejava/blade-core.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.bladejava%22)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![@biezhi on weibo](https://img.shields.io/badge/weibo-%40biezhi-red.svg?style=flat-square)](http://weibo.com/u/5238733773)

[English](https://github.com/biezhi/blade/blob/master/README.md)

## Blade是什么?

Blade 是一个轻量级的MVC框架. 它拥有简洁的代码，优雅的设计。
如果你喜欢,欢迎 [Star and Fork](https://github.com/biezhi/blade), 谢谢!

## 特性

* [x] 轻量级。代码简洁,结构清晰,更容易开发
* [x] 模块化(你可以选择使用哪些组件)
* [x] 插件扩展机制
* [x] Restful风格的路由接口
* [x] 多种配置文件支持(当前支持properties、json和硬编码)
* [x] 内置Jetty服务,模板引擎支持
* [x] 支持JDK1.6或者更高版本

## 概述

* 简洁的：框架设计简单,容易理解,不依赖于更多第三方库。Blade框架目标让用户在一天内理解并使用。
* 优雅的：`blade` 支持 REST 风格路由接口, 提供 DSL 语法编写，无侵入式的拦截器。

## 快速入门

开始之前,首先 [引入Blade的库文件](http://bladejava.com/docs/intro/getting_start) ：

`Maven` 配置：

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

编写 `Main`函数：

```java
public static void main(String[] args) {
	Blade blade = me();
	blade.get("/", (request, response) -> {
		response.html("<h1>Hello blade!</h1>");
	});
	blade.listen(9001).start();
}
```

用浏览器打开 http://localhost:9001 这样就可以看到第一个Blade应用了！

## API示例

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

## REST URL参数获取

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

## Form URL参数获取

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

## 上传文件

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

## 配置文件路由

`route.conf`

```sh
GET		/					IndexRoute.home
GET		/signin				IndexRoute.show_signin
POST	/signin				IndexRoute.signin
GET		/signout			IndexRoute.signout
POST	/upload_img			UploadRoute.upload_img
```

## 路由拦截

```java
public static void main(String[] args) {
	Blade blade = me();
	blade.before("/.*", (request, response) -> {
		System.out.println("before...");
	});
	blade.listen(9001).start();
}
```

## DSL数据库操作

```java
// 保存操作
public boolean save(Integer cid, Integer tid, Integer fuid, Integer tuid) {
    return model.insert().param("cid", cid)
    .param("tid", tid)
    .param("fuid", fuid)
    .param("tuid", tuid)
    .param("addtime", new Date())
    .param("ntype", 0).executeAndCommit() > 0;
}

// 登录操作
public User signin(String username, String password) {
    String pwd = EncrypKit.md5(username + password);
    return model.select().eq("username", username)
    .eq("password", pwd).fetchOne();
}

// 查询条数
public Long getUserCount(String email){
    return model.count().eq("email", email).fetchCount();
}
```

OK，这一切看起来多么的简单，查阅使用指南更多现成的例子供你参考:

+ [hello工程](https://github.com/blade-samples/hello)
+ [API文档](http://bladejava.com/apidocs)
+ [使用指南](http://bladejava.com/docs)
+ [相关案例](https://github.com/blade-samples)
+ [版本查询](LAST_VERSION.md)

### 计划

- 1. 添加测试代码
- 2. 优化基础代码
- 3. 优化并发能力

## 更新日志

[更新日志](https://github.com/biezhi/blade/blob/master/UPDATE_LOG.md)

## 联系我

- Blog:[https://biezhi.me](https://biezhi.me)
- Mail: biezhi.me#gmail.com
- Java交流群: [1013565](http://shang.qq.com/wpa/qunwpa?idkey=932642920a5c0ef5f1ae902723c4f168c58ea63f3cef1139e30d68145d3b5b2f)

## 贡献

非常感谢下面的开发者朋友对本项目的帮助，如果你也愿意提交PR，欢迎你！

- [mfarid](https://github.com/mfarid)
- [shenjie1993](https://github.com/shenjie1993)
- [sumory](https://github.com/sumory)
- [udaykadaboina](https://github.com/udaykadaboina)
- [SyedWasiHaider](https://github.com/SyedWasiHaider)
- [Awakens](https://github.com/Awakens)
- [shellac](https://github.com/shellac)

## 开源协议

请查看 [Apache License](LICENSE)

