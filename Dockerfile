# Используйте образ с JDK (Java Development Kit)
FROM openjdk:11-jre-slim

# Установите рабочую директорию
WORKDIR /app

# Скопируйте JAR-файл с вашим приложением
COPY build/libs/ваше-приложение.jar .

# Задайте команду для запуска приложения
CMD ["java", "-jar", "ваше-приложение.jar"]
