package es.um.redes.nanoFiles.udp.message;

public class DirMessageOps {

	/*
	 * TODO: (Boletín MensajesASCII) Añadir aquí todas las constantes que definen
	 * los diferentes tipos de mensajes del protocolo de comunicación con el
	 * directorio (valores posibles del campo "operation").
	 */
	public static final String OPERATION_INVALID = "invalid_operation";
	public static final String OPERATION_PING = "ping";
	public static final String OPERATION_PING_OK = "welcome";
	public static final String OPERATION_PING_FAILED = "denied";
	public static final String OPERATION_GET_FILELIST = "filelist";
	public static final String OPERATION_GET_FILELIST_OK = "filelist_ok";
	public static final String OPERATION_REGISTER_FILESERVER = "fileserver";
	public static final String OPERATION_REGISTER_FILESERVER_OK = "fileserver_ok";
	public static final String OPERATION_GET_SERVERS_SHARING_THIS_FILE = "servers_sharing";
	public static final String OPERATION_GET_SERVERS_SHARING_THIS_FILE_OK = "servers_sharing_ok";
	public static final String OPERATION_UNREGISTER_FILESERVER = "unregister_fileserver";
	public static final String OPERATION_UNREGISTER_FILESERVER_OK = "unregister_fileserver_ok";
	public static final String OPERATION_UNREGISTER_FILESERVER_FAILED = "unregister_fileserver_failed";

	




}
