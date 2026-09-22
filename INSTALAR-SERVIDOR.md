# Servidor 24/7 — Tipo de cambio

Actualiza USD/MXN y EUR/MXN en Dynamics **5 veces al día** (hora Ciudad de México):

- **00:01** madrugada
- **07:00** mañana
- **13:01** (Banxico suele publicar el euro cerca de la 1 pm)
- **15:00** tarde
- **18:00** tarde

**Solo inserta fechas que Banxico publicó en su API y que aún no están en Dynamics.** Si no hay nada nuevo, no escribe. No rellena fines de semana ni huecos.

## Requisitos en la PC servidor

Ver `requirements.txt` (Git, Java 21).

- Windows 10/11, encendida 24/7 (sin suspender)
- Internet

## Botones (doble clic)

1. Clona el repo en `C:\Olnatura\TipoCambio`
2. Doble clic en `INICIO.cmd` y elige:
   - **Ejecutar ahora** — una corrida inmediata
   - **Vigilar 24/7** — deja la ventana abierta; corre en los 5 horarios
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
git pull
Vigilar-24-7.cmd
```

Deja esa ventana abierta. En cada horario ejecuta una vez si toca.
Logs: `logs\tipo-cambio-AAAAMMDD.log`

## Alternativa: tarea de Windows (sin terminal abierta)

CMD **como administrador**:

```bat
cd C:\Olnatura\TipoCambio
programar-horarios.cmd
```

## Prueba manual

```bat
Ejecutar-ahora.cmd
```

Código 0 = OK. Ver `[DIAG]` en pantalla y en el log. Si ya estaba al día, termina 0 sin crear registros.

## Configuración recomendada de Windows

- **Energía:** nunca suspender / apagar pantalla
- **Inicio automático (opcional):** acceso directo a `Vigilar-24-7.cmd` en la carpeta Inicio del usuario

## Qué hace el programa

1. Consulta Banxico SF60653 (USD) y SF46410 (EUR)
2. Compara con Dynamics en ventana de 14 días
3. Crea solo registros **faltantes** cuya fecha **existe en la API**
4. No inventa fines de semana ni rellena huecos
5. Si Dynamics ya tiene esas fechas, no actualiza nada
