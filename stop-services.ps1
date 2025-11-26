# Loan Origination System - Service Stop Script

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  Stopping All Services" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Kill Java processes
Write-Host "Stopping Spring Boot services..." -ForegroundColor Yellow
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force
Start-Sleep -Seconds 2
Write-Host "✓ All Spring Boot services stopped`n" -ForegroundColor Green

# Stop Docker containers
Write-Host "Stopping infrastructure containers..." -ForegroundColor Yellow
docker-compose down
Write-Host "`n✓ All containers stopped`n" -ForegroundColor Green

Write-Host "========================================" -ForegroundColor Green
Write-Host "  All Services Stopped!" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Green
