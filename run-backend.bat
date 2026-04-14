@echo off
setlocal

set "MAVEN_CMD=%~dp0tools\apache-maven-3.9.9\bin\mvn.cmd"

if not exist "%MAVEN_CMD%" (
    echo Maven was not found at:
    echo %MAVEN_CMD%
    pause
    exit /b 1
)

cd /d "%~dp0"
call "%MAVEN_CMD%" spring-boot:run

pause
