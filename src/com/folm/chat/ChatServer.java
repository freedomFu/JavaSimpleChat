package com.folm.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    boolean started = false;
    ServerSocket ss = null;

    // 如果想接收到其他客户端的消息
    // 需要得到得到其他客户端的对象  这里选择用变量存储
    List<Client> clients = new ArrayList<>();

    public void startServer(){
        try {
            ss = new ServerSocket(8888);
        } catch (BindException e){
            System.out.println("端口占用中......");
            System.out.println("请关掉相关程序并重新运行服务器");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            started = true;

            while(started) {
                Socket s = ss.accept();
                Client c = new Client(s);
                System.out.println("a client connected");
                new Thread(c).start();
                clients.add(c);
            }
        }catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                // 整个的serversocket是必须要关掉的
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new ChatServer().startServer();
    }

    class Client implements Runnable {
        // 每一个客户端都要有一个单独的socket和一个单独的DataInputStream
        private Socket s;
        private DataInputStream dis = null;
        private DataOutputStream dos = null;
        private boolean bConnected = false;

        public Client(Socket s){
            this.s = s;
            try {
                dis = new DataInputStream(s.getInputStream());
                dos = new DataOutputStream(s.getOutputStream());
                bConnected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void send(String str) {
            try {
                dos.writeUTF(str);
            } catch (IOException e) {
                clients.remove(this);
                System.out.println("对方退出了！我从List中去除了！");
            }
        }

        @Override
        public void run() {
            try {
                while(bConnected) {
                    String str = dis.readUTF();
                    System.out.println(str);
                    for(int i=0;i<clients.size();i++){
                        Client c = clients.get(i);
                        c.send(str);
                    }
                }
            } catch (EOFException e){
                System.out.println("Client Closed");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(null!=dis) dis.close();
                    if(null != dos) dos.close();
                    if(null!=s) s.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
