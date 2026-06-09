# Servidor 24/7 — Tipo de cambio

Actualiza USD/MXN y EUR/MXN en Dynamics **cada día a las 12:01** (hora Ciudad de México).  
**Solo inserta fechas que Banxico publicó en su API.** Si un día no está en la API, no se sube.

## Requisitos en la PC servidor

Ver `requirements.txt` (Git, Java 21, VS Code).

- Windows 10/11, encendida 24/7 (sin suspender)
- Internet
- Cuenta con permisos para crear tareas programadas (opcional)

## Instalación rápida

1. Abre **CMD** o **PowerShell** como usuario normal.
2. Ejecuta:

```bat
git clone https://github.com/olnaturaqr-suite/Tipo-de-cambio.git C:\Olnatura\TipoCambio
cd C:\Olnatura\TipoCambio
instalar-servidor.cmd
```

El script instala Git y Java 21 si faltan, pide credenciales y compila el JAR.

### Credenciales

Copia y edita (no subas esto a Git):

```bat
copy src\main\resources\application-local.yml.example src\main\resources\application-local.yml
notepad src\main\resources\application-local.yml
```

Completa `banxico.token` y los valores de `dynamics.*`.

## Dejarlo corriendo 24/7 (terminal abierta)

```bat
cd C:\Olnatura\TipoCambio
vigilar-1201.cmd
```

Deja esa ventana abierta. A las **12:01** ejecuta `ejecutar.cmd` una vez por día.  
Logs: `logs\tipo-cambio-AAAAMMDD.log`

## Alternativa: tarea de Windows (sin terminal abierta)

CMD **como administrador**:

```bat
cd C:\Olnatura\TipoCambio
programar-1201.cmd
```

## Prueba manual

```bat
ejecutar.cmd
```

Código 0 = OK. Ver `[DIAG]` en pantalla y en el log.

## VS Code (opcional)

```bat
winget install Microsoft.VisualStudioCode
code C:\Olnatura\TipoCambio
```

## Configuración recomendada de Windows

- **Energía:** nunca suspender / apagar pantalla
- **Inicio automático (opcional):** acceso directo a `vigilar-1201.cmd` en la carpeta Inicio del usuario

## Qué hace el programa

1. Consulta Banxico SF60653 (USD) y SF46410 (EUR)
2. Compara con Dynamics en ventana de 14 días
3. Crea solo registros **faltantes** cuya fecha **existe en la API**
4. No inventa fines de semana ni rellena huecos
