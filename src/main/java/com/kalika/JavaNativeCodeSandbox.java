package com.kalika;

import com.kalika.model.ExecuteCodeRequest;
import com.kalika.model.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

/**
 * @Author 倪彤
 * @Date 2024/1/16 12:37
 * @Version 1.0
 */

/**
 * java 原生代码沙箱实现（直接复用模板方法）
 */

@Component
public class JavaNativeCodeSandbox extends JavaCodeSandboxTemplate {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return super.executeCode(executeCodeRequest);
    }
}
