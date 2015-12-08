
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
	<version>最终版本</version>
</dependency>
```

编写 `Main`函数：

```java
public class App {
		
	public static void main(String[] args) {
		Blade blade = Blade.me();
		blade.get("/", (request, response) -> {
			response.html("<h1>Hello blade!</h1>");
		});
		blade.listen(9001).start();
	}
}
```

用浏览器打开 http://localhost:9001 这样就可以看到第一个Blade应用了！

OK，这一切看起来多么的简单，查阅使用指南更多现成的例子供你参考:

+ [hello工程](https://github.com/bladejava/hello)
+ [API文档](http://bladejava.com/apidocs/)
+ [使用指南](https://github.com/biezhi/blade/wiki)
+ [相关案例](https://github.com/bladejava)
+ [版本查询](LAST_VERSION.md)

### 计划

- 1. 开发社交应用平台
- 2. 添加测试代码
- 3. 优化基础代码
- 4. 优化并发能力

## 更新日志

[更新日志](https://github.com/biezhi/blade/blob/master/UPDATE_LOG.md)

## 联系我

- Blog:[https://biezhi.me](https://biezhi.me)
- Mail: biezhi.me#gmail.com
- Java交流群: [1013565](http://shang.qq.com/wpa/qunwpa?idkey=932642920a5c0ef5f1ae902723c4f168c58ea63f3cef1139e30d68145d3b5b2f)

## 开源协议

```
Copyright 2015 biezhi

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
