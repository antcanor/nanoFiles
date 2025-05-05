package es.um.redes.nanoFiles.tcp.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.udp.server.NFDirectoryServer;
import es.um.redes.nanoFiles.util.FileDigest;

//Esta clase proporciona la funcionalidad necesaria para intercambiar mensajes entre el cliente y el servidor
public class NFConnector {
	private Socket socket;
	private InetSocketAddress serverAddr;
	private DataInputStream dis;
	private DataOutputStream dos;



	public NFConnector(InetSocketAddress fserverAddr) throws UnknownHostException, IOException {
		serverAddr = fserverAddr;
		/*
		 * TODO: (Boletín SocketsTCP) Se crea el socket a partir de la dirección del
		 * servidor (IP, puerto). La creación exitosa del socket significa que la
		 * conexión TCP ha sido establecida.
		 */
		socket = new Socket(fserverAddr.getAddress(), fserverAddr.getPort());
		/*
		 * TODO: (Boletín SocketsTCP) Se crean los DataInputStream/DataOutputStream a
		 * partir de los streams de entrada/salida del socket creado. Se usarán para
		 * enviar (dos) y recibir (dis) datos del servidor.
		 */
		
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());

	}

	public void test() {
		/*
		 * TODO: (Boletín SocketsTCP) Enviar entero cualquiera a través del socket y
		 * después recibir otro entero, comprobando que se trata del mismo valor.
		 */
		
		try {
			dos.writeInt(5);
			int num = dis.readInt();
			System.out.println("Numero enviado: 5");
			System.out.println("Numero recibido: " + num);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public boolean downloadFile(String targetFilenameSubstr, File file) throws IOException {
		boolean downloaded = false;
		
		/*
		 * TODO: Recibir mensajes del servidor a través del "dis" del socket usando
		 * PeerMessage.readMessageFromInputStream, y actuar en función del tipo de
		 * mensaje recibido, extrayendo los valores necesarios de los atributos del
		 * objeto (valores de los campos del mensaje).
		 */
		/*
		 * TODO: Para escribir datos de un fichero recibidos en un mensaje, se puede
		 * crear un FileOutputStream a partir del parámetro "file" para escribir cada
		 * fragmento recibido (array de bytes) en el fichero mediante el método "write".
		 * Cerrar el FileOutputStream una vez se han escrito todos los fragmentos.
		 */
		

		PeerMessage request = new PeerMessage(PeerMessageOps.DOWNLOAD_FILE);
		request.setFileName(targetFilenameSubstr.getBytes());
		request.writeMessageToOutputStream(dos);

		FileOutputStream fos = new FileOutputStream(file);

		boolean endOfFile = false;
		while (!endOfFile) {
			PeerMessage response = PeerMessage.readMessageFromInputStream(dis);
			switch (response.getOpcode()) {
			case PeerMessageOps.FILE_NOT_FOUND:
				System.err.println("* File not found in server.");
				endOfFile = true;
				break;
			case PeerMessageOps.FILE:
				fos.write(response.getFileData(), 0, response.getFileData().length);
				break;
			case PeerMessageOps.END_OF_FILE:
				endOfFile = true;
				downloaded = true;


				break;
			default:
				System.err.println("* Unexpected response opcode: " + response.getOpcode());
				endOfFile = true;
				break;
			}
		}

		fos.close();

		return downloaded;
	}



	public InetSocketAddress getServerAddr() {
		return serverAddr;
	}

}
