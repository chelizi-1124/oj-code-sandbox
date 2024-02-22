package com.kalika.security;

import java.security.Permission;

/**
 * @Author 倪彤
 * @Date 2024/1/4 21:26
 * @Version 1.0
 */

/**
 * 默认权限管路器
 */
public class DefaultSecurityManager extends SecurityManager {

    @Override
    public void checkPermission(Permission perm) {
        System.out.println("默认不做任何权限检查");
        System.out.println(perm.toString());
//        super.checkPermission(perm);
    }


}
