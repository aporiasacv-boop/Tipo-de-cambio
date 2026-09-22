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
echo  Horarios: 00:01  07:00  13:01  15:00  18:00  ^(Mexico^)
echo.
echo   [1] Ejecutar ahora
echo   [2] Vigilar 24/7
echo   [0] Salir
echo.
set "OPCION="
set /p "OPCION=Elige un numero y Enter: "

if "%OPCION%"=="1" call "%~dp0Ejecutar-ahora.cmd" & goto menu
if "%OPCION%"=="2" call "%~dp0Vigilar-24-7.cmd" & goto menu
if "%OPCION%"=="0" exit /b 0

echo Opcion no valida.
timeout /t 2 /nobreak >nul
goto menu
