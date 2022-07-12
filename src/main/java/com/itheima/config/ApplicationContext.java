package com.itheima.config;

import com.itheima.annotation.Component;
import com.itheima.annotation.ComponentScan;
import com.itheima.annotation.Scope;
import com.itheima.pojo.BeanDefinition;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {
    //创建所有的Bean，包括单例，多例
    private Map<String, BeanDefinition> allBeanMap = new HashMap<>();
    //创建所有的单例Bean
    private Map<String, Object> singletonBeanMap = new HashMap<>();

    public ApplicationContext(Class configClass) {
        //判断配置类是否有ComponentScan注解
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            //如果有，将注解中的值拿出来
            ComponentScan componentScan = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            //value = com.itheima.Demo
            String value = componentScan.value();
            //这里只能用/替换掉.不然解析resource会出问题
            String path = value.replace(".", "/");
            ClassLoader classLoader = ApplicationContext.class.getClassLoader();
            //resource = file:/D:/Idea/Code/IOCDemo/target/classes/com/itheima/Demo
            URL resource = classLoader.getResource(path);
            File file = new File(resource.getPath());
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    //D:\Idea\Code\IOCDemo\target\classes\com\itheima\Demo\BrandService.class
                    String absolutePath = f.getAbsolutePath();
                    // System.out.println(absolutePath);
                    //path1 = com\itheima\Demo\BrandService
                    String path1 = absolutePath.substring(absolutePath.indexOf("com"), absolutePath.lastIndexOf(".class"));
                    // System.out.println(path1);
                    String path2 = path1.replace("\\", ".");
                    Class clazz = null;
                    try {
                        clazz = classLoader.loadClass(path2);
                        //由于要创建对象，因此对应的类中是需要有Component注解的
                        if (clazz.isAnnotationPresent(Component.class)) {
                            //如果有的话就准备去创建对象
                            Component component = (Component) clazz.getAnnotation(Component.class);
                            String beanName = component.value();
                            if (clazz.isAnnotationPresent(Scope.class)) {
                                Scope scope = (Scope) clazz.getAnnotation(Scope.class);
                                String beanScope = scope.value();
                                BeanDefinition beanDefinition = new BeanDefinition();
                                beanDefinition.setType(clazz);
                                beanDefinition.setScope(beanScope);
                                allBeanMap.put(beanName, beanDefinition);
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //创建Bean
    private Object createBean(BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getType();
        Object o = null;
        try {
            o = clazz.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }

    //获取Bean
    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = allBeanMap.get(beanName);
        if (beanDefinition == null) {
            throw new NullPointerException();
        } else {
            String beanScope = beanDefinition.getScope();
            if ("singleton".equals(beanScope)) {
                //单例
                Object bean = singletonBeanMap.get(beanName);
                if(bean == null){
                    bean = createBean(beanDefinition);
                    singletonBeanMap.put(beanName,bean);
                }
                return bean;
            } else {
                //多例就重新创建一个Bean即可
                return createBean(beanDefinition);
            }
        }
    }
}
