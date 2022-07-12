package com.itheima.Demo;

import com.itheima.config.ApplicationContext;
import com.itheima.config.IOCConfig;

public class IOCTest {
    public static void main(String[] args) {
        ApplicationContext ctx = new ApplicationContext(IOCConfig.class);

        System.out.println(ctx.getBean("brandService"));
        System.out.println(ctx.getBean("brandService"));
        System.out.println(ctx.getBean("brandService"));

        System.out.println("-----------------------------");

        System.out.println(ctx.getBean("userService"));
        System.out.println(ctx.getBean("userService"));
        System.out.println(ctx.getBean("userService"));
    }
}
