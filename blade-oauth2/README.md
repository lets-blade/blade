# blade-oauth2

修改自[jfinal-oauth2.0-server](http://git.oschina.net/brucezcq/jfinal-oauth2.0-server)

参考[RFC6749](http://www.rfcreader.com/#rfc6749)

实现了OAuth 2.0定义了四种授权方式

* 授权码模式（authorization code）： 先获取下次请求token的code，然后在带着code去请求token
* 简化模式（implicit）：直接请求token
* 密码模式（resource owner password credentials）： 先完成授权，然后再获取token
* 客户端模式（client credentials）： 类似密码保护模式
