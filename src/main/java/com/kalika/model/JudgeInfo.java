package com.kalika.model;

import lombok.Data;

/**
 * @Author 倪彤
 * @Date 2023/12/20 22:44
 * @Version 1.0
 * 判题信息
 */
@Data
public class JudgeInfo {

    /**
     * 程序执行信息
     */
    private String message;

    /**
     * 消耗内存
     */

    private Long memory;

    /**
     * 消耗时间
     */

    private Long time;

}
