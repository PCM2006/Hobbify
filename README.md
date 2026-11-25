# Hobbify

Hobbify conecta a personas con talleres, lugares de reunión y charlas relacionados con sus intereses.

## Backend local

Necesitas Java 21 y un servidor PostgreSQL con la extensión `btree_gist` disponible. Usa pgAdmin o `psql` para crear una base **vacía** llamada `hobbify`, con tu usuario de desarrollo como propietario. Flyway aplica el esquema en el primer arranque, así que basta con crear la base.

Desde la raíz del repositorio, abre PowerShell y entra al módulo.

```powershell
cd hobbify
```

Desde `hobbify/`, crea tu archivo `.env` junto a `pom.xml`. Si ya tienes un `.env`, edita ese archivo.

```powershell
Copy-Item -LiteralPath example.env -Destination .env
```

Rellena `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD` con las credenciales de PostgreSQL de la cuenta propietaria de la base. Ajusta `SPRING_DATASOURCE_URL` si usas otro puerto o nombre de base. Guarda el archivo en UTF-8 con formato `CLAVE=valor`, sin comillas alrededor del valor. Para escribir una barra invertida literal, usa `\\`. Git ignora `.env`.

Arranca el backend desde `hobbify/`.

```powershell
.\run-local.cmd
```

Spring Boot carga `.env` tanto con `.\run-local.cmd` como con `.\mvnw.cmd spring-boot:run`. Si lo ejecutas desde IntelliJ, usa la carpeta `hobbify/` como directorio de trabajo. Las variables de entorno del proceso tienen prioridad sobre los valores del archivo.

En Windows, puedes fijar el JDK de Hobbify creando `.mvn/java-home.local` dentro de `hobbify/`. Escribe la ruta absoluta a la carpeta del JDK 21 en una sola línea y sin comillas. Tanto `.\mvnw.cmd` como `.\run-local.cmd` usan esa ruta. Git ignora este archivo local. Comprueba el JDK activo con `.\mvnw.cmd -version`.

El servidor web usa el puerto 8080 por defecto. El mensaje `Started HobbifyApplication` confirma que arrancó y la aplicación sigue ejecutándose en la terminal.

Para ejecutar la prueba de arranque, apunta la configuración a una base de desarrollo o pruebas y ejecuta `.\mvnw.cmd test` desde `hobbify/`.

Las migraciones viven en `hobbify/src/main/resources/db/migration/`. Flyway registra cada versión en `public.flyway_schema_history`. Si ya ejecutaste manualmente el SQL original en otra base, crea una base nueva para esta configuración. Los cambios posteriores del esquema se incorporan mediante nuevas migraciones.

## Autenticación

El [contrato OpenAPI](hobbify/openapi.yaml) describe el registro de usuarios y empresas, el inicio de sesión, la consulta de la cuenta propia y el cierre de sesión.

1. Solicita `GET /api/v1/auth/csrf` antes del primer POST. Usa el `headerName` y el `token` de la respuesta como cabecera de los POST.
2. Registra una cuenta con `POST /api/v1/auth/registro` e inicia sesión con `POST /api/v1/auth/login`. La respuesta incluye el tipo de cuenta (`USUARIO` o `EMPRESA`).
3. Usa `GET /api/v1/auth/me` para recuperar la cuenta de la sesión. Pide un token CSRF nuevo después de iniciar sesión y después de cerrar sesión con `POST /api/v1/auth/logout`.

Desde frontend, envía las peticiones con `credentials: 'include'` para conservar la cookie de sesión. En local usa `localhost` en ambos servicios. Si frontend usa un origen diferente de `http://localhost:5173`, configura `HOBBIFY_FRONTEND_ORIGINS` en `.env` con los orígenes permitidos separados por comas.
