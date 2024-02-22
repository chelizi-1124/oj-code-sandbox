package com.kalika.security;

import cn.hutool.core.io.FileUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Author 倪彤
 * @Date 2024/1/4 22:03
 * @Version 1.0
 */
public class TestSecurityManager {
    public static void main(String[] args) {
       System.setSecurityManager(new MySecurityManager());
        List<String> strings = FileUtil.readLines("E:\\OJ\\oj-code-sandbox\\src\\main\\resources\\application.yml", StandardCharsets.UTF_8);
        System.out.println(strings);
    }

}
