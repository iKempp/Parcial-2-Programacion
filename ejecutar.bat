@echo off
chcp 65001 > nul
title DevPlus Software - Sistema de Gestion de Proyectos
cls
echo ======================================================================
echo             INICIANDO SISTEMA DEVPLUS (PROGRAMACION I)
echo ======================================================================
echo.
java -jar DevPlus.jar
if %errorlevel% neq 0 (
    echo.
    echo [!] Si 'java' no esta en el PATH del sistema, intentando con Temurin 17...
    "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot\bin\java.exe" -jar DevPlus.jar
)
pause
