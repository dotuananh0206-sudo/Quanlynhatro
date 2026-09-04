@echo off
title TroManager - Quan ly phong tro (Swing)
cd /d "%~dp0"
echo ========================================================
echo   TroManager - Phan mem quan ly phong tro (Swing)
echo   Dang khoi dong ung dung...
echo ========================================================
java -jar "QuanLyPhongTro_ModernUI\QuanLyPhongTro_ModernUI.jar"
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Co loi khi khoi chay ung dung! Nhan phim bat ky de thoat...
    pause >nul
)