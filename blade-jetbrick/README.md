#blade-jetbrick

```java
public class App extends BladeApplication{

	Logger logger = Logger.getLogger(App.class);
	
	@Override
	public void init() {
		
		// 设置路由、拦截器包所在包
		Blade.defaultRoute("blade.sample");
		
		// 设置JetbrickRender引擎
		Blade.viewEngin(new JetbrickRender());
	}
	
}
```

Route Code

```java
@Route("/you/:username")
public String you(Request request) {
	ModelAndView modelAndView = new ModelAndView("");
	modelAndView.add("username", request.pathParam(":username"));
	return R.render(modelAndView);
}
```

View Code

```html
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Blade Jetbrick Demo</title>
</head>
<body>
	<h2>hello ${username}</h2>
</body>
</html>
```