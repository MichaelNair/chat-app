import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class GWackClientGUI extends JFrame{
    // sorry for using a static object but it was the easiet way I found to be able to updated the
    // title to say "connected" or "disconnected" when the connect button is clicked
    public static GWackClientGUI f;
    public Socket soc;
    public PrintWriter pw;
    public BufferedReader input;
    public JTextArea mem;
    public JTextArea mes;

    public GWackClientGUI(){
        super();
        this.setSize(800,800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(100, 100);

        JPanel panel = new JPanel(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout());
        JPanel middle = new JPanel(new BorderLayout());
        JPanel bottom = new JPanel(new FlowLayout());

        //make top bar
        JLabel name = new JLabel("Name");
        JTextField n = new JTextField("username", 20);
        JLabel IP = new JLabel("IP Address");
        JTextField address = new JTextField("localhost", 20);
        JLabel port = new JLabel("Port");
        JTextField port_num = new JTextField(10);
        JButton connect = new JButton("   connect   ");
        top.add(name); top.add(n); top.add(IP); top.add(address); top.add(port); top.add(port_num);
        top.add(connect);

        //make middle bar
        JLabel members = new JLabel("Members Online");
        JLabel messages = new JLabel("messages");
        JLabel compose = new JLabel("compose");
        mem = new JTextArea(1, 5);
        mem.setEditable(false);
        mes = new JTextArea(5, 10);
        mes.setEditable(false);
        JTextArea com = new JTextArea(2, 10);
        JPanel left = new JPanel(new BorderLayout());
        JPanel right = new JPanel(new GridLayout(4, 1));
        left.add(members, BorderLayout.PAGE_START); left.add(mem, BorderLayout.CENTER);
        right.add(messages); right.add(mes); right.add(compose); right.add(com);

        middle.add(left, BorderLayout.LINE_START);
        middle.add(right, BorderLayout.CENTER);

        //make bottom bar
        JButton send = new JButton("Send");
        bottom.add(send);

        //make panel
        panel.add(top, BorderLayout.PAGE_START);
        panel.add(middle, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.PAGE_END);
        this.add(panel);
        this.setTitle("GWASK -- GW Slack Simulator (" + connect.getText() + ")");
        this.pack();

        connect.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try{
                    soc = new Socket(address.getText(), Integer.parseInt(port_num.getText()));
                    pw = new PrintWriter(soc.getOutputStream(), true);
                    input = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    connect.setText("disconnect");
                    (new Handler(soc)).start();
                    pw.println("usr" + n.getText());
                    f.setTitle("GWASK -- GW Slack Simulator (connected)");
                }catch(Exception ee){
                     System.err.println("Cannot Connect");
                     connect.setText("   connect   ");
                     f.setTitle("GWASK -- GW Slack Simulator (disconnected)");
                     //System.exit(1);
                }
            }
        });

        send.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try{
                    pw.println("mes" + com.getText());
                    com.setText("");
                }catch(Exception ee){
                    System.err.println("IOException");
                    ee.printStackTrace();
                    System.out.println(ee.toString());
                    System.out.println(ee.getMessage());
                    //System.exit(1);
                }

            }
        });
    }

    private class Handler extends Thread{

        Socket sock;
        public Handler(Socket s){
            this.sock=s;
        }

        public void run(){
            //PrintWriter out=null;
            BufferedReader in=null;
            try{
                in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                String msg;

                while((msg = in.readLine()) != null){
                    System.out.println(msg);
                    if(msg.equals("start")){
                        mem.setText("");
                        while((msg = in.readLine()) != null){
                            if(msg.equals("end")) break;
                            mem.append(msg + "\n");
                        }
                    }
                    else mes.append(msg + "\n");
                }

                //close the connections
                in.close();
                //out.close();
                sock.close();
                
            }catch(Exception e){
                System.out.println("error");
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String args[]){
        f = new GWackClientGUI();
        f.setVisible(true);

    }
}
