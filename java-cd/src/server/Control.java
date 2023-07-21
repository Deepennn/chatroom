package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Control extends Thread{//服务器端控制线程，用于接收管理员的指令，控制服务器端。
    public void enumerateName(){
        for(SenderReceiver s : Server.senderReceivers){
            System.out.print(" "+s.name+" ");
        }
    }
    public static boolean shut(String name){
        for(SenderReceiver s : Server.senderReceivers){
            if(s.name.equals(name)){
                try {
                    s.interrupt();
                    s.cout.println("$kill_me");
                }catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }
        return false;
    }
    public boolean shutAll(){
        if(Server.clientNames.isEmpty()) return true;

        boolean result = true;
        for (String clientName : Server.clientNames){
            result&=shut(clientName);
        }
        return result;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String instruction;
        while (!isInterrupted()) {
            System.out.println(">>>请输入指令:\t\n\tend —— 结束程序\n\tcount —— 聊天者数量\n\tchatters —— 列出所有聊天者\n\tkickout + 空格 + 昵称 —— 踢出聊天室");
            System.out.print(">>>");
            instruction=scanner.nextLine();
            String [] instructionSlice = instruction.split("\\s+");
            if (instructionSlice[0].equals("end")) {
                try {
                    Server.sendAll("各位，聊天到此结束，各自安好！");
                    Server.sendAll("$end_server");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Server.endServer[0] = true;
                if (shutAll()) {
                    System.out.println(">>>聊天已终止，已与所有的聊天客户端终端连接");
                    Socket end_socket =null;
                    try {
                        end_socket = new Socket("127.0.0.1", 8888);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    BufferedReader sin = null;
                    try {
                        sin = new BufferedReader(new InputStreamReader(end_socket.getInputStream()));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    PrintWriter sout = null;
                    try {
                        sout = new PrintWriter(end_socket.getOutputStream(), true);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    sout.println("$end_socket");
                    try {
                        if (sin.readLine().equals("$end_succeeded")) {
                            try {
                                sin.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            sout.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else System.out.println(">>>终止失败");
                break;
            }
            else if (instructionSlice[0].equals("count")) {
                System.out.println(">>>共有 "+ Server.senderReceivers.size()+" 位聊天者");
            }
            else if (instructionSlice[0].equals("chatters")) {
                System.out.print(">>>所有的聊天者: ");
                enumerateName();
                System.out.println();
            }
            else if (instructionSlice[0].equals("kickout")&&instructionSlice.length==2) {
                if (shut(instructionSlice[1])){
                    System.out.println(">>>用户 " + instructionSlice[1] + " 成功踢出！");
                }
                else {
                    System.out.println(">>>踢出失败，请检查用户 " + instructionSlice[1] + " 是否存在");
                }
            }
            else {
                System.out.println(">>>错误的指令，请再次尝试！");
            }
        }
    }
}
