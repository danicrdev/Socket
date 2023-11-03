/**
 * 
 */
package view;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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

    private Client client;

    public ClientView() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Cliente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(565, 375);
        setLocationRelativeTo(null);

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
        chatArea.setBounds(10, 191, 375, 100);
        panel.add(chatArea);

        messageField = new JTextField();
        messageField.setBounds(10, 300, 200, 25);
        panel.add(messageField);

        sendButton = new JButton("Enviar");
        sendButton.setBounds(220, 300, 80, 25);
        panel.add(sendButton);

        setVisible(true);


        // Panel para la sección de selección de usuario
        JPanel userSelectionPanel = new JPanel();
        userSelectionPanel.setBounds(10, 162, 375, 30);
        userSelectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel selectUserLabel = new JLabel("Seleccionar usuario:");
        userSelectionPanel.add(selectUserLabel);

        // Agregar una lista desplegable para que el cliente elija con quién quiere
        // hablar
        userDropdown = new JComboBox<>();
        userDropdown.setPreferredSize(new Dimension(150, 20));
        userSelectionPanel.add(userDropdown);

         // Agregar un botón para seleccionar un usuario y habilitar la funcionalidad de
        // envío de mensajes
        selectButton = new JButton("Seleccionar");
        selectButton.setPreferredSize(new Dimension(80, 20));
        userSelectionPanel.add(selectButton);

        panel.add(userSelectionPanel);


    }
    
}
