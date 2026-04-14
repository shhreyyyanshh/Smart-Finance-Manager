@echo off
setlocal

cd /d "%~dp0"

set /p FINANCE_API_BASE_URL=Enter deployed backend URL (example: https://finance-manager-backend.onrender.com): 

if "%FINANCE_API_BASE_URL%"=="" (
    echo Backend URL is required.
    pause
    exit /b 1
)

if not exist "FinanceManagerClient.java" (
    echo FinanceManagerClient.java was not found.
    pause
    exit /b 1
)

javac FinanceManagerClient.java
if errorlevel 1 (
    echo Failed to compile FinanceManagerClient.java
    pause
    exit /b 1
)

echo Using backend: %FINANCE_API_BASE_URL%
java FinanceManagerClient

pause
