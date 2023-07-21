package server;

public class Inspection extends Thread{//服务器端巡检线程，查看哪个客户端需要踢出（客户自己退出、管理员主动踢出、异常出现时的退出）。

    @Override
    public void run() {
        while (!isInterrupted()){
            // removal
            if(Server.senderReceivers.isEmpty()&&!Server.endServer[0])continue;
            if(Server.senderReceivers.isEmpty()&&Server.endServer[0]){
                Server.endServer[1]=true;
                break;
            }
            SenderReceiver t =null;
            for(int i=0;i<Server.senderReceivers.size();i++){//SenderReceiver s : Server.senderReceivers
                SenderReceiver s = Server.senderReceivers.get(i);
                if(s !=null && s.logout){
                    t=s;
                    Server.clientNames.remove(t.name);
                    Server.senderReceivers.remove(t);
                    synchronized(t){
                        t.notify();
                    }
                }
            }
        }
    }
}
