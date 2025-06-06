# Script to create allocation template
# Usage: .\create_allocation_template.ps1 [-LegalEntityId <id>]
# Example: .\create_allocation_template.ps1 -LegalEntityId 15

param(
    [Parameter()]
    [int]$LegalEntityId = 15
)

# Set up authentication and headers
$base64AuthInfo = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes(('admin:admin')))
$headers = @{
    Authorization = "Basic $base64AuthInfo"
    "Content-Type" = "application/json"
}

# Create a unique code using timestamp
$timestamp = Get-Date -Format "yyyyMMddHHmmss"
$uniqueCode = "ALLOC{0}" -f $timestamp

# Create template body
$template = @{
    code = $uniqueCode
    name = "Allocation Template"
    description = "Standard allocation template"
    allocation_details = @(
        @{
            accountId = $null
            accountCode = "ACC001"
            allocationOrder = 1
            isSource = $true
        },
        @{
            accountCode = "ACC002"
            accountId = $null
            allocationOrder = 2
            isSource = $false
        }
    )
}

$body = $template | ConvertTo-Json -Depth 10
Write-Host "Creating allocation template for Legal Entity ID: $LegalEntityId..."
Write-Host "Request body:`n$body`n"
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/allocation-templates/legal-entity/$LegalEntityId" `
        -Method Post `
        -Headers $headers `
        -Body $body `
        -MaximumRedirection 0

    Write-Host "`nSuccessfully created allocation template:" -ForegroundColor Green
    $response | Format-Table -AutoSize
} catch {
    Write-Error "Error creating allocation template: $_"
    if ($_.Exception.Response) {
        $reader = [System.IO.StreamReader]::new($_.Exception.Response.GetResponseStream())
        $errorResponse = $reader.ReadToEnd() | ConvertFrom-Json -ErrorAction SilentlyContinue
        Write-Error "Server response: $($errorResponse.error)"
    } elseif ($_.ErrorDetails) {
        Write-Error $_.ErrorDetails.Message
    }
    exit 1
}
