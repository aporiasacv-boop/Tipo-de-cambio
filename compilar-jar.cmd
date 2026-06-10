@echo off
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"

if not exist "mvnw.cmd" (
  echo.
  echo ERROR: No estas en la carpeta del proyecto.
  echo Carpeta actual: %CD%
  exit /b 1
)

if not exist "src\main\resources\application-local.yml" (
  echo.
  echo ERROR: Faltan credenciales.
  echo Archivo que falta:
  echo   %CD%\src\main\resources\application-local.yml
  echo.
  echo Solucion: ejecuta 1-Instalar.cmd o copia el ejemplo:
  echo   copy src\main\resources\application-local.yml.example src\main\resources\application-local.yml
  exit /b 1
)

echo.
echo Compilando JAR... puede tardar 1-2 minutos la primera vez.
echo Carpeta: %CD%
echo.

call mvnw.cmd package -DskipTests
if errorlevel 1 (
  echo.
  echo ERROR DE COMPILACION. Lee los mensajes de Maven arriba.
  exit /b 1
)

if not exist "target\actualizar-tipo-cambio.jar" (
  echo.
  echo ERROR: Maven termino pero no creo target\actualizar-tipo-cambio.jar
  exit /b 1
)

copy /Y "target\actualizar-tipo-cambio.jar" "actualizar-tipo-cambio.jar" >nul
for %%F in ("actualizar-tipo-cambio.jar") do set JAR_SIZE=%%~zF
echo.
echo OK: actualizar-tipo-cambio.jar creado ^(!JAR_SIZE! bytes^)
exit /b 0
