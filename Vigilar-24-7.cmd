@echo off
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"
title Tipo de cambio - Vigilar 24/7

echo.
echo === VIGILAR 24/7 ===
echo Ejecuta el tipo de cambio 5 veces al dia (hora Mexico):
echo   00:01  07:00  13:01  15:00  18:00
echo Solo inserta fechas nuevas de Banxico que falten en Dynamics.
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

set SLOT=
if "!HORA_MX!"=="00:01" set SLOT=00:01
if "!HORA_MX!"=="07:00" set SLOT=07:00
if "!HORA_MX!"=="13:01" set SLOT=13:01
if "!HORA_MX!"=="15:00" set SLOT=15:00
if "!HORA_MX!"=="18:00" set SLOT=18:00
if "!SLOT!"=="" goto esperar

set CLAVE=!FECHA_HOY! !SLOT!
if "!ULTIMA_EJECUCION!"=="!CLAVE!" goto esperar

echo !AHORA! - iniciando actualizacion ^(horario !SLOT!^)...
call "%~dp0ejecutar.cmd"
set ULTIMA_EJECUCION=!CLAVE!
echo Esperando al siguiente horario...
timeout /t 90 /nobreak >nul

:esperar
timeout /t 20 /nobreak >nul
goto loop
