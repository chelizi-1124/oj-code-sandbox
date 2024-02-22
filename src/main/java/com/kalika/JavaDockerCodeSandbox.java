package com.kalika;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ArrayUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.kalika.model.ExecuteCodeRequest;
import com.kalika.model.ExecuteCodeResponse;
import com.kalika.model.ExecuteMessage;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author 倪彤
 * @Date 2024/1/1 12:19
 * @Version 1.0
 */

/**
 * java 代码沙箱模板方法的实现
 */

@Component
public class JavaDockerCodeSandbox extends JavaCodeSandboxTemplate {

    private static final long TIME_OUT = 5000L;
    private static final Boolean FIRST_INIT = true;

//    public static void main(String[] args) {
//        JavaDockerCodeSandbox javaNactiveCodeSandbox = new JavaDockerCodeSandbox();
//        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
//        executeCodeRequest.setInputList(Arrays.asList("1 2", "3 4"));
//        //方式一
//        String code = ResourceUtil.readStr("testCode/simpleComputeArgs/Main.java", StandardCharsets.UTF_8);
//        //方式二
////        String code = ResourceUtil.readStr("testCode/simpleCompute/Main.java", StandardCharsets.UTF_8);
//        executeCodeRequest.setCode(code);
//        executeCodeRequest.setLanguage("java");
//        ExecuteCodeResponse executeCodeResponse = javaNactiveCodeSandbox.executeCode(executeCodeRequest);
//        System.out.println(executeCodeResponse);
//    }

    /**
     * 3.创建容器，把文件复制到容器内
     *
     * @param userCodeFile
     * @param inputList
     * @return
     */
    @Override
    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        System.out.println("runFile method called. InputList size: " + inputList.size());
        //拉取镜像
        String image = "openjdk:11-jdk-slim-buster";
        if (FIRST_INIT) {
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    System.out.println("下载镜像" + item.getStatus());
                    super.onNext(item);
                }
            };
            try {
                pullImageCmd.exec(pullImageResultCallback).awaitCompletion();
            } catch (InterruptedException e) {
                System.out.println("拉取镜像异常");
                throw new RuntimeException(e);
            }
        }
        System.out.println("下载完成");
        //创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(100 * 1000 * 1000L);
        hostConfig.withMemorySwap(0L); //内存交换
        hostConfig.withCpuCount(1L);
//        hostConfig.withSecurityOpts(Arrays.asList("seccomp=安全管理配置字符串"));
        //创建容器时，可以指定文件路径（Volumn） 映射，作用把本地的文件同步到容器中，可以让容器访问
        //也可以叫容器挂在目录
        hostConfig.setBinds(new Bind(userCodeParentPath, new Volume("/app")));
        CreateContainerResponse createContainerResponse = containerCmd.withHostConfig(hostConfig).withNetworkDisabled(false) //禁用网络
                .withReadonlyRootfs(false) //限制用户不能向root根目录写文件
                .withAttachStdin(true).withAttachStderr(true).withAttachStdout(true).withTty(true).exec();
        System.out.println(createContainerResponse);
        String id = createContainerResponse.getId();
        //4.启动容器
        dockerClient.startContainerCmd(id).exec();
        //docker exec elastic_wescoff java -cp /app Main 1 3
        //执行命令获取结果
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
            System.out.println("Executing Docker command for input: " + inputArgs);
            StopWatch stopWatch = new StopWatch();
            String[] inputArgsArray = inputArgs.split(" ");
            String[] cmdArray = ArrayUtil.append(new String[]{"java", "-cp", "/app", "Main"}, inputArgsArray);
            ExecCreateCmdResponse exec = dockerClient.execCreateCmd(id).withCmd(cmdArray).withAttachStdin(true).withAttachStderr(true).withAttachStdout(true).exec();
            System.out.println("创建执行命令:" + exec.toString());
            ExecuteMessage executeMessage = new ExecuteMessage();
            final String[] message = {""}; // 初始化为一个空字符串

            final String[] errorMessage = {null};

            long time = 0L;
            //判断是否超时
            final boolean[] timeout = {true};
            String execId = exec.getId();
            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
                @Override
                public void onComplete() {
                    //如果执行完成则表示没超时
                    timeout[0] = false;
                    super.onComplete();
                }

//                @Override
//                public void onNext(Frame frame) {
//                    errorMessage[0] = null;
//                    message[0] = null;
//                    byte[] payload = frame.getPayload();
//
//                    if (payload != null && payload.length > 0) {
//                        if (StreamType.STDERR.equals(frame.getStreamType())) {
//                            errorMessage[0] = new String(payload);
//                            System.out.println("输出错误结果:" + errorMessage[0]);
//                        } else {
//                            message[0] = new String(payload);
//                            if (!message[0].isEmpty()) {
//                                message[0] = message[0].trim();
//                                System.out.println("输出结果:" + message[0]);
//                            }
//                        }
//                    }
//
//                    super.onNext(frame);
//                }

                @Override
                public void onNext(Frame frame) {
                    StreamType streamType = frame.getStreamType();
                    String output = new String(frame.getPayload()).trim();
                    if (StreamType.STDERR.equals(streamType)) {
                        errorMessage[0] = output;
                        System.out.println("输出错误结果:" + output);
                    } else if (!output.trim().isEmpty()) {
                        message[0] = output;
                        System.out.println("输出结果:" + output.trim());
                    }
                    super.onNext(frame);
                }



            };
            //获取占用内存
            final long[] maxMemory = {0L};
            StatsCmd statsCmd = dockerClient.statsCmd(id);
            ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {
                @Override
                public void onNext(Statistics statistics) {
//                    System.out.println("内存占用:" + statistics.getMemoryStats().getUsage());
                    maxMemory[0] = Math.max(statistics.getMemoryStats().getUsage(), maxMemory[0]);
                }

                @Override
                public void onStart(Closeable closeable) {


                }


                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onComplete() {

                }

                @Override
                public void close() throws IOException {

                }
            });
            statsCmd.exec(statisticsResultCallback);
            try {
                stopWatch.start();
                dockerClient.execStartCmd(execId).exec(execStartResultCallback).awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS);
                stopWatch.stop();
                time = stopWatch.getLastTaskTimeMillis();
                statsCmd.close();
            } catch (InterruptedException e) {
                System.out.println("程序执行异常");
                throw new RuntimeException(e);
            }
            executeMessage.setMessage(message[0]);
            executeMessage.setErrorMessage(errorMessage[0]);
            executeMessage.setTime(time);
            executeMessage.setMemory(maxMemory[0]);
            executeMessageList.add(executeMessage);


        }
        System.out.println(executeMessageList + "Ssssssssssss");
        return executeMessageList;
    }

}
