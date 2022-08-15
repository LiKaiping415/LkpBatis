package com.lkp.batis.proxy;

import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

public class Invoker {
    public Object getInstance(Class<?> cls){
        MethodProxy methodProxy=new MethodProxy();
        return Proxy.newProxyInstance(cls.getClassLoader(),new Class[]{cls},methodProxy);
    }
}
