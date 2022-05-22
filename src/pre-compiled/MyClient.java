import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.*;

class MyClient {
    public static void main(String args[]) throws Exception {
        //Connection Settings
        Socket s = new Socket("localhost", 50000);
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
        BufferedReader brin = new BufferedReader(new InputStreamReader(s.getInputStream()));

        //Establish connection
        sendToServer("HELO\n", dout);
        //RESPONDS: OK
        receivedFromServer(brin);
        sendToServer("AUTH focal\n", dout);
        //RESPONDS: OK
        receivedFromServer(brin);
        
        //-=- FIRST LOOP -=-
        Boolean firstLoop = true;
        Boolean schedLoop = true;
        //RESPONDS DATA MSG
        List<Server> listOfServers = new ArrayList<Server>();
        List<Server> listOfCapable = new ArrayList<Server>();
        //List<Integer> listOfIntegers = new ArrayList<Integer>();
        while (schedLoop) {
            sendToServer("REDY\n", dout);
            //RESPONDS JOBN
            String serverMsg = receivedFromServer(brin);
            String[] dsServerMsgArr = serverMsg.trim().split("\\s+");
            switch (dsServerMsgArr[0]) {
                case "JOBN":
                    sendToServer("GETS Capable " +  dsServerMsgArr[4] +
                                        " " + dsServerMsgArr[5] +
                                        " " + dsServerMsgArr[6] +
                                        "\n", dout);
                    String dataMsg= receivedFromServer(brin);
                    sendToServer("OK\n", dout);
                    //RESPONDS DATA: Server States
                    //#region
                    //Extract number of data lines from 'Data message' and Print Servers
                    //Create an object for each server read and add to listOfServerStates
                    String[] dataMsgArr = dataMsg.trim().split("\\s+");
                    Integer noDataLines = Integer.parseInt(dataMsgArr[1]);
                    for (int i = 0; i < noDataLines; i++) {
                        String serverStatesMsg = receivedFromServer(brin);

                        String[] serverMsgArr = serverStatesMsg.trim().split("\\s+");
                        Server serverState = new Server(
                            serverMsgArr[0],                    //Type
                            Integer.parseInt(serverMsgArr[1]),  //ID
                            serverMsgArr[2],  //State
                            Integer.parseInt(serverMsgArr[3]),  //curStartTime
                            Integer.parseInt(serverMsgArr[4]),  //Cores
                            Integer.parseInt(serverMsgArr[5]),  //Memory
                            Integer.parseInt(serverMsgArr[6]),  //Disk
                            Integer.parseInt(serverMsgArr[7]), 
                            Integer.parseInt(serverMsgArr[8])
                        );
                        listOfCapable.add(serverState);
                    }

                    sendToServer("OK\n", dout);
                    receivedFromServer(brin);

                    sendToServer("REDY\n", dout);
                    //RESPONDS JOBN
                    receivedFromServer(brin);
                    
                    Boolean capableServerInactiveCheck = true;
                    for (Server server : listOfCapable) {
                        if(!server.serverState.equals("inactive")){
                            capableServerInactiveCheck = false;
                        }
                        if(server.serverState.equals("booting") ||
                                    server.serverState.equals("idle")){
                            sendToServer("SCHD " + dsServerMsgArr[2] + " "
                                            + server.type + " "
                                            + server.serverId
                                            + "\n", dout);
                            break;
                        }
                    }

                    //Schedule job on biggest capable server if all server is inactive
                    if(capableServerInactiveCheck){
                        sendToServer("SCHD " + dsServerMsgArr[2] + " "
                                        + listOfCapable.get(listOfCapable.size() - 1).type + " "
                                        + listOfCapable.get(listOfCapable.size() - 1).serverId
                                        + "\n", dout);
                    }
                    
                    //RESPONSE 'OK'
                    receivedFromServer(brin);
                    break;
                    //Break the loop when no jobs are available - NONE message
                    //received
                case "NONE":
                    schedLoop = false;
                    break;
                default:
            }
            //REMOVE
            // schedLoop = false;
        }
        
        for (Server serverState : listOfServers) {
            System.out.println(serverState.type);
        }

        //Quit and close socket (End connection)
        sendToServer("QUIT\n", dout);
        dout.close();
        s.close();
    }

    //METHOD: Send message to server
    public static void sendToServer(String msg, DataOutputStream dout){
        try {
            dout.write(msg.getBytes());
            dout.flush();
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
        }
    }

    //METHOD: Acquire message from server
    public static String receivedFromServer(BufferedReader brin){
        try {
            //Read buffer
            String msg = brin.readLine();
            return msg;
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return "";
        }
    }
}