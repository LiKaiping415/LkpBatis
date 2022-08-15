package com.lkp.batis.processor;

import com.lkp.batis.annotation.MyDelete;
import com.lkp.batis.annotation.MyInsert;
import com.lkp.batis.annotation.MySelect;
import com.lkp.batis.annotation.MyUpdate;
import com.lkp.batis.connection.Executor;
import com.sun.org.apache.xerces.internal.impl.xpath.XPath;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.print.attribute.standard.Fidelity;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("all")
@Slf4j
public class Mapper {
    @Autowired
    ApplicationContext applicationContext;
    /*private final Map<Method,ServiceMethod<?>> serviceMethodCache=new ConcurrentHashMap<>();*/
    public <T> T create(final Class<T> service){
        return (T) Proxy.newProxyInstance(
                service.getClassLoader(),
                new Class<?>[]{service},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (method.getDeclaringClass()==Object.class){
                            log.warn("it's a class instead of interface,check your code!");
                            return method.invoke(this,args);
                        }else{
                            return loadSericeMethodAndExecute(method,args);
                        }
                    }
                }
        );
    }

    private Object loadSericeMethodAndExecute(Method method,Object[] args) {
        Annotation[] annotations = method.getAnnotations();
        if (annotations.length==0){
            log.error("no annotation on method:"+method.getName());
        }else if(annotations.length>1){
            log.error("to many annotations on method:"+method.getName());
        }
        System.out.println("annotation on method: aa:"+annotations[0].annotationType().getName());
        if(annotations[0].annotationType()== MySelect.class){
            log.info("begin execute action:select");
            log.info("ready to execute sql:"+method.getAnnotation(MySelect.class).sql());
            return executeSelect(method.getAnnotation(MySelect.class),method.getReturnType());
        }
        if (annotations[0].annotationType()== MyUpdate.class){
            log.info("begin execute action:update");
            log.info("ready to execute sql:"+method.getAnnotation(MyUpdate.class).sql());
            return executeUpdate(method.getAnnotation(MyUpdate.class),method.getReturnType());
        }
        if (annotations[0].annotationType()== MyDelete.class){
            log.info("begin execute action:delete");
            log.info("ready to execute sql:"+method.getAnnotation(MyDelete.class).sql());
            return executeDelete(method.getAnnotation(MyDelete.class),method.getReturnType());
        }
        if (annotations[0].annotationType()== MyInsert.class){
            log.info("begin execute action:delete");
            log.info("ready to execute sql:"+method.getAnnotation(MyInsert.class).sql());
            return executeInsert(method.getAnnotation(MyInsert.class),method.getReturnType());
        }
        return null;
    }

    /**
     * 执行的delete的sql语句
     * @param annotation
     * @param responseType
     * @return
     */
    public int executeInsert(MyInsert annotation,Class responseType){
        if (responseType!=int.class){
            throw new RuntimeException("this method just can only return int!");
        }
        String res= null;
        try {
            res = MyMapperFactoryBean.getApplicationContext().getBean(Executor.class).execute(annotation.sql());
        } catch (Exception e) {
            log.error("error while executing update sql....");
            e.printStackTrace();
        }
        if (validUpdateInsertDeleteRes(res)){
            return Integer.parseInt(res.split(" ")[1]);
        }
        return -1;
    }

    /**
     * 执行的delete的sql语句
     * @param annotation
     * @param responseType
     * @return
     */
    public int executeDelete(MyDelete annotation,Class responseType){
        if (responseType!=int.class){
            throw new RuntimeException("this method just can only return int!");
        }
        String res= null;
        try {
            res = MyMapperFactoryBean.getApplicationContext().getBean(Executor.class).execute(annotation.sql());
        } catch (Exception e) {
            log.error("error while executing update sql....");
            e.printStackTrace();
        }
        if (validUpdateInsertDeleteRes(res)){
            return Integer.parseInt(res.split(" ")[1]);
        }
        return -1;
    }
    /**
     * 执行的update的sql语句
     * @param annotation
     * @param responseType
     * @return
     */
    public int executeUpdate(MyUpdate annotation,Class responseType){
        if (responseType!=int.class){
            throw new RuntimeException("this method just can only return int!");
        }
        String res= null;
        try {
            res = MyMapperFactoryBean.getApplicationContext().getBean(Executor.class).execute(annotation.sql());
        } catch (Exception e) {
            log.error("error while executing update sql....");
            e.printStackTrace();
        }
        if (validUpdateInsertDeleteRes(res)){
            return Integer.parseInt(res.split(" ")[1]);
        }
        return -1;
    }
    public boolean validUpdateInsertDeleteRes(String res){
        String[] s = res.split(" ");
        return s.length==2 && s[0].equals("update");
    }
    /**
     * 执行select的sql语句
     * @param annotation
     * @param responseType
     * @return
     */
    public Object executeSelect(MySelect annotation,Class responseType){
        int i=0;
        String res= null;
        try {
            res = MyMapperFactoryBean.getApplicationContext().getBean(Executor.class).execute(annotation.sql());
        } catch (Exception e) {
            log.error("error while executing select sql....");
            e.printStackTrace();
        }
        if (validSelectRes(res)){
            Map<String, String> fieldNameMap = parseSelectRes(res);
            return assembleObject(responseType,fieldNameMap);
        }else{
            log.error("invalid response for select sql");
            throw new RuntimeException("invalid response for select sql");
        }
    }

    /**
     * 验证MYDB的返回是否合法
     * @param res
     * @return
     */
    public boolean validSelectRes(String res){
        System.out.println(res.charAt(res.length()-1));
        return res.charAt(0)=='[' && res.charAt(res.length()-1)=='\n';
    }

    /**
     * 解析MYDB返回的结果
     * @param res
     * @return  key为字段名  value为值
     */
    public Map<String, String> parseSelectRes(String res){
        String strForParse = res.substring(1, res.length() - 2);
        String[] fields = strForParse.split(", ");
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        for (String field : fields) {
            String[] fieldAndValue = field.split(":", 2);
            stringStringHashMap.put(fieldAndValue[0],fieldAndValue[1]);
        }
        return stringStringHashMap;
    }

    /**
     * 组装对象
     * @param responseType    接口方法的返回类型
     * @param fieldValueMap   从MYDB返回中解析到的字段和值信息
     * @return
     */
    public Object assembleObject(Class responseType,Map<String,String> fieldValueMap)  {
        List<Object> values=new LinkedList();
        List<Field> fieldClass=new LinkedList<>();
        //找到返回对象的class的field以及value，各自填充到list里
        for (Field field : responseType.getDeclaredFields()) {
            fieldClass.add(field);
            for (Map.Entry<String, String> stringStringEntry : fieldValueMap.entrySet()) {
                if (stringStringEntry.getKey().equals(field.getName())){
                    values.add(stringStringEntry.getValue());
                    break;
                }
            }
        }
        int fields= fieldClass.size();
        //创建空的返回对象
        Object o = null;
        try {
            o = responseType.newInstance();
        } catch (InstantiationException e) {
            log.error("unnable to create instance :"+responseType.getClass().getSimpleName());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            log.error("unnable to create instance :"+responseType.getClass().getSimpleName());
            e.printStackTrace();
        }
        //通过反射获得各个字段的set方法并执行
        for (int i=0;i<fieldClass.size();i++) {
            try {
                responseType.getMethod("set"+fieldClass.get(i).getName()
                        .replace(fieldClass.get(i).getName().charAt(0),
                                String.valueOf(fieldClass.get(i).getName().charAt(0)).toUpperCase().charAt(0)),fieldClass.get(i).getType())
                .invoke(o,values.get(i));
            } catch (IllegalAccessException e) {
                log.error("unnable to get and invoke set method:illegal access");
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                log.error("unnable to get and invoke set method:invocation target exception");
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                log.error("unnable to get and invoke set method:no set method found for field: "+fieldClass.get(i).getName());
                e.printStackTrace();
            }
        }
        return o;
    }
}
