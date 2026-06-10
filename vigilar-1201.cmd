@echo off
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"

echo.
echo === Tipo de cambio - vigilancia 24/7 ===
echo Ejecuta actualizar-tipo-cambio cada dia a las 12:01 (hora Mexico).
echo Carpeta: %CD%
echo Log diario: logs\tipo-cambio-AAAAMMDD.log
echo.
echo Deja esta ventana abierta. Ctrl+C para detener.
echo.

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

if not "!HORA_MX!"=="12:01" goto esperar
if "!ULTIMA_EJECUCION!"=="!FECHA_HOY!" goto esperar

echo !AHORA! - iniciando actualizacion...
call ejecutar.cmd
set ULTIMA_EJECUCION=!FECHA_HOY!
echo Esperando al siguiente dia...
timeout /t 90 /nobreak >nul

:esperar
timeout /t 20 /nobreak >nul
goto loop
