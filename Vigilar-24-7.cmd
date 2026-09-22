@echo off
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"
title Tipo de cambio - Vigilar 24/7

echo.
echo === VIGILAR 24/7 ===
echo Ejecuta el tipo de cambio cada dia a las 13:01 (hora Mexico).
echo USD y EUR: Banxico publica el euro cerca de la 1 pm.
echo Carpeta: %CD%
echo Log diario: logs\tipo-cambio-AAAAMMDD.log
echo.
echo Deja esta ventana abierta. Ctrl+C para detener.
echo.

if not exist "src\main\resources\application-local.yml" goto instalar
if exist "actualizar-tipo-cambio.jar" goto listo
if exist "target\actualizar-tipo-cambio.jar" goto listo

:instalar
echo Falta instalacion. Se ejecutara ahora...
echo.
set SILENCIOSO=1
call "%~dp0Instalar.cmd"
if errorlevel 1 (
  pause
  exit /b 1
)

:listo
set ULTIMA_EJECUCION=

:loop
set AHORA=
for /f "usebackq delims=" %%i in (`powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0hora-mexico.ps1"`) do set AHORA=%%i

set FECHA_HOY=
set HORA_MX=
for /f "tokens=1,2 delims= " %%a in ("!AHORA!") do (
  set FECHA_HOY=%%a
  set HORA_MX=%%b
)

if not "!HORA_MX!"=="13:01" goto esperar
if "!ULTIMA_EJECUCION!"=="!FECHA_HOY!" goto esperar

echo !AHORA! - iniciando actualizacion...
call ejecutar.cmd
set ULTIMA_EJECUCION=!FECHA_HOY!
echo Esperando al siguiente dia...
timeout /t 90 /nobreak >nul

:esperar
timeout /t 20 /nobreak >nul
goto loop
