# Loan Origination System - Service Startup Script

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  Loan Origination System - Startup" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Check if infrastructure is running
Write-Host "Checking infrastructure..." -ForegroundColor Yellow
$containers = docker ps --format "{{.Names}}" | Where-Object { $_ -like "los-*" }
if ($containers.Count -lt 4) {
    Write-Host "ERROR: Infrastructure not running!" -ForegroundColor Red
    Write-Host "Please run first: docker-compose up -d postgres kafka zookeeper keycloak" -ForegroundColor Yellow
    exit 1
}

Write-Host "✓ Infrastructure is running`n" -ForegroundColor Green

# Function to start a service
function Start-Service {
    param(
        [string]$ServiceName,
        [string]$ServicePath,
        [int]$Port
    )
    
    Write-Host "Starting $ServiceName (port $Port)..." -ForegroundColor Cyan
    $jarFile = Join-Path $ServicePath "target\$ServiceName-1.0.0.jar"
    
    if (!(Test-Path $jarFile)) {
        Write-Host "  ERROR: JAR file not found: $jarFile" -ForegroundColor Red
        return $false
    }
    
    Start-Process java -ArgumentList "-jar", $jarFile -WindowStyle Hidden
    Start-Sleep -Seconds 3
    Write-Host "  ✓ $ServiceName started`n" -ForegroundColor Green
    return $true
}

# Start all services
$basePath = "d:\projek\Loan Origination & Disbursement System"

Start-Service -ServiceName "api-gateway" -ServicePath "$basePath\api-gateway" -Port 8080
Start-Service -ServiceName "customer-service" -ServicePath "$basePath\customer-service" -Port 8081
Start-Service -ServiceName "loan-service" -ServicePath "$basePath\loan-service" -Port 8082
Start-Service -ServiceName "credit-engine-service" -ServicePath "$basePath\credit-engine-service" -Port 8083
Start-Service -ServiceName "notification-service" -ServicePath "$basePath\notification-service" -Port 8084

Write-Host "`n========================================" -ForegroundColor Green
Write-Host "  All Services Started!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

Write-Host "`nServices:" -ForegroundColor Cyan
Write-Host "  - API Gateway:        http://localhost:8080" -ForegroundColor White
Write-Host "  - Customer Service:   http://localhost:8081" -ForegroundColor White
Write-Host "  - Loan Service:       http://localhost:8082" -ForegroundColor White
Write-Host "  - Credit Engine:      http://localhost:8083" -ForegroundColor White
Write-Host "  - Notification:       http://localhost:8084" -ForegroundColor White

Write-Host "`nInfrastructure:" -ForegroundColor Cyan
Write-Host "  - PostgreSQL:         localhost:5432" -ForegroundColor White
Write-Host "  - Kafka:              localhost:9092" -ForegroundColor White
Write-Host "  - Keycloak:           http://localhost:8080" -ForegroundColor White

Write-Host "`nWait ~30 seconds for all services to be fully ready..." -ForegroundColor Yellow
Write-Host "Then test with: curl http://localhost:8080/actuator/health`n" -ForegroundColor Yellow
