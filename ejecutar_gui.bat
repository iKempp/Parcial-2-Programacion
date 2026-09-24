@echo off
chcp 65001 > nul
title DevPlus Software - Interfaz Grafica (GUI Swing)
cls
echo ======================================================================
echo       INICIANDO INTERFAZ GRAFICA DEVPLUS (PROGRAMACION I)
echo ======================================================================
echo.
echo Compilando e iniciando VentanaPrincipal...
java -cp "target\classes" co.edu.uniquindio.devplus.gui.VentanaPrincipal
if %errorlevel% neq 0 (
    echo.
    echo [!] Probando con ruta especifica de Temurin 17...
    "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot\bin\java.exe" -cp "target\classes" co.edu.uniquindio.devplus.gui.VentanaPrincipal
)
