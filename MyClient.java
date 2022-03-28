import java.net.*;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.*;

//./ds-server ../../configs/sample-configs/ds-sample-config01.xml -n brief

class MyClient {
    public static void main(String args[]) throws Exception {
        Socket s = new Socket("localhost", 50000);
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
        BufferedReader brin = new BufferedReader(new InputStreamReader(s.getInputStream()));

        String msg = "";
        //Establish connection
        sendToServer("HELO\n", dout);
        //RESPONDS: OK
        System.out.println("SERVER: " + receivedFromServer(brin));
        sendToServer("AUTH focal\n", dout);
        //RESPONDS: OK
        System.out.println("SERVER: " + receivedFromServer(brin));

        List<Server> listOfServers = new ArrayList<Server>();

        //READ XML, ADD SERVER TO LIST
        //#region
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File("ds-sim/src/pre-compiled/ds-system.xml"));
            
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("server");
            
            //List of servers
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);

                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;

                    String type = element.getAttribute("type");
                    Integer limit = Integer.parseInt(element.getAttribute("limit"));
                    Integer bootupTime = Integer.parseInt(element.getAttribute("bootupTime"));
                    Float hourlyRate = Float.parseFloat(element.getAttribute("hourlyRate"));
                    Integer cores = Integer.parseInt(element.getAttribute("cores"));
                    Integer memory = Integer.parseInt(element.getAttribute("memory"));
                    Integer disk = Integer.parseInt(element.getAttribute("disk"));

                    Server server = new Server(type,
                                                limit,
                                                bootupTime,
                                                hourlyRate,
                                                cores,
                                                memory,
                                                disk);
                    listOfServers.add(server);
                }
            }
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
        }
        //#endregion
        
        // //-=- FIRST LOOP -=-
        Boolean firstLoop = true;
        while(firstLoop){

            sendToServer("REDY\n", dout);
            //RESPONDS: JOBN
            String jobMsg = receivedFromServer(brin);
            System.out.println("SERVER: " + jobMsg);
            String[] jobMsgArr = jobMsg.trim().split("\\s+");
            // System.out.println("Time submit: " + jobMsgArr[1]);
            // System.out.println("JobID: " + jobMsgArr[2]);
            // System.out.println("Estimated runtime: " + jobMsgArr[3]);
            // System.out.println("Core: " + jobMsgArr[4]);
            // System.out.println("Memory: " + jobMsgArr[5]);
            // System.out.println("Disk: " + jobMsgArr[6] + "\n");

            switch (jobMsgArr[0]) {
                case "JOBN":
                    sendToServer("GETS Capable " + jobMsgArr[4] + " " +         //Core
                    jobMsgArr[5] + " " +        //Memory
                    jobMsgArr[6] + "\n", dout); //Disk

                    //Data Message
                    String dataMsg = receivedFromServer(brin);
                    // System.out.println(dataMsg);

                    //Extract number of data lines from 'Data message' and Print Servers
                    String[] dataMsgArr = dataMsg.trim().split("\\s+");
                    Integer noDataLines = Integer.parseInt(dataMsgArr[1]);

                    //RESPOND TO DATA
                    sendToServer("OK\n", dout);

                    //READ DATA INTO OBJECT AND LIST
                    //#region
                    List<ServerState> listOfServerStates = new ArrayList<ServerState>();
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

                    //RESPOND TO DATA COMPLETION
                    sendToServer("OK\n", dout);
                    System.out.println(receivedFromServer(brin));
                    
                    
                    



                    //SCHEDULE JOB TO SERVER
                    String serverType = assignServerType(listOfServers, Integer.parseInt(jobMsgArr[4]));
                    Integer serverId = assignServerId(listOfServerStates, Integer.parseInt(jobMsgArr[4]));
                    sendToServer("SCHD " + jobMsgArr[2] + " " + serverType + " " + serverId + "\n", dout);
                    System.out.println(receivedFromServer(brin));  
                    break;
                case "JOBP":
                    System.out.println("TESTING JOBP");
                    break;
                case "RESF":
                    System.out.println("TESTING RESF");
                    break;
                case "RESR":
                    System.out.println("TESTING RESR");
                    break;
                case "JCPL":
                    break;
                default:
                    //BREAK FIRST LOOP
                    firstLoop = false;
            }
        }

        //Quit and close socket
        sendToServer("QUIT\n", dout);
        System.out.println(brin.readLine());
        dout.close();
        s.close();

        //COMMENTED OUT LOOP TEMPLATE
        //#region
        //LOOP 1
        // while (true) {
        //     sendToServer("REDY\n", dout);
        //     msg = receivedFromServer(brin).toString();
        //     //RESPONDS: JOBN n
        //     System.out.println(msg);
        //     String[] stringAttNextEvent = msg.trim().split("\\s+");
        //     // for (String string : stringAtt) {
        //     //     System.out.println(string + "\n");
        //     // }
        //     if(!msg.equals("NONE")){
        //         // //LOOP 2
        //         while(true){
        //             //ACTION FOR EVENT
        //             sendToServer("GETS Capable 2 900 2500\n", dout);
        //             msg = receivedFromServer(brin).toString();
        //             //RESPONDS: DATA n (server info)
        //             System.out.println(msg);
        //             String[] stringAttResponse = msg.trim().split("\\s+");
        //             //NEW LOOP WITH SWITCH
        //             switch (stringAttResponse[0]) {
        //                 case "DATA":
        //                     sendToServer("OK\n", dout);
        //                     System.out.println(brin.readLine());
        //                     //LOOP 3
        //                     while (true) {
        //                         sendToServer("OK\n", dout);
        //                         msg = receivedFromServer(brin).toString();
        //                         System.out.println(msg);
        //                         //TEST
        //                         break;
        //                         // if(/*[.]*/){
        //                         //     //BREAK 3RD LOOP
        //                         //     break;
        //                         // }
        //                     }
        //                     break;
        //                 case "OK":
        //                     break;
        //                 case "ERR":
                        
        //                 break;
        //             }
        //             // //OLD LOOP WITH IF ELSE
        //             // if(stringAttResponse[0].equals("DATA")){
                        
        //             // }
        //             // else{
        //             //     //BREAK 2ND LOOP
        //             //     break;
        //             // }
        //             //TEST
        //             break;
        //         }
        //     }
        //     else{
        //         //BREAK 1ST LOOP
        //         break;
        //     }
        //     //TEST
        //     break;
        // }
        //#endregion
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

    public static String receivedFromServer(BufferedReader brin){
        try {
            return brin.readLine();
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return "ERROR";
        }
    }

    public static String assignServerType(List<Server> listOfServer, Integer core){
        for (Server server : listOfServer) {
            if(server.cores >= core){
                return server.type;
            }
        }
        return "";
    }

    public static Integer assignServerId(List<ServerState> listOfServerStates, Integer core){
        List<ServerState> sameCoreServerStates = new ArrayList<ServerState>();
        for (ServerState serverState : listOfServerStates) {
            System.out.println(serverState.type);
            if(serverState.cores >= core){
                System.out.println("TEST");
                sameCoreServerStates.add(serverState);
            }
        }
        
        ServerState tempServerState = sameCoreServerStates.get(0);
        for (ServerState serverState : sameCoreServerStates) {
            if(serverState.wJobs < tempServerState.wJobs){
                tempServerState = serverState;
            }
        }
        return tempServerState.serverId;
    }

    // public static Object simulationEvent(String[] stringAtt){
    //     Job job;
    //     switch (stringAtt[0]) {
    //         case "JOBN":
    //             return job = new Job(Integer.parseInt(stringAtt[2]), "TYPE" ,
    //             Integer.parseInt(stringAtt[1]),
    //             Integer.parseInt(stringAtt[3]), Integer.parseInt(stringAtt[4]),
    //             Integer.parseInt(stringAtt[5]), Integer.parseInt(stringAtt[6]));        
    //         default:
    //             break;
    //     }
    // }
}