@echo off
setlocal EnableExtensions
cd /d "%~dp0"

echo.
echo Creando accesos directos en el Escritorio...
echo.

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$dir = '%CD%';" ^
  "$desk = [Environment]::GetFolderPath('Desktop');" ^
  "$shell = New-Object -ComObject WScript.Shell;" ^
  "$items = @(" ^
  "  @{N='Tipo Cambio - Menu'; F='INICIO.cmd'}," ^
  "  @{N='Tipo Cambio - Instalar'; F='1-Instalar.cmd'}," ^
  "  @{N='Tipo Cambio - Ejecutar'; F='2-Ejecutar-ahora.cmd'}," ^
  "  @{N='Tipo Cambio - Vigilar 24-7'; F='3-Vigilar-24-7.cmd'}," ^
  "  @{N='Tipo Cambio - Ver log'; F='4-Ver-log.cmd'}" ^
  ");" ^
  "foreach ($i in $items) {" ^
  "  $s = $shell.CreateShortcut((Join-Path $desk ($i.N + '.lnk')));" ^
  "  $s.TargetPath = Join-Path $dir $i.F;" ^
  "  $s.WorkingDirectory = $dir;" ^
  "  $s.Save();" ^
  "  Write-Host ('  OK: ' + $i.N)" ^
  "}"

echo.
echo Listo. Revisa iconos en tu Escritorio.
echo.
pause
exit /b 0
