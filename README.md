# PlayChess

Aplicación de escritorio de ajedrez desarrollada en JavaFX como Trabajo
de Fin de Grado. Permite jugar contra una IA con tres niveles de
dificultad o en local entre dos jugadores, con cronómetro,
historial de partidas y revisión movimiento a movimiento.

## Descripción

PlayChess nace con la idea de ofrecer una forma sencilla, divertida y
sin complicaciones de jugar al ajedrez sin necesidad de un tablero
físico. Está pensada para una persona que quiera echar una partida
rápida contra la IA o para dos jugadores que compartan el mismo
dispositivo y quieran jugar a turnos.

## Características

- Partidas contra IA con tres niveles de dificultad: Fácil, Normal y Difícil 
- Partida local entre dos jugadores.
- Reloj que se usa para tres modos predefinidos: partida clásica
  (30 min), Rápida (10 min) y Blitz (5 min).
- Lógica completa de ajedrez: jaque, jaque mate, tablas por rey ahogado,
  enroque corto y largo, captura al paso y promoción de peón.
- Inicio de sesión con Google y Firebase Authentication, o
  acceso como invitado.
- Persistencia automática de partidas en curso y registro del
  historial.
- Revisión movimiento a movimiento de las partidas terminadas.
- Estadísticas por usuario (victorias, derrotas y tablas).
- Avatar personalizable a partir de una imagen local.
- Tema claro y tema oscuro

## Tecnologías

- Java 17.
- JavaFX 21
- Maven como gestor de dependencias y ciclo de vida.
- Jackson Databind 2.17 para serialización JSON.
- Google API Client 2.2 y Google OAuth Client 1.34 para el flujo OAuth
  2.0 con Google.
- Firebase Authentication mediante el endpoint REST de Identity
  Toolkit.
- jpackage (a través del plugin `jpackage-maven-plugin`) para generar
  el ejecutable de Windows.

## Requisitos previos

- JDK 17 o superior instalado y `JAVA_HOME` configurado.
- Maven 3.8 o superior.
- Conexión a internet la primera vez que se inicia sesión con Google.
- Para la fase de configuración: una cuenta de Google Cloud y un
  proyecto Firebase.

## Configuración

La aplicación necesita dos archivos de credenciales que no se
incluyen en el repositorio y que cada desarrollador debe generar de
forma local y ambos se colocan en `src/main/resources/`:

- `client_secret.json` — credenciales OAuth 2.0 obtenidas en
  [Google Cloud Console](https://console.cloud.google.com/), en
  *APIs y servicios → Credenciales*, creando un *ID de cliente OAuth*
  de tipo **Aplicación de escritorio**.
- `firebase_config.json` — fichero con al menos el campo `apiKey`
  obtenido en la
  [consola de Firebase](https://console.firebase.google.com/), en
  *Configuración del proyecto → Tus aplicaciones → SDK web*. Formato
  esperado:

  ```json
  {
    "apiKey": "AIza..."
  }
  ```

En la consola de Firebase debe estar habilitado el proveedor *Google*
dentro de *Authentication → Sign-in method*. La aplicación de
escritorio escucha el callback OAuth en `http://localhost:8888`, así
que dicha URI debe estar registrada como *URI de redirección
autorizado* en las credenciales OAuth de Google Cloud.

## Ejecución

Desde la raíz del proyecto:

```bash
mvn javafx:run
```

También se puede ejecutar desde IntelliJ pulsando a la derecha en maven->plugins->
javafx->javafx:run

## Empaquetado

```bash
mvn package
```

El plugin de Maven se encarga de copiar las dependencias en
`target/jpackage-input/` y, a continuación, jpackage genera la imagen
de aplicación en `target/dist/MiChess/`. El ejecutable resultante es
`MiChess.exe`.

## Estructura del proyecto

- `com.tfg.ajedrez` — clase de arranque (`Launcher` y
  `AjedrezApplication`).
- `com.tfg.ajedrez.auth` — servicios de autenticación con Google
 y Firebase.
- `com.tfg.ajedrez.clock` — reloj de partida 
- `com.tfg.ajedrez.controller` — controladores JavaFX de cada vista.
- `com.tfg.ajedrez.model` — modelo del tablero, piezas, posiciones y
  movimientos.
- `com.tfg.ajedrez.persistence` — guardado y carga de partidas e
  historial mediante JSON con Jackson.
- `com.tfg.ajedrez.state` — sesión de usuario, ajustes de partida y
  gestor de tema.
- `com.tfg.ajedrez.util` — diferentes utilidades como el sonido o el avatar

Los recursos visuales como los estilos CSS e imágenes están en
`src/main/resources/com/tfg/ajedrez/`.

## Datos de usuario

PlayChess no utiliza una base de datos. La sesión se gestiona contra
Firebase Authentication y todos los datos del usuario (partidas en
curso, historial, estadísticas, avatar) se persisten **localmente**
en el equipo del usuario, en formato JSON, dentro de:

```
~/.tfg-ajedrez/<userId>.json
```

donde `<userId>` es el `localId` proporcionado por Firebase, o el
correo saneado en caso de inicio de sesión incompleto. La carpeta se
crea automáticamente la primera vez que se guarda una partida.

## Autores

- Diego Sánchez Godoy
- Iván Alonso Campuzano
- Francisco Pereira García

## Licencia

Trabajo de Fin de Grado de Desarrollo de Aplicaciones Multiplataforma. Uso académico.
