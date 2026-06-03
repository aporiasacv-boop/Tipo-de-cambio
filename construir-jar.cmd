@echo off
cd /d "%~dp0"
call mvnw.cmd -q clean package -DskipTests
if errorlevel 1 exit /b 1
echo Listo: target\actualizar-tipo-cambio.jar
exit /b 0
