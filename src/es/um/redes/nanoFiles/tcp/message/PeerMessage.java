package es.um.redes.nanoFiles.tcp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import es.um.redes.nanoFiles.util.FileInfo;

public class PeerMessage {




	private byte opcode;

	/*
	 * TODO: (Boletín MensajesBinarios) Añadir atributos u otros constructores
	 * específicos para crear mensajes con otros campos, según sea necesario
	 * 
	 */
	private byte[] hash;
	private byte[] fileName;
	private byte[] fileData;



	public PeerMessage() {
		opcode = PeerMessageOps.OPCODE_INVALID_CODE;
	}

	public PeerMessage(byte op) {
		opcode = op;
	}

	/*
	 * TODO: (Boletín MensajesBinarios) Crear métodos getter y setter para obtener
	 * los valores de los atributos de un mensaje. Se aconseja incluir código que
	 * compruebe que no se modifica/obtiene el valor de un campo (atributo) que no
	 * esté definido para el tipo de mensaje dado por "operation".
	 */
	public byte getOpcode() {
		return opcode;
	}

	public byte[] getHash() {
		return this.hash;
	}

	public byte[] getFileNname() {
		return this.fileName;
	}

	public byte[] getFileData() {
		return this.fileData;
	}

	public void setOpcode(byte opcode) {
		this.opcode = opcode;
	}

	public void setHash(byte[] hash) {
		this.hash = hash;
	}

	public void setFileName(byte[] fileName) {
		this.fileName = fileName;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}



	/**
	 * Método de clase para parsear los campos de un mensaje y construir el objeto
	 * DirMessage que contiene los datos del mensaje recibido
	 * 
	 * @param data El array de bytes recibido
	 * @return Un objeto de esta clase cuyos atributos contienen los datos del
	 *         mensaje recibido.
	 * @throws IOException
	 */
	public static PeerMessage readMessageFromInputStream(DataInputStream dis) throws IOException {
		/*
		 * TODO: (Boletín MensajesBinarios) En función del tipo de mensaje, leer del
		 * socket a través del "dis" el resto de campos para ir extrayendo con los
		 * valores y establecer los atributos del un objeto DirMessage que contendrá
		 * toda la información del mensaje, y que será devuelto como resultado. NOTA:
		 * Usar dis.readFully para leer un array de bytes, dis.readInt para leer un
		 * entero, etc.
		 */
		PeerMessage message = new PeerMessage();
		byte opcode = dis.readByte();
		message.setOpcode(opcode);
		int hashLength;
		byte[] hashBuffer;
		switch (opcode) {
			case PeerMessageOps.FILE_NOT_FOUND:
				break;
			case PeerMessageOps.DOWNLOAD_FILE:
				hashLength = dis.readInt();
				hashBuffer = new byte[hashLength];
				dis.readFully(hashBuffer);
				int nameLength = dis.readInt();
				byte[] nameBuffer = new byte[nameLength];
				dis.readFully(nameBuffer);
				message.setHash(hashBuffer);
				message.setFileName(nameBuffer);
				break;
			case PeerMessageOps.FILE:
				int dataLength = dis.readInt();
				byte[] dataBuffer = new byte[dataLength];
				dis.readFully(dataBuffer);
				message.setFileData(dataBuffer);
				break;
			case PeerMessageOps.END_OF_FILE:
				hashLength = dis.readInt();
				hashBuffer = new byte[hashLength];
				dis.readFully(hashBuffer);
				message.setHash(hashBuffer);
				break;

		default:
			System.err.println("PeerMessage.readMessageFromInputStream doesn't know how to parse this message opcode: "
					+ PeerMessageOps.opcodeToOperation(opcode));
			System.exit(-1);
		}
		return message;
	}

	public void writeMessageToOutputStream(DataOutputStream dos) throws IOException {
		/*
		 * TODO (Boletín MensajesBinarios): Escribir los bytes en los que se codifica el
		 * mensaje en el socket a través del "dos", teniendo en cuenta opcode del
		 * mensaje del que se trata y los campos relevantes en cada caso. NOTA: Usar
		 * dos.write para leer un array de bytes, dos.writeInt para escribir un entero,
		 * etc.
		 */

		dos.writeByte(opcode);
		switch (opcode) {
			case PeerMessageOps.FILE_NOT_FOUND:
				break;
			case PeerMessageOps.DOWNLOAD_FILE:
				dos.writeInt(hash.length);
				dos.write(hash);
				dos.writeInt(fileName.length);
				dos.write(fileName);
				break;
			case PeerMessageOps.FILE:
				dos.writeInt(fileData.length);
				dos.write(fileData);
				break;
			case PeerMessageOps.END_OF_FILE:
				dos.writeInt(hash.length);
				dos.write(hash);
				break;

		default:
			System.err.println("PeerMessage.writeMessageToOutputStream found unexpected message opcode " + opcode + "("
					+ PeerMessageOps.opcodeToOperation(opcode) + ")");
		}
	}




}
