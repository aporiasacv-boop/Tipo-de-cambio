@echo off
cd /d "%~dp0"
call construir-jar.cmd
if errorlevel 1 exit /b 1

set DEST=%~dp0servidor
if not exist "%DEST%" mkdir "%DEST%"

copy /Y "target\actualizar-tipo-cambio.jar" "%DEST%\actualizar-tipo-cambio.jar"
copy /Y "ejecutar-produccion.cmd" "%DEST%\"
copy /Y "programar-tarea-0400.cmd" "%DEST%\"
copy /Y "configurar-variables.cmd.ejemplo" "%DEST%\"

echo.
echo Carpeta lista para copiar al servidor:
echo %DEST%
dir "%DEST%"
exit /b 0
