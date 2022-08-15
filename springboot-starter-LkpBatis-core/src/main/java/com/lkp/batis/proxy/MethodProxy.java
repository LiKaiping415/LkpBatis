package com.lkp.batis.proxy;

import com.lkp.batis.annotation.MySelect;
import com.lkp.batis.connection.Executor;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MethodProxy implements InvocationHandler {
    @Autowired
    Executor executor;
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //1.判断是否是实现类，是的话就直接执行，我们这里只代理接口
        if(Object.class.equals(method.getDeclaringClass())){
            try{
                return method.invoke(this,args);
            }catch (Throwable t){
                t.printStackTrace();
            }
        //不是的话，那就是接口了，自己实现
        }else{
            return run(method,args);
        }
        return null;
    }

    private Object run(Method method,Object[] args) {
        for (Annotation declaredAnnotation : method.getDeclaredAnnotations()) {
            if (declaredAnnotation.getClass().equals(MySelect.class)){
                try {
                    return executor.execute(method.getAnnotation(MySelect.class).sql());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                throw new RuntimeException("暂不支持的注解："+declaredAnnotation.getClass().getSimpleName());
            }
        }
        return "lkpbatis failed during executing sql or preparing sql";
    }
}
