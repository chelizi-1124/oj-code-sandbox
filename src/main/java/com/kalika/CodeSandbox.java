package com.kalika;


import com.kalika.model.ExecuteCodeRequest;
import com.kalika.model.ExecuteCodeResponse;

/**
 * @Author 倪彤
 * @Date 2023/12/27 17:58
 * @Version 1.0
 * 代码沙箱接口定义
 */
public interface CodeSandbox {
    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
