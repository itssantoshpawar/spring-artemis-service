@echo off
echo ========================================
echo Building and Running Docker Containers
echo ========================================

echo.
echo Step 1: Building Maven projects...
call build-all.bat

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Build failed! Cannot proceed with Docker.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo Step 2: Starting Docker Compose...
docker-compose up --build

pause
