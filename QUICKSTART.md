# 🚀 Quick Start Guide

## Prerequisites
- Java 17+ installed
- Maven 3.8+ installed
- Docker Desktop running

## Step 1: Build All Services

```powershell
.\build-all.ps1
```

This will build:
- ✅ API Gateway (Port 8000)
- ✅ Customer Service (Port 8081)
- ✅ Loan Service (Port 8082)
- ✅ Credit Engine Service (Port 8083)
- ✅ Notification Service (Port 8084)

## Step 2: Start Infrastructure

```powershell
docker-compose up -d postgres zookeeper kafka keycloak
```

Wait 30-60 seconds for services to be ready.

Check Keycloak is ready:
```powershell
docker-compose logs -f keycloak
```
(Press Ctrl+C to exit logs)

## Step 3: Start All Microservices

```powershell
docker-compose up -d
```

## Step 4: Verify Services

Check all services are running:
```powershell
docker-compose ps
```

Check API Gateway health:
```powershell
curl http://localhost:8000/actuator/health
```

## Step 5: Test the System

### Create a Customer

```powershell
$body = @{
    nik = "3174012345670001"
    fullName = "John Doe"
    email = "john.doe@example.com"
    phoneNumber = "08123456789"
    dateOfBirth = "1990-01-15"
    address = "Jl. Sudirman No. 123, Jakarta"
    monthlyIncome = 15000000
    occupation = "Software Engineer"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8000/api/customers" `
    -Method Post `
    -Body $body `
    -ContentType "application/json"
```

### Get Customer by ID

```powershell
Invoke-RestMethod -Uri "http://localhost:8000/api/customers/1" -Method Get
```

### Apply for Loan

```powershell
$loanBody = @{
    customerId = 1
    productId = 1
    requestedAmount = 50000000
    tenor = 24
    downPayment = 10000000
    purpose = "Pembelian Motor Honda"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8000/api/loans/apply" `
    -Method Post `
    -Body $loanBody `
    -ContentType "application/json"
```

### Check Loan Status

```powershell
Invoke-RestMethod -Uri "http://localhost:8000/api/loans/1/status" -Method Get
```

## Access Points

| Service | URL |
|---------|-----|
| API Gateway | http://localhost:8000 |
| Customer Service | http://localhost:8081 |
| Loan Service | http://localhost:8082 |
| Credit Engine | http://localhost:8083 |
| Notification Service | http://localhost:8084 |
| Keycloak Admin | http://localhost:8080 (admin/admin) |

## View Logs

```powershell
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f customer-service
docker-compose logs -f loan-service
```

## Stop Everything

```powershell
docker-compose down
```

## Troubleshooting

### PostgreSQL not ready
```powershell
docker-compose restart postgres
docker-compose logs postgres
```

### Kafka not ready
```powershell
docker-compose restart kafka
docker-compose logs kafka
```

### Service not starting
```powershell
# Rebuild specific service
cd customer-service
mvn clean package
cd ..
docker-compose up -d --build customer-service
```

## Next Steps

1. ✅ Configure Keycloak realm and clients
2. ✅ Add OAuth2 security to services
3. ✅ Implement approval workflow
4. ✅ Add PDF generation for SPK
5. ✅ Add unit tests

Happy Coding! 🎉
