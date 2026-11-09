@echo off
cd /d "%~dp0"
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0compilar.ps1" -Testar
if errorlevel 1 (
    pause
    exit /b 1
)
exit /b 0
