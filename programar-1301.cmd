@echo off
setlocal EnableExtensions
cd /d "%~dp0"

set TASK_1301=Olnatura-TipoCambio-1301
set DIR=%~dp0
if "%DIR:~-1%"=="\" set DIR=%DIR:~0,-1%

echo.
echo Crea tarea programada diaria a las 13:01 (USD y EUR ya publicados en Banxico)
echo Carpeta: %DIR%
echo.
echo Requiere CMD como ADMINISTRADOR.
echo Te pedira la contraseña de Windows de: %USERNAME%
echo.

schtasks /Delete /TN "%TASK_1301%" /F >nul 2>&1
schtasks /Delete /TN "Olnatura-TipoCambio-1201" /F >nul 2>&1
schtasks /Delete /TN "Olnatura-TipoCambio-SF60653" /F >nul 2>&1
schtasks /Delete /TN "Olnatura-TipoCambio-SF60653-1230" /F >nul 2>&1

schtasks /Create /TN "%TASK_1301%" /TR "cmd.exe /c \"cd /d \"%DIR%\" ^&^& ejecutar.cmd\"" /SC DAILY /ST 13:01 /RL HIGHEST /F
if errorlevel 1 goto :error

schtasks /Change /TN "%TASK_1301%" /RU "%USERNAME%" /RP *
if errorlevel 1 (
  echo.
  echo ADVERTENCIA: configura a mano en taskschd.msc si falla la contraseña.
)

powershell -NoProfile -Command ^
  "$t = Get-ScheduledTask -TaskName '%TASK_1301%' -ErrorAction SilentlyContinue; if ($t) { $s = $t.Settings; $s.DisallowStartIfOnBatteries = $false; $s.StopIfGoingOnBatteries = $false; $s.StartWhenAvailable = $true; $s.WakeToRun = $true; $s.AllowStartOnDemand = $true; $s.MultipleInstances = 'StopExisting'; $s.ExecutionTimeLimit = 'PT2H'; Set-ScheduledTask -TaskName '%TASK_1301%' -Settings $s | Out-Null; $a = $t.Actions[0]; $a.WorkingDirectory = '%DIR%'; Set-ScheduledTask -TaskName '%TASK_1301%' -Action $a | Out-Null }"

echo.
echo Tarea creada:
schtasks /Query /TN "%TASK_1301%" /FO LIST | findstr /I "Nombre Tarea Hora Proxima"
echo.
echo Prueba ahora:
echo   schtasks /Run /TN "%TASK_1301%"
echo Luego revisa: %DIR%\logs\tipo-cambio-AAAAMMDD.log
exit /b 0

:error
echo ERROR al crear la tarea.
exit /b 1
