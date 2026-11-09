@echo off
cd /d "%~dp0"
if not exist "dist\SimuladorArvores.jar" (
    call compilar.cmd
    if errorlevel 1 exit /b 1
)
java --enable-native-access=ALL-UNNAMED -jar "dist\SimuladorArvores.jar" %*
if errorlevel 1 pause
