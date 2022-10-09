import java.util.*;
import java.net.*;
import java.io.*;

public class Client {
    String username;
    Socket soc;
    public Client(Socket s, String un){
        username = un;
        soc = s;
    }
}
