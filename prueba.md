# Prueba en consola

## Caso incorrecto

```bash
git switch pruebas-funcionamiento

python3 -m venv .venv
. .venv/bin/activate
pip install "codescan-ai @ git+https://github.com/enzov11/analisis-codigo.git"

curl -L -o codescan-ai-artifacts-codescan-v1.0.0.tar.gz \
  https://github.com/enzov11/analisis-codigo/releases/download/codescan-v1.0.0/codescan-ai-artifacts-codescan-v1.0.0.tar.gz
mkdir -p .codescan
tar -xzf codescan-ai-artifacts-codescan-v1.0.0.tar.gz -C .codescan

codescan-ai scan . --format json --output codescan-ai-report.json
echo $?
```

Resultado esperado: tres hallazgos en los archivos `PullRequest*.java` y código `1`.

## Corrección

Reemplazar `PullRequestSqlExample.java` por:

```java
package demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PullRequestSqlExample {
    public ResultSet findByUsername(Connection connection, String username)
            throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT id, username FROM users WHERE username = ?");
        statement.setString(1, username);
        return statement.executeQuery();
    }
}
```

Reemplazar `PullRequestCommandExample.java` por:

```java
package demo;

import java.io.IOException;
import java.util.Set;

public class PullRequestCommandExample {
    private static final Set<String> ALLOWED_HOSTS = Set.of("localhost", "127.0.0.1");

    public Process pingHost(String host) throws IOException {
        if (!ALLOWED_HOSTS.contains(host)) {
            throw new IllegalArgumentException("Host is not allowed");
        }
        return new ProcessBuilder("ping", host).start();
    }
}
```

Reemplazar `PullRequestQueryExample.java` por:

```java
package demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PullRequestQueryExample {
    public ResultSet findByStatus(Connection connection, String status)
            throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT id, username FROM users WHERE status = ?");
        statement.setString(1, status);
        return statement.executeQuery();
    }
}
```

Ejecutar:

```bash
codescan-ai scan . --format json --output codescan-ai-report.json
echo $?
```

Resultado esperado: ningún hallazgo y código `0`.

# Prueba de pull request

## Caso incorrecto

```bash
git switch pruebas-funcionamiento
git push -u origin pruebas-funcionamiento
```

1. Abrir un pull request desde `pruebas-funcionamiento` hacia `main`.
2. Abrir el check **CodeScan-AI Security Gate**.
3. Verificar que **Run CodeScan-AI** falla con código `1`.
4. Verificar que **Upload SARIF** termina correctamente.
5. Abrir **Security > Code scanning** y filtrar con `is:open branch:pruebas-funcionamiento`.
6. Verificar tres alertas en los archivos `PullRequest*.java`.

No fusionar el pull request.

## Corrección

1. Reemplazar los tres archivos con los contenidos de la sección **Corrección**.
2. Ejecutar el escaneo de consola y verificar código `0`.

```bash
git add src/main/java/demo/PullRequestSqlExample.java \
  src/main/java/demo/PullRequestCommandExample.java \
  src/main/java/demo/PullRequestQueryExample.java
git commit -m "Corregir ejemplos de seguridad"
git push
```

1. Esperar la nueva ejecución del mismo pull request.
2. Verificar que **CodeScan-AI Security Gate** termina correctamente.
3. Abrir **Security > Code scanning** y filtrar con `branch:pruebas-funcionamiento`.
4. Verificar `0 Open` para los archivos `PullRequest*.java`.
5. Mantener el pull request sin fusionar.
