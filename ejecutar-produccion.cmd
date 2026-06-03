@echo off
cd /d "%~dp0"

if exist configurar-variables.cmd call configurar-variables.cmd

if "%BANXICO_TOKEN%"=="" (
  echo ERROR: Falta BANXICO_TOKEN
  exit /b 1
)
if "%DYNAMICS_BASE_URL%"=="" (
  echo ERROR: Falta DYNAMICS_BASE_URL
  exit /b 1
)
if "%DYNAMICS_TENANT_ID%"=="" (
  echo ERROR: Falta DYNAMICS_TENANT_ID
  exit /b 1
)
if "%DYNAMICS_CLIENT_ID%"=="" (
  echo ERROR: Falta DYNAMICS_CLIENT_ID
  exit /b 1
)
if "%DYNAMICS_CLIENT_SECRET%"=="" (
  echo ERROR: Falta DYNAMICS_CLIENT_SECRET
  exit /b 1
)

set JAR=
if exist "actualizar-tipo-cambio.jar" set JAR=actualizar-tipo-cambio.jar
if "%JAR%"=="" if exist "target\actualizar-tipo-cambio.jar" set JAR=target\actualizar-tipo-cambio.jar

if "%JAR%"=="" (
  echo ERROR: No se encuentra actualizar-tipo-cambio.jar en esta carpeta ni en target\
  exit /b 1
)

if not exist "logs" mkdir logs
for /f %%i in ('powershell -NoProfile -Command "Get-Date -Format yyyyMMdd"') do set FECHA=%%i

set LOG=logs\tipo-cambio-%FECHA%.log
echo Ejecutando %JAR% ... log: %LOG%
java -jar "%JAR%" >> "%LOG%" 2>&1
set EXIT_CODE=%ERRORLEVEL%
echo Terminado codigo %EXIT_CODE%. Ver: %LOG%
exit /b %EXIT_CODE%
