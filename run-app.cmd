@echo off
setlocal

set "MAVEN_CMD=%~dp0tools\apache-maven-3.9.9\bin\mvn.cmd"

if not exist "%MAVEN_CMD%" (
    echo Local Maven was not found at:
    echo %MAVEN_CMD%
    exit /b 1
)

if "%DB_USERNAME%"=="" set "DB_USERNAME=root"
if "%DB_PASSWORD%"=="" (
    set /p DB_PASSWORD=Enter MySQL password for %DB_USERNAME% ^(press Enter to use "root"^): 
)
if "%DB_PASSWORD%"=="" set "DB_PASSWORD=root"

echo Starting Spring Boot application...
call "%MAVEN_CMD%" spring-boot:run
