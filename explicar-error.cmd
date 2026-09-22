@echo off
setlocal EnableExtensions
set CODE=%~1

if "%CODE%"=="1" (
  echo Causa: Falta actualizar-tipo-cambio.jar
  echo Solucion: Doble clic en Instalar.cmd
  goto fin
)
if "%CODE%"=="2" (
  echo Causa: Java 21 no instalado o no en PATH
  echo Solucion: winget install EclipseAdoptium.Temurin.21.JDK
  goto fin
)
if "%CODE%"=="3" (
  echo Causa: JAR corrupto o incompleto
  echo Solucion: Vuelve a ejecutar Instalar.cmd
  goto fin
)
if "%CODE%"=="10" (
  echo Causa: Credenciales faltantes o incorrectas
  echo Solucion: Edita src\main\resources\application-local.yml y recompila con Instalar.cmd
  goto fin
)
if "%CODE%"=="11" (
  echo Causa: Error con Banxico ^(token, red o series SF60653/SF46410^)
  goto fin
)
if "%CODE%"=="12" (
  echo Causa: Error con Dynamics ^(conexion, permisos^)
  goto fin
)
if "%CODE%"=="13" (
  echo Causa: Algunas fechas no se insertaron - revisa el log
  goto fin
)
echo Causa: Codigo %CODE% - revisa logs\tipo-cambio-AAAAMMDD.log

:fin
echo Log: carpeta logs\ del proyecto
