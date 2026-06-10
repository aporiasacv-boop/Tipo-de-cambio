@echo off
setlocal EnableExtensions
cd /d "%~dp0"
title Tipo de cambio - Ejecutar ahora

echo.
echo === 2 - EJECUTAR AHORA ===
echo Carpeta: %CD%
echo.

call "%~dp0ejecutar.cmd"
set RC=%ERRORLEVEL%

echo.
if %RC%==0 (
  echo ========================================
  echo   TERMINO BIEN - codigo 0
  echo ========================================
) else (
  echo ========================================
  echo   ERROR - codigo %RC%
  echo ========================================
  call "%~dp0explicar-error.cmd" %RC%
)
echo.
pause
exit /b %RC%
