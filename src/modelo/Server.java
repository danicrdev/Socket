/**
 * 
 */
package modelo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
/**
 * 
 */
public class Server {
	
    
    /**
     * Método principal del servidor que escucha las conexiones de los clientes.
     *
     * @param args Argumentos de la línea de comandos (no se utilizan en este caso).
     * @throws IOException Excepción de E/S que puede ocurrir durante la
     *                     comunicación con los clientes.
     */
	public static void main(String[] args) throws IOException {
		
		// esto debe cambiar cuando el servidor funcione con una ip publica para conectar los clientes en diferentes redes
		// ip de prueba
		String ipAddress = "localhost"; // Dirección IP del servidor.
        int port = 5000; // Puerto del servidor.

        // Configuración del socket del servidor para escuchar en la dirección IP y
        // puerto especificados.
        ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName(ipAddress));
        System.out.println("Servidor escuchando en " + ipAddress + " puerto " + port);
        
           
	}

}
