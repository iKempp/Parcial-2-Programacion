@echo off
chcp 65001 > nul
title PixelRent - Sistema de Gestion de Alquiler de Equipos Audiovisuales
cls
echo ======================================================================
echo           PIXELRENT - SISTEMA DE GESTION DE ALQUILER
echo                  PROGRAMACION I - UQ
echo ======================================================================
echo.

REM Intentar ejecutar desde target/classes o compilar si no existe
if not exist "target\classes\co\edu\uniquindio\pixelrent\app\Main.class" (
    echo [*] Compilando codigo fuente con javac...
    if not exist "target\classes" mkdir target\classes
    javac -encoding UTF-8 -d target\classes src\main\java\co\edu\uniquindio\pixelrent\model\*.java src\main\java\co\edu\uniquindio\pixelrent\exception\*.java src\main\java\co\edu\uniquindio\pixelrent\service\*.java src\main\java\co\edu\uniquindio\pixelrent\util\*.java src\main\java\co\edu\uniquindio\pixelrent\app\*.java
    if %errorlevel% neq 0 (
        echo [!] Error de compilacion.
        pause
        exit /b %errorlevel%
    )
    echo [+] Compilacion exitosa.
    echo.
)

java -cp "target\classes" co.edu.uniquindio.pixelrent.app.Main
if %errorlevel% neq 0 (
    echo.
    echo [!] Si 'java' no esta en el PATH del sistema, intentando con Temurin 17...
    "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot\bin\java.exe" -cp "target\classes" co.edu.uniquindio.pixelrent.app.Main
)
pause
