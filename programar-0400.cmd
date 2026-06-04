@echo off
setlocal
cd /d "%~dp0"

set TASK=Olnatura-TipoCambio-SF60653

schtasks /Delete /TN "%TASK%" /F >nul 2>&1
schtasks /Create /TN "%TASK%" /TR "\"%~dp0ejecutar.cmd\"" /SC DAILY /ST 04:00 /F
if errorlevel 1 (
  echo ERROR: no se pudo crear la tarea. Ejecuta este CMD como administrador.
  exit /b 1
)

echo Tarea "%TASK%" creada: todos los dias a las 04:00
echo Accion: %~dp0ejecutar.cmd
echo.
schtasks /Query /TN "%TASK%" /FO LIST | findstr /I "Nombre Tarea Hora Proxima Modo Estado"
echo.
echo Si dice "Solo interactivo", en taskschd.msc marca "Ejecutar tanto si el usuario inicio sesion como no".
exit /b 0
