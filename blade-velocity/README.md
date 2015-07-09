#blade-velocity

```java
public class App implements BladeApplication{

	Logger logger = Logger.getLogger(App.class);
	
	@Override
	public void init() {
		
		// 设置路由、拦截器包所在包
		Blade.defaultRoute("blade.sample");
		
		// 设置VelocityRender引擎
		final Render render = new VelocityRender();
		Blade.viewEngin(render);
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
<title>Blade Velocity Demo</title>
</head>
<body>
	<h2>hello $username</h2>
</body>
</html>
```