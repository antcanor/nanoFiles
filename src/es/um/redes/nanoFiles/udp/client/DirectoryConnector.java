package es.um.redes.nanoFiles.udp.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.List;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.udp.message.DirMessage;
import es.um.redes.nanoFiles.udp.message.DirMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;

/**
 * Cliente con métodos de consulta y actualización específicos del directorio
 */
public class DirectoryConnector {
	/**
	 * Puerto en el que atienden los servidores de directorio
	 */
	private static final int DIRECTORY_PORT = 6868;
	/**
	 * Tiempo máximo en milisegundos que se esperará a recibir una respuesta por el
	 * socket antes de que se deba lanzar una excepción SocketTimeoutException para
	 * recuperar el control
	 */
	private static final int TIMEOUT = 1000;
	/**
	 * Número de intentos máximos para obtener del directorio una respuesta a una
	 * solicitud enviada. Cada vez que expira el timeout sin recibir respuesta se
	 * cuenta como un intento.
	 */
	private static final int MAX_NUMBER_OF_ATTEMPTS = 5;

	/**
	 * Socket UDP usado para la comunicación con el directorio
	 */
	private DatagramSocket socket;
	/**
	 * Dirección de socket del directorio (IP:puertoUDP)
	 */
	private InetSocketAddress directoryAddress;
	/**
	 * Nombre/IP del host donde se ejecuta el directorio
	 */
	private String directoryHostname;





	public DirectoryConnector(String hostname) throws IOException {
		// Guardamos el string con el nombre/IP del host
		directoryHostname = hostname;
		/*
		 * TODO: (Boletín SocketsUDP) Convertir el string 'hostname' a InetAddress y
		 * guardar la dirección de socket (address:DIRECTORY_PORT) del directorio en el
		 * atributo directoryAddress, para poder enviar datagramas a dicho destino.
		 */
		InetSocketAddress socket_address = new InetSocketAddress(InetAddress.getByName(hostname), DIRECTORY_PORT);
		this.directoryAddress = socket_address;
		
		
		/*
		 * TODO: (Boletín SocketsUDP) Crea el socket UDP en cualquier puerto para enviar
		 * datagramas al directorio
		 */
		this.socket= new DatagramSocket();



	}

	/**
	 * Método para enviar y recibir datagramas al/del directorio
	 * 
	 * @param requestData los datos a enviar al directorio (mensaje de solicitud)
	 * @return los datos recibidos del directorio (mensaje de respuesta)
	 */
	private byte[] sendAndReceiveDatagrams(byte[] requestData) {
		byte responseData[] = new byte[DirMessage.PACKET_MAX_SIZE];
		byte response[] = null;
		if (directoryAddress == null) {
			System.err.println("DirectoryConnector.sendAndReceiveDatagrams: UDP server destination address is null!");
			System.err.println(
					"DirectoryConnector.sendAndReceiveDatagrams: make sure constructor initializes field \"directoryAddress\"");
			System.exit(-1);

		}
		if (socket == null) {
			System.err.println("DirectoryConnector.sendAndReceiveDatagrams: UDP socket is null!");
			System.err.println(
					"DirectoryConnector.sendAndReceiveDatagrams: make sure constructor initializes field \"socket\"");
			System.exit(-1);
		}
		/*
		 * TODO: (Boletín SocketsUDP) Enviar datos en un datagrama al directorio y
		 * recibir una respuesta. El array devuelto debe contener únicamente los datos
		 * recibidos, *NO* el búfer de recepción al completo.
		 */
		DatagramPacket packetToServer = new DatagramPacket(requestData,requestData.length,directoryAddress);
		DatagramPacket paquetesDelServidor = new DatagramPacket(responseData, responseData.length);
		
		int intentos=0;
		boolean hayRespuesta=false;
		
		while(intentos<MAX_NUMBER_OF_ATTEMPTS && !hayRespuesta) {
			try {
				socket.send(packetToServer);
				
				socket.setSoTimeout(TIMEOUT);
				socket.receive(paquetesDelServidor);	
				hayRespuesta=true;
			}catch (SocketTimeoutException e) {
				System.err.println("Timeout exception. Intento " + (intentos + 1) + " de " + MAX_NUMBER_OF_ATTEMPTS);
				intentos++;
			}catch (IOException e) {
				System.err.println("Error al recibir respuestas del directorio.");
			}
		}
		/*
		 * TODO: (Boletín SocketsUDP) Una vez el envío y recepción asumiendo un canal
		 * confiable (sin pérdidas) esté terminado y probado, debe implementarse un
		 * mecanismo de retransmisión usando temporizador, en caso de que no se reciba
		 * respuesta en el plazo de TIMEOUT. En caso de salte el timeout, se debe volver
		 * a enviar el datagrama y tratar de recibir respuestas, reintentando como
		 * máximo en MAX_NUMBER_OF_ATTEMPTS ocasiones.
		 */
		/*
		 * TODO: (Boletín SocketsUDP) Las excepciones que puedan lanzarse al
		 * leer/escribir en el socket deben ser capturadas y tratadas en este método. Si
		 * se produce una excepción de entrada/salida (error del que no es posible
		 * recuperarse), se debe informar y terminar el programa.
		 */
		/*
		 * NOTA: Las excepciones deben tratarse de la más concreta a la más genérica.
		 * SocketTimeoutException es más concreta que IOException.
		 */

		if(!hayRespuesta) {
			System.err.println("No ha habido respuesta tras "+MAX_NUMBER_OF_ATTEMPTS+" intentos.");
		}else {
			int longitudRespuesta=paquetesDelServidor.getLength();
			response=new byte[longitudRespuesta];
			System.arraycopy(responseData, 0, response, 0, longitudRespuesta);
		}
		

		if (response != null && response.length == responseData.length) {
			System.err.println("Your response is as large as the datagram reception buffer!!\n"
					+ "You must extract from the buffer only the bytes that belong to the datagram!");
		}
		return response;
	}

	/**
	 * Método para probar la comunicación con el directorio mediante el envío y
	 * recepción de mensajes sin formatear ("en crudo")
	 * 
	 * @return verdadero si se ha enviado un datagrama y recibido una respuesta
	 */
	public boolean testSendAndReceive() {
		/*
		 * TODO: (Boletín SocketsUDP) Probar el correcto funcionamiento de
		 * sendAndReceiveDatagrams. Se debe enviar un datagrama con la cadena "ping" y
		 * comprobar que la respuesta recibida empieza por "pingok". En tal caso,
		 * devuelve verdadero, falso si la respuesta no contiene los datos esperados.
		 */
		boolean success = false;
		String mensaje="ping";

		byte[] dataToServer = mensaje.getBytes();
		byte[] dataFromServer = null;
		
		dataFromServer = sendAndReceiveDatagrams(dataToServer);
		if(dataFromServer != null) {
			int longitudDatos = dataFromServer.length;
			String messageFromServer = new String(dataFromServer, 0, longitudDatos);
			if(messageFromServer.equals("pingok")) {
				success=true;
			}
		}
		return success;
	}

	public String getDirectoryHostname() {
		return directoryHostname;
	}

	/**
	 * Método para "hacer ping" al directorio, comprobar que está operativo y que
	 * usa un protocolo compatible. Este método no usa mensajes bien formados.
	 * 
	 * @return Verdadero si
	 */
	public boolean pingDirectoryRaw() {
		boolean success = false;
		/*
		 * TODO: (Boletín EstructuraNanoFiles) Basándose en el código de
		 * "testSendAndReceive", contactar con el directorio, enviándole nuestro
		 * PROTOCOL_ID (ver clase NanoFiles). Se deben usar mensajes "en crudo" (sin un
		 * formato bien definido) para la comunicación.
		 * 
		 * PASOS: 1.Crear el mensaje a enviar (String "ping&protocolId"). 
		 */
		String mensaje="ping&"+NanoFiles.PROTOCOL_ID;
		 /* 2.Crear un datagrama con los bytes en que se codifica la cadena : */
		
		byte[] bytesDatagrama = mensaje.getBytes();
		
		  /*4.Enviar datagrama y recibir una respuesta (sendAndReceiveDatagrams). : */
		byte[] dataFromServer=null;
		dataFromServer = sendAndReceiveDatagrams(bytesDatagrama);
		
		/*5. Comprobar si la cadena recibida en el datagrama de respuesta es "welcome", imprimir si éxito o
		 * fracaso.  */
		if(dataFromServer!=null) {
			String datagramaRespuesta = new String(dataFromServer,0,dataFromServer.length);
			if(datagramaRespuesta.equals("welcome")) {
				success=true;
				System.out.println("**EXITO**");
			}else {
				System.err.println("**FRACASO**");
			}
		}
		//6.Devolver éxito/fracaso de la operación	
			
		return success;
	}

	/**
	 * Método para "hacer ping" al directorio, comprobar que está operativo y que es
	 * compatible.
	 * 
	 * @return Verdadero si el directorio está operativo y es compatible
	 */
	public boolean pingDirectory() {
		boolean success = false;
		/*
		 * TODO: (Boletín MensajesASCII) Hacer ping al directorio 1.Crear el mensaje a
		 * enviar (objeto DirMessage) con atributos adecuados (operation, etc.) NOTA:
		 * Usar como operaciones las constantes definidas en la clase DirMessageOps :
		 */
		
		DirMessage mensajeEnviar= new DirMessage(DirMessageOps.OPERATION_PING);
		mensajeEnviar.setProtocolID(NanoFiles.PROTOCOL_ID);
		
		/* 2.Convertir el objeto DirMessage a enviar a un string (método toString)*/
		String mensajeString = mensajeEnviar.toString();
		
		
		/* 3.Crear un datagrama con los bytes en que se codifica la cadena : */
		
		byte[] bytesDatagrama = mensajeString.getBytes();
		
		/*4.Enviar datagrama y recibir una respuesta (sendAndReceiveDatagrams). :*/
		
		byte[] dataFromServer=sendAndReceiveDatagrams(bytesDatagrama);
		
		if (dataFromServer == null || dataFromServer.length == 0) {
	        System.err.println("Error: No se recibió respuesta del servidor.");
	        return false;
	    }
		
		/* 5.Convertir respuesta recibida en un objeto DirMessage (método DirMessage.fromString)*/
		String messageFromServer = new String(dataFromServer, 0, dataFromServer.length);
		DirMessage respuesta = DirMessage.fromString(messageFromServer);
		
		/* 6.Extraer datos del objeto DirMessage y procesarlos 7.Devolver éxito/fracaso
		 * de la operación
		 */
		
		if (respuesta != null && DirMessageOps.OPERATION_PING_OK.equals(respuesta.getOperation())) {
	        success = true;
	    }



		return success;
	}

	/**
	 * Método para dar de alta como servidor de ficheros en el puerto indicado y
	 * publicar los ficheros que este peer servidor está sirviendo.
	 * 
	 * @param serverPort El puerto TCP en el que este peer sirve ficheros a otros
	 * @param files      La lista de ficheros que este peer está sirviendo.
	 * @return Verdadero si el directorio tiene registrado a este peer como servidor
	 *         y acepta la lista de ficheros, falso en caso contrario.
	 */
	public boolean registerFileServer(int serverPort, FileInfo[] files) {
		boolean success = false;

		// TODO: Ver TODOs en pingDirectory y seguir esquema similar

		/*
		 * TODO: (Boletín MensajesASCII) Hacer ping al directorio 1.Crear el mensaje a
		 * enviar (objeto DirMessage) con atributos adecuados (operation, etc.) NOTA:
		 * Usar como operaciones las constantes definidas en la clase DirMessageOps :
		 */
		
		DirMessage mensajeEnviar= new DirMessage(DirMessageOps.OPERATION_REGISTER_FILESERVER);	
		mensajeEnviar.setServerPort(serverPort);
		mensajeEnviar.setFileList(files);
		
		/* 2.Convertir el objeto DirMessage a enviar a un string (método toString)*/
		String mensajeString = mensajeEnviar.toString();
		/* 3.Crear un datagrama con los bytes en que se codifica la cadena : */
		
		byte[] bytesDatagrama = mensajeString.getBytes();
		
		/*4.Enviar datagrama y recibir una respuesta (sendAndReceiveDatagrams). :*/
		
		byte[] dataFromServer=sendAndReceiveDatagrams(bytesDatagrama);
		
		
		/* 5.Convertir respuesta recibida en un objeto DirMessage (método DirMessage.fromString)*/
		String messageFromServer = new String(dataFromServer, 0, dataFromServer.length);
		DirMessage respuesta = DirMessage.fromString(messageFromServer);
		
		/* 6.Extraer datos del objeto DirMessage y procesarlos 7.Devolver éxito/fracaso
		 * de la operación
		 */
		if (respuesta != null && DirMessageOps.OPERATION_PING_FAILED.equals(respuesta.getOperation())) {
			System.err.println("Error: Se debe hacer ping con el directorio, antes de cualquier otro comando.");
	    }
		if (respuesta != null && DirMessageOps.OPERATION_REGISTER_FILESERVER_OK.equals(respuesta.getOperation())) {
	        success = true;
	    }

		return success;
	}

	/**
	 * Método para obtener la lista de ficheros que los peers servidores han
	 * publicado al directorio. Para cada fichero se debe obtener un objeto FileInfo
	 * con nombre, tamaño y hash. Opcionalmente, puede incluirse para cada fichero,
	 * su lista de peers servidores que lo están compartiendo.
	 * 
	 * @return Los ficheros publicados al directorio, o null si el directorio no
	 *         pudo satisfacer nuestra solicitud
	 */
	public FileInfo[] getFileList() {
		FileInfo[] filelist = null;
		// TODO: Ver TODOs en pingDirectory y seguir esquema similar
		
		DirMessage mensajeEnviar= new DirMessage(DirMessageOps.OPERATION_GET_FILELIST);		
		/* 2.Convertir el objeto DirMessage a enviar a un string (método toString)*/
		String mensajeString = mensajeEnviar.toString();
		/* 3.Crear un datagrama con los bytes en que se codifica la cadena : */
		
		byte[] bytesDatagrama = mensajeString.getBytes();
		
		/*4.Enviar datagrama y recibir una respuesta (sendAndReceiveDatagrams). :*/
		
		byte[] dataFromServer=sendAndReceiveDatagrams(bytesDatagrama);
		
		
		/* 5.Convertir respuesta recibida en un objeto DirMessage (método DirMessage.fromString)*/
		String messageFromServer = new String(dataFromServer, 0, dataFromServer.length);
		DirMessage respuesta = DirMessage.fromString(messageFromServer);
		
		/* 6.Extraer datos del objeto DirMessage y procesarlos 7.Devolver éxito/fracaso
		 * de la operación
		 */
		if (respuesta != null && DirMessageOps.OPERATION_PING_FAILED.equals(respuesta.getOperation())) {
			System.err.println("Error: Se debe hacer ping con el directorio, antes de cualquier otro comando.");
	    }
		if (respuesta != null && DirMessageOps.OPERATION_GET_FILELIST_OK.equals(respuesta.getOperation())) {
			FileInfo[] files = respuesta.getFileList();
			filelist = files;
	    }


		return filelist;
	}

	/**
	 * Método para obtener la lista de servidores que tienen un fichero cuyo nombre
	 * contenga la subcadena dada.
	 * 
	 * @filenameSubstring Subcadena del nombre del fichero a buscar
	 * 
	 * @return La lista de direcciones de los servidores que han publicado al
	 *         directorio el fichero indicado. Si no hay ningún servidor, devuelve
	 *         una lista vacía.
	 */
	public InetSocketAddress[] getServersSharingThisFile(String filenameSubstring) {
		// TODO: Ver TODOs en pingDirectory y seguir esquema similar
		InetSocketAddress[] serversList = new InetSocketAddress[0];

		DirMessage mensajeEnviar= new DirMessage(DirMessageOps.OPERATION_GET_SERVERS_SHARING_THIS_FILE);
		mensajeEnviar.setFileNameSubString(filenameSubstring);
		
		/* 2.Convertir el objeto DirMessage a enviar a un string (método toString)*/
		String mensajeString = mensajeEnviar.toString();
		
		
		/* 3.Crear un datagrama con los bytes en que se codifica la cadena : */
		
		byte[] bytesDatagrama = mensajeString.getBytes();
		
		/*4.Enviar datagrama y recibir una respuesta (sendAndReceiveDatagrams). :*/
		
		byte[] dataFromServer=sendAndReceiveDatagrams(bytesDatagrama);
		
		if (dataFromServer == null || dataFromServer.length == 0) {
	        System.err.println("Error: No se recibió respuesta del servidor.");
	        return serversList;
	    }
		
		/* 5.Convertir respuesta recibida en un objeto DirMessage (método DirMessage.fromString)*/
		String messageFromServer = new String(dataFromServer, 0, dataFromServer.length);
		DirMessage respuesta = DirMessage.fromString(messageFromServer);
		
		/* 6.Extraer datos del objeto DirMessage y procesarlos 7.Devolver éxito/fracaso
		 * de la operación
		 */
		if (respuesta != null && DirMessageOps.OPERATION_PING_FAILED.equals(respuesta.getOperation())) {
			System.err.println("Error: Se debe hacer ping con el directorio, antes de cualquier otro comando.");
	    }
		if (respuesta != null && DirMessageOps.OPERATION_GET_SERVERS_SHARING_THIS_FILE_OK.equals(respuesta.getOperation())) {
			List<InetSocketAddress> servers = respuesta.getServerList();
			serversList = servers.toArray(new InetSocketAddress[servers.size()]);
	        
	    }

		return serversList;
	}

	/**
	 * Método para darse de baja como servidor de ficheros.
	 * 
	 * @return Verdadero si el directorio tiene registrado a este peer como servidor
	 *         y ha dado de baja sus ficheros.
	 */
	public boolean unregisterFileServer() {
		boolean success = false;
		
		DirMessage mensajeEnviar= new DirMessage(DirMessageOps.OPERATION_UNREGISTER_FILESERVER);	
		
		/* 2.Convertir el objeto DirMessage a enviar a un string (método toString)*/
		String mensajeString = mensajeEnviar.toString();
		/* 3.Crear un datagrama con los bytes en que se codifica la cadena : */
		
		byte[] bytesDatagrama = mensajeString.getBytes();
		
		/*4.Enviar datagrama y recibir una respuesta (sendAndReceiveDatagrams). :*/
		
		byte[] dataFromServer=sendAndReceiveDatagrams(bytesDatagrama);
		
		
		/* 5.Convertir respuesta recibida en un objeto DirMessage (método DirMessage.fromString)*/
		String messageFromServer = new String(dataFromServer, 0, dataFromServer.length);
		DirMessage respuesta = DirMessage.fromString(messageFromServer);
		
		/* 6.Extraer datos del objeto DirMessage y procesarlos 7.Devolver éxito/fracaso
		 * de la operación
		 */
		if (respuesta != null && DirMessageOps.OPERATION_PING_FAILED.equals(respuesta.getOperation())) {
			System.err.println("Error: Se debe hacer ping con el directorio, antes de cualquier otro comando.");
	    }
		if (respuesta != null && DirMessageOps.OPERATION_UNREGISTER_FILESERVER_OK.equals(respuesta.getOperation())) {
	        success = true;
	    }else if (respuesta != null && DirMessageOps.OPERATION_UNREGISTER_FILESERVER_FAILED.equals(respuesta.getOperation())) {
	        success = false;
			
		}

		return success;
	}




}
