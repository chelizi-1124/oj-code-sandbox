package com.kalika.security;

import java.security.Permission;

/**
 * @Author 倪彤
 * @Date 2024/1/4 21:49
 * @Version 1.0
 */
public class MySecurityManager extends SecurityManager {

//    //监测所有权限
    @Override
    public void checkPermission(Permission perm) {
//        super.checkPermission(perm);
    }
    //监测程序是否可执行文件
    @Override
    public void checkExec(String cmd) {
        throw new SecurityException("权限异常:" + cmd);
    }

//    //检测程序是否允许读文件
//    @Override
//    public void checkRead(String file) {
//        throw new SecurityException("权限异常:" + file);
//    }
//
//    //检测程序是否允许写文件
//    @Override
//    public void checkWrite(String file) {
//        throw new SecurityException("权限异常:" + file);
//    }
//
//
//    //检测程序是否允许删除文件
//    @Override
//    public void checkDelete(String file) {
//        throw new SecurityException("权限异常:" + file);
//    }
//
//    //检测程序是否允许连接网络
//    @Override
//    public void checkConnect(String host, int port) {
//        throw new SecurityException("权限异常:" + host+":"+port);
//    }
}
