# Script to import transactions from CSV file
# Usage: .\import_transactions.ps1 [-LegalEntityId <id>] [-CsvPath <path>]
# Example: .\import_transactions.ps1 -LegalEntityId 15
# Example: .\import_transactions.ps1 -LegalEntityId 15 -CsvPath "custom_import.csv"
# Make sure the application is running on http://localhost:8080 before running this script

param(
    [Parameter()]
    [int]$LegalEntityId = 15,
    
    [Parameter()]
    [string]$CsvPath = "$PSScriptRoot\..\src\test\resources\test_transactions.csv"
)

# Check if the application is running
$base64AuthInfo = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes('admin:admin'))
try {
    $headers = @{
        Authorization = "Basic $base64AuthInfo"
    }
    $testResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/transactions" -Method Get -Headers $headers
    if ($testResponse.StatusCode -ne 200) {
        Write-Error "Application is not running properly. Please start the application first."
        exit 1
    }
} catch {
    Write-Error "Could not connect to the application. Please make sure it's running on http://localhost:8080"
    exit 1
}

# Validate and read the CSV file
if (-not (Test-Path $CsvPath)) {
    Write-Error "CSV file not found at path: $CsvPath"
    exit 1
}

Write-Host "Importing transactions for Legal Entity ID: $LegalEntityId..."
try {
    $base64AuthInfo = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes('admin:admin'))
    $fileBin = [IO.File]::ReadAllBytes($CsvPath)
    $boundary = [System.Guid]::NewGuid().ToString()
    $contentInfo = @{
        Headers = @{
            Authorization = "Basic $base64AuthInfo"
            "Content-Type" = "multipart/form-data; boundary=$boundary"
        }
        Method = "POST"
        Uri = "http://localhost:8080/api/import/transaction-lines?legalEntityId=$LegalEntityId"
    }
    $bodyLines = @(
        "--$boundary",
        "Content-Disposition: form-data; name=`"file`"; filename=`"$(Split-Path $CsvPath -Leaf)`"",
        "Content-Type: text/csv`r`n"
    )
    $bodyLines += [Text.Encoding]::UTF8.GetString($fileBin)
    $bodyLines += "--$boundary--"
    $body = $bodyLines -join "`r`n"

    Write-Host "Request body:"
    Write-Host $body
    Write-Host "`nSending request..."
    
    $response = Invoke-RestMethod @contentInfo -Body $body

    Write-Host "`nSuccessfully imported transactions:" -ForegroundColor Green
    $response | Format-Table -AutoSize
} catch {
    Write-Error "Error importing transactions: $_"
    if ($_.ErrorDetails) {
        Write-Error $_.ErrorDetails.Message
    }
    exit 1
}
