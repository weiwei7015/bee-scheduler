package com.bee.scheduler.admin;


import com.alibaba.fastjson.JSON;

public class SimpleTest {
    public static void main(String[] args) throws Exception {
        System.out.println("SimpleTest.main");


        System.out.println(JSON.parseObject("{a:'hello'//test}"));
    }
}
