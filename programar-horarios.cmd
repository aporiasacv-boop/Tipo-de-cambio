@echo off
setlocal EnableExtensions
cd /d "%~dp0"

set DIR=%~dp0
if "%DIR:~-1%"=="\" set DIR=%DIR:~0,-1%
set TASK=Olnatura-TipoCambio

echo.
echo Crea tarea programada 5 veces al dia (hora local de la PC):
echo   00:01  07:00  13:01  15:00  18:00
echo Alternativa a dejar Vigilar-24-7.cmd abierto.
echo Solo inserta fechas nuevas de Banxico que falten en Dynamics.
echo Carpeta: %DIR%
echo.
echo Requiere CMD como ADMINISTRADOR.
echo Te pedira la contraseña de Windows de: %USERNAME%
echo.

schtasks /Delete /TN "%TASK%" /F >nul 2>&1
schtasks /Delete /TN "Olnatura-TipoCambio-1301" /F >nul 2>&1
schtasks /Delete /TN "Olnatura-TipoCambio-1201" /F >nul 2>&1
schtasks /Delete /TN "Olnatura-TipoCambio-SF60653" /F >nul 2>&1
schtasks /Delete /TN "Olnatura-TipoCambio-SF60653-1230" /F >nul 2>&1

schtasks /Create /TN "%TASK%" /TR "cmd.exe /c \"cd /d \"%DIR%\" ^&^& ejecutar.cmd\"" /SC DAILY /ST 00:01 /RL HIGHEST /F
if errorlevel 1 goto :error

schtasks /Change /TN "%TASK%" /RU "%USERNAME%" /RP *
if errorlevel 1 (
  echo.
  echo ADVERTENCIA: configura a mano en taskschd.msc si falla la contraseña.
)

powershell -NoProfile -Command ^
  "$name = '%TASK%';" ^
  "$dir = '%DIR%';" ^
  "$t = Get-ScheduledTask -TaskName $name -ErrorAction SilentlyContinue;" ^
  "if ($t) {" ^
  "  $s = $t.Settings;" ^
  "  $s.DisallowStartIfOnBatteries = $false;" ^
  "  $s.StopIfGoingOnBatteries = $false;" ^
  "  $s.StartWhenAvailable = $true;" ^
  "  $s.WakeToRun = $true;" ^
  "  $s.AllowStartOnDemand = $true;" ^
  "  $s.MultipleInstances = 'IgnoreNew';" ^
  "  $s.ExecutionTimeLimit = 'PT2H';" ^
  "  $triggers = @(" ^
  "    (New-ScheduledTaskTrigger -Daily -At '00:01')," ^
  "    (New-ScheduledTaskTrigger -Daily -At '07:00')," ^
  "    (New-ScheduledTaskTrigger -Daily -At '13:01')," ^
  "    (New-ScheduledTaskTrigger -Daily -At '15:00')," ^
  "    (New-ScheduledTaskTrigger -Daily -At '18:00')" ^
  "  );" ^
  "  $a = $t.Actions[0];" ^
  "  $a.WorkingDirectory = $dir;" ^
  "  Set-ScheduledTask -TaskName $name -Action $a -Trigger $triggers -Settings $s | Out-Null" ^
  "}"

echo.
echo Tarea creada:
schtasks /Query /TN "%TASK%" /FO LIST | findstr /I "Nombre Tarea Hora Proxima"
echo.
echo Horarios: 00:01, 07:00, 13:01, 15:00, 18:00
echo Prueba ahora:
echo   schtasks /Run /TN "%TASK%"
echo Luego revisa: %DIR%\logs\tipo-cambio-AAAAMMDD.log
exit /b 0

:error
echo ERROR al crear la tarea.
exit /b 1
