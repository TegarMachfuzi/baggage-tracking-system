@echo off
echo ==========================================
echo   Baggage Tracking System - Setup
echo ==========================================
echo.

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo X Docker is not running. Please start Docker first.
    exit /b 1
)

echo √ Docker is running
echo.

REM Start infrastructure
echo Starting infrastructure services...
docker-compose up -d

echo.
echo Waiting for services to be ready...
timeout /t 10 /nobreak >nul

REM Check services
echo.
echo Checking services status...
docker-compose ps

echo.
echo ==========================================
echo   Building lib-common
echo ==========================================
echo.

cd lib-common
call mvn clean install -DskipTests

if errorlevel 1 (
    echo.
    echo X Failed to build lib-common
    exit /b 1
)

echo.
echo √ lib-common installed successfully!
echo    Location: %USERPROFILE%\.m2\repository\com\baggage\lib-common\

cd ..

echo.
echo ==========================================
echo   Setup Complete!
echo ==========================================
echo.
echo Infrastructure services running:
echo   • PostgreSQL  : localhost:5432
echo   • Kafka       : localhost:9092
echo   • Zookeeper   : localhost:2181
echo   • Redis       : localhost:6379
echo.
echo lib-common installed to local Maven repository
echo.
echo Next steps:
echo   1. cd baggage-service
echo   2. mvn spring-boot:run
echo.
