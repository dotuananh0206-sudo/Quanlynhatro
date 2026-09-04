Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  TroManager - Phần mềm quản lý phòng trọ (JavaFX)" -ForegroundColor Green
Write-Host "  Đang khởi động ứng dụng dự phòng..." -ForegroundColor Yellow
Write-Host "========================================================" -ForegroundColor Cyan
Set-Location -Path $PSScriptRoot
& ".\mvnw.cmd" javafx:run