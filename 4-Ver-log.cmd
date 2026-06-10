@echo off
setlocal EnableExtensions
cd /d "%~dp0"
title Tipo de cambio - Ver log

for /f %%i in ('powershell -NoProfile -Command "Get-Date -Format yyyyMMdd"') do set FECHA=%%i
set LOG=logs\tipo-cambio-%FECHA%.log

echo.
echo Log de hoy: %LOG%
echo.

if not exist "%LOG%" (
  echo Aun no hay log de hoy. Ejecuta primero "2-Ejecutar-ahora.cmd".
  echo.
  pause
  exit /b 1
)

notepad "%LOG%"
exit /b 0
