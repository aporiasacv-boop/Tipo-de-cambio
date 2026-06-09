@echo off
setlocal EnableExtensions
cd /d "%~dp0"

set REPO_URL=https://github.com/olnaturaqr-suite/Tipo-de-cambio.git
set DESTINO=C:\Olnatura\TipoCambio

echo.
echo === Instalacion servidor 24/7 - Tipo de cambio ===
echo.

where git >nul 2>&1
if errorlevel 1 (
  echo Instalando Git...
  winget install --id Git.Git -e --accept-source-agreements --accept-package-agreements
  if errorlevel 1 (
    echo ERROR: instala Git manualmente desde https://git-scm.com
    exit /b 1
  )
)

where java >nul 2>&1
if errorlevel 1 (
  echo Instalando Java 21 (Temurin)...
  winget install --id EclipseAdoptium.Temurin.21.JDK -e --accept-source-agreements --accept-package-agreements
  if errorlevel 1 (
    echo ERROR: instala Java 21 manualmente desde https://adoptium.net
    exit /b 1
  )
)

if /I not "%CD%"=="%DESTINO%" (
  if not exist "%DESTINO%" mkdir "%DESTINO%"
  if not exist "%DESTINO%\.git" (
    echo Clonando repositorio en %DESTINO% ...
    git clone "%REPO_URL%" "%DESTINO%"
    if errorlevel 1 exit /b 1
  ) else (
    echo Actualizando repositorio en %DESTINO% ...
    pushd "%DESTINO%"
    git pull
    popd
  )
  echo.
  echo Continua la instalacion desde: %DESTINO%
  cd /d "%DESTINO%"
)

if not exist "src\main\resources\application-local.yml" (
  echo.
  echo IMPORTANTE: crea credenciales antes de compilar.
  copy /Y "src\main\resources\application-local.yml.example" "src\main\resources\application-local.yml"
  echo Edita src\main\resources\application-local.yml con token Banxico y Dynamics.
  echo Luego vuelve a ejecutar este script.
  notepad "src\main\resources\application-local.yml"
  exit /b 1
)

echo Compilando JAR...
call mvnw.cmd -q package -DskipTests
if errorlevel 1 (
  echo ERROR en compilacion.
  exit /b 1
)

copy /Y "target\actualizar-tipo-cambio.jar" "actualizar-tipo-cambio.jar" >nul
echo JAR listo: actualizar-tipo-cambio.jar

echo.
echo Prueba manual:
echo   ejecutar.cmd
echo.
echo Para dejarlo corriendo 24/7 (recomendado por direccion):
echo   vigilar-1201.cmd
echo.
echo Alternativa con tarea de Windows a las 12:01:
echo   programar-1201.cmd   (como administrador)
echo.
exit /b 0
