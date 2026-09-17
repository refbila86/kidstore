@echo off

cd /d "%~dp0"

start "" javaw -jar "sales-management-0.0.1-SNAPSHOT.jar"

timeout /t 5 /nobreak >nul

start "" "http://localhost:8085"
