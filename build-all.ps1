# Build all services
Write-Host "🚀 Building Loan Origination System..." -ForegroundColor Green
Write-Host ""

# Build API Gateway
Write-Host "📦 Building API Gateway..." -ForegroundColor Cyan
Set-Location "api-gateway"
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ API Gateway build failed!" -ForegroundColor Red
    exit 1
}
Set-Location ..

# Build Customer Service
Write-Host "📦 Building Customer Service..." -ForegroundColor Cyan
Set-Location "customer-service"
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Customer Service build failed!" -ForegroundColor Red
    exit 1
}
Set-Location ..

# Build Loan Service
Write-Host "📦 Building Loan Service..." -ForegroundColor Cyan
Set-Location "loan-service"
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Loan Service build failed!" -ForegroundColor Red
    exit 1
}
Set-Location ..

# Build Credit Engine Service
Write-Host "📦 Building Credit Engine Service..." -ForegroundColor Cyan
Set-Location "credit-engine-service"
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Credit Engine Service build failed!" -ForegroundColor Red
    exit 1
}
Set-Location ..

# Build Notification Service
Write-Host "📦 Building Notification Service..." -ForegroundColor Cyan
Set-Location "notification-service"
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Notification Service build failed!" -ForegroundColor Red
    exit 1
}
Set-Location ..

Write-Host ""
Write-Host "✅ All services built successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "  1. Start infrastructure: docker-compose up -d postgres kafka zookeeper keycloak" -ForegroundColor White
Write-Host "  2. Wait 30-60 seconds for services to initialize" -ForegroundColor White
Write-Host "  3. Start all services: docker-compose up -d" -ForegroundColor White
Write-Host "  4. Access API Gateway: http://localhost:8000" -ForegroundColor White
