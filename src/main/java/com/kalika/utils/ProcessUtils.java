package com.kalika.utils;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.StrUtil;
import com.kalika.model.ExecuteMessage;

import java.io.*;

/**
 * @Author 倪彤
 * @Date 2024/1/1 13:58
 * @Version 1.0
 * 进程工具类
 */
public class ProcessUtils {
    /**
     * 执行进程并获取工具类
     *
     * @param runProcess
     * @param opName
     * @return
     */
    public static ExecuteMessage runProcessAndGetMessage(Process runProcess, String opName) {
        ExecuteMessage executeMessage = new ExecuteMessage();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            //等待程序执行，获取错误码
            int exitValue = runProcess.waitFor();
            executeMessage.setExitValue(exitValue);
            //正常退出
            if (exitValue == 0) {
                System.out.println(opName + "成功");
                //分批获取进程的输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                StringBuilder compileOutputStringBuilder = new StringBuilder();
                // 逐行读取
                String compileOutputline;
                while ((compileOutputline = bufferedReader.readLine()) != null) {
                    // 去除每行的空格
                    String trimmedLine = compileOutputline.replaceAll("\\s", ""); // 使用正则表达式去除所有空格
                    compileOutputStringBuilder.append(trimmedLine);
                }
                executeMessage.setMessage(compileOutputStringBuilder.toString());

            } else {
                //异常退出
                System.out.println(opName + "失败，错误码：" + exitValue);
                //分批获取进程的正常输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                StringBuilder compileOutputStringBuilder = new StringBuilder();
                // 逐行读取
                String compileOutputline;
                while ((compileOutputline = bufferedReader.readLine()) != null) {
                    // 去除每行的空格
                    String trimmedLine = compileOutputline.replaceAll("\\s", ""); // 使用正则表达式去除所有空格
                    compileOutputStringBuilder.append(trimmedLine);
                }
                executeMessage.setMessage(compileOutputStringBuilder.toString());

                //分批获取进程的错误输出
                BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
                StringBuilder errorCompileOutputStringBuilder = new StringBuilder();
                // 逐行读取
                String errorCompileOutputline;
                while ((errorCompileOutputline = errorBufferedReader.readLine()) != null) {
                    // 去除每行的空格
                    String trimmedLine = errorCompileOutputline.replaceAll("\\s", ""); // 使用正则表达式去除所有空格
                    errorCompileOutputStringBuilder.append(trimmedLine);
                }
                executeMessage.setErrorMessage(errorCompileOutputStringBuilder.toString());

            }
            stopWatch.stop();
            long lastTaskTimeMillis = stopWatch.getLastTaskTimeMillis();
            executeMessage.setTime(lastTaskTimeMillis);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return executeMessage;
    }

    /**
     * 执行交互式进程并获取工具类
     *
     * @param runProcess
     * @param args
     * @return
     */
    public static ExecuteMessage runInteracProcessAndGetMessage(Process runProcess, String args) {
        ExecuteMessage executeMessage = new ExecuteMessage();
        try {
            //向控制台输入程序
            OutputStream outputStream = runProcess.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            String[] s = args.split(" ");
            String join = StrUtil.join("\n", s) + "\n";
            outputStreamWriter.write(join);
            //相当于按了enter键，执行输入的发送
            outputStreamWriter.flush();
            //分批获取进程的输出
            InputStream inputStream = runProcess.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder compileOutputStringBuilder = new StringBuilder();
            //逐行读取
            String compileOutputline;
            while ((compileOutputline = bufferedReader.readLine()) != null) {
                compileOutputStringBuilder.append(compileOutputline);
            }
            executeMessage.setMessage(compileOutputStringBuilder.toString());
            outputStreamWriter.close();
            outputStream.close();
            inputStream.close();
            runProcess.destroy();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return executeMessage;
    }
}
