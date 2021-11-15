FROM openjdk:8-jdk
RUN mkdir /app
COPY ./build/install/snake-points/ /app/
WORKDIR /app/bin
CMD ["./snake-points"]
EXPOSE 80