$z = [TimeZoneInfo]::FindSystemTimeZoneById('Central Standard Time (Mexico)')
$n = [TimeZoneInfo]::ConvertTimeFromUtc((Get-Date).ToUniversalTime(), $z)
Write-Output ($n.ToString('yyyy-MM-dd') + ' ' + $n.ToString('HH:mm'))
