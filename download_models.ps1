# download_models.ps1
# PowerShell script to automate downloading high-quality offline neural TTS models (VITS Gigaspeech)
# and required dictionaries/grapheme-to-phoneme datasets for local compilation/execution on Windows.
#
# Usage: .\download_models.ps1

$ErrorActionPreference = "Stop"

$ProjectRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition
$AssetsDir = Join-Path $ProjectRoot "app\src\main\assets"
$JniLibsDir = Join-Path $ProjectRoot "app\src\main\jniLibs"

Write-Host "==========================================================" -ForegroundColor Green
Write-Host "    KOKILA TTS - Neural Assets Downloader (Windows)       " -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Green
Write-Host "Project Root: $ProjectRoot"
Write-Host "Assets Target: $AssetsDir"

# Ensure output directories exist
if (-not (Test-Path $AssetsDir)) {
    New-Item -ItemType Directory -Path $AssetsDir | Out-Null
}
if (-not (Test-Path $JniLibsDir)) {
    New-Item -ItemType Directory -Path $JniLibsDir | Out-Null
}

# --- 1. Download VITS Neural Voice Model ---
$ModelUrl = "https://github.com/k2-fsa/sherpa-onnx/releases/download/tts-models/vits-en-gigaspeech.tar.bz2"
$ArchiveName = Join-Path $ProjectRoot "vits-en-gigaspeech.tar.bz2"

$ModelOnnx = Join-Path $AssetsDir "vits-en-gigaspeech.onnx"
if (-not (Test-Path $ModelOnnx)) {
    Write-Host "Downloading VITS Gigaspeech English Model archive..." -ForegroundColor Cyan
    Invoke-WebRequest -Uri $ModelUrl -OutFile $ArchiveName -UserAgent "Mozilla/5.0"
    
    Write-Host "Extracting archive contents..." -ForegroundColor Cyan
    # Check if tar tool is available (Windows 10/11 includes bsdtar as tar)
    if (Get-Command tar -ErrorAction SilentlyContinue) {
        cd $ProjectRoot
        tar -xjf $ArchiveName
        
        Copy-Item "vits-en-gigaspeech\vits-en-gigaspeech.onnx" (Join-Path $AssetsDir "vits-en-gigaspeech.onnx") -Force
        Copy-Item "vits-en-gigaspeech\tokens.txt" (Join-Path $AssetsDir "tokens.txt") -Force
        Copy-Item "vits-en-gigaspeech\lexicon.txt" (Join-Path $AssetsDir "lexicon.txt") -Force
        
        # Clean up
        Remove-Item "vits-en-gigaspeech" -Recurse -Force | Out-Null
        Remove-Item $ArchiveName -Force | Out-Null
        Write-Host "Neural Voice Assets installed successfully!" -ForegroundColor Green
    } else {
        Write-Host "Error: 'tar' utility not found. Please install git or download manually." -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "VITS Gigaspeech Model files already present in assets, skipping." -ForegroundColor Yellow
}

# --- 2. Download espeak-ng-data phoneme datasets ---
$EspeakUrl = "https://raw.githubusercontent.com/k2-fsa/sherpa-onnx/master/android/SherpaOnnxTtsEngine/app/src/main/assets/espeak-ng-data.tar.bz2"
$EspeakArchive = Join-Path $AssetsDir "espeak-ng-data.tar.bz2"
$EspeakDir = Join-Path $AssetsDir "espeak-ng-data"

if (-not (Test-Path $EspeakDir)) {
    Write-Host "Downloading espeak-ng-data phoneme dictionaries..." -ForegroundColor Cyan
    Invoke-WebRequest -Uri $EspeakUrl -OutFile $EspeakArchive -UserAgent "Mozilla/5.0"
    
    cd $AssetsDir
    tar -xjf $EspeakArchive
    Remove-Item $EspeakArchive -Force | Out-Null
    Write-Host "espeak-ng dictionaries installed successfully!" -ForegroundColor Green
} else {
    Write-Host "espeak-ng dictionaries already present in assets, skipping." -ForegroundColor Yellow
}

Write-Host "==========================================================" -ForegroundColor Green
Write-Host "    ALL OFFLINE ASSETS PREPARED SUCCESSFULLY!             " -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Green
