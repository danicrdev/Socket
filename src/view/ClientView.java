/**
 * 
 */
package view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import modelo.Client;

public class ClientView extends JFrame {

    private JTextField ipAddressField;
    private JTextField portField;
    private JTextField usernameField;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton connectButton;
    private JButton sendButton;
    private JComboBox<String> userDropdown;
    private  JButton selectButton;
    private JScrollPane chatScrollPane;
 

    private Client client;

    /**
     * La clase ClientView extiende JFrame y representa la interfaz gráfica de
     * usuario para el cliente de chat.
     * Contiene todos los componentes de la UI necesarios para interactuar con la
     * aplicación de chat.
     */
    public ClientView() {
        initializeUI();
        setClientListeners();
    }

    /**
     * Constructor que inicializa la vista del cliente.
     * Configura la interfaz de usuario y establece los oyentes para manejar las
     * acciones del usuario.
     */
    private void initializeUI() {


        // Configuraciones básicas del JFrame
        setTitle("Cliente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(565, 444);
        setLocationRelativeTo(null);

        // Configuración de paneles y componentes de la UI

        JPanel panel = new JPanel();
        getContentPane().add(panel);
        panel.setLayout(null);

        JLabel ipAddressLabel = new JLabel("IP del servidor:");
        ipAddressLabel.setBounds(10, 20, 100, 25);
        panel.add(ipAddressLabel);

        ipAddressField = new JTextField(20);
        ipAddressField.setBounds(120, 20, 165, 25);
        panel.add(ipAddressField);

        JLabel portLabel = new JLabel("Puerto:");
        portLabel.setBounds(10, 50, 80, 25);
        panel.add(portLabel);

        portField = new JTextField(20);
        portField.setBounds(120, 50, 165, 25);
        panel.add(portField);

        JLabel usernameLabel = new JLabel("Nombre de usuario:");
        usernameLabel.setBounds(10, 80, 120, 25);
        panel.add(usernameLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(120, 80, 165, 25);
        panel.add(usernameField);

        connectButton = new JButton("Conectar");
        connectButton.setBounds(10, 110, 100, 25);
        panel.add(connectButton);

        chatArea = new JTextArea();
        chatArea.setEditable(false); // Hace que chatArea no sea editable.
        chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setBounds(10, 197, 535, 150);
        panel.add(chatScrollPane);

        messageField = new JTextField();
        messageField.setBounds(10, 358, 200, 25);
        panel.add(messageField);

        sendButton = new JButton("Enviar");
        sendButton.setBounds(220, 358, 80, 25);
        panel.add(sendButton);

        setVisible(true);


        // Panel para la sección de selección de usuario
        JPanel userSelectionPanel = new JPanel();
        userSelectionPanel.setBounds(10, 162, 400, 30);
        userSelectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel selectUserLabel = new JLabel("Seleccionar usuario:");
        userSelectionPanel.add(selectUserLabel);

        // Agregar una lista desplegable para que el cliente elija con quién quiere
        // hablar
        userDropdown = new JComboBox<>();
        userDropdown.setPreferredSize(new Dimension(150, 20));
        userSelectionPanel.add(userDropdown);

        panel.add(userSelectionPanel);
                
                         // Agregar un botón para seleccionar un usuario y habilitar la funcionalidad de
                        // envío de mensajes
                        selectButton = new JButton("Seleccionar");
                        userSelectionPanel.add(selectButton);
                        selectButton.setPreferredSize(new Dimension(80, 20));


    }

    /**
     * Establece los oyentes de los eventos para los botones y otros componentes
     * interactivos de la UI.
     */
    private void setClientListeners() {

        // Oyentes para los botones 'connectButton', 'sendButton' y 'selectButton'
        connectButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                String ipAddress = ipAddressField.getText();
                int port = Integer.parseInt(portField.getText());
                String username = usernameField.getText();
                try {
                    client = new Client(ipAddress, port, username, chatArea, userDropdown);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });

       
        // Agregar un ActionListener para el botón de selección que permita al cliente
        // elegir con quién hablar
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (client != null) {
                    String recipient = (String) userDropdown.getSelectedItem();
                    if (recipient == null) {
                        JOptionPane.showMessageDialog(null, "Seleccione un usuario válido.");
                    }
                }
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (client != null) {
                    String recipient = (String) userDropdown.getSelectedItem(); // Puede ser null si no se ha seleccionado ningún usuario
                    String message = messageField.getText();
                    client.sendMessage(message, chatArea, recipient);
                    messageField.setText("");
                }
            }
        });

    }

}
