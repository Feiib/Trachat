# ===========================
# 🏗 构建阶段：使用 Maven + JDK
# ===========================
FROM maven:3.9-amazoncorretto-21 AS builder
WORKDIR /app

# 先复制 pom.xml 方便缓存依赖
COPY pom.xml .
RUN mvn -B dependency:go-offline

# 再复制源代码
COPY src ./src

# 构建 jar 包（跳过测试，加快构建）
RUN mvn -T 1C clean package -DskipTests


# ===========================
#  运行阶段：JRE + Node.js 22
# ===========================
FROM eclipse-temurin:21-jre

WORKDIR /app

# 安装 Node.js 22 + npm
RUN apt-get update && apt-get install -y curl ca-certificates gnupg \
    && curl -fsSL https://deb.nodesource.com/setup_22.x | bash - \
    && apt-get install -y nodejs \
    && npm install -g npm@latest \
    && node -v && npm -v \
    && rm -rf /var/lib/apt/lists/*

# 拷贝构建产物
COPY --from=builder /app/target/fei-ai-agent-0.0.1-SNAPSHOT.jar ./app.jar

# 端口配置
EXPOSE 8123

# 环境变量
ENV SPRING_PROFILES_ACTIVE=prod

# 启动命令
CMD ["java", "-jar", "/app/app.jar"]
