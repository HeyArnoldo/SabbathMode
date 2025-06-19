# SabbathMode

SabbathMode es un plugin para BungeeCord que restringe el acceso al servidor durante el Sabbath.

## Características
- Bloquea las conexiones al servidor desde el viernes a las 18:00 hasta el sábado a las 18:00 (zona horaria local del jugador).
- Permite personalizar el mensaje de denegación en `config.yml`.
- Incluye un modo de pruebas para ajustar manualmente el rango de horario.

## Compilación
Se utiliza Maven para generar el JAR del plugin.

```bash
mvn clean package
```

El JAR sombreado aparecerá en `target/`.

## Configuración
1. Copia `config.yml` en la carpeta de datos del plugin si no se genera automáticamente.
2. Ajusta el mensaje de denegación en `messages.sabbath-denied`.
3. Para pruebas, activa la sección `testing` y define días/horarios personalizados.

## Uso
Coloca el JAR generado en la carpeta `plugins` de tu servidor BungeeCord y reinícialo. Cualquier conexión durante el Sabbath mostrará el mensaje configurado y se cancelará.

## Licencia
Este proyecto se distribuye sin una licencia específica.
