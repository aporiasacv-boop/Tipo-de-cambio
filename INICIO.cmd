@echo off
setlocal EnableExtensions
cd /d "%~dp0"
title Tipo de cambio - Menu

:menu
cls
echo.
echo  ============================================
echo       TIPO DE CAMBIO - OL NATURA
echo  ============================================
echo  Carpeta: %CD%
echo.
echo   [1] Instalar y compilar  ^(primera vez^)
echo   [2] Ejecutar ahora       ^(una vez^)
echo   [3] Vigilar 24/7         ^(12:01 diario^)
echo   [4] Ver log de hoy
echo   [5] Crear botones en Escritorio
echo   [0] Salir
echo.
set "OPCION="
set /p "OPCION=Elige un numero y Enter: "

if "%OPCION%"=="1" call "%~dp01-Instalar.cmd" & goto menu
if "%OPCION%"=="2" call "%~dp02-Ejecutar-ahora.cmd" & goto menu
if "%OPCION%"=="3" call "%~dp03-Vigilar-24-7.cmd" & goto menu
if "%OPCION%"=="4" call "%~dp04-Ver-log.cmd" & goto menu
if "%OPCION%"=="5" call "%~dp05-Crear-botones-escritorio.cmd" & goto menu
if "%OPCION%"=="0" exit /b 0

echo Opcion no valida.
timeout /t 2 /nobreak >nul
goto menu
