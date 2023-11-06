package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Servidor  {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        MarcoServidor mimarco=new MarcoServidor();
        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}

class MarcoServidor extends JFrame  implements Runnable{

    public MarcoServidor(){

        setBounds(1200,300,280,350);
        JPanel milamina= new JPanel();
        milamina.setLayout(new BorderLayout());
        areaTexto=new JTextArea();
        milamina.add(areaTexto,BorderLayout.CENTER);
        add(milamina);
        setVisible(true);
        Thread hilo = new Thread(this);
        hilo.start();

    }

    private	JTextArea areaTexto;

    @Override
    public void run() {

        try {
            ServerSocket servidor = new ServerSocket(9999);
            String nick, ip, mensaje;
            Datos datosRecibidos;
            ArrayList<String>listaIp=new ArrayList<String>();


            while(true) {
                Socket socket = servidor.accept();

                ObjectInputStream datos = new ObjectInputStream(socket.getInputStream());
                datosRecibidos = (Datos) datos.readObject();
                socket.close();
                nick=datosRecibidos.getNick();
                ip=datosRecibidos.getIp();
                mensaje=datosRecibidos.getMensaje();

                if(!mensaje.equalsIgnoreCase("Conectado")){

                areaTexto.append(("\n"+ nick + ": " + mensaje)+ " para " + ip);
                Socket envioAdestino = new Socket(ip,9090);
                ObjectOutputStream datosDestino = new ObjectOutputStream(envioAdestino.getOutputStream());
                datosDestino.writeObject(datosRecibidos);
                datosDestino.close();
                envioAdestino.close();
                socket.close();

                }else{
                    /*
                    Detecta a los usuarios que están conectados
                     */
                    InetAddress localizacion = socket.getInetAddress();
                    String iPRemota=localizacion.getHostAddress();
                    //System.out.println("Online; " + iPRemota);

                    listaIp.add(iPRemota);
                    datosRecibidos.setListaIps(listaIp);
                    for (String i:listaIp
                         ) {
                        Socket envioAdestino = new Socket(i,9090);
                        ObjectOutputStream datosDestino = new ObjectOutputStream(envioAdestino.getOutputStream());
                        datosDestino.writeObject(datosRecibidos);
                        datosDestino.close();
                        envioAdestino.close();
                        socket.close();
                    }

            }

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
