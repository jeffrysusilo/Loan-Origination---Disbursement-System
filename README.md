# 🏦 Loan Origination & Disbursement System (Mini LOS)

> **Sistem inti multifinance dengan arsitektur microservices** - Mengelola pengajuan kredit, credit scoring, approval berjenjang, hingga pencairan dana.

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.5-black.svg)](https://kafka.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

## 🎯 Tujuan

Membuat sistem inti multifinance yang meniru pilar utama **AdIns CONFINS** dengan fitur:
- Customer Onboarding dengan validasi KTP & OCR
- Loan Application dengan berbagai produk pembiayaan
- Internal Credit Checking dengan DTI calculation
- Approval Flow berjenjang (Surveyor → Analyst → Manager)
- Disbursement Integration dengan PDF SPK

## 🌟 Fitur Utama

### 1. Customer Onboarding
- ✅ Upload KTP dan validasi NIK
- ✅ OCR sederhana (simulasi ekstraksi data)
- ✅ Verifikasi data customer

### 2. Loan Application
- ✅ Pilih produk pembiayaan (Motor, Mobil, Multiguna)
- ✅ Konfigurasi tenor, plafon, DP
- ✅ Interest calculation otomatis
- ✅ Installment calculation

### 3. Internal Credit Checking
- ✅ Debt-to-Income Ratio (DTI) calculation
- ✅ Rule engine untuk credit scoring
- ✅ Blacklist checking

### 4. Approval Flow Berjenjang
- ✅ Multi-level approval (Surveyor → Credit Analyst → Manager)
- ✅ Setiap level dapat approve/reject dengan catatan
- ✅ Audit trail lengkap

### 5. Disbursement Integration
- ✅ Generate PDF SPK (Surat Pencairan Kredit)
- ✅ Webhook simulasi untuk transfer
- ✅ Disbursement tracking

## 🏛️ Arsitektur Microservices

```
                    ┌─────────────────────┐
                    │   API Gateway       │
                    │  (Port 8000)        │
                    └──────────┬──────────┘
                               │
          ┌────────────────────┼────────────────────┐
          │                    │                    │
┌─────────▼─────────┐ ┌───────▼────────┐ ┌────────▼─────────┐
│ Customer Service  │ │  Loan Service  │ │Credit Engine Svc │
│   (Port 8081)     │ │  (Port 8082)   │ │   (Port 8083)    │
└─────────┬─────────┘ └───────┬────────┘ └────────┬─────────┘
          │                   │                    │
          └───────────────────┼────────────────────┘
                              │
                    ┌─────────▼──────────┐
                    │  Kafka Event Bus   │
                    └─────────┬──────────┘
                              │
                    ┌─────────▼──────────┐
                    │Notification Service│
                    │   (Port 8084)      │
                    └────────────────────┘
```

## 🛢️ Database Schema

### Tables
- **customers** - Data pelanggan
- **documents** - Upload KTP & dokumen lainnya
- **loan_products** - Produk pembiayaan (Motor, Mobil, Multiguna)
- **loans** - Aplikasi kredit
- **approvals** - Workflow approval berjenjang
- **credit_checks** - Hasil credit checking
- **disbursements** - Data pencairan
- **audit_logs** - Audit trail sistem

## 🔧 Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Java 17+ |
| Framework | Spring Boot 3.2 |
| Gateway | Spring Cloud Gateway |
| Security | Spring Security + Keycloak OAuth2 |
| Database | PostgreSQL 15 |
| Migration | Liquibase |
| Messaging | Apache Kafka |
| Mapping | MapStruct |
| PDF | iText / Apache PDFBox |
| Testing | JUnit 5 + Mockito |
| Container | Docker + Docker Compose |

## 📋 Prerequisites

- Java 17 atau lebih tinggi
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL Client (optional)

## 🚀 Quick Start

### 1. Clone Repository
```bash
cd "d:\projek\Loan Origination & Disbursement System"
```

### 2. Build All Services
```bash
# Build API Gateway
cd api-gateway
mvn clean package -DskipTests
cd ..

# Build Customer Service
cd customer-service
mvn clean package -DskipTests
cd ..

# Build Loan Service
cd loan-service
mvn clean package -DskipTests
cd ..

# Build Credit Engine Service
cd credit-engine-service
mvn clean package -DskipTests
cd ..

# Build Notification Service
cd notification-service
mvn clean package -DskipTests
cd ..
```

### 3. Start Infrastructure with Docker
```bash
# Start PostgreSQL, Kafka, Keycloak
docker-compose up -d postgres zookeeper kafka keycloak

# Wait for services to be ready (30-60 seconds)
docker-compose logs -f keycloak
```

### 4. Start Microservices
```bash
# Start all services
docker-compose up -d

# Or start individually for development
cd customer-service && mvn spring-boot:run
cd loan-service && mvn spring-boot:run
cd credit-engine-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
```

### 5. Access Applications
- **API Gateway**: http://localhost:8000
- **Keycloak Admin**: http://localhost:8080 (admin/admin)
- **Customer Service**: http://localhost:8081
- **Loan Service**: http://localhost:8082
- **Credit Engine**: http://localhost:8083
- **Notification Service**: http://localhost:8084

## 📍 Quick Reference

### Service Ports
| Service | Port | Health Endpoint |
|---------|------|----------------|
| API Gateway | 8000 | http://localhost:8000/actuator/health |
| Customer Service | 8081 | http://localhost:8081/actuator/health |
| Loan Service | 8082 | http://localhost:8082/actuator/health |
| Credit Engine | 8083 | - |
| Notification Service | 8084 | - |
| PostgreSQL | 5432 | - |
| Kafka | 9092 | - |
| Zookeeper | 2181 | - |
| Keycloak | 8080 | http://localhost:8080 |

### Database Credentials
```
Host: localhost:5432
Database: los_db
Username: los_user
Password: los_password
```

### Default Test Data
```
Loan Products:
- ID 1: Kredit Motor (12.5%, max 36 months)
- ID 2: Kredit Mobil (10.5%, max 60 months)
- ID 3: Kredit Multiguna (15%, max 48 months)
```

### Sample API Request Flow
```powershell
# 1. Create customer → Get ID (e.g., ID=1)
# 2. Apply loan with customerId=1, productId=2
# 3. Get loan ID (e.g., ID=1)
# 4. Approve loan ID=1 with approverRole=CREDIT_ANALYST
# 5. Update status to APPROVED in DB
# 6. Disburse loan ID=1
```

## 📌 API Documentation

### Customer Service APIs

#### 1. Create Customer
```http
POST http://localhost:8000/api/customers
Content-Type: application/json

{
  "nik": "3174012345670001",
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "08123456789",
  "dateOfBirth": "1990-01-15",
  "address": "Jl. Sudirman No. 123, Jakarta",
  "monthlyIncome": 15000000,
  "occupation": "Software Engineer"
}
```

#### 2. Upload KTP
```http
POST http://localhost:8000/api/customers/{customerId}/documents
Content-Type: multipart/form-data

documentType: KTP
file: [binary file]
```

### Loan Service APIs

#### 1. Get Loan Products
```http
GET http://localhost:8000/api/loans/products
```

#### 2. Apply for Loan
```http
POST http://localhost:8000/api/loans/apply
Content-Type: application/json

{
  "customerId": 1,
  "productId": 1,
  "requestedAmount": 50000000,
  "tenor": 24,
  "downPayment": 10000000,
  "purpose": "Pembelian Motor Honda"
}
```

#### 3. Get Loan Status
```http
GET http://localhost:8000/api/loans/{loanId}/status
```

#### 4. Approve Loan
```http
POST http://localhost:8000/api/loans/{loanId}/approve
Content-Type: application/json
Authorization: Bearer {token}

{
  "approverRole": "CREDIT_ANALYST",
  "decision": "APPROVED",
  "notes": "Customer memenuhi syarat kredit"
}
```

#### 5. Reject Loan
```http
POST http://localhost:8000/api/loans/{loanId}/approve
Content-Type: application/json

{
  "approverRole": "MANAGER",
  "decision": "REJECTED",
  "notes": "DTI ratio melebihi batas maksimal"
}
```

#### 6. Disburse Loan
```http
POST http://localhost:8000/api/loans/{loanId}/disburse
Content-Type: application/json
Authorization: Bearer {token}

{
  "disbursementMethod": "BANK_TRANSFER",
  "accountNumber": "1234567890",
  "accountName": "John Doe",
  "bankCode": "BCA"
}
```

### Credit Engine APIs

#### 1. Run Credit Check
```http
POST http://localhost:8000/api/credit/check
Content-Type: application/json

{
  "customerId": 1,
  "loanId": 1,
  "requestedAmount": 50000000,
  "monthlyIncome": 15000000,
  "existingDebt": 2000000
}
```

## 🔐 Security Configuration

### Keycloak Setup

1. Access Keycloak Admin Console: http://localhost:8080
2. Login dengan `admin/admin`
3. Create Realm: `los-realm`
4. Create Client: `los-client`
5. Create Roles:
   - `CUSTOMER`
   - `SURVEYOR`
   - `CREDIT_ANALYST`
   - `MANAGER`
   - `ADMIN`
6. Create Users dan assign roles

### OAuth2 Token

```bash
# Get Access Token
curl -X POST http://localhost:8080/realms/los-realm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=los-client" \
  -d "client_secret=your-client-secret" \
  -d "username=john.doe" \
  -d "password=password" \
  -d "grant_type=password"
```

## 📊 Monitoring & Logging

### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f loan-service
```

### Kafka Topics
```bash
# List topics
docker exec -it los-kafka kafka-topics --bootstrap-server localhost:9092 --list

# View messages
docker exec -it los-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic loan-events \
  --from-beginning
```

## 🧪 Testing & Demo Commands

### Quick Health Check
```powershell
# Check all services health
@(8000, 8081, 8082, 8083, 8084) | ForEach-Object {
    try {
        $h = Invoke-RestMethod "http://localhost:$_/actuator/health" -TimeoutSec 3
        Write-Host "✓ Port $_: $($h.status)" -ForegroundColor Green
    } catch {
        Write-Host "✗ Port $_: NOT READY" -ForegroundColor Red
    }
}
```

### End-to-End Demo Workflow

#### Step 1: Create Customer
```powershell
$customer = @{
    nik = "3201012345670001"
    fullName = "John Doe"
    email = "john.doe@example.com"
    phoneNumber = "+6281234567890"
    dateOfBirth = "1990-01-15"
    address = "Jl. Sudirman No. 123, Jakarta Selatan"
    monthlyIncome = 15000000
    occupation = "Software Engineer"
} | ConvertTo-Json

$custResponse = Invoke-RestMethod -Uri "http://localhost:8081/customers" -Method POST -Body $customer -ContentType "application/json"
Write-Host "✓ Customer Created - ID: $($custResponse.id)" -ForegroundColor Green
$custResponse | Format-List
```

#### Step 2: Check Available Loan Products
```powershell
# Query database untuk melihat produk
docker exec -it los-postgres psql -U los_user -d los_db -c "SELECT id, product_name, product_type, interest_rate, max_tenor FROM loan.loan_products;"
```

#### Step 3: Apply for Loan
```powershell
$loanApp = @{
    customerId = 1  # Sesuaikan dengan ID customer yang dibuat
    productId = 2   # 1=Motor, 2=Mobil, 3=Multiguna
    requestedAmount = 250000000
    tenor = 48
    downPayment = 50000000
    purpose = "Membeli mobil Toyota Avanza untuk kebutuhan keluarga"
} | ConvertTo-Json

$loanResponse = Invoke-RestMethod -Uri "http://localhost:8082/loans/apply" -Method POST -Body $loanApp -ContentType "application/json"
Write-Host "✓ Loan Applied - ID: $($loanResponse.id) | Status: $($loanResponse.status)" -ForegroundColor Green
$loanResponse | Format-List id, status, requestedAmount, monthlyInstallment, totalPayment
```

#### Step 4: Approve Loan (Multi-Level)
```powershell
$approval = @{
    approverRole = "CREDIT_ANALYST"  # Bisa: SURVEYOR, CREDIT_ANALYST, MANAGER
    decision = "APPROVED"             # Bisa: APPROVED, REJECTED
    notes = "Approved - Excellent credit profile and stable income"
    approverName = "John Analyst"
} | ConvertTo-Json

$approveResponse = Invoke-RestMethod -Uri "http://localhost:8082/loans/1/approve" -Method POST -Body $approval -ContentType "application/json"
Write-Host "✓ Loan Approved!" -ForegroundColor Green
$approveResponse | Format-List id, status, approvedAmount, approvedTenor
```

#### Step 5: Check Loan Status
```powershell
# Get loan details
$loan = Invoke-RestMethod -Uri "http://localhost:8082/loans/1" -Method GET
$loan | Format-List id, customerId, productName, status, requestedAmount, approvedAmount, monthlyInstallment

# Get customer loans
$customerLoans = Invoke-RestMethod -Uri "http://localhost:8082/loans/customer/1" -Method GET
$customerLoans | Format-Table id, productName, status, requestedAmount
```

#### Step 6: Disburse Loan (After Approval)
```powershell
# Update status ke APPROVED di database (jika perlu bypass Kafka)
docker exec -it los-postgres psql -U los_user -d los_db -c "UPDATE loan.loans SET status = 'APPROVED', approved_amount = 250000000 WHERE id = 1;"

# Disburse
$disburse = @{
    accountNumber = "1234567890"
    accountName = "John Doe"
    bankCode = "BCA"
    disbursementMethod = "BANK_TRANSFER"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8082/loans/1/disburse" -Method POST -Body $disburse -ContentType "application/json"
Write-Host "✓ Loan Disbursed!" -ForegroundColor Green
```

### Database Queries for Verification

```powershell
# Check customers
docker exec -it los-postgres psql -U los_user -d los_db -c "SELECT id, nik, full_name, email, monthly_income, occupation FROM customer.customers;"

# Check loans
docker exec -it los-postgres psql -U los_user -d los_db -c "SELECT id, customer_id, product_id, requested_amount, approved_amount, status, monthly_installment FROM loan.loans;"

# Check approvals
docker exec -it los-postgres psql -U los_user -d los_db -c "SELECT id, loan_id, approver_role, decision, notes FROM loan.approvals;"

# Check disbursements
docker exec -it los-postgres psql -U los_user -d los_db -c "SELECT id, loan_id, amount, account_number, bank_code, status FROM loan.disbursements;"
```

### Unit & Integration Tests
```bash
# Run all tests
mvn test

# Run specific service tests
cd customer-service && mvn test
cd loan-service && mvn test
```

## 📁 Project Structure

```
loan-origination-system/
├── api-gateway/               # Spring Cloud Gateway
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── customer-service/          # Customer management
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── loan-service/              # Loan application & approval
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── credit-engine-service/     # Credit scoring & checking
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── notification-service/      # Async notifications
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── docker-compose.yml
├── init-db.sql
└── README.md
```

## ⭐ Kenapa Relevan untuk AdIns?

1. **CONFINS Core Module**: Sistem ini meniru modul utama CONFINS (Core Finance System) yang digunakan AdIns
2. **Microservices Architecture**: Align dengan digital modernization multi-finance modern
3. **Event-Driven**: Menggunakan Kafka untuk async processing seperti sistem enterprise
4. **Multi-Level Approval**: Workflow approval yang kompleks seperti di dunia nyata
5. **Security**: OAuth2/OIDC dengan Keycloak untuk enterprise-grade security
6. **Audit Trail**: Complete logging untuk compliance requirements

## 🔄 Development Workflow

### Loan Application Flow
1. Customer register → Customer Service
2. Upload KTP → Document validation & OCR
3. Apply loan → Loan Service
4. Credit check → Credit Engine Service
5. Approval flow → Multi-level approval (Surveyor → Analyst → Manager)
6. Disburse → Generate SPK PDF & webhook transfer
7. Notifications → Kafka events untuk setiap step

## 🐛 Troubleshooting

### Common Issues & Solutions

#### PostgreSQL Connection Issues
```bash
# Check PostgreSQL logs
docker-compose logs postgres

# Restart PostgreSQL
docker-compose restart postgres

# Verify connection
docker exec -it los-postgres psql -U los_user -d los_db -c "\conninfo"
```

#### Kafka Not Ready
```bash
# Check Kafka logs
docker-compose logs kafka

# Wait until you see "Started Successfully"
docker-compose logs kafka | Select-String "started"

# Verify Kafka topics
docker exec -it los-kafka kafka-topics --bootstrap-server kafka:9092 --list
```

#### Keycloak Not Starting
```bash
docker-compose logs keycloak
docker-compose restart keycloak
```

#### Services Can't Connect to Kafka
```powershell
# Check if services are using correct Kafka hostname (kafka:9092, not localhost:9092)
docker exec los-loan-service printenv | Select-String "SPRING_PROFILES_ACTIVE"
# Should show: SPRING_PROFILES_ACTIVE=docker

# Check Kafka connection from loan service
docker logs los-loan-service 2>&1 | Select-String "kafka|Kafka" | Select-Object -Last 10
```

#### Rebuild & Restart Everything (Clean Slate)
```powershell
# Stop all containers
docker-compose down

# Remove all JARs (force clean)
Get-ChildItem -Recurse -Filter "*.jar" -Exclude "wrapper" | Remove-Item -Force

# Rebuild all services
@("api-gateway", "customer-service", "loan-service", "credit-engine-service", "notification-service") | ForEach-Object {
    cd $_
    mvn clean package -DskipTests
    cd ..
}

# Rebuild and start all containers
docker-compose up -d --build

# Wait for startup (60-90 seconds)
Start-Sleep -Seconds 70

# Verify all services
docker-compose ps
```

### Docker Management Commands

```powershell
# View all containers status
docker-compose ps

# View logs for all services
docker-compose logs -f

# View logs for specific service
docker-compose logs -f loan-service

# Restart specific service
docker-compose restart customer-service

# Stop all services
docker-compose down

# Stop and remove volumes (complete reset)
docker-compose down -v

# Rebuild specific service
docker-compose up -d --build loan-service

# Check resource usage
docker stats
```


