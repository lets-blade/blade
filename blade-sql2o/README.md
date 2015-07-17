# Blade-Sql2o

轻小，简单，快速，高效的数据库框架。

### 特性
基于`blade`框架为核心搭建的操作数据库基础框架，一行代码即可完成增删改查，分页等操作；
内置基于LRU的缓存机制进行缓存，可配置的缓存开关以及扩展。

##快速入门
它使用起来非常方便，首先配置好数据库

```java
// 配置数据库插件
Sql2oPlugin sql2oPlugin = Sql2oPlugin.INSTANCE;
sql2oPlugin.config("jdbc:mysql://localhost:3306/test", "com.mysql.jdbc.Driver", "root", "root");
// 开启缓存
sql2oPlugin.openCache();
sql2oPlugin.run();
```
* 添加一条数据
```java
User model = new User();

Date birthday = new Date();
Long uid = model.insert().param("name", "jack").param("birthday", birthday).param("sex", "男").executeAndCommit();
LOGGER.INFO("uid = " + uid);
```

* 查询一条数据
```java
User model = new User();

// 根据主键查询User
User user = model.select().fetchByPk(1);
LOGGER.INFO("user = " + user);

// 查询性别为男性，名字以`张`开头，年龄在50一下的User
User user = model.select().where("sex", "男").like("name", "张%").less("age", 50).fetchOne();
LOGGER.INFO("user = " + user);
```

* 查询一个数据集合
```java
User model = new User();

// 查询性别为男性，名字以`张`开头，年龄在50一下的User集合
List<User> users = model.select().where("sex", "男").like("name", "张%").less("age", 50).order("age asc").fetchList();
LOGGER.INFO("user = " + user);
```


* 分页查询一个数据集合
```java
Post model = new Post();

Page<Post> postPage = model.select("select pid, title, type, access_count, create_time, update_time from post")
				.where("status", "publish")
				.like("title", "%java")
				.orderBy("pid desc")
				.fetchPage(1, 10);
```


* 联合查询一个数据集合
```java
Post model = new Post();

List<Post> posts = model.select("select a.* from post a inner join relation b on a.pid = b.pid")
				.where("a.type", "post")
				.where("b.mid", 15)
				.like("a.title", "%java")
				.orderBy("a.pid desc")
				.fetchList();
```

* 修改数据
```java
Post model = new Post();

Long count = model.update()
			.param("title", "test1")
			.where("pid", 11).executeAndCommit();

```

* 删除数据
```java
Post model = new Post();

Long count = model.delete().where("pid", pid).executeAndCommit();

```

### v1.2.x
	1. 修复分页bug
	2. 修复按主键查询bug
	3. 添加内置缓存支持

### v1.1.x
	1. 适配`blade`最新版本
	2. 添加动态数据源支持
		
### 联系我
Mail: biezhi.me#gmail.com

Java交流群: [1013565](http://shang.qq.com/wpa/qunwpa?idkey=932642920a5c0ef5f1ae902723c4f168c58ea63f3cef1139e30d68145d3b5b2f)