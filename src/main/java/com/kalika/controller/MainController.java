package com.kalika.controller;

import com.kalika.JavaDockerCodeSandbox;
import com.kalika.JavaNactiveCodeSandboxOld;
import com.kalika.JavaNativeCodeSandbox;
import com.kalika.model.ExecuteCodeRequest;
import com.kalika.model.ExecuteCodeResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author 倪彤
 * @Date 2024/1/1 11:45
 * @Version 1.0
 */
@RestController("/")
public class MainController {

    //定义鉴权请求头和密钥
    private static  final String AUTH_REQUEST_HEADER = "auth";
    private  static  final String AUTH_REQUEST_SECRET = "secretKey";

    @Resource
    private JavaDockerCodeSandbox javaDockerCodeSandbox;

    @RequestMapping("/health")
    public String healthCheck() {
        return "ok";
    }

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    @PostMapping("/executeCode")
    ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader(AUTH_REQUEST_HEADER);
        if (!authHeader.equals(AUTH_REQUEST_SECRET)){
            response.setStatus(401);
            return null;
        }


        if (executeCodeRequest == null) {
            throw new RuntimeException("请求参数为空");
        }
        return javaDockerCodeSandbox.executeCode(executeCodeRequest);
    }
}
