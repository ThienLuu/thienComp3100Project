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
        sendToServer("REDY\n", dout);
        //RESPONDS JOBN
        receivedFromServer(brin);

        //GET EACH SERVER CONFIGURATIONS
        sendToServer("GETS All\n", dout);
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
        //#endregion
        
        sendToServer("OK\n", dout);
        //RESPOND '.'
        receivedFromServer(brin);

        //GET LIST OF LARGEST CORES
        Collections.sort(listOfServerStates);
        Integer maxCore = listOfServerStates.get(0).cores;
        List<ServerState> listOfMaxCores = new ArrayList<ServerState>();
        for (ServerState serverState : listOfServerStates) {
            if(serverState.cores.toString().equals(maxCore.toString())){
                listOfMaxCores.add(serverState);
            }
        }

        //GET THE FIRST SERVER TYPE WITH LARGEST CORE ADDED TO LIST
        String firstMaxCoreType = listOfMaxCores.get(0).type;
        List<ServerState> listFirstMaxType = new ArrayList<ServerState>();

        for (ServerState serverState : listOfMaxCores) {
            if(serverState.type.equals(firstMaxCoreType)){
                listFirstMaxType.add(serverState);
            }
        }
        
        //-=- FIRST LOOP -=-
        Boolean firstLoop = true;
        Integer serverId = 0;
        while (firstLoop) {
            sendToServer("REDY\n", dout);
            //RESPONDS JOBN
            String serverMsg = receivedFromServer(brin);
            String[] serverMsgArr = serverMsg.trim().split("\\s+");
            switch (serverMsgArr[0]) {
                case "JOBN":
                    //SCHEDULE JOB TO SERVER 
                    sendToServer("SCHD " + serverMsgArr[2] + " "
                                        + firstMaxCoreType + " "
                                        + serverId
                                        + "\n", dout);
                    //RESPONSE 'OK'
                    receivedFromServer(brin);
                    //Increment serverId to achieve LRR algorithm
                    serverId++;
                    //Reset the serverId to achieve LRR algorithm
                    //once it equals the number of servers of type
                    if(serverId == listFirstMaxType.size()){
                        serverId = 0;
                    }
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