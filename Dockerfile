# 1. Java 21 JDK가 포함된 베이스 이미지 사용
FROM eclipse-temurin:21-jdk

# 2. 앱 실행될 디렉토리 생성
WORKDIR /app

# 3. 로컬에서 빌드한 jar를 컨테이너에 복사
COPY build/libs/*.jar app.jar

# 4. 앱 실행
ENTRYPOINT ["java", "-jar", "app.jar"]