package com.kalika.model;

import lombok.Data;

/**
 * @Author 倪彤
 * @Date 2024/1/1 13:59
 * @Version 1.0
 */
@Data
public class ExecuteMessage {
    private Integer exitValue;
    private String message;
    private String errorMessage;
    private Long time;
    private Long memory;
}
