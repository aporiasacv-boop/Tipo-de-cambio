@echo off
setlocal EnableExtensions
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
for /f "usebackq delims=" %%i in (`powershell -NoProfile -Command ^
  "$z = [TimeZoneInfo]::FindSystemTimeZoneById('Central Standard Time (Mexico)'); ^
   $n = [TimeZoneInfo]::ConvertTimeFromUtc((Get-Date).ToUniversalTime(), $z); ^
   Write-Output ($n.ToString('yyyy-MM-dd') + ' ' + $n.ToString('HH:mm'))"`) do set AHORA=%%i

for /f "tokens=1,2 delims= " %%a in ("%AHORA%") do (
  set FECHA_HOY=%%a
  set HORA_MX=%%b
)

if "%HORA_MX%"=="12:01" (
  if not "%ULTIMA_EJECUCION%"=="%FECHA_HOY%" (
    echo [%date% %time%] Hora Mexico %AHORA% - iniciando actualizacion...
    call ejecutar.cmd
    set ULTIMA_EJECUCION=%FECHA_HOY%
    echo [%date% %time%] Esperando al siguiente dia...
    timeout /t 90 /nobreak >nul
  )
)

timeout /t 20 /nobreak >nul
goto loop
