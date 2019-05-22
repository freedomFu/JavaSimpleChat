package com.folm.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ChatClient extends JFrame {

    Socket s = null;
    DataOutputStream dos = null;
    DataInputStream dis = null;
    private boolean bConnected = false;

    TextField tfTxt = new TextField();
    TextArea taContent = new TextArea();

    Thread tRecv = new Thread(new RecvThread());

    public static void main(String[] args) {
        new ChatClient().launchFrame();
    }

    public void launchFrame() {
        setLocation(400, 300);
        this.setSize(300, 300);

        add(tfTxt, BorderLayout.SOUTH);
        add(taContent, BorderLayout.NORTH);
        pack();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
                System.exit(0);
            }
        });

        tfTxt.addActionListener(new TFListener());
        setVisible(true);
        connect();

        tRecv.start();
    }

    public void connect(){
        try {
            s = new Socket("127.0.0.1", 8888);
            dos = new DataOutputStream(s.getOutputStream());
            dis = new DataInputStream(s.getInputStream());
            System.out.println("connected!");
            bConnected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(){
        /*try {
            bConnected = false;
            // 这里是使线程正常停止的方法 join 方法
            // 但是因为这个线程中存在阻塞性方法 readUTF() 所以这种方法需要长时间的等待
            tRecv.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                dos.close();
                dis.close();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        try {
            dos.close();
            dis.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class TFListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 拿到字符串 如何写到服务器端呢
            String str = tfTxt.getText().trim();
            taContent.append(str+"\n");
            tfTxt.setText("");
            try {
                dos.writeUTF(str);
                dos.flush();
//                dos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private class RecvThread implements Runnable {

        @Override
        public void run() {
            try {
                while(bConnected){
                    String str = dis.readUTF();
                    System.out.println(str);
                    taContent.append(str+"\n");
                }
            } catch (SocketException e){
                System.out.println("退出，bye bye!");
            } catch (IOException e){
                e.printStackTrace();
            }

        }
    }

}
