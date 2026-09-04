@echo off
title TroManager - Quan ly phong tro (JavaFX)
cd /d "%~dp0"
call mvnw.cmd javafx:run
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Co loi khi khoi chay ung dung JavaFX! Nhan phim bat ky de thoat...
    pause >nul
)