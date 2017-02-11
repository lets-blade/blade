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
    <version>1.4.0</version>
</dependency>
```

## [blade-core](http://search.maven.org/#search%7Cga%7C1%7Cblade-core)
```xml
<dependency>
    <groupId>com.bladejava</groupId>
    <artifactId>blade-core</artifactId>
    <version>1.7.0</version>
</dependency>
```

## [blade-embed-jetty](http://search.maven.org/#search%7Cga%7C1%7Cblade-embed-jetty)
```xml
<dependency>
    <groupId>com.bladejava</groupId>
    <artifactId>blade-embed-jetty</artifactId>
    <version>0.0.9</version>
</dependency>
```

## [blade-template-jetbrick](http://search.maven.org/#search%7Cga%7C1%7Cblade-template-jetbrick)
```xml
<dependency>
    <groupId>com.bladejava</groupId>
    <artifactId>blade-template-jetbrick</artifactId>
    <version>0.0.8</version>
</dependency>
```

## [blade-jdbc](http://search.maven.org/#search%7Cga%7C1%7Cblade-jdbc)
```xml
<dependency>
    <groupId>com.bladejava</groupId>
    <artifactId>blade-jdbc</artifactId>
    <version>0.1.4-alpha</version>
</dependency>
```

## [blade-patchca](http://search.maven.org/#search%7Cga%7C1%7Cblade-patchca)
```xml
<dependency>
    <groupId>com.bladejava</groupId>
    <artifactId>blade-patchca</artifactId>
    <version>1.0.5</version>
</dependency>
```

