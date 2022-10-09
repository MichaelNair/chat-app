import java.util.*;
import java.net.*;
import java.io.*;

public class GWackChannel{
    public List<Socket> clients;
    public List<Client> Clients;
    public List<String> usernames;
    public ServerSocket SSock;

    public GWackChannel(int port){
        this.clients = new LinkedList<Socket>();
        this.Clients = new LinkedList<Client>();
        this.usernames = new LinkedList<String>();
        try{
            SSock = new ServerSocket(port);
        }catch(Exception e){
            System.err.println("Cannot establish server socket");
            System.exit(1);
        }  
    }

    public void serve(){
        while(true){
            try{
                //accept incoming connection
                Socket CSock = SSock.accept();
                System.out.println("New connection: "+CSock.getRemoteSocketAddress());
                //clients.add(CSock);
                //Clients.add(CSock, );
                
                //start the thread
                (new ClientHandler(CSock)).start();
                
                //continue looping
            }catch(Exception e){} //exit serve if exception
        }
    }

    private class ClientHandler extends Thread{

        Socket sock;
        public ClientHandler(Socket s){
            this.sock=s;
        }

        public void run(){
            PrintWriter out=null;
            BufferedReader in=null;
            try{
                in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                String msg;


                //read and echo back forever!
                while((msg = in.readLine()) != null){
                    System.out.println(msg);
                    if(msg.substring(0,3).equals("usr")){
                        Clients.add(new Client(sock, msg.substring(3)));
                        for(Client s : Clients){
                            out = new PrintWriter(s.soc.getOutputStream());
                            out.println("start");
                            out.flush();
                            for(Client n : Clients){
                                out.println(n.username);
                                out.flush();
                            }
                            out.println("end");
                            out.flush();
                        }
                    }

                    if(msg.substring(0,3).equals("mes")){
                        for(Client n : Clients){
                            out = new PrintWriter(n.soc.getOutputStream());
                            for(Client nn : Clients){
                                if(nn.soc == sock) out.println("{" + nn.username + "} " + msg.substring(3));
                            }
                            out.flush();
                        }
                    }
                }

                //close the connections
                System.out.println("leaving");
                in.close();
                out.close();
                sock.close();
            }catch(Exception e){
                System.out.println("error");
                e.printStackTrace();
                System.out.println(e.toString());
                System.out.println(e.getMessage());
            }

            //note the loss of the connection
            for(Client n : Clients){
                if(n.soc == sock) Clients.remove(n);
            }
            System.out.println("Connection lost: "+sock.getRemoteSocketAddress());
        }
    }

    public static void main(String args[]){
        int p = Integer.parseInt(args[0]);
        GWackChannel server = new GWackChannel(p);
        server.serve();
    }
}