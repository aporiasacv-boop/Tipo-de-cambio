@echo off
cd /d "%~dp0"

set JAR=
if exist "actualizar-tipo-cambio.jar" set JAR=actualizar-tipo-cambio.jar
if "%JAR%"=="" if exist "target\actualizar-tipo-cambio.jar" set JAR=target\actualizar-tipo-cambio.jar

if "%JAR%"=="" (
  echo Compilando JAR...
  call mvnw.cmd -q package -DskipTests
  if errorlevel 1 exit /b 1
  set JAR=target\actualizar-tipo-cambio.jar
)

if not exist "logs" mkdir logs
for /f %%i in ('powershell -NoProfile -Command "Get-Date -Format yyyyMMdd"') do set FECHA=%%i
set LOG=logs\tipo-cambio-%FECHA%.log

set JAVA_CMD=java
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set JAVA_CMD=%JAVA_HOME%\bin\java.exe

echo Ejecutando %JAR% ...
echo [%date% %time%] Inicio >> "%LOG%"
"%JAVA_CMD%" -jar "%JAR%" >> "%LOG%" 2>&1
set EXIT_CODE=%ERRORLEVEL%
echo [%date% %time%] Fin codigo %EXIT_CODE% >> "%LOG%"
echo Terminado codigo %EXIT_CODE%. Log: %LOG%
exit /b %EXIT_CODE%
