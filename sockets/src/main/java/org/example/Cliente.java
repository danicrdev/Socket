package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;


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
    }

}

class LaminaMarcoCliente extends JPanel{

    public LaminaMarcoCliente(){
        nick = new JTextField(5);
        add(nick);
        ip = new JTextField(8);
        add(ip);
        JLabel texto=new JLabel("CHAT");
        add(texto);
        campoChat = new JTextArea(12,20);
        add(campoChat);
        campo1=new JTextField(20);
        add(campo1);
        miboton=new JButton("Enviar");
        EnviaTexto miEvento = new EnviaTexto();
        miboton.addActionListener(miEvento);
        add(miboton);

    }
    /*
    Clase interna que gestiona los eventos del boton
     */
    private class EnviaTexto implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Socket socket = new Socket("192.168.1.188", 9999);
                Datos datos = new Datos();
                datos.setNick(nick.getText());
                datos.setIp(ip.getText());
                datos.setMenaje(campo1.getText());

                ObjectOutputStream paquete_datos = new ObjectOutputStream(socket.getOutputStream());
                paquete_datos.writeObject(datos);
                socket.close();

                /*DataOutputStream flujo_salida = new DataOutputStream(socket.getOutputStream());

                flujo_salida.writeUTF(campo1.getText()); // escribe en el flujo lo que hay en el campo1 y ese flujo circula por el socket
                System.out.println(campo1.getText());

                flujo_salida.close();*/

            } catch (IOException ex) {
                //System.out.println(ex.getMessage());
            }
        }
    }


    private JTextField campo1, nick, ip;
    private JTextArea campoChat;

    private JButton miboton;

}

class Datos implements Serializable {

    private String ip, menaje,nick;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMenaje() {
        return menaje;
    }

    public void setMenaje(String menaje) {
        this.menaje = menaje;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
