# STAGE DE BUILD
# Utilise une image Maven avec un JDK pour compiler ton application
FROM maven:3.9.9-openjdk-17 AS build

# Définit le répertoire de travail dans le conteneur
WORKDIR /app

# Copie le fichier pom.xml et les dépendances pour les télécharger d'abord
# Cela permet au cache Docker de fonctionner plus efficacement si seuls les fichiers sources changent
COPY pom.xml .
COPY src ./src

# Exécute la commande Maven pour compiler et empaqueter l'application
# -DskipTests: pour sauter les tests pendant le build Docker (tu devrais les faire avant)
RUN mvn clean package -DskipTests

# STAGE D'EXÉCUTION
# Utilise une image JDK minimale pour l'exécution (plus légère)
FROM openjdk:17-jdk-slim

# Définit le répertoire de travail
WORKDIR /app

# Copie le fichier JAR généré à partir du stage de build
# Assure-toi que le nom du JAR ici correspond au nom réel de ton fichier JAR
# Exemple : stationapp-0.0.1-SNAPSHOT.jar (vérifie ton pom.xml)
COPY --from=build /stationapp/target/stationapp-0.0.1-SNAPSHOT.jar app.jar

# Expose le port sur lequel ton application Spring Boot écoute (généralement 8080)
EXPOSE 8080

# Commande pour démarrer l'application Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]