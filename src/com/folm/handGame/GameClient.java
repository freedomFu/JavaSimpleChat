package com.folm.handGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;

public class GameClient extends JFrame{
    Socket s = null;
    DataOutputStream dos = null;
    DataInputStream dis = null;
    private boolean bConnected = false;

    private String saveValue = null;

    TextArea taContent = new TextArea();
    ButtonGroup group = new ButtonGroup();
    JPanel ButtonPanel = new JPanel();
    JButton btn = new JButton("确定按钮");

    Thread tRecv = new Thread(new RecvThread());

    public static void main(String[] args) {
        new GameClient().launchFrame();
    }

    public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            System.out.println("MD5加密出错");
            e.printStackTrace();
        }
        return "";
    }

    public void launchFrame() {
        setLocation(400, 300);
        this.setSize(300, 300);

        JRadioButton stoneButton=new JRadioButton("石头",false);
        group.add(stoneButton);
        JRadioButton scissorsButton=new JRadioButton("剪刀",false);
        group.add(scissorsButton);
        JRadioButton bagButton=new JRadioButton("布",false);
        group.add(bagButton);

        ButtonPanel.add(stoneButton);
        ButtonPanel.add(scissorsButton);
        ButtonPanel.add(bagButton);
        add(ButtonPanel,BorderLayout.CENTER);
        add(btn,BorderLayout.SOUTH);
        add(taContent, BorderLayout.NORTH);
        pack();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
                System.exit(0);
            }
        });

        stoneButton.addActionListener(new TFListener());
        scissorsButton.addActionListener(new TFListener());
        bagButton.addActionListener(new TFListener());
        btn.addActionListener(new CommitListener());

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
            JRadioButton temp=(JRadioButton)e.getSource();
            boolean flag = false;
            if(temp.isSelected()){
                saveValue=temp.getText();
            }
        }
    }

    private class CommitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String choice = saveValue;
            System.out.println(choice);
            // 拿到字符串 如何写到服务器端呢
            String str = choice.trim();
            String hashstr = getMD5(str);
            taContent.append(str+"\n");
            try {
                dos.writeUTF(hashstr);
                dos.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                ButtonPanel.setEnabled(false);
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
                System.out.println("退出， bye bye!");
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
