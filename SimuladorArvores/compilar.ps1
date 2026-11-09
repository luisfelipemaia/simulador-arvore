param( [switch]$Testar )
$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    New-Item -ItemType Directory -Force build/classes, dist/lib | Out-Null
    $fontes = @(Get-ChildItem -LiteralPath src/simulador -Filter '*.java' | ForEach-Object { $_.FullName })
    & javac -encoding UTF-8 --release 17 -cp 'lib/JSGE-Latest/*' -d build/classes $fontes
    if ($LASTEXITCODE -ne 0) { throw 'Falha na compilacao. Use um JDK 17 ou superior.' }

    if ($Testar) {
        New-Item -ItemType Directory -Force build/test | Out-Null
        $testes = @(Get-ChildItem -LiteralPath test -Filter '*.java' | ForEach-Object { $_.FullName })
        & javac -encoding UTF-8 --release 17 -cp 'build/classes;lib/JSGE-Latest/*' -d build/test $testes
        if ($LASTEXITCODE -ne 0) { throw 'Falha ao compilar os testes.' }
        & java -cp 'build/test;build/classes;lib/JSGE-Latest/*' simulador.TesteArvores
        if ($LASTEXITCODE -ne 0) { throw 'Falha nos testes.' }
        & java -cp 'build/test;build/classes;lib/JSGE-Latest/*' simulador.TesteComparacao
        if ($LASTEXITCODE -ne 0) { throw 'Falha nos testes de comparacao.' }
        & java -cp 'build/test;build/classes;lib/JSGE-Latest/*' simulador.TesteEntradaNumeros
        if ($LASTEXITCODE -ne 0) { throw 'Falha nos testes do sorteio.' }
    }

    $bibliotecas = @(Get-ChildItem -LiteralPath lib/JSGE-Latest -Filter '*.jar')
    foreach ($biblioteca in $bibliotecas) {
        Copy-Item -LiteralPath $biblioteca.FullName -Destination dist/lib -Force
    }
    # Continua o manifesto com um espaco no inicio de cada linha
    $manifesto = "Manifest-Version: 1.0`r`nMain-Class: simulador.Main`r`nClass-Path: `r`n"
    foreach ($biblioteca in $bibliotecas) {
        $manifesto += ' lib/' + $biblioteca.Name + " `r`n"
    }
    $manifesto += "`r`n"
    Add-Type -AssemblyName System.IO.Compression
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    $destino = Join-Path $PSScriptRoot 'dist/SimuladorArvores.jar'
    if (Test-Path -LiteralPath $destino) { Remove-Item -LiteralPath $destino }
    $arquivo = [IO.Compression.ZipFile]::Open($destino, [IO.Compression.ZipArchiveMode]::Create)
    try {
        $entrada = $arquivo.CreateEntry('META-INF/MANIFEST.MF')
        $escritor = [IO.StreamWriter]::new($entrada.Open(), [Text.UTF8Encoding]::new($false))
        $escritor.Write($manifesto)
        $escritor.Dispose()
        $baseClasses = (Resolve-Path build/classes).Path
        foreach ($classe in (Get-ChildItem -LiteralPath $baseClasses -Filter '*.class' -Recurse)) {
            $nome = $classe.FullName.Substring($baseClasses.Length + 1).Replace('\', '/')
            [IO.Compression.ZipFileExtensions]::CreateEntryFromFile($arquivo, $classe.FullName, $nome) | Out-Null
        }
    } finally {
        $arquivo.Dispose()
    }
    Write-Host 'Projeto compilado. Execute iniciar.cmd ou abra o projeto no NetBeans.'
} finally {
    Pop-Location
}
