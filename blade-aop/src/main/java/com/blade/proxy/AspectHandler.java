package com.blade.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

import blade.kit.log.Logger;

public class AspectHandler implements InvocationHandler {

  private static final Logger logger = Logger.getLogger(AspectHandler.class);

  private Object target = null;
  private List<Aspect> aspects = null;
  private int index = -1;

  public AspectHandler(int index, Object target, List<Aspect> aspects) {
    this.index = index;
    this.target = target;
    this.aspects = aspects;
  }

  public AspectHandler(Object target, List<Aspect> aspects) {
    this.target = target;
    this.aspects = aspects;
  }


  public Object getTarget() {
    return target;
  }


  public void setTarget(Object target) {
    this.target = target;
  }

  public List<Aspect> getAspects() {
    return aspects;
  }


  public void setAspects(List<Aspect> aspects) {
    this.aspects = aspects;
  }

  /**
   * 委托方法
   *
   * @param proxy  代理对象
   * @param method 代理方法
   * @param args   方法参数
   */
  public Object invoke(Object proxy, Method method, Object[] args)
      throws Throwable {
    if (index == -1) {
      logger.info("Instance an AspectHandler to invoke method %s.", method.getName());
      return new AspectHandler(0, target, aspects).invoke(proxy, method, args);
    }
    Object result = null;
    int len = aspects.size();
    if (index < len) {
    	result = aspects.get(index++).aspect(this, proxy, method, args);
    } else if (index++ == len) {
      result = method.invoke(target, args);
    }
    return result;
  }

}