$files = Get-ChildItem -Recurse -Filter *.java

foreach ($file in $files) {
    $text = Get-Content $file.FullName -Raw

    # vervang alleen hele identifiers
    $text = [regex]::Replace($text, '\benum2\b', 'enumeration2')
    $text = [regex]::Replace($text, '\benum\b', 'enumeration')

    # optioneel voorbereid op oud assert-gebruik
    $text = [regex]::Replace($text, '\bassert2\b', 'assertValue2')
    $text = [regex]::Replace($text, '\bassert\b', 'assertValue')

    Set-Content $file.FullName $text
}