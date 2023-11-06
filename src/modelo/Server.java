/**
 * 
 */
package modelo;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    /**
     * Lista de usuarios conectados al servidor. Esta lista es segura para ser
     * modificada
     * por múltiples hilos concurrentemente, lo cual es necesario ya que múltiples
     * clientes
     * pueden conectarse o desconectarse al mismo tiempo.
     */
    private static final List<String> connectedUsers = new CopyOnWriteArrayList<>();

    /**
     * Mapa que asocia el nombre de usuario con su respectivo PrintWriter.
     * El PrintWriter se utiliza para enviar mensajes al cliente. Esta estructura
     * es concurrente para permitir múltiples hilos acceder y modificarla sin
     * conflictos.
     */
    private static final Map<String, PrintWriter> userWriters = new ConcurrentHashMap<>();

    /**
     * Punto de entrada principal del servidor. Establece la conexión del servidor y
     * espera
     * indefinidamente a que los clientes se conecten.
     *
     * @param args Argumentos de línea de comandos, no utilizados en esta
     *             aplicación.
     * @throws IOException Si ocurre un error de entrada/salida durante la operación
     *                     del servidor.
     */
    public static void main(String[] args) throws IOException {
        String ipAddress = "0.0.0.0"; // Dirección IP donde el servidor estará escuchando.
        int port = 5000; // Puerto TCP donde el servidor aceptará conexiones.

        // Crea un socket de servidor atado a la dirección IP y puerto especificados.
        ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName(ipAddress));
        System.out.println("Servidor escuchando en " + ipAddress + ":" + port);

        // Bucle infinito para aceptar conexiones de clientes de forma continua.
        while (true) {
            Socket clientSocket = serverSocket.accept(); // Acepta una conexión de cliente.

            // Establece flujos de entrada y salida para comunicarse con el cliente.
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Lee el nombre de usuario enviado por el cliente.
            String userName = in.readLine();

            // Verifica si el nombre de usuario es válido y no está duplicado.
            if (userName != null && !userName.isEmpty() && !connectedUsers.contains(userName)) {
                connectedUsers.add(userName); // Agrega el usuario a la lista de conectados.
                userWriters.put(userName, out); // Asocia el PrintWriter con el nombre de usuario.
                updateConnectedUsers(); // Actualiza y notifica la lista de usuarios conectados.

                // Inicia un nuevo hilo para manejar la comunicación con el cliente conectado.
                Thread t = new Thread(new ClientHandler(clientSocket, userName));
                t.start();
            }
        }
    }

    /**
     * Actualiza la lista de usuarios conectados y envía la lista actualizada a
     * todos los clientes.
     * Este método se utiliza para informar a todos los usuarios del servidor sobre
     * el estado actual
     * de las conexiones, asegurando que cada cliente tenga la información más
     * reciente.
     */
    private static void updateConnectedUsers() {
        // Construye una cadena que representa a todos los usuarios conectados.
        String users = "Usuarios conectados: " + String.join(", ", connectedUsers);

        // Envía la cadena de usuarios conectados a cada cliente conectado al servidor.
        for (PrintWriter writer : userWriters.values()) {
            writer.println(users);
        }
    }

    /**
     * Clase que maneja la interacción con un cliente conectado al servidor.
     * Implementa {@code Runnable} para poder ser ejecutada por un hilo separado.
     * Se encarga de procesar los mensajes entrantes y manejar la desconexión del
     * cliente.
     */
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket; // Socket para comunicarse con el cliente.
        private final String userName; // Nombre de usuario del cliente conectado.
        private boolean disconnectedGracefully = false; // Indicador de si el cliente se desconectó de manera
                                                        // intencionada.

        /**
         * Constructor que inicializa el manejador con el socket del cliente y su nombre
         * de usuario.
         * 
         * @param socket   El socket a través del cual el servidor se comunica con el
         *                 cliente.
         * @param userName El nombre de usuario asignado al cliente.
         */
        public ClientHandler(Socket socket, String userName) {
            this.clientSocket = socket;
            this.userName = userName;
        }

        /**
         * Método sobreescrito de la interfaz Runnable que se ejecuta cuando el hilo del
         * cliente se inicia.
         * Se encarga de leer los mensajes del cliente de forma continua y procesar
         * cualquier acción solicitada.
         * Si el cliente envía un mensaje de 'chao', se trata como una solicitud de
         * desconexión y se cierra la conexión.
         * Cualquier excepción de E/S durante la comunicación se maneja de manera
         * adecuada.
         */
        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String input;
                // Bucle que lee los mensajes del cliente hasta que se desconecte.
                while ((input = in.readLine()) != null) {
                    // Comprueba si el cliente envió el mensaje de desconexión 'chao'.
                    if ("chao".equalsIgnoreCase(input)) {
                        System.out.println(userName + " solicitó desconexión.");
                        disconnectedGracefully = true; // Marca la desconexión como intencional.
                        break; // Rompe el bucle para proceder con el cierre de la conexión.
                    }
                    // Maneja cualquier otro mensaje entrante.
                    handleIncomingMessage(input);
                }
            } catch (IOException e) {
                // Si se produce una excepción y la desconexión no fue intencionada, se registra el error.
                if (!disconnectedGracefully) {
                    System.out.println("Error en la conexión con " + userName);
                }
            } finally {
                // Independientemente de cómo se salga del bucle, intenta cerrar la conexión de manera ordenada.
                handleDisconnection(); // Asegura que el usuario se elimine de las listas y se cierre el socket.
            }
        }

        /**
         * Procesa los mensajes entrantes del cliente. Diferencia entre mensajes
         * dirigidos al servidor
         * y mensajes destinados a otros usuarios, y actúa en consecuencia.
         * 
         * @param input El mensaje recibido del cliente.
         */
        private void handleIncomingMessage(String input) {
            // Comprueba si el mensaje está dirigido al servidor con un prefijo específico.
            if (input.startsWith("Servidor:")) {
                // Extrae el mensaje real quitando el prefijo.
                String messageDesconectionServer = input.substring("Servidor:".length());
                // Si el mensaje es una solicitud de desconexión, marca la desconexión como
                // intencionada.
                if ("chao".equalsIgnoreCase(input) || "Servidor:chao".equalsIgnoreCase(input)) {
                    disconnectedGracefully = true; // Establece la desconexión como intencional.
                    return; // Sale del método para permitir que la desconexión sea manejada en el bloque
                            // finally.
                }
                // Aquí se pueden agregar más comandos dirigidos al servidor si fuera necesario.
                System.out.println(userName + "--> " + messageDesconectionServer);
            } else {
                // Si el mensaje no está dirigido al servidor, se asume que es un mensaje a otro
                // usuario.
                // Intenta enviar el mensaje al usuario destinatario si existe.
                int splitIndex = input.indexOf(':');
                String recipient = input.substring(0, splitIndex);
                String message = input.substring(splitIndex + 1).trim();
                PrintWriter writer = userWriters.get(recipient);
                if (writer != null) {
                    // Envía el mensaje y registra la acción en la consola del servidor.
                    String directMessage = userName + " --> " + message;
                    writer.println(directMessage);
                    System.out.println(userName + " envió un mensaje directo a " + recipient + " --> " + message);
                }
            }
        }

        /**
         * Maneja el proceso de desconexión de un cliente. Este método se encarga de
         * eliminar
         * al usuario de la lista de usuarios conectados, cerrar su {@code PrintWriter}
         * y
         * su socket de conexión. Además, actualiza la lista de usuarios conectados
         * enviándola
         * a los clientes restantes. Si la desconexión fue intencional, imprime un
         * mensaje de
         * confirmación en el servidor.
         */
        private void handleDisconnection() {
            try {
                // Elimina al usuario de la lista de usuarios conectados y su writer asociado.
                connectedUsers.remove(userName);
                PrintWriter writer = userWriters.remove(userName);

                // Asegura que el writer del usuario se cierre correctamente.
                if (writer != null) {
                    writer.close();
                }

                // Notifica a los otros clientes la nueva lista de usuarios conectados.
                updateConnectedUsers();

                // Cierra el socket de conexión del cliente.
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException ex) {
                // La excepción se captura pero no se maneja, ya que no afecta la lógica de
                // desconexión.
            } finally {
                // Si la desconexión fue intencional, se registra en el servidor.
                if (disconnectedGracefully) {
                    System.out.println(userName + " se ha desconectado.");
                }
                // Restablece la bandera para futuras conexiones.
                disconnectedGracefully = false;
            }
        }

    }
}

