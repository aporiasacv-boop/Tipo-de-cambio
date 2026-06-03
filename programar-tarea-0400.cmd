@echo off
setlocal
set DIR=%~dp0
set TASK=Olnatura-TipoCambio-SF60653

schtasks /Create /TN "%TASK%" /TR "\"%DIR%ejecutar-produccion.cmd\"" /SC DAILY /ST 04:00 /F
if errorlevel 1 exit /b 1

echo Tarea "%TASK%" creada: diaria a las 04:00
schtasks /Query /TN "%TASK%" /FO LIST /V
exit /b 0
