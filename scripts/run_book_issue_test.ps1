# Automated test runner for EX-4 Book Issue Process

$baseUrl = "http://localhost:8080/api/book-issue"
$headers = @{ "Content-Type" = "application/json" }

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " Camunda 8 Lab EX-4: User Task Form & Variable Runner     " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# Step 1: Start process instance
Write-Host "`n[1] Starting new Book Issue process instance..." -ForegroundColor Yellow
$startResp = Invoke-RestMethod -Uri "$baseUrl/start" -Method Post -Headers $headers -Body "{}"
Write-Host "SUCCESS! Instance started with key:" $startResp.processInstanceKey -ForegroundColor Green
Write-Host "Status:" $startResp.status -ForegroundColor Green
Write-Host "Current Activity:" $startResp.currentActivity -ForegroundColor Green

$key = $startResp.processInstanceKey

# Step 2: Complete User Task Form
Write-Host "`n[2] Submitting User Task Form in Tasklist for instance $key..." -ForegroundColor Yellow
$formData = @{
    studentName = "Sudhanshu Kumar"
    regNo       = "RA2311003010001"
    bookTitle   = "Clean Architecture: A Craftsman's Guide"
    issueDate   = (Get-Date).ToString("yyyy-MM-dd")
    reason      = "Reference material for semester capstone project"
    agreeTerms  = $true
} | ConvertTo-Json

$completeResp = Invoke-RestMethod -Uri "$baseUrl/instances/$key/complete-form" -Method Post -Headers $headers -Body $formData
Write-Host "SUCCESS! Task completed. Process advanced to End Event." -ForegroundColor Green
Write-Host "Status:" $completeResp.status -ForegroundColor Green

# Step 3: Verify variables in Operate
Write-Host "`n[3] Verifying recorded process variables in Operate..." -ForegroundColor Yellow
$instances = Invoke-RestMethod -Uri "$baseUrl/instances" -Method Get
Write-Host "Total instances in registry:" $instances.totalCount -ForegroundColor Green

Write-Host "`nRecorded Process Variables for Key $key:" -ForegroundColor Cyan
$completeResp.variables | Format-Table -AutoSize

Write-Host "`nEX-4 Automated Verification Complete!" -ForegroundColor Green
