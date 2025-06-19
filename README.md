# SabbathMode

SabbathMode es un plugin para BungeeCord que restringe el acceso al servidor durante el Sabbath.

## Características
- Bloquea las conexiones desde el **viernes a las 18:00** hasta el **sábado a las 18:00** (según la zona horaria del jugador).
- Mensaje de denegación personalizable mediante `config.yml`.
- Modo de pruebas para ajustar manualmente el rango de horario.
- Whitelist de jugadores que pueden ingresar durante el Sabbath.

## Compilación
1. Asegúrate de tener **Java 17** y **Maven** instalados.
2. Ejecuta:
   ```bash
   mvn clean package
   ```
   El JAR sombreado aparecerá en `target/`.

## Instalación y uso
1. Copia `config.yml` en la carpeta de datos del plugin si no se genera automáticamente.
2. Ajusta `messages.sabbath-denied` con el mensaje que verán los jugadores.
3. Modifica `whitelist` para agregar jugadores exentos de la restricción.
4. Coloca el JAR generado en la carpeta `plugins` de tu servidor BungeeCord y reinícialo.

### Activar modo de pruebas
Si deseas probar horarios diferentes, edita la sección `testing` de `config.yml`:
```yaml
# Ejemplo de configuración de prueba
testing:
  enabled: true
  sabbath-start-day: FRIDAY
  sabbath-start-time: "18:00"
  sabbath-end-day: SATURDAY
  sabbath-end-time: "18:00"
```
Recuerda deshabilitarlo en producción.

### Comandos
El plugin incluye el comando `/sabbath` (permiso `sabbathmode.admin`) con varias opciones:

```
/sabbath reload                     # Recarga la configuración
/sabbath whitelist add <jugador>    # Añade un jugador a la whitelist
/sabbath whitelist remove <jugador> # Quita un jugador de la whitelist
/sabbath message show               # Muestra el mensaje de denegación actual
/sabbath message set <mensaje>      # Cambia el mensaje de denegación
/sabbath testing on|off             # Activa o desactiva el modo de pruebas
/sabbath testing start <DIA> <HH:mm> # Define inicio del Sabbath en pruebas
/sabbath testing end <DIA> <HH:mm>   # Define fin del Sabbath en pruebas
```

## Limitaciones conocidas
- La detección de zona horaria ahora se realiza a través de `ipapi.co`. Si la consulta falla se usará la zona horaria por defecto del servidor.
- La descarga de dependencias puede fallar en entornos sin acceso a Internet (por ejemplo, en este entorno de pruebas).

## Licencia
Proyecto sin licencia específica. Puedes adaptarlo según tus necesidades.
