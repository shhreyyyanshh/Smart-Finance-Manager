@echo off
setlocal

cd /d "%~dp0"

if "%FINANCE_API_BASE_URL%"=="" (
    set "FINANCE_API_BASE_URL=http://localhost:8080"
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
