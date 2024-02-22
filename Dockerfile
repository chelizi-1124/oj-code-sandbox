# 使用包含 JDK 的基础镜像
FROM openjdk:11-jdk-slim-buster
# 指定工作目录
WORKDIR /app

# 将 jar 包添加到工作目录
ADD target/oj-code-sandbox-0.0.1-SNAPSHOT.jar .

# 授予 /app 目录读写权限
RUN chmod 777 /app

# 授予 JAR 文件执行权限
RUN chmod +x /app/oj-code-sandbox-0.0.1-SNAPSHOT.jar

# 创建一个新用户和 docker 组
RUN groupadd docker && \
    adduser --disabled-password --gecos '' myuser && \
    usermod -aG docker myuser

USER myuser
# 暴露应用程序端口（如果需要）
EXPOSE 8100

# 启动命令
ENTRYPOINT ["java","-Xms64M","-Xmx128m","-jar","/app/oj-code-sandbox-0.0.1-SNAPSHOT.jar"]

# 声明 Docker 套接字作为挂载点
VOLUME /var/run/docker.sock
