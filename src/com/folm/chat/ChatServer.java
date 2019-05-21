package com.folm.chat;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    public static void main(String[] args) {

        boolean started = false;
        ServerSocket ss = null;
        Socket s = null;
        DataInputStream dis = null;

        try {
            ss = new ServerSocket(8888);
        } catch (BindException e){
            System.out.println("端口占用中......");
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            started = true;
            while(started){
                boolean bConnected = false;
                s = ss.accept();
                System.out.println("a client connected");
                bConnected = true;
                dis = new DataInputStream(s.getInputStream());
                while(bConnected){
                    String str = dis.readUTF();
                    System.out.println(str);
                }
                dis.close();
            }
        } catch (EOFException e){
            System.out.println("Client Closed");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(null!=dis) dis.close();
                if(null!=s) s.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

}
