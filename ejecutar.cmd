@echo off
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"

set LOG=
set FECHA=
call :preparar_log
if errorlevel 1 exit /b %DIAG_CODE%

call :buscar_jar
if errorlevel 1 exit /b %DIAG_CODE%

call :buscar_java
if errorlevel 1 exit /b %DIAG_CODE%

call :validar_jar
if errorlevel 1 exit /b %DIAG_CODE%

call :marca >> "%LOG%" 2>&1
echo Inicio carpeta=!CD! jar=!JAR! java=!JAVA_CMD! >> "%LOG%"
echo Ejecutando %JAR% ...
"%JAVA_CMD%" -version >> "%LOG%" 2>&1
if errorlevel 1 (
  call :diag 2 "Java no responde - instala Java 21 o define JAVA_HOME"
  exit /b 2
)

"%JAVA_CMD%" -jar "%JAR%" >> "%LOG%" 2>&1
set EXIT_CODE=%ERRORLEVEL%
call :interpretar_salida %EXIT_CODE%
call :marca >> "%LOG%" 2>&1
echo Fin codigo !EXIT_CODE! >> "%LOG%"
echo Terminado codigo %EXIT_CODE%. Log: %LOG%
exit /b %EXIT_CODE%

:preparar_log
if not exist "logs" mkdir logs 2>nul
if not exist "logs" (
  call :diag 4 "No se pudo crear la carpeta logs"
  exit /b 4
)
for /f %%i in ('powershell -NoProfile -Command "Get-Date -Format yyyyMMdd" 2^>nul') do set FECHA=%%i
if "%FECHA%"=="" (
  call :diag 5 "No se pudo obtener la fecha para el nombre del log"
  exit /b 5
)
set LOG=logs\tipo-cambio-%FECHA%.log
exit /b 0

:buscar_jar
set JAR=
if exist "actualizar-tipo-cambio.jar" set JAR=actualizar-tipo-cambio.jar
if "%JAR%"=="" (
  call :diag 1 "No se encuentra actualizar-tipo-cambio.jar en esta carpeta"
  exit /b 1
)
exit /b 0

:buscar_java
set JAVA_CMD=
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set JAVA_CMD=%JAVA_HOME%\bin\java.exe
if "%JAVA_CMD%"=="" for %%J in (
  "C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot\bin\java.exe"
  "C:\Program Files\Eclipse Adoptium\jdk-21\bin\java.exe"
  "C:\Program Files\Java\jdk-21\bin\java.exe"
  "C:\Program Files\Microsoft\jdk-21.0.11.10-hotspot\bin\java.exe"
) do if exist %%J set JAVA_CMD=%%~J
if "%JAVA_CMD%"=="" (
  where java >nul 2>&1
  if errorlevel 1 (
    call :diag 2 "Java 21 no encontrado - instala Temurin 21 o define JAVA_HOME"
    exit /b 2
  )
  set JAVA_CMD=java
)
exit /b 0

:validar_jar
set JAR_SIZE=
for %%F in ("%JAR%") do set JAR_SIZE=%%~zF
if not defined JAR_SIZE (
  call :diag 3 "No se pudo leer el tamano del JAR"
  exit /b 3
)
if !JAR_SIZE! LSS 5000000 (
  call :diag 3 "JAR muy pequeno o corrupto (!JAR_SIZE! bytes) - vuelve a copiar el archivo"
  exit /b 3
)
exit /b 0

:interpretar_salida
set EXIT_CODE=%~1
if "%EXIT_CODE%"=="0" (
  call :diag_ok 0 "Ejecucion correcta"
  exit /b 0
)
if "%EXIT_CODE%"=="10" (
  call :diag 10 "Credenciales faltantes - revisa application-local.yml dentro del JAR"
  exit /b 10
)
if "%EXIT_CODE%"=="11" (
  call :diag 11 "Error Banxico - token, red o serie SF60653"
  exit /b 11
)
if "%EXIT_CODE%"=="12" (
  call :diag 12 "Error Dynamics - conexion, permisos o OData"
  exit /b 12
)
if "%EXIT_CODE%"=="13" (
  call :diag 13 "Proceso incompleto - algunas fechas no se insertaron"
  exit /b 13
)
if "%EXIT_CODE%"=="99" (
  call :diag 99 "Error inesperado en el JAR - ver lineas ERROR arriba en el log"
  exit /b 99
)
call :diag 6 "El JAR termino con codigo %EXIT_CODE% - busca CODIGO_SALIDA en el log"
exit /b %EXIT_CODE%

:marca
for /f "usebackq delims=" %%t in (`powershell -NoProfile -Command "Get-Date -Format 'yyyy-MM-dd HH:mm:ss'"`) do echo [%%t]
exit /b 0

:diag
set DIAG_CODE=%~1
set DIAG_MSG=%~2
call :marca >> "%LOG%" 2>&1
echo [DIAG] ERROR !DIAG_CODE!: !DIAG_MSG! >> "%LOG%"
echo [DIAG] ERROR !DIAG_CODE!: !DIAG_MSG!
exit /b 0

:diag_ok
set DIAG_CODE=%~1
set DIAG_MSG=%~2
call :marca >> "%LOG%" 2>&1
echo [DIAG] OK !DIAG_CODE!: !DIAG_MSG! >> "%LOG%"
exit /b 0
