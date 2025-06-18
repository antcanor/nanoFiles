
# NanoFiles

NanoFiles es una plataforma de compartición y transferencia de archivos implementada en Java. El proyecto simula un entorno de red distribuida con arquitectura híbrida cliente-servidor y peer-to-peer (P2P), permitiendo a los usuarios registrar, consultar y descargar archivos a través de un directorio centralizado y otros pares.

## 📦 Estructura del Proyecto

- `Directory.java`: Servidor central que registra archivos y peers.
- `NanoFiles.java`: Cliente/Peer que interactúa con el directorio y otros peers.
- Comunicación basada en protocolos personalizados:
  - Protocolo de mensajes con el directorio (formato textual).
  - Protocolo de transferencia de ficheros (formato binario).

## 🔧 Características

- Registro de peers como servidores de ficheros.
- Consulta y descarga de archivos disponibles.
- Comunicación mediante mensajes de texto tipo `campo:valor`.
- Transferencia binaria de archivos con fragmentación.
- Mensajes personalizados como `ping`, `filelist`, `fileserver`, etc.
- Gestión de baja de peers mediante el comando `quit`.

## 🧪 Ejecución

1. Compila los ficheros Java:

   ```bash
   javac Directory.java
   javac NanoFiles.java
   ```

2. Ejecuta primero el servidor de directorio:

   ```bash
   java Directory
   ```

3. Luego ejecuta uno o más peers:

   ```bash
   java NanoFiles
   ```

## 💬 Ejemplo de Intercambio de Mensajes

```text
Cliente → Directorio:
operation: ping
portocol: 123456789A

Directorio → Cliente:
operation: welcome

Cliente → Directorio:
operation: filelist

Directorio → Cliente:
operation: filelist_ok
file:archivo1.txt,1024,abc123...
```

## 📈 Mejoras Implementadas

- Comando `quit` para eliminar registros del peer en el directorio al salir.
- Manejo de errores en la baja de servidores.


## 🧑‍💻 Autores

- Antonio Cano Ruiz
- Fátima Zahra Daoudi Daoudi
