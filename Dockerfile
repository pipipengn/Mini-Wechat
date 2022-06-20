FROM openjdk:8-jdk-alpine

COPY weixin-app.jar app.jar

EXPOSE 8080
EXPOSE 8088

CMD ["nohup","java","-jar","app.jar"]


