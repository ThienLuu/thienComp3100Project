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
        while (firstLoop) {
            sendToServer("REDY\n", dout);
            //RESPONDS JOBN
            String serverMsg = receivedFromServer(brin);
            String[] serverMsgArr = serverMsg.trim().split("\\s+");
            switch (serverMsgArr[0]) {
                case "JOBN":
                    Integer serverId = 0;
                    String serverType = "";
                    //GET EACH SERVER CONFIGURATIONS
                    sendToServer("GETS Capable " + serverMsgArr[4] + " " + serverMsgArr[5] + " " + serverMsgArr[6] + "\n", dout);
                    //RESPONDS DATA MSG
                    List<ServerState> listOfServerStates = new ArrayList<ServerState>();
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

                        String[] serverStatesMsgArr = serverStatesMsg.trim().split("\\s+");
                        ServerState serverState = new ServerState(
                            serverStatesMsgArr[0],                    //Type
                            Integer.parseInt(serverStatesMsgArr[1]),  //ID
                            serverStatesMsgArr[2],  //State
                            Integer.parseInt(serverStatesMsgArr[3]),  //curStartTime
                            Integer.parseInt(serverStatesMsgArr[4]),  //Cores
                            Integer.parseInt(serverStatesMsgArr[5]),  //Memory
                            Integer.parseInt(serverStatesMsgArr[6]),  //Disk
                            Integer.parseInt(serverStatesMsgArr[7]), 
                            Integer.parseInt(serverStatesMsgArr[8])
                        );
                        listOfServerStates.add(serverState);
                    }

                    sendToServer("OK\n", dout);
                    //RESPONDS: OK
                    receivedFromServer(brin);

                    serverId = listOfServerStates.get(0).serverId;
                    serverType = listOfServerStates.get(0).type;

                    //#endregion
                    //SCHEDULE JOB TO SERVER 
                    sendToServer("SCHD " + serverMsgArr[2] + " "
                                        + serverType + " "
                                        + serverId
                                        + "\n", dout);
                    //RESPONSE 'OK'
                    receivedFromServer(brin);
                    //Increment serverId to achieve LRR algorithm
                    serverId++;
                    //Reset the serverId to achieve LRR algorithm
                    //once it equals the number of servers of type
                    break;
                //Break the loop when no jobs are available - NONE message
                //received
                case "NONE":
                    firstLoop = false;
                    break;
                default:
            }
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