@echo off
cd /d "%~dp0"

if exist configurar-variables.cmd call configurar-variables.cmd

if "%BANXICO_TOKEN%"=="" (
  echo Falta BANXICO_TOKEN
  exit /b 1
)
if "%DYNAMICS_BASE_URL%"=="" (
  echo Falta DYNAMICS_BASE_URL
  exit /b 1
)
if "%DYNAMICS_TENANT_ID%"=="" (
  echo Falta DYNAMICS_TENANT_ID
  exit /b 1
)
if "%DYNAMICS_CLIENT_ID%"=="" (
  echo Falta DYNAMICS_CLIENT_ID
  exit /b 1
)
if "%DYNAMICS_CLIENT_SECRET%"=="" (
  echo Falta DYNAMICS_CLIENT_SECRET
  exit /b 1
)

if exist "actualizar-tipo-cambio.jar" (
  java -jar "actualizar-tipo-cambio.jar"
  exit /b %ERRORLEVEL%
)
if exist "target\actualizar-tipo-cambio.jar" (
  java -jar "target\actualizar-tipo-cambio.jar"
  exit /b %ERRORLEVEL%
)

call mvnw.cmd -q spring-boot:run
exit /b %ERRORLEVEL%
