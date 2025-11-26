# Loan Origination System - Architecture Documentation

## System Overview

Mini LOS (Loan Origination System) adalah sistem multifinance yang mengelola seluruh lifecycle pengajuan kredit, dari customer onboarding hingga pencairan dana.

## Microservices Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Client Applications                       │
│                    (Web, Mobile, Third-Party)                    │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      API Gateway (Port 8000)                     │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ • Route Management                                        │  │
│  │ • Load Balancing                                          │  │
│  │ • Circuit Breaker (Resilience4j)                          │  │
│  │ • Request Logging & Correlation ID                        │  │
│  │ • CORS Configuration                                      │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────┘
                             │
          ┌──────────────────┼──────────────────┐
          │                  │                  │
          ▼                  ▼                  ▼
┌──────────────────┐ ┌──────────────┐ ┌──────────────────┐
│ Customer Service │ │ Loan Service │ │ Credit Engine    │
│   (Port 8081)    │ │ (Port 8082)  │ │   (Port 8083)    │
└──────────────────┘ └──────────────┘ └──────────────────┘
          │                  │                  │
          └──────────────────┼──────────────────┘
                             │
                    ┌────────▼─────────┐
                    │   Kafka Broker   │
                    │   (Port 9092)    │
                    └────────┬─────────┘
                             │
                    ┌────────▼──────────┐
                    │  Notification Svc │
                    │   (Port 8084)     │
                    └───────────────────┘
                             
┌─────────────────────────────────────────────────────────────────┐
│                     Infrastructure Layer                         │
├─────────────────────────────────────────────────────────────────┤
│  PostgreSQL DB  │  Zookeeper  │  Keycloak OAuth  │  File Store │
│  (Port 5432)    │  (Port 2181)│  (Port 8080)     │             │
└─────────────────────────────────────────────────────────────────┘
```

## Service Responsibilities

### 1. API Gateway
**Technology**: Spring Cloud Gateway
**Purpose**: Single entry point for all client requests

**Responsibilities**:
- Route requests to appropriate microservices
- Implement circuit breaker pattern
- Request/Response logging with correlation ID
- CORS handling
- Load balancing (in production with multiple instances)

**Key Features**:
- Circuit breaker with Resilience4j
- Fallback mechanisms
- Gateway filters for cross-cutting concerns

### 2. Customer Service
**Technology**: Spring Boot + PostgreSQL + Kafka Producer
**Purpose**: Customer lifecycle management

**Responsibilities**:
- Customer onboarding (registration)
- KTP upload and validation
- OCR simulation for KTP data extraction
- Customer verification
- Blacklist management
- Customer data CRUD operations

**Database Tables**:
- `customer.customers` - Customer master data
- `customer.documents` - Uploaded documents

**Events Published**:
- `CUSTOMER_CREATED`
- `CUSTOMER_UPDATED`
- `CUSTOMER_VERIFIED`
- `CUSTOMER_BLACKLISTED`

**Key APIs**:
- `POST /customers` - Create customer
- `GET /customers/{id}` - Get customer
- `POST /customers/{id}/verify` - Verify customer
- `POST /customers/{id}/documents` - Upload KTP

### 3. Loan Service
**Technology**: Spring Boot + PostgreSQL + Kafka + iText PDF
**Purpose**: Loan application and approval workflow

**Responsibilities**:
- Loan application processing
- Loan product management
- Multi-level approval workflow:
  - Surveyor approval
  - Credit Analyst approval
  - Manager approval
- Interest calculation
- Installment calculation
- Disbursement processing
- SPK (Surat Pencairan Kredit) PDF generation

**Database Tables**:
- `loan.loan_products` - Product catalog (Motor, Mobil, Multiguna)
- `loan.loans` - Loan applications
- `loan.approvals` - Approval workflow tracking
- `loan.disbursements` - Disbursement records

**Events Published**:
- `LOAN_APPLIED`
- `LOAN_APPROVED`
- `LOAN_REJECTED`
- `LOAN_DISBURSED`

**Key APIs**:
- `POST /loans/apply` - Submit loan application
- `GET /loans/{id}/status` - Get loan status
- `POST /loans/{id}/approve` - Approve/Reject loan
- `POST /loans/{id}/disburse` - Disburse loan
- `GET /loans/{id}/spk/download` - Download SPK PDF

### 4. Credit Engine Service
**Technology**: Spring Boot + PostgreSQL
**Purpose**: Credit scoring and risk assessment

**Responsibilities**:
- Debt-to-Income (DTI) ratio calculation
- Credit score calculation (simulated rule engine)
- Blacklist checking
- Risk assessment
- Auto-decision for low-risk cases

**Database Tables**:
- `credit.credit_checks` - Credit check results
- `credit.blacklist` - Blacklisted customers

**Calculation Logic**:
```
DTI Ratio = (Existing Debt + Monthly Installment) / Monthly Income
Credit Score = f(DTI, Age, Income, Employment)

Decision Rules:
- DTI > 40% → Auto Reject
- DTI 30-40% → Manual Review
- DTI < 30% → Auto Approve (low amounts)
```

**Key APIs**:
- `POST /credit/check` - Run credit check
- `GET /credit/check/{id}` - Get credit check result

### 5. Notification Service
**Technology**: Spring Boot + Kafka Consumer
**Purpose**: Async event processing and notifications

**Responsibilities**:
- Listen to all events from Kafka topics
- Send notifications (simulated):
  - Email notifications
  - SMS notifications
  - Push notifications
- Event logging

**Kafka Topics Consumed**:
- `customer-events`
- `loan-events`
- `disbursement-events`

## Data Flow Examples

### Loan Application Flow

```
1. Customer applies for loan
   POST /api/loans/apply
   ↓
2. Loan Service validates customer exists
   ↓
3. Loan Service triggers Credit Check
   POST /api/credit/check
   ↓
4. Credit Engine calculates DTI & Score
   Returns: APPROVED / REJECTED / REVIEW
   ↓
5. If REVIEW required:
   a. Surveyor reviews → Approve/Reject
   b. Credit Analyst reviews → Approve/Reject
   c. Manager final approval → Approve/Reject
   ↓
6. If all approved:
   POST /api/loans/{id}/disburse
   ↓
7. Generate SPK PDF
   ↓
8. Simulate bank transfer (webhook)
   ↓
9. Publish LOAN_DISBURSED event
   ↓
10. Notification Service sends confirmation
```

### Customer Onboarding Flow

```
1. Create customer
   POST /api/customers
   ↓
2. Upload KTP
   POST /api/customers/{id}/documents
   ↓
3. OCR Service extracts KTP data
   ↓
4. Validate extracted data
   ↓
5. Manual verification
   POST /api/customers/{id}/verify
   ↓
6. Customer status → ACTIVE
   ↓
7. Publish CUSTOMER_VERIFIED event
```

## Technology Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.2** - Application framework
- **Spring Cloud Gateway** - API Gateway
- **Spring Data JPA** - Data access layer
- **Liquibase** - Database migration
- **MapStruct** - Object mapping

### Database
- **PostgreSQL 15** - Primary database
- **Schema-based multi-tenancy**:
  - `customer` schema
  - `loan` schema
  - `credit` schema
  - `audit` schema

### Messaging
- **Apache Kafka** - Event streaming
- **Zookeeper** - Kafka coordination

### Security
- **Keycloak** - OAuth2/OIDC authentication
- **Spring Security** - Authorization

### PDF Generation
- **iText 5** - SPK PDF generation

### Containerization
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration

## Database Schema

### Customer Schema
```sql
customer.customers
  - id (PK)
  - nik (UNIQUE)
  - full_name
  - email (UNIQUE)
  - phone_number
  - date_of_birth
  - address
  - monthly_income
  - occupation
  - status (ACTIVE, INACTIVE, BLACKLISTED, PENDING_VERIFICATION)
  - is_verified
  - created_at
  - updated_at

customer.documents
  - id (PK)
  - customer_id (FK)
  - document_type (KTP, NPWP, etc.)
  - file_name
  - file_path
  - ocr_data (JSON)
  - status (UPLOADED, VERIFIED, REJECTED)
  - uploaded_at
```

### Loan Schema
```sql
loan.loan_products
  - id (PK)
  - product_name (Motor, Mobil, Multiguna)
  - min_amount
  - max_amount
  - min_tenor
  - max_tenor
  - interest_rate
  - is_active

loan.loans
  - id (PK)
  - customer_id (FK)
  - product_id (FK)
  - requested_amount
  - approved_amount
  - tenor
  - down_payment
  - interest_rate
  - monthly_installment
  - total_payment
  - purpose
  - status (PENDING, APPROVED, REJECTED, DISBURSED)
  - created_at

loan.approvals
  - id (PK)
  - loan_id (FK)
  - approver_level (SURVEYOR, CREDIT_ANALYST, MANAGER)
  - approver_name
  - decision (APPROVED, REJECTED, PENDING)
  - notes
  - approved_at

loan.disbursements
  - id (PK)
  - loan_id (FK)
  - amount
  - disbursement_method
  - account_number
  - account_name
  - bank_code
  - spk_file_path
  - status (PENDING, COMPLETED, FAILED)
  - disbursed_at
```

### Credit Schema
```sql
credit.credit_checks
  - id (PK)
  - customer_id (FK)
  - loan_id (FK)
  - monthly_income
  - existing_debt
  - requested_amount
  - dti_ratio
  - credit_score
  - decision (APPROVED, REJECTED, REVIEW)
  - checked_at
```

## Event-Driven Architecture

### Kafka Topics

1. **customer-events**
   - CUSTOMER_CREATED
   - CUSTOMER_UPDATED
   - CUSTOMER_VERIFIED
   - CUSTOMER_BLACKLISTED

2. **loan-events**
   - LOAN_APPLIED
   - LOAN_APPROVED
   - LOAN_REJECTED
   - APPROVAL_RECEIVED

3. **disbursement-events**
   - DISBURSEMENT_REQUESTED
   - DISBURSEMENT_COMPLETED
   - DISBURSEMENT_FAILED

### Event Schema Example
```json
{
  "eventId": "uuid",
  "eventType": "LOAN_APPROVED",
  "timestamp": "2024-01-15T10:30:00Z",
  "payload": {
    "loanId": 123,
    "customerId": 456,
    "amount": 50000000,
    "tenor": 24
  },
  "metadata": {
    "source": "loan-service",
    "version": "1.0"
  }
}
```

## Security Architecture

### Authentication Flow (Keycloak)
```
1. User requests token from Keycloak
   POST /realms/los-realm/protocol/openid-connect/token
   
2. Keycloak validates credentials
   
3. Keycloak returns JWT access token
   
4. Client includes token in API requests
   Authorization: Bearer <token>
   
5. API Gateway validates token
   
6. Request forwarded to microservice
   
7. Microservice extracts user context from token
```

### Roles
- **CUSTOMER** - End user
- **SURVEYOR** - Field verification
- **CREDIT_ANALYST** - Credit analysis
- **MANAGER** - Final approval
- **ADMIN** - System administration

## Performance Considerations

### Scalability
- **Horizontal Scaling**: Each microservice can scale independently
- **Database Connection Pooling**: HikariCP
- **Kafka Partitioning**: For high-throughput event processing

### Resilience
- **Circuit Breaker**: Resilience4j in API Gateway
- **Retry Mechanism**: For external service calls
- **Timeout Configuration**: Prevent cascading failures

### Monitoring
- **Spring Boot Actuator**: Health checks, metrics
- **Distributed Tracing**: Correlation ID in logs
- **Kafka Consumer Lag Monitoring**: Ensure event processing

## Deployment

### Local Development
```powershell
# Build all services
.\build-all.ps1

# Start infrastructure
docker-compose up -d postgres kafka zookeeper keycloak

# Start services
docker-compose up -d
```

### Production Considerations
- Use Kubernetes for orchestration
- Implement API rate limiting
- Add Redis for caching
- Implement proper secrets management
- Setup monitoring (Prometheus + Grafana)
- Setup centralized logging (ELK stack)

## Testing Strategy

### Unit Tests
- Service layer logic
- Calculation accuracy (interest, installment)
- Business rules validation

### Integration Tests
- API endpoint testing
- Database operations
- Kafka event publishing/consuming

### E2E Tests
- Complete loan application flow
- Approval workflow
- Disbursement process

## Future Enhancements

1. **Real OCR Integration**: Google Vision API / AWS Textract
2. **Real Payment Gateway**: Integration with banks
3. **Mobile App**: Customer mobile application
4. **Analytics Dashboard**: Loan performance metrics
5. **ML-based Credit Scoring**: Replace rule engine
6. **Real-time Notifications**: WebSocket for instant updates
7. **Document E-signing**: Digital signature for contracts

---

**Version**: 1.0.0  
**Last Updated**: November 2024  
**Author**: LOS Development Team
