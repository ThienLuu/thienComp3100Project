import java.net.*;
import java.io.*;

//./ds-server ../../configs/sample-configs/ds-sample-config01.xml -n brief

class MyClient {
    public static void main(String args[]) throws Exception {
        Socket s = new Socket("localhost", 50000);
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
        BufferedReader brin = new BufferedReader(new InputStreamReader(s.getInputStream()));

        //Establish connection
        sendToServer("HELO\n", dout);
        System.out.println(brin.readLine());
        sendToServer("AUTH focal\n", dout);
        System.out.println(brin.readLine());
        sendToServer("REDY\n", dout);
        System.out.println(brin.readLine());

        

        //Quit and close socket
        sendToServer("QUIT\n", dout);
        System.out.println(brin.readLine());
        dout.close();
        s.close();
    }

    public static  void sendToServer(String msg, DataOutputStream dout){
        try {
            dout.write(msg.getBytes());
            dout.flush();
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
        }
    }
}