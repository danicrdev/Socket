package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class Cliente {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        MarcoCliente mimarco=new MarcoCliente();
        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}


class MarcoCliente extends JFrame{

    public MarcoCliente(){

        setBounds(600,300,280,350);
        LaminaMarcoCliente milamina=new LaminaMarcoCliente();
        add(milamina);
        setVisible(true);
        addWindowListener(new EnvioConectado());
    }
}

class EnvioConectado extends WindowAdapter{

    /**
     * Apenas se abre la venta le envia señal al servidor que elusuario está en línea
     * @param e the event to be processed
     */
    public void windowOpened(WindowEvent e){
        try{
            Socket socket=new Socket("192.168.1.188", 9999);
            Datos datos = new Datos();
            datos.setMensaje("Conectado");
            ObjectOutputStream paquete = new ObjectOutputStream(socket.getOutputStream());
            paquete.writeObject(datos);
            socket.close();

        }catch(Exception ex){

        }
    }
}

class LaminaMarcoCliente extends JPanel implements  Runnable{

    public LaminaMarcoCliente(){
        String nick_usuario=JOptionPane.showInputDialog("Nick: ");
        JLabel usuario = new JLabel("Usuario: ");
        add(usuario);
        nick = new JLabel();
        nick.setText(nick_usuario);
        add(nick);
        JLabel texto=new JLabel("Conectados: ");
        add(texto);
        ip = new JComboBox();
        /*ip.addItem("192.168.1.188");
        ip.addItem("192.168.1.198");*/
        add(ip);

        campoChat = new JTextArea(12,20);
        add(campoChat);
        campo1=new JTextField(20);
        add(campo1);
        miboton=new JButton("Enviar");
        EnviaTexto miEvento = new EnviaTexto();
        miboton.addActionListener(miEvento);
        add(miboton);
        Thread hilo = new Thread(this);
        hilo.start();

    }

    @Override

    public void run() {
        try{
            ServerSocket servidor_cliente = new ServerSocket(9090);
            Socket cliente;
            Datos datosRecibidos;
            while(true){
                cliente = servidor_cliente.accept();
                ObjectInputStream flujo_entrada = new ObjectInputStream(cliente.getInputStream());
                datosRecibidos= (Datos) flujo_entrada.readObject();
                if (!datosRecibidos.getMensaje().equalsIgnoreCase("Conectado")) {
                    campoChat.append("\n" + datosRecibidos.getNick() + ": " + datosRecibidos.getMensaje());
                } else {
                    campoChat.append("\n" + datosRecibidos.getListaIps());
                    ArrayList<String> desplegable = new ArrayList<>();
                    desplegable = datosRecibidos.getListaIps();
                    ip.removeAllItems();
                    for (String i : desplegable) {
                        ip.addItem(i);
                    }
                }
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    /*
    Clase interna que gestiona los eventos del boton
     */
    private class EnviaTexto implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            campoChat.append("\n"+campo1.getText());
            try {

                Socket socket = new Socket("192.168.1.188", 9999);
                Datos datos = new Datos();
                datos.setNick(nick.getText());
                datos.setIp(ip.getSelectedItem().toString());
                datos.setMensaje(campo1.getText());

                ObjectOutputStream paquete_datos = new ObjectOutputStream(socket.getOutputStream());
                paquete_datos.writeObject(datos);
                socket.close();
                campo1.setText("");


            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }


    private JTextField campo1;
    private JComboBox ip;
    private JLabel nick;
    private JTextArea campoChat;

    private JButton miboton;

}

class Datos implements Serializable {

    private String ip, mensaje,nick;
    private ArrayList<String>listaIps;

    public ArrayList<String> getListaIps() {
        return listaIps;
    }

    public void setListaIps(ArrayList<String> listaIps) {
        this.listaIps = listaIps;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
