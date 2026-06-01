@echo off
cd /d "%~dp0"
echo Sube UN solo commit a qr-enterprise/Tipo-de-cambio (rama empresa-main).
echo Requiere que el repo exista en la org qr-enterprise y tengas acceso.
echo.
git push enterprise empresa-main:main --force
pause
