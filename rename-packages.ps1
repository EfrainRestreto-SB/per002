# Script para renombrar paquetes de org.acme a com.davivienda.per
$rootPath = "c:\Users\user\Documents\Proyectos\Davivienda\per\per002\local\per002\src"
$files = Get-ChildItem -Path $rootPath -Filter *.java -Recurse
$count = 0

Write-Host "Actualizando archivos Java..." -ForegroundColor Cyan

foreach ($file in $files) {
    $content = Get-Content $file.FullName -Raw -Encoding UTF8
    $original = $content
    
    # Reemplazar declaraciones de paquete e imports
    $content = $content -replace 'package org\.acme\.', 'package com.davivienda.per.'
    $content = $content -replace 'import org\.acme\.', 'import com.davivienda.per.'
    
    if ($content -ne $original) {
        Set-Content $file.FullName -Value $content -NoNewline -Encoding UTF8
        $count++
        Write-Host "  ✓ $($file.Name)" -ForegroundColor Green
    }
}

Write-Host "`n$count archivos actualizados exitosamente" -ForegroundColor Green
