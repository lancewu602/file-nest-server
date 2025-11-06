FROM bellsoft/liberica-openjdk-debian:17

# 设置工作目录
WORKDIR /app

# 复制打包好的 JAR 文件
COPY target/*.jar app.jar

# 暴露应用端口
EXPOSE 8080

# 启动命令（运行 JAR 包）
CMD ["java", "-jar", "-Dspring.profiles.active=online", "app.jar"]