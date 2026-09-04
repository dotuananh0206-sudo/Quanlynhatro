@echo off
title Push TroManager to GitHub
cd /d "%~dp0"
echo ========================================================
echo   Dang day ma nguon len https://github.com/dotuananh0206-sudo/Quanlyphongtro
echo ========================================================
git push -u origin main
if %ERRORLEVEL% EQU 0 (
    echo.
    echo [THANH CONG] Ma nguon da duoc push len GitHub!
) else (
    echo.
    echo [CHU Y] Vui long kiem tra dang nhap tai khoan GitHub hoac nhap Personal Access Token.
)
pause