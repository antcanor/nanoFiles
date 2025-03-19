package es.um.redes.nanoFiles.udp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.udp.message.DirMessage;
import es.um.redes.nanoFiles.udp.message.DirMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;

public class NFDirectoryServer {
	/**
	 * Número de puerto UDP en el que escucha el directorio
	 */
	public static final int DIRECTORY_PORT = 6868;

	/**
	 * Socket de comunicación UDP con el cliente UDP (DirectoryConnector)
	 */
	private DatagramSocket socket = null;
	/*
	 * TODO: Añadir aquí como atributos las estructuras de datos que sean necesarias
	 * para mantener en el directorio cualquier información necesaria para la
	 * funcionalidad del sistema nanoFilesP2P: ficheros publicados, servidores
	 * registrados, etc.
	 */

	private static Map<FileInfo, LinkedList<InetSocketAddress>> files; //Mapa de ficheros y servidores que los tienen
	private Map<InetSocketAddress, LinkedList<FileInfo>> servers; //Mapa de servidores y ficheros que tienen
	private static Map<String, LinkedList<FileInfo>> fileInfoMap; //Mapa de hash de fichero y fichero

	/**
	 * Probabilidad de descartar un mensaje recibido en el directorio (para simular
	 * enlace no confiable y testear el código de retransmisión)
	 */
	private double messageDiscardProbability;

	public NFDirectoryServer(double corruptionProbability) throws SocketException {
		/*
		 * Guardar la probabilidad de pérdida de datagramas (simular enlace no
		 * confiable)
		 */
		messageDiscardProbability = corruptionProbability;
		/*
		 * TODO: (Boletín SocketsUDP) Inicializar el atributo socket: Crear un socket
		 * UDP ligado al puerto especificado por el argumento directoryPort en la
		 * máquina local,
		 */
		this.socket=new DatagramSocket(DIRECTORY_PORT);
		
		
		/*
		 * TODO: (Boletín SocketsUDP) Inicializar atributos que mantienen el estado del
		 * servidor de directorio: ficheros, etc.)
		 */

		this.files= new HashMap<FileInfo, LinkedList<InetSocketAddress>>();
		this.servers = new HashMap<InetSocketAddress, LinkedList<FileInfo>>();
		this.fileInfoMap = new HashMap<String, LinkedList<FileInfo>>();


		if (NanoFiles.testModeUDP) {
			if (socket == null) {
				System.err.println("[testMode] NFDirectoryServer: code not yet fully functional.\n"
						+ "Check that all TODOs in its constructor and 'run' methods have been correctly addressed!");
				System.exit(-1);
			}
		}
	}

	public DatagramPacket receiveDatagram() throws IOException {
		DatagramPacket datagramReceivedFromClient = null;
		boolean datagramReceived = false;
		while (!datagramReceived) {
			/*
			 * TODO: (Boletín SocketsUDP) Crear un búfer para recibir datagramas y un
			 * datagrama asociado al búfer (datagramReceivedFromClient)
			 */
			
			byte[] recvBuf = new byte[DirMessage.PACKET_MAX_SIZE];
			datagramReceivedFromClient=new DatagramPacket(recvBuf, recvBuf.length);
			
			/*
			 * TODO: (Boletín SocketsUDP) Recibimos a través del socket un datagrama
			 */
			socket.receive(datagramReceivedFromClient);
			


			if (datagramReceivedFromClient == null) {
				System.err.println("[testMode] NFDirectoryServer.receiveDatagram: code not yet fully functional.\n"
						+ "Check that all TODOs have been correctly addressed!");
				System.exit(-1);
			} else {
				// Vemos si el mensaje debe ser ignorado (simulación de un canal no confiable)
				double rand = Math.random();
				if (rand < messageDiscardProbability) {
					System.err.println(
							"Directory ignored datagram from " + datagramReceivedFromClient.getSocketAddress());
				} else {
					datagramReceived = true;
					System.out
							.println("Directory received datagram from " + datagramReceivedFromClient.getSocketAddress()
									+ " of size " + datagramReceivedFromClient.getLength() + " bytes.");
				}
			}

		}

		return datagramReceivedFromClient;
	}

	public void runTest() throws IOException {

		System.out.println("[testMode] Directory starting...");

		System.out.println("[testMode] Attempting to receive 'ping' message...");
		DatagramPacket rcvDatagram = receiveDatagram();
		sendResponseTestMode(rcvDatagram);

		System.out.println("[testMode] Attempting to receive 'ping&PROTOCOL_ID' message...");
		rcvDatagram = receiveDatagram();
		sendResponseTestMode(rcvDatagram);
	}

	private void sendResponseTestMode(DatagramPacket pkt) throws IOException {
		/*
		 * TODO: (Boletín SocketsUDP) Construir un String partir de los datos recibidos
		 * en el datagrama pkt. A continuación, imprimir por pantalla dicha cadena a
		 * modo de depuración.
		 */
		
		String datosRecibidos = new String(pkt.getData(),0,pkt.getLength());
		System.out.println("datos recibidos: \n"+datosRecibidos);

		/*
		 * TODO: (Boletín SocketsUDP) Después, usar la cadena para comprobar que su
		 * valor es "ping"; en ese caso, enviar como respuesta un datagrama con la
		 * cadena "pingok". Si el mensaje recibido no es "ping", se informa del error y
		 * se envía "invalid" como respuesta.
		 */
		if("ping".equals(datosRecibidos)) {
			String datosEnviar = "pingok";
			byte[] pktEnvio = datosEnviar.getBytes();
			DatagramPacket datagramaEnviar = new DatagramPacket(pktEnvio, pktEnvio.length,pkt.getSocketAddress());	
			socket.send(datagramaEnviar);
		}else if(datosRecibidos.startsWith("ping&")){
			if(datosRecibidos.equals("ping&"+NanoFiles.PROTOCOL_ID)) {
				String datosEnviar = "welcome";
				byte[] pktEnvio = datosEnviar.getBytes();
				DatagramPacket datagramaEnviar = new DatagramPacket(pktEnvio, pktEnvio.length,pkt.getSocketAddress());	
				socket.send(datagramaEnviar);
			}else {
				String datosEnviar = "denied";
				byte[] pktEnvio = datosEnviar.getBytes();
				DatagramPacket datagramaEnviar = new DatagramPacket(pktEnvio, pktEnvio.length,pkt.getSocketAddress());	
				socket.send(datagramaEnviar);
			}
		}else {
			String datosEnviar = "invalid";
			byte[] pktEnvio = datosEnviar.getBytes();
			DatagramPacket datagramaEnviar = new DatagramPacket(pktEnvio, pktEnvio.length,pkt.getSocketAddress());	
			socket.send(datagramaEnviar);
		}

		/*
		 * TODO: (Boletín Estructura-NanoFiles) Ampliar el código para que, en el caso
		 * de que la cadena recibida no sea exactamente "ping", comprobar si comienza
		 * por "ping&" (es del tipo "ping&PROTOCOL_ID", donde PROTOCOL_ID será el
		 * identificador del protocolo diseñado por el grupo de prácticas (ver
		 * NanoFiles.PROTOCOL_ID). Se debe extraer el "protocol_id" de la cadena
		 * recibida y comprobar que su valor coincide con el de NanoFiles.PROTOCOL_ID,
		 * en cuyo caso se responderá con "welcome" (en otro caso, "denied").
		 */

		String messageFromClient = new String(pkt.getData(), 0, pkt.getLength());
		System.out.println("Data received: " + messageFromClient);



	}

	public void run() throws IOException {

		System.out.println("Directory starting...");

		while (true) { // Bucle principal del servidor de directorio
			DatagramPacket rcvDatagram = receiveDatagram();

			sendResponse(rcvDatagram);

		}
	}

	private void sendResponse(DatagramPacket pkt) throws IOException {
		/*
		 * TODO: (Boletín MensajesASCII) Construir String partir de los datos recibidos
		 * en el datagrama pkt. A continuación, imprimir por pantalla dicha cadena a
		 * modo de depuración. Después, usar la cadena para construir un objeto
		 * DirMessage que contenga en sus atributos los valores del mensaje. A partir de
		 * este objeto, se podrá obtener los valores de los campos del mensaje mediante
		 * métodos "getter" para procesar el mensaje y consultar/modificar el estado del
		 * servidor.
		 */
		String datosRecibidos = new String(pkt.getData(),0,pkt.getLength());
		System.out.println("datos recibidos: "+datosRecibidos);
		DirMessage mensajeRecibido=DirMessage.fromString(datosRecibidos);
		

		/*
		 * TODO: Una vez construido un objeto DirMessage con el contenido del datagrama
		 * recibido, obtener el tipo de operación solicitada por el mensaje y actuar en
		 * consecuencia, enviando uno u otro tipo de mensaje en respuesta.
		 */
		String operation = mensajeRecibido.getOperation();
		InetSocketAddress clientAddress = (InetSocketAddress) pkt.getSocketAddress();
		System.out.println("Operation: " + operation + " from " + clientAddress);
		/*
		 * TODO: (Boletín MensajesASCII) Construir un objeto DirMessage (msgToSend) con
		 * la respuesta a enviar al cliente, en función del tipo de mensaje recibido,
		 * leyendo/modificando según sea necesario el "estado" guardado en el servidor
		 * de directorio (atributos files, etc.). Los atributos del objeto DirMessage
		 * contendrán los valores adecuados para los diferentes campos del mensaje a
		 * enviar como respuesta (operation, etc.)
		 */
		DirMessage mensajeRespuesta=null;

		switch (operation) {
		case DirMessageOps.OPERATION_PING: {
			/*
			 * TODO: (Boletín MensajesASCII) Comprobamos si el protocolId del mensaje del
			 * cliente coincide con el nuestro.
			 */
			if(mensajeRecibido.getProtocolId().equals(NanoFiles.PROTOCOL_ID)) {
				/*
				 * TODO: (Boletín MensajesASCII) Construimos un mensaje de respuesta que indique
				 * el éxito/fracaso del ping (compatible, incompatible), y lo devolvemos como
				 * resultado del método.
				 */
				
				mensajeRespuesta= new DirMessage(DirMessageOps.OPERATION_PING_OK);
				
			}else {
				mensajeRespuesta= new DirMessage(DirMessageOps.OPERATION_PING_FAILED);
				System.out.println("No se ha podido hacer ping con el servidor");
			}
			
			/*
			 * TODO: (Boletín MensajesASCII) Imprimimos por pantalla el resultado de
			 * procesar la petición recibida (éxito o fracaso) con los datos relevantes, a
			 * modo de depuración en el servidor
			 */
			System.out.println(mensajeRespuesta.toString());
			break;
		}

		case DirMessageOps.OPERATION_REGISTER_FILESERVER:{
			FileInfo[] listaFicheros = mensajeRecibido.getFileList();
			int serverPort = mensajeRecibido.getServerPort();
			InetSocketAddress server = new InetSocketAddress(clientAddress.getAddress().getHostName(), 10000);
			System.out.println("Server address: "+server.getAddress().getHostAddress()+" Server port: "+server.getPort());
			for(FileInfo f:listaFicheros) {
				String fileHash = f.fileHash;
				if (fileInfoMap.containsKey(fileHash)) {
					fileInfoMap.get(fileHash).add(f);
					
				}else {
					LinkedList<FileInfo> lista = new LinkedList<FileInfo>();
					lista.add(f);
					fileInfoMap.put(fileHash, lista);
				} 
				
				if(files.containsKey(f)) {
					files.get(f).add(server);
				}else {
					LinkedList<InetSocketAddress> servidores = new LinkedList<InetSocketAddress>();
					servidores.add(server);
					files.put(f, servidores);
				}
			}
			if(servers.containsKey(server)) {
				servers.get(server).addAll(Arrays.asList(listaFicheros));
			}else {
				LinkedList<FileInfo> ficheros = new LinkedList<>(Arrays.asList(listaFicheros));
				servers.put(server, ficheros);
			}

			mensajeRespuesta= new DirMessage(DirMessageOps.OPERATION_REGISTER_FILESERVER_OK);
			System.out.println(mensajeRespuesta.toString());
			break;
		}

		case DirMessageOps.OPERATION_GET_FILELIST:{
			mensajeRespuesta= new DirMessage(DirMessageOps.OPERATION_GET_FILELIST_OK);
			LinkedList<FileInfo> listaFicheros = new LinkedList<FileInfo>();
			for(FileInfo f:files.keySet()) {
				listaFicheros.add(f);
			}
			FileInfo[] listaFicherosArray = new FileInfo[listaFicheros.size()];
			listaFicherosArray = listaFicheros.toArray(listaFicherosArray);
			mensajeRespuesta.setFileList(listaFicherosArray);
			System.out.println(mensajeRespuesta.toString());
			break;
		}

		case DirMessageOps.OPERATION_GET_SERVERS_SHARING_THIS_FILE:{
			String subString = mensajeRecibido.getFileNameSubString();
			Set<FileInfo> ficheros = files.keySet();
			LinkedList<InetSocketAddress> servidores = new LinkedList<InetSocketAddress>();
			for(FileInfo f:ficheros) {
				if(f.fileName.contains(subString)) {
					servidores.addAll(files.get(f));
				}
			}
			mensajeRespuesta= new DirMessage(DirMessageOps.OPERATION_GET_SERVERS_SHARING_THIS_FILE_OK);
			mensajeRespuesta.setServerList(servidores);
			System.out.println(mensajeRespuesta.toString());
			break;
			
		}
		



		default:
			System.err.println("Unexpected message operation: \"" + operation + "\"");
			System.exit(-1);
		}

		/*
		 * TODO: (Boletín MensajesASCII) Convertir a String el objeto DirMessage
		 * (msgToSend) con el mensaje de respuesta a enviar, extraer los bytes en que se
		 * codifica el string y finalmente enviarlos en un datagrama
		 */
		String datosEnviar = mensajeRespuesta.toString();
		byte[] pktEnvio = datosEnviar.getBytes();
		DatagramPacket datagramaEnviar = new DatagramPacket(pktEnvio, pktEnvio.length,pkt.getSocketAddress());	
		socket.send(datagramaEnviar);

	}

	public static String getFileHasgByFileNameSubString(String subString){
		for(FileInfo f : files.keySet()) {
			if(f.fileName.contains(subString)) {
				return f.fileHash;
			}
		}
		return null;
	}
}
