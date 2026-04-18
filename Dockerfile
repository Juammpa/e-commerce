# Elegimos imagen de Java
FROM eclipse-temurin:17-jdk-alpine

# Definimos un argumento para el nombre del archivo .jar
ARG JAR_FILE=target/e-commerce-0.0.1-SNAPSHOT.jar

# Copiamos el JAR de nuestra PC a la imagen
COPY ${JAR_FILE} app.jar

# Informamos que la API corre en el puerto 8080
EXPOSE 8080

# Comando para ejecutar la aplicacion
ENTRYPOINT ["java", "-jar", "app.jar"]



