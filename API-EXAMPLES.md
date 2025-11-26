# API Testing Collection

## Customer Service APIs

### 1. Create Customer
```http
POST http://localhost:8000/api/customers
Content-Type: application/json

{
  "nik": "3174012345670001",
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "08123456789",
  "dateOfBirth": "1990-01-15",
  "address": "Jl. Sudirman No. 123, Jakarta Selatan",
  "monthlyIncome": 15000000,
  "occupation": "Software Engineer"
}
```

### 2. Get All Customers
```http
GET http://localhost:8000/api/customers
```

### 3. Get Customer by ID
```http
GET http://localhost:8000/api/customers/1
```

### 4. Get Customer by NIK
```http
GET http://localhost:8000/api/customers/nik/3174012345670001
```

### 5. Update Customer
```http
PUT http://localhost:8000/api/customers/1
Content-Type: application/json

{
  "nik": "3174012345670001",
  "fullName": "John Doe Updated",
  "email": "john.doe@example.com",
  "phoneNumber": "08123456789",
  "dateOfBirth": "1990-01-15",
  "address": "Jl. Sudirman No. 456, Jakarta Selatan",
  "monthlyIncome": 18000000,
  "occupation": "Senior Software Engineer"
}
```

### 6. Verify Customer
```http
POST http://localhost:8000/api/customers/1/verify
```

### 7. Blacklist Customer
```http
POST http://localhost:8000/api/customers/1/blacklist?reason=Fraud detected
```

## Loan Service APIs

### 1. Apply for Loan
```http
POST http://localhost:8000/api/loans/apply
Content-Type: application/json

{
  "customerId": 1,
  "productId": 1,
  "requestedAmount": 50000000,
  "tenor": 24,
  "downPayment": 10000000,
  "purpose": "Pembelian Motor Honda CBR 150"
}
```

### 2. Get Loan Status
```http
GET http://localhost:8000/api/loans/1/status
```

### 3. Get All Loans
```http
GET http://localhost:8000/api/loans
```

### 4. Get Loans by Customer
```http
GET http://localhost:8000/api/loans/customer/1
```

### 5. Approve Loan (Surveyor)
```http
POST http://localhost:8000/api/loans/1/approve
Content-Type: application/json

{
  "approverRole": "SURVEYOR",
  "decision": "APPROVED",
  "notes": "Customer terverifikasi, lokasi sesuai"
}
```

### 6. Approve Loan (Credit Analyst)
```http
POST http://localhost:8000/api/loans/1/approve
Content-Type: application/json

{
  "approverRole": "CREDIT_ANALYST",
  "decision": "APPROVED",
  "notes": "Credit score baik, DTI ratio acceptable"
}
```

### 7. Approve Loan (Manager)
```http
POST http://localhost:8000/api/loans/1/approve
Content-Type: application/json

{
  "approverRole": "MANAGER",
  "decision": "APPROVED",
  "notes": "Final approval granted"
}
```

### 8. Reject Loan
```http
POST http://localhost:8000/api/loans/1/approve
Content-Type: application/json

{
  "approverRole": "CREDIT_ANALYST",
  "decision": "REJECTED",
  "notes": "DTI ratio melebihi batas maksimal 40%"
}
```

### 9. Disburse Loan
```http
POST http://localhost:8000/api/loans/1/disburse
Content-Type: application/json

{
  "disbursementMethod": "BANK_TRANSFER",
  "accountNumber": "1234567890",
  "accountName": "John Doe",
  "bankCode": "BCA"
}
```

### 10. Download SPK PDF
```http
GET http://localhost:8000/api/loans/1/spk/download
```

## Credit Engine APIs

### 1. Run Credit Check
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

### 2. Get Credit Check Result
```http
GET http://localhost:8000/api/credit/check/1
```

## PowerShell Examples

### Create Customer
```powershell
$customer = @{
    nik = "3174012345670001"
    fullName = "John Doe"
    email = "john.doe@example.com"
    phoneNumber = "08123456789"
    dateOfBirth = "1990-01-15"
    address = "Jl. Sudirman No. 123, Jakarta"
    monthlyIncome = 15000000
    occupation = "Software Engineer"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8000/api/customers" `
    -Method Post -Body $customer -ContentType "application/json"
$response
```

### Apply for Loan
```powershell
$loan = @{
    customerId = 1
    productId = 1
    requestedAmount = 50000000
    tenor = 24
    downPayment = 10000000
    purpose = "Pembelian Motor Honda"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8000/api/loans/apply" `
    -Method Post -Body $loan -ContentType "application/json"
$response
```

### Approve Loan
```powershell
$approval = @{
    approverRole = "CREDIT_ANALYST"
    decision = "APPROVED"
    notes = "Memenuhi syarat kredit"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8000/api/loans/1/approve" `
    -Method Post -Body $approval -ContentType "application/json"
$response
```

## Expected Response Codes

- `200 OK` - Request successful
- `201 Created` - Resource created successfully
- `400 Bad Request` - Validation error
- `404 Not Found` - Resource not found
- `409 Conflict` - Resource already exists
- `500 Internal Server Error` - Server error

## Testing Flow

1. Create Customer → Get `customerId`
2. Verify Customer (optional)
3. Apply for Loan → Get `loanId`
4. System runs Credit Check automatically
5. Approve by Surveyor
6. Approve by Credit Analyst
7. Approve by Manager
8. Disburse Loan
9. Download SPK PDF

Enjoy testing! 🚀
