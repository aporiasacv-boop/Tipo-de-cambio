@echo off
setlocal EnableExtensions
cd /d "%~dp0"
title Tipo de cambio - Instalar

echo.
echo === 1 - INSTALAR Y COMPILAR ===
echo Carpeta: %CD%
echo.

where java >nul 2>&1
if errorlevel 1 (
  echo ERROR: Java 21 no encontrado.
  echo Instala con: winget install EclipseAdoptium.Temurin.21.JDK
  echo.
  goto fin_error
)

if not exist "src\main\resources\application-local.yml" (
  echo Creando archivo de credenciales...
  copy /Y "src\main\resources\application-local.yml.example" "src\main\resources\application-local.yml"
  echo.
  echo Se abrira el bloc de notas. Pega token Banxico y datos Dynamics.
  echo Guarda el archivo y cierra el bloc de notas.
  echo.
  notepad "src\main\resources\application-local.yml"
)

call "%~dp0compilar-jar.cmd"
if errorlevel 1 goto fin_error

echo.
echo ========================================
echo   INSTALACION OK
echo ========================================
echo Siguiente paso: doble clic en "2-Ejecutar-ahora.cmd"
echo.
pause
exit /b 0

:fin_error
echo.
echo ========================================
echo   INSTALACION FALLIDA - revisa arriba
echo ========================================
echo.
pause
exit /b 1
