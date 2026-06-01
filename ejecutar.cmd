@echo off
cd /d "%~dp0"

echo Solo INSERTA tipos de cambio USD/MXN (Banxico SF60653). No borra registros.
echo Define BANXICO_TOKEN, DYNAMICS_BASE_URL, DYNAMICS_TENANT_ID,
echo DYNAMICS_CLIENT_ID y DYNAMICS_CLIENT_SECRET, luego: ejecutar.cmd
echo.

if "%BANXICO_TOKEN%"=="" (
  echo ERROR: Falta BANXICO_TOKEN.
  exit /b 1
)
if "%DYNAMICS_BASE_URL%"=="" (
  echo ERROR: Falta DYNAMICS_BASE_URL.
  exit /b 1
)
if "%DYNAMICS_TENANT_ID%"=="" (
  echo ERROR: Falta DYNAMICS_TENANT_ID.
  exit /b 1
)
if "%DYNAMICS_CLIENT_ID%"=="" (
  echo ERROR: Falta DYNAMICS_CLIENT_ID.
  exit /b 1
)
if "%DYNAMICS_CLIENT_SECRET%"=="" (
  echo ERROR: Falta DYNAMICS_CLIENT_SECRET.
  exit /b 1
)

call mvnw.cmd -q spring-boot:run
set EXIT_CODE=%ERRORLEVEL%
pause
exit /b %EXIT_CODE%
