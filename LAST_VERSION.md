# blade last version

如果在maven仓库中下载不到最新版本的依赖，请添加maven snapshots仓库

```xml
<repositories>
 <repository>
   <id>oss-snapshots</id>
   <url>https://oss.sonatype.org/content/repositories/snapshots</url>
   <releases>
     <enabled>false</enabled>
   </releases>
   <snapshots>
     <enabled>true</enabled>
   </snapshots>
 </repository>
</repositories>
```
和`dependencies`相同级别


## [blade-kit](http://search.maven.org/#search%7Cga%7C1%7Cblade-kit)
```xml
<dependency>
    <groupId>com.bladejava</groupId>
    <artifactId>blade-kit</artifactId>
    <version>1.3.0M1</version>
</dependency>
```

## [blade-core](http://search.maven.org/#search%7Cga%7C1%7Cblade-core)
```xml
<dependency>
    <groupId>com.bladejava</groupId>
    <artifactId>blade-core</artifactId>
    <version>1.6.2</version>
</dependency>
```

## [blade-jdbc](http://search.maven.org/#search%7Cga%7C1%7Cblade-jdbc)
```xml
<dependency>
    <groupId>com.bladejava</groupId>
    <artifactId>blade-jdbc</artifactId>
    <version>0.0.8</version>
</dependency>
```

## [blade-cache](http://search.maven.org/#search%7Cga%7C1%7Cblade-cache)
```xml
<dependency>
    <groupId>com.bladejava</groupId>
    <artifactId>blade-cache</artifactId>
    <version>1.2.3</version>
</dependency>
```

## [blade-redis](http://search.maven.org/#search%7Cga%7C1%7Cblade-redis)
```xml
<dependency>
    <groupId>com.bladejava</groupId>
    <artifactId>blade-redis</artifactId>
    <version>1.2.3</version>
</dependency>
```

## [blade-startup](http://search.maven.org/#search%7Cga%7C1%7Cblade-startup)
```xml
<dependency>
    <groupId>com.bladejava</groupId>
    <artifactId>blade-startup</artifactId>
    <version>1.0.1</version>
</dependency>
```

## [blade-patchca](http://search.maven.org/#search%7Cga%7C1%7Cblade-patchca)
```xml
<dependency>
    <groupId>com.bladejava</groupId>
    <artifactId>blade-patchca</artifactId>
    <version>1.0.2</version>
</dependency>
```

## [blade-oauth2](http://search.maven.org/#search%7Cga%7C1%7Cblade-oauth2)
```xml
<dependency>
    <groupId>com.bladejava</groupId>
    <artifactId>blade-oauth2</artifactId>
    <version>1.0.2</version>
</dependency>
```
