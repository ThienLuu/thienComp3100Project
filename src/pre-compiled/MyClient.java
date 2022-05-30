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
        
        // Boolean firstLoop = true;
        Boolean schedLoop = true;
        while (schedLoop) {
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

                    //Get a list of capable servers
                    List<Server> listOfCapable = new ArrayList<Server>();
                    listOfCapable = getsCapableServers(job.core, job.memory, job.disk, dout, brin);

                    sendToServer("REDY\n", dout);
                    //RESPONDS JOBN
                    receivedFromServer(brin);

                    //Get list of Available Servers
                    List<Server> listOfAvailable = new ArrayList<Server>();
                    listOfAvailable = getsAvailableServers(job.core, job.memory, job.disk, dout, brin);

                    Server selectedServer;

                    //Check if list of available server is empty, if not schedule job to the largest available server
                    //If it is, then schedule to a server capable of handling the job with the least of queued jobs
                    if(!(listOfAvailable.isEmpty())){
                        selectedServer = listOfAvailable.get(listOfAvailable.size() - 1);
                        sendToServer("SCHD " + job.jobId + " "
                                + selectedServer.type + " "
                                + selectedServer.serverId
                                + "\n", dout);
                    }
                    else{
                        //Sort list of capable jobs 'with scheduled jobs' in ascending order
                        //REFERENCE: https://stackoverflow.com/questions/15326248/sort-an-array-of-custom-objects-in-descending-order-on-an-int-property
                        Collections.sort(listOfCapable, Comparator.comparingInt(Server::getWJobs));
                        selectedServer = listOfCapable.get(0);

                        sendToServer("REDY\n", dout);
                        //RESPONDS JOBN
                        receivedFromServer(brin);

                        sendToServer("SCHD " + job.jobId + " "
                                + selectedServer.type + " "
                                + selectedServer.serverId
                                + "\n", dout);
                    }

                    //RESPONSE 'OK'
                    receivedFromServer(brin);
                    break;
                //Break the loop when no jobs are available - NONE message
                case "NONE":
                    schedLoop = false;
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
    
    //METHOD: Get all 'capable' servers
    public static List<Server> getsCapableServers(Integer core, Integer memory, Integer disk, DataOutputStream dout, BufferedReader brin){
        List<Server> listOfCapable = new ArrayList<Server>();
        String dataMsg;
        String[] dataMsgArr;
        Integer noDataLines;
        sendToServer("GETS Capable " +  core +
                                        " " + memory +
                                        " " + disk +
                                        "\n", dout);
        dataMsg = receivedFromServer(brin);
        dataMsgArr = dataMsg.trim().split("\\s+");
        noDataLines = Integer.parseInt(dataMsgArr[1]);

        sendToServer("OK\n", dout);
        //RESPONDS DATA: Server States
        //Extract number of data lines from 'Data message' and Print Servers
        //Create an object for each server read and add to listOfServer
        for (int i = 0; i < noDataLines; i++) {
            String serverMsg = receivedFromServer(brin);

            String[] serverMsgArr = serverMsg.trim().split("\\s+");
            Server server = new Server(
                serverMsgArr[0],                    //Type
                Integer.parseInt(serverMsgArr[1]),  //ID
                serverMsgArr[2],                    //State
                Integer.parseInt(serverMsgArr[3]),  //curStartTime
                Integer.parseInt(serverMsgArr[4]),  //Cores
                Integer.parseInt(serverMsgArr[5]),  //Memory
                Integer.parseInt(serverMsgArr[6]),  //Disk
                Integer.parseInt(serverMsgArr[7]), 
                Integer.parseInt(serverMsgArr[8])
            );
            listOfCapable.add(server);
        }

        sendToServer("OK\n", dout);
        receivedFromServer(brin);

        return listOfCapable;
    }
    
    //METHOD: Get all 'available servers'
    public static List<Server> getsAvailableServers(Integer core, Integer memory, Integer disk, DataOutputStream dout, BufferedReader brin){
        List<Server> listOfAvailable = new ArrayList<Server>();
        String dataMsg;
        String[] dataMsgArr;
        Integer noDataLines;

        sendToServer("GETS Avail " +  core +
                                        " " + memory +
                                        " " + disk +
                                        "\n", dout);
        dataMsg = receivedFromServer(brin);
        sendToServer("OK\n", dout);
        //RESPONDS DATA: Server States
        //Extract number of data lines from 'Data message' and Print Servers
        //Create an object for each server read and add to listOfServer
        dataMsgArr = dataMsg.trim().split("\\s+");
        noDataLines = Integer.parseInt(dataMsgArr[1]);
        if(!(noDataLines == 0)){
            for (int i = 0; i < noDataLines; i++) {
                String serverMsg = receivedFromServer(brin);

                String[] serverMsgArr = serverMsg.trim().split("\\s+");
                Server server = new Server(
                    serverMsgArr[0],                    //Type
                    Integer.parseInt(serverMsgArr[1]),  //ID
                    serverMsgArr[2],                    //State
                    Integer.parseInt(serverMsgArr[3]),  //curStartTime
                    Integer.parseInt(serverMsgArr[4]),  //Cores
                    Integer.parseInt(serverMsgArr[5]),  //Memory
                    Integer.parseInt(serverMsgArr[6]),  //Disk
                    Integer.parseInt(serverMsgArr[7]), 
                    Integer.parseInt(serverMsgArr[8])
                );
                listOfAvailable.add(server);
            }
            sendToServer("OK\n", dout);
        }

        receivedFromServer(brin);
        return listOfAvailable;
    }
}