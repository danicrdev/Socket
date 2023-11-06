/**
 * 
 */
package modelo;

//Dentro de la clase Client
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import view.ClientView;

/**
* La clase Client maneja la conexión de un cliente a un servidor de chat, así
* como la interacción con la interfaz de usuario del chat.
* Permite enviar mensajes y recibir actualizaciones del servidor para mostrar
* en la interfaz de usuario.
*/
public class Client {
  private Socket socket;
  private BufferedReader in;
  private PrintWriter out;
  private static JComboBox<String> userDropdown;
  private static String userName;

  /** 
   * @param args
   */
  public static void main(String[] args) {
      new ClientView();
  }

  /**
   * Constructor de la clase Cliente que establece la conexión con el servidor de
   * chat.
   * Inicializa la conexión de red, los flujos de entrada/salida y configura la
   * interfaz de usuario
   * para interacciones de chat. Inicia un hilo para leer mensajes entrantes del
   * servidor.
   *
   * @param ipAddress    La dirección IP del servidor de chat al que se conectará
   *                     el cliente.
   * @param port         El puerto del servidor de chat al que se conectará el
   *                     cliente.
   * @param username     El nombre de usuario que se registrará en el chat.
   * @param chatArea     Área de texto de la interfaz de usuario donde se
   *                     mostrarán los mensajes del chat.
   * @param userDropdown Componente ComboBox que muestra los usuarios conectados
   *                     actualmente.
   * @throws IOException Si hay un error al intentar establecer una conexión con
   *                     el servidor o
   *                     al inicializar los flujos de entrada/salida.
   */
  public Client(String ipAddress, int port, String username, JTextArea chatArea, JComboBox<String> userDropdown)
          throws IOException {
      // Asigna los componentes de la interfaz de usuario a las variables de
      // instancia.
      this.userDropdown = userDropdown;
      this.userName = username;

      // Establece la conexión con el servidor y crea los flujos de entrada y salida.
      socket = new Socket(ipAddress, port);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(socket.getOutputStream(), true);

      // Envía el nombre de usuario al servidor para registrarse.
      out.println(username);

      // Configura la interfaz de usuario agregando 'Servidor' como la primera opción
      // en el ComboBox.
      SwingUtilities.invokeLater(() -> {
          userDropdown.removeAllItems(); // Limpia el ComboBox antes de agregar nuevos elementos.
          userDropdown.addItem("Servidor"); // Agrega 'Servidor' como la primera opción.
          userDropdown.setSelectedItem("Servidor"); // Selecciona 'Servidor' por defecto.
          System.out.println("Servidor añadido al dropdown."); // Mensaje de depuración.
      });

      // Agrega el mensaje de conexión al área de chat.
      appendToChatArea("Conectado al servidor\n", chatArea);

      // Inicia un hilo para leer mensajes del servidor.
      Thread readerThread = new Thread(new MessageReader(in, chatArea, username));
      readerThread.start();
  }


  // mensaje que envia el cliente
  public void sendMessage(String message, JTextArea chatArea, String selectedUser) {
      if ("Servidor".equals(selectedUser)) {
          out.println("Servidor:" + message);
          if ("chao".equalsIgnoreCase(message)) {
              try {
                  socket.close(); // Cierra el socket para desconectar al cliente del servidor
                  resetInterface(chatArea);
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      } else {
          // Mensaje a un usuario o a todos los usuarios
          if (selectedUser != null && !selectedUser.isEmpty()) {
              // Envía el mensaje al servidor con el formato "emisor:message"
              out.println(selectedUser + ":" + message);
          } else {
              // Si no hay un usuario seleccionado, envía el mensaje a todos
              out.println(message);
          }
          // Muestra el mensaje en el chatArea del emisor sin el nombre del receptor
          appendToChatArea(userName + " --> " + message, chatArea);
      }
  }

  private static class MessageReader implements Runnable {
      private BufferedReader reader;
      private JTextArea chatArea;
      private String userName;

      public MessageReader(BufferedReader reader, JTextArea chatArea, String userName) {
          this.reader = reader;
          this.chatArea = chatArea;
          this.userName = userName;
      }

      /**
       * El método run escucha los mensajes del servidor y realiza acciones basadas en
       * el tipo de mensaje.
       */
      @Override
      public void run() {
          try {
              String message;
              // Bucle para leer mensajes del servidor y realizar acciones según el contenido del mensaje.
              while ((message = reader.readLine()) != null) {
                  if (message.startsWith("Usuarios conectados: ")) {
                      updateConnectedUsersDropdown(message);
                  } else if (message.startsWith("Mensaje directo de ")) {
                      appendToChatArea(message, chatArea);
                  } else {
                      appendToChatArea(message + "\n", chatArea); // Agregar mensaje al chatArea
                  }
              }
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
  }

  /**
   * Actualiza el JComboBox con la lista de usuarios conectados basándose en el
   * mensaje recibido del servidor.
   *
   * @param message El mensaje del servidor que contiene la lista de usuarios
   *                conectados.
   */
  private static void updateConnectedUsersDropdown(String message) {
      SwingUtilities.invokeLater(() -> {
          String usersList = message.substring("Usuarios conectados: ".length());
          usersList = usersList.replace("[", "").replace("]", ""); // Eliminar corchetes si están presentes
          String[] users = usersList.split(", ");
          userDropdown.removeAllItems();
          userDropdown.addItem("Servidor");
          for (String user : users) {
              if (!user.equals(userName)) {
                  userDropdown.addItem(user);
              }
          }
          userDropdown.setSelectedItem("Servidor"); // selecciona "Servidor" por defecto
      });
  }

  /**
   * Añade un mensaje al área de chat en la interfaz de usuario.
   *
   * @param message  El mensaje que se añadirá al área de chat.
   * @param chatArea El área de texto de la interfaz de usuario donde se mostrarán
   *                 los mensajes del chat.
   */
  public static void appendToChatArea(String message, JTextArea chatArea) {
      SwingUtilities.invokeLater(() -> {
          chatArea.append(message + "\n");
          // chatArea.setCaretPosition(chatArea.getDocument().getLength());

      });
  }

  // Método para restablecer la interfaz de usuario después de la desconexión
  private void resetInterface(JTextArea chatArea) {
      SwingUtilities.invokeLater(() -> {
          chatArea.setText(""); // Limpia el área de chat
          userDropdown.removeAllItems(); // Limpia la lista desplegable de usuarios
          userDropdown.addItem("Servidor"); // Agrega "Servidor" como opción
          userDropdown.setSelectedItem("Servidor"); // Selecciona "Servidor" por defecto
          // Aquí puedes agregar más lógica para restablecer otros componentes si es
          // necesario
      });
  }

}

