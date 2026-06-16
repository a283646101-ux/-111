param(
    [string]$WsUrl = "ws://127.0.0.1:9995/ws/realtime",
    [string]$Token = "",
    [int]$Clients = 500,
    [int]$DurationSec = 60,
    [int]$SendIntervalMs = 1000,
    [string]$Room = "load-test"
)

if ([string]::IsNullOrWhiteSpace($Token)) {
    Write-Error "Token is required. Use -Token <jwt>"
    exit 1
}

$env:WS_URL = $WsUrl
$env:WS_TOKEN = $Token
$env:WS_CLIENTS = "$Clients"
$env:WS_DURATION_SEC = "$DurationSec"
$env:WS_SEND_INTERVAL_MS = "$SendIntervalMs"
$env:WS_ROOM = $Room

node .\scripts\ws-load-test.js
