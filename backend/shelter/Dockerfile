# ./Dockerfile
# Esto lo he hecho modificando cosas de la guida de la hardened image de temurin: 
# https://hub.docker.com/hardened-images/catalog/dhi/eclipse-temurin/guides, las versiones las tuve qeu buscar en images, para la version 21 de java que es la que uso

# a partir de la imagen del jdk vamos a montar el jar
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /build

# copiamos el pom (instrucciones para maven), la carpeta de .mvn que es donde estan los modulos maven, copiamos el wrapper mvnw
# luego hacemos una descarga de las dependencias, copiamos el src qeu contiene nuesta app, y montamos el paquete 
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw clean package -DskipTests


# a partir de la imagen del runtime de java, copiamos el jar que hicimos con el jdk, copiamos la carpeta de uploads con las imagenes que 
# ya tenemos en la app
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar
COPY uploads  /app/uploads

# Exponemos el puerto
EXPOSE 8080

# comando que ejecuta el contenedor al arrancar
CMD ["java", "-jar", "app.jar"]
