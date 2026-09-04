Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  TroManager - Phần mềm quản lý phòng trọ (Swing)" -ForegroundColor Green
Write-Host "  Đang khởi động ứng dụng..." -ForegroundColor Yellow
Write-Host "========================================================" -ForegroundColor Cyan
Set-Location -Path $PSScriptRoot
& java -jar ".\QuanLyPhongTro_ModernUI\QuanLyPhongTro_ModernUI.jar"