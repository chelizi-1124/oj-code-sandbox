package com.kalika.security;

import java.security.Permission;

/**
 * @Author 倪彤
 * @Date 2024/1/4 21:48
 * @Version 1.0
 * 禁用所有权限安全管理器
 */
public class DenySecurityManger extends SecurityManager {
    //检查所有权限
    @Override
    public void checkPermission(Permission perm) {
        throw new SecurityException("权限异常:" + perm.toString());
    }

}
