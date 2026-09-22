@echo off
setlocal EnableExtensions
cd /d "%~dp0"
title Tipo de cambio - Ejecutar ahora

echo.
echo === EJECUTAR AHORA ===
echo Carpeta: %CD%
echo.

call :asegurar_listo
if errorlevel 1 (
  pause
  exit /b 1
)

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

:asegurar_listo
if not exist "src\main\resources\application-local.yml" goto instalar
if exist "actualizar-tipo-cambio.jar" exit /b 0
if exist "target\actualizar-tipo-cambio.jar" exit /b 0
:instalar
echo Falta instalacion. Se ejecutara ahora...
echo.
set SILENCIOSO=1
call "%~dp0Instalar.cmd"
exit /b %ERRORLEVEL%
