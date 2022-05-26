import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        List<Server> listOfAvailable = new ArrayList<Server>();
        List<Server> listOfReady = new ArrayList<Server>();
        Integer serverSelectMark = 0;
        //List<Integer> listOfIntegers = new ArrayList<Integer>();
        while (schedLoop) {
            //-=-firstCycle-=-
            //#region
            if(firstLoop){
                sendToServer("REDY\n", dout);
                //RESPONDS JOBN
                receivedFromServer(brin);

                //GET EACH SERVER CONFIGURATIONS
                sendToServer("GETS All\n", dout);
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
                    Server server = new Server(
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
                    listOfServers.add(server);
                }
                //#endregion

                Collections.sort(listOfServers);

                sendToServer("OK\n", dout);
                receivedFromServer(brin);
                firstLoop = false;
            }
            //#endregion
            //-=-firstCycle-=-
            sendToServer("REDY\n", dout);
            //RESPONDS JOBN
            String serverMsg = receivedFromServer(brin);
            String[] dsServerMsgArr = serverMsg.trim().split("\\s+");
            switch (dsServerMsgArr[0]) {
                case "JOBN":
                    //Assign Job message to Job object
                    Job job = new Job(
                        Integer.parseInt(dsServerMsgArr[1]),
                        Integer.parseInt(dsServerMsgArr[2]),
                        Integer.parseInt(dsServerMsgArr[3]),
                        Integer.parseInt(dsServerMsgArr[4]),
                        Integer.parseInt(dsServerMsgArr[5]),
                        Integer.parseInt(dsServerMsgArr[6]));

                    String dataMsg;
                    String[] dataMsgArr;
                    Integer noDataLines;

                    //Get a list of capable servers
                    //#region
                    listOfCapable.clear();
                    sendToServer("GETS Capable " +  job.core +
                                        " " + job.memory +
                                        " " + job.disk +
                                        "\n", dout);
                    dataMsg = receivedFromServer(brin);
                    sendToServer("OK\n", dout);
                    //RESPONDS DATA: Server States
                    //Extract number of data lines from 'Data message' and Print Servers
                    //Create an object for each server read and add to listOfServerStates
                    dataMsgArr = dataMsg.trim().split("\\s+");
                    System.out.println(dataMsgArr[0]);
                    noDataLines = Integer.parseInt(dataMsgArr[1]);
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
                    //#endregion

                    sendToServer("OK\n", dout);
                    receivedFromServer(brin);

                    sendToServer("REDY\n", dout);
                    //RESPONDS JOBN
                    receivedFromServer(brin);

                    //Get list of Available Servers
                    //#region
                    listOfAvailable.clear();
                    sendToServer("GETS Avail " +  job.core +
                                        " " + job.memory +
                                        " " + job.disk +
                                        "\n", dout);
                    dataMsg = receivedFromServer(brin);
                    sendToServer("OK\n", dout);
                    //RESPONDS DATA: Server States
                    //Extract number of data lines from 'Data message' and Print Servers
                    //Create an object for each server read and add to listOfServerStates
                    dataMsgArr = dataMsg.trim().split("\\s+");
                    noDataLines = Integer.parseInt(dataMsgArr[1]);
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
                        listOfAvailable.add(serverState);
                    }
                    //#endregion

                    listOfReady.clear();
                    for (Server serverCapable : listOfCapable) {
                        for (Server serverAvailable : listOfAvailable) {
                            if(serverAvailable.serverId == serverCapable.serverId){
                                listOfReady.add(serverCapable);
                            }
                        }
                    }

                    //Sort list of capable jobs with scheduled jobs ascending order
                    Collections.sort(listOfCapable, Comparator.comparingInt(Server::getWJobs));

                    sendToServer("OK\n", dout);
                    receivedFromServer(brin);

                    sendToServer("REDY\n", dout);
                    //RESPONDS JOBN
                    receivedFromServer(brin);

                    Server selectedServer;

                    if(listOfReady.isEmpty()){
                        selectedServer = listOfCapable.get(0);

                        sendToServer("SCHD " + job.jobId + " "
                                + selectedServer.type + " "
                                + selectedServer.serverId
                                + "\n", dout);
                    }
                    else{
                        selectedServer = listOfReady.get(listOfAvailable.size() - 1);
                    
                        sendToServer("SCHD " + job.jobId + " "
                                + selectedServer.type + " "
                                + selectedServer.serverId
                                + "\n", dout);
                    }

                    //RESPONSE 'OK'
                    receivedFromServer(brin);

                    serverSelectMark++;
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