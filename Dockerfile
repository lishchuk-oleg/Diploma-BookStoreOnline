ARG BUILD_IMAGE=public.ecr.aws/docker/library/maven:3.8-amazoncorretto-17
ARG RUNTIME_IMAGE=public.ecr.aws/amazoncorretto/amazoncorretto:17-al2-full

###STEP 1
#build jar

FROM ${BUILD_IMAGE} as builder

ENV TEMP_DIR /tmp/

#PREPARE SRC TO BUILD
COPY pom.xml $TEMP_DIR
COPY src /tmp/src/
WORKDIR $TEMP_DIR

#Build
RUN mvn package

###STEP 2
#build image
FROM ${RUNTIME_IMAGE}
COPY --from=builder /tmp/target/*.jar /app/application.jar

WORKDIR /app

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "application.jar" ]



