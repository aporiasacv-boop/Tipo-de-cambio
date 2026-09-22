# Servidor 24/7 — Tipo de cambio

Actualiza USD/MXN y EUR/MXN en Dynamics **cada día a las 13:01** (hora Ciudad de México). El euro en Banxico suele publicarse cerca de la 1 pm.
**Solo inserta fechas que Banxico publicó en su API.** Si un día no está en la API, no se sube.

## Requisitos en la PC servidor

Ver `requirements.txt` (Git, Java 21).

- Windows 10/11, encendida 24/7 (sin suspender)
- Internet

## Botones (doble clic)

1. Clona el repo en `C:\Olnatura\TipoCambio`
2. Doble clic en `INICIO.cmd` y elige:
   - **Ejecutar ahora** — una corrida inmediata
   - **Vigilar 24/7** — deja la ventana abierta; corre solo a las 13:01
3. La primera vez pide credenciales y compila el JAR (`Instalar.cmd`).

Desde terminal también sirve `instalar-servidor.cmd`.

### Credenciales

Copia y edita (no subas esto a Git):

```bat
copy src\main\resources\application-local.yml.example src\main\resources\application-local.yml
notepad src\main\resources\application-local.yml
```

Completa `banxico.token` y los valores de `dynamics.*`.

## Dejarlo corriendo 24/7

```bat
cd C:\Olnatura\TipoCambio
Vigilar-24-7.cmd
```

Deja esa ventana abierta. A las **13:01** ejecuta una vez por día.
Logs: `logs\tipo-cambio-AAAAMMDD.log`

## Alternativa: tarea de Windows (sin terminal abierta)

CMD **como administrador**:

```bat
cd C:\Olnatura\TipoCambio
programar-1301.cmd
```

## Prueba manual

```bat
Ejecutar-ahora.cmd
```

Código 0 = OK. Ver `[DIAG]` en pantalla y en el log.

## Configuración recomendada de Windows

- **Energía:** nunca suspender / apagar pantalla
- **Inicio automático (opcional):** acceso directo a `Vigilar-24-7.cmd` en la carpeta Inicio del usuario

## Qué hace el programa

1. Consulta Banxico SF60653 (USD) y SF46410 (EUR)
2. Compara con Dynamics en ventana de 14 días
3. Crea solo registros **faltantes** cuya fecha **existe en la API**
4. No inventa fines de semana ni rellena huecos
