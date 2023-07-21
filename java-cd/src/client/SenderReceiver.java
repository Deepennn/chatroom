package client;

import java.io.*;

public class SenderReceiver extends Thread{//客户端发送接收数据线程
    public String name;
    BufferedReader sin = null;
    public PrintWriter sout = null;
    volatile boolean endClientSenderReceiver = false;
    volatile boolean preEndClientSenderReceiver = false;
    public SenderReceiver(String name, BufferedReader sin, PrintWriter sout) throws IOException {
        this.name=name;
        this.sin=sin;
        this.sout=sout;
    }
    public void send(String msg){
        if(msg.contains("$interrupt"))msg=msg.replaceAll("\\u0024interrupt","**********");
        if(msg.contains("$terminate"))msg=msg.replaceAll("\\u0024terminate","**********");
        if(msg.contains("$kill_me"))msg=msg.replaceAll("\\u0024kill_me","**********");
        if(msg.contains("$end_server"))msg=msg.replaceAll("\\u0024end_server","**********");
        if(msg.contains("$end_life")&&!preEndClientSenderReceiver)msg=msg.replaceAll("\\u0024end_life","**********");
//        if(msg.contains("$end_life")&&preEndClientSenderReceiver) System.out.println("client -> $end_life");
        sout.println(msg);
        sout.println("$terminate");
    }
    public String receive(){
        String msg = null;
        try {
            msg = sin.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (msg == null) return null;
        if (msg.contains("$kill_me")){
            sout.println("$interrupt");
//            System.out.println("client -> $interrupt");
            return null;
        }
        return msg;
    }

    @Override
    public void run(){
        while(!endClientSenderReceiver){
            //
        }
        try {
            sin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sout.close();
    }
}

//class Sender extends Thread{ } //用于向服务器发送数据。
//class Receiver extends Thread{ } //用于接收服务器端发来的数据。
