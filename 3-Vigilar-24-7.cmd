@echo off
setlocal EnableExtensions
cd /d "%~dp0"
title Tipo de cambio - Vigilar 24/7

echo.
echo === 3 - VIGILAR 24/7 ===
echo.

if not exist "actualizar-tipo-cambio.jar" (
  if not exist "target\actualizar-tipo-cambio.jar" (
    echo ERROR: No hay JAR compilado.
    echo.
    echo Solucion: doble clic en "1-Instalar.cmd" primero.
    echo.
    pause
    exit /b 1
  )
)

call "%~dp0vigilar-1201.cmd"
