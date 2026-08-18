# CodeScan-AI Java Demo

Proyecto Java minimo para demostrar CodeScan-AI como herramienta SAST integrada a un
repositorio externo mediante GitHub Actions.

El repositorio contiene codigo seguro, vulnerable y ambiguo a proposito. Por eso, cuando
se ejecuta como compuerta de CI, el analisis debe fallar: ese fallo demuestra que
CodeScan-AI bloquea cambios con vulnerabilidades confirmadas.

## Requisitos

- Python 3.11 o superior.
- `git`, necesario para instalar CodeScan-AI desde GitHub.
- `curl` y `tar`, necesarios para restaurar los artefactos publicados como release
  asset.

## Uso Local

Crear y activar un entorno virtual:

```bash
python3 -m venv .venv
source .venv/bin/activate
```

Instalar CodeScan-AI desde el repositorio de la herramienta:

```bash
pip install --upgrade pip wheel
pip install "codescan-ai @ git+https://github.com/enzov11/analisis-codigo.git"
```

Restaurar los artefactos publicados como release asset:

```bash
curl -L -o codescan-ai-artifacts-cwe15-roadmap-v1.tar.gz \
  https://github.com/enzov11/analisis-codigo/releases/download/cwe15-roadmap-v1/codescan-ai-artifacts-cwe15-roadmap-v1.tar.gz

mkdir -p .codescan
tar -xzf codescan-ai-artifacts-cwe15-roadmap-v1.tar.gz -C .codescan
```

Ejecutar el analizador:

```bash
codescan-ai scan .
```

Ese comando usa la politica definida en `.codescan.env`: genera SARIF y devuelve codigo
`1` si encuentra vulnerabilidades confirmadas.

Para inspeccionar el resultado sin fallar la ejecucion local:

```bash
codescan-ai scan . --format json --output codescan-ai-report.json --fail-on never
```

Salida esperada:

```text
files_scanned: 4
findings: 3
vulnerable: 2
review_required: 1
```

La configuracion vive en `.codescan.env`, por lo que no hace falta pasar rutas de modelo
ni parametros de fusion en cada corrida.

## GitHub Actions

El workflow `.github/workflows/codescan-ai.yml` ejecuta CodeScan-AI en cada `push` y
`pull_request`. Si encuentra vulnerabilidades confirmadas, el job falla y el Pull
Request queda bloqueado por la compuerta de seguridad.

La salida SARIF se sube a GitHub Code Scanning para que los hallazgos queden visibles en
la interfaz de seguridad del repositorio.

Para probarlo desde cero:

```bash
git add .codescan.env .github .gitignore README.md pom.xml src
git commit -m "Add CodeScan-AI security gate demo"
git branch -M main
git remote add origin https://github.com/enzov11/codescan-ai-java-demo.git
git push -u origin main
```

En GitHub, el workflow debe quedar en rojo porque el proyecto contiene vulnerabilidades
intencionales. Si se corrigen o eliminan `UnsafeSqlExample` y `UnsafeCommandExample`, la
compuerta deberia pasar.

## Experimento Reproducible

Este repositorio usa dos ramas para demostrar la compuerta de seguridad:

- `main`: contiene vulnerabilidades intencionales. El workflow debe fallar y GitHub Code
  Scanning debe mostrar alertas.
- `secure-pass`: corrige los ejemplos vulnerables. El mismo workflow debe pasar.

Para reproducir localmente el caso vulnerable en `main`:

```bash
git checkout main
codescan-ai scan . --format json --fail-on never
```

Resultado esperado:

```text
files_scanned: 4
findings: 3
vulnerable: 2
review_required: 1
```

Para probar la compuerta como CI:

```bash
codescan-ai scan .
echo $?
```

Resultado esperado en `main`: codigo de salida `1`.

Para reproducir el caso corregido:

```bash
git checkout secure-pass
codescan-ai scan . --format json --fail-on never
```

Resultado esperado en `secure-pass`: sin vulnerabilidades confirmadas y codigo de salida
`0` al ejecutar `codescan-ai scan .`.

## Casos Incluidos

- `SafeSqlExample`: consulta SQL parametrizada.
- `UnsafeSqlExample`: SQL dinamico vulnerable a CWE89.
- `UnsafeCommandExample`: ejecucion dinamica vulnerable a CWE78.
- `ReviewRequiredSqlExample`: operacion SQL sensible con flujo local no resuelto.
