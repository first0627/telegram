FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

# gradle wrapper 실행 권한 부여
RUN chmod +x ./gradlew

# 빌드 수행
RUN ./gradlew build --no-daemon

# 최종 JAR 경로 확인 후 실행
CMD ["java", "-jar", "build/libs/tel-0.0.1-SNAPSHOT.jar"]