@echo off
chcp 65001 > nul
title PixelRent - Sistema de Gestion de Alquiler de Equipos Audiovisuales
cls
echo ======================================================================
echo           PIXELRENT - SISTEMA DE GESTION DE ALQUILER
echo                  PROGRAMACION I - UQ
echo ======================================================================
echo.

REM Si no existe el archivo JAR ejecutable, compilarlo automaticamente
if not exist "PixelRent.jar" (
    echo [*] Compilando codigo fuente y empaquetando PixelRent.jar...
    if not exist "target\classes" mkdir "target\classes"
    powershell -NoProfile -ExecutionPolicy Bypass -Command "javac -encoding UTF-8 -d target/classes (Get-ChildItem -Recurse -Filter *.java src/main/java).FullName; if ($LASTEXITCODE -eq 0) { jar --create --file PixelRent.jar --main-class co.edu.uniquindio.pixelrent.app.Main -C target/classes . }"
    if %errorlevel% neq 0 (
        echo [!] Error de compilacion.
        pause
        exit /b %errorlevel%
    )
    echo [+] Compilacion y empaquetado exitoso.
    echo.
)

REM Ejecutar la aplicacion
java -jar PixelRent.jar
if %errorlevel% neq 0 (
    echo.
    echo [!] Si 'java' no esta en el PATH del sistema, intentando con Temurin 17...
    "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot\bin\java.exe" -jar PixelRent.jar
)

echo.
pause
