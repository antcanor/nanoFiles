package es.um.redes.nanoFiles.tcp.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;


import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;




public class NFServer implements Runnable {

	public static final int PORT = 10000;

	private boolean stopServer = false;

	private ServerSocket serverSocket = null;

	public NFServer() throws IOException {
		/*
		 * TODO: (Boletín SocketsTCP) Crear una direción de socket a partir del puerto
		 * especificado (PORT)
		 */
		
		InetSocketAddress socketAddress= new InetSocketAddress(PORT);
		/*
		 * TODO: (Boletín SocketsTCP) Crear un socket servidor y ligarlo a la dirección
		 * de socket anterior
		 */
		serverSocket= new ServerSocket();
		serverSocket.bind(socketAddress);

	}

	/**
	 * Método para ejecutar el servidor de ficheros en primer plano. Sólo es capaz
	 * de atender una conexión de un cliente. Una vez se lanza, ya no es posible
	 * interactuar con la aplicación.
	 * 
	 */
	public void test() {
		if (serverSocket == null || !serverSocket.isBound()) {
			System.err.println(
					"[fileServerTestMode] Failed to run file server, server socket is null or not bound to any port");
			return;
		} else {
			System.out
					.println("[fileServerTestMode] NFServer running on " + serverSocket.getLocalSocketAddress() + ".");
		}

		while (true) {
			/*
			 * TODO: (Boletín SocketsTCP) Usar el socket servidor para esperar conexiones de
			 * otros peers que soliciten descargar ficheros.
			 */
			
			try{
				Socket socketCliente = serverSocket.accept();
				/*
				 * TODO: (Boletín SocketsTCP) Tras aceptar la conexión con un peer cliente, la
				 * comunicación con dicho cliente para servir los ficheros solicitados se debe
				 * implementar en el método serveFilesToClient, al cual hay que pasarle el
				 * socket devuelto por accept.
				 */
				
				serveFilesToClient(socketCliente);
				
			}catch(IOException ex) {
				System.out.println("Server exception: " + ex.getMessage());
				ex.printStackTrace();
			}



		}
	}

	/**
	 * Método que ejecuta el hilo principal del servidor en segundo plano, esperando
	 * conexiones de clientes.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/*
		 * TODO: (Boletín SocketsTCP) Usar el socket servidor para esperar conexiones de
		 * otros peers que soliciten descargar ficheros
		 */

		while (!stopServer) {
			try {
				Socket socketCliente = serverSocket.accept();
				System.out.println("New client connected: " + socketCliente.getInetAddress().toString() + ":" + socketCliente.getPort());
				NFServerThread hilo = new NFServerThread(socketCliente);
				hilo.run();
			} catch (IOException e) {
				System.err.println("Error al aceptar la conexión con el cliente: " + e.getMessage());
			}

		
		}
		System.out.println("* Server stopped.");
		/*
		 * TODO: (Boletín SocketsTCP) Al establecerse la conexión con un peer, la
		 * comunicación con dicho cliente se hace en el método
		 * serveFilesToClient(socket), al cual hay que pasarle el socket devuelto por
		 * accept
		 */
		/*
		 * TODO: (Boletín TCPConcurrente) Crear un hilo nuevo de la clase
		 * NFServerThread, que llevará a cabo la comunicación con el cliente que se
		 * acaba de conectar, mientras este hilo vuelve a quedar a la escucha de
		 * conexiones de nuevos clientes (para soportar múltiples clientes). Si este
		 * hilo es el que se encarga de atender al cliente conectado, no podremos tener
		 * más de un cliente conectado a este servidor.
		 */

		 



	}
	/*
	 * TODO: (Boletín SocketsTCP) Añadir métodos a esta clase para: 1) Arrancar el
	 * servidor en un hilo nuevo que se ejecutará en segundo plano 2) Detener el
	 * servidor (stopserver) 3) Obtener el puerto de escucha del servidor etc.
	 */

	public void startServer() {
		Thread hilo = new Thread(this);
		hilo.start();
	}

	public void stopServer(){
		stopServer = true;
		/*try {
			serverSocket.close();
		} catch (IOException e) {
			System.err.println("Error al cerrar el socket del servidor: " + e.getMessage());
		}*/
	}

	public int getPort() {
		return serverSocket.getLocalPort();
	}

	/**
	 * Método de clase que implementa el extremo del servidor del protocolo de
	 * transferencia de ficheros entre pares.
	 * 
	 * @param socket El socket para la comunicación con un cliente que desea
	 *               descargar ficheros.
	 */
	public static void serveFilesToClient(Socket socket) {
		/*
		 * TODO: (Boletín SocketsTCP) Crear dis/dos a partir del socket
		 */
		try {
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			
		
			/*
			 * TODO: (Boletín SocketsTCP) Mientras el cliente esté conectado, leer mensajes
			 * de socket, convertirlo a un objeto PeerMessage y luego actuar en función del
			 * tipo de mensaje recibido, enviando los correspondientes mensajes de
			 * respuesta.
			 */
			PeerMessage mensajeCliente = PeerMessage.readMessageFromInputStream(dis);
			byte opcode=mensajeCliente.getOpcode();
			/*
			 * TODO: (Boletín SocketsTCP) Para servir un fichero, hay que localizarlo a
			 * partir de su hash (o subcadena) en nuestra base de datos de ficheros
			 * compartidos. Los ficheros compartidos se pueden obtener con
			 * NanoFiles.db.getFiles(). Los métodos lookupHashSubstring y
			 * lookupFilenameSubstring de la clase FileInfo son útiles para buscar ficheros
			 * coincidentes con una subcadena dada del hash o del nombre del fichero. El
			 * método lookupFilePath() de FileDatabase devuelve la ruta al fichero a partir
			 * de su hash completo.
			 */
			PeerMessage respuesta = null;
			switch (opcode) {
			case PeerMessageOps.DOWNLOAD_FILE: {
				byte[] name = mensajeCliente.getFileName();
				FileInfo[] files = NanoFiles.db.getFiles();
				FileInfo[] filesConNombre = FileInfo.lookupFilenameSubstring(files, new String(name));
				if(filesConNombre.length == 0) {
					respuesta = new PeerMessage(PeerMessageOps.FILE_NOT_FOUND);
					respuesta.writeMessageToOutputStream(dos);
				} else if(filesConNombre.length == 1) {
					String hash = filesConNombre[0].fileHash;
					String path = NanoFiles.db.lookupFilePath(hash);
					sendFileChunk(path, dos, hash);
				}
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + opcode);
			}
			//int num = dis.readInt();
			//dos.writeInt(num);
			
		
		}catch(IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
		



	}

	private static void sendFileChunk(String filepath, DataOutputStream dos, String hash) {
		final int CHUNK_SIZE = 32;
		try{
			RandomAccessFile file = new RandomAccessFile(filepath, "r");
			long fileLength = file.length();
			long offset = 0;
			while(offset < fileLength) {
				int bytesToRead = (int) Math.min(CHUNK_SIZE, fileLength - offset);
	            byte[] chunk = new byte[bytesToRead];
	            file.seek(offset);
	            file.readFully(chunk, 0, bytesToRead);

	            PeerMessage chunkMessage = new PeerMessage(PeerMessageOps.FILE);
				chunkMessage.setFileData(chunk);
	            chunkMessage.writeMessageToOutputStream(dos);

	            offset += bytesToRead;
			}
			file.close();
			PeerMessage endMessage = new PeerMessage(PeerMessageOps.END_OF_FILE);
			endMessage.setHash(hash.getBytes());
			endMessage.writeMessageToOutputStream(dos);

		} catch(IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}




}