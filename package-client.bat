@echo off
setlocal

cd /d "%~dp0"

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

echo Main-Class: FinanceManagerClient> manifest.txt
jar cfm FinanceManagerClient.jar manifest.txt FinanceManagerClient.class FinanceManagerClient$TransactionRow.class
del manifest.txt

echo Created FinanceManagerClient.jar
pause
