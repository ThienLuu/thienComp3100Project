import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
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
        //Connection Settings
        Socket s = new Socket("localhost", 50000);
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
        BufferedReader brin = new BufferedReader(new InputStreamReader(s.getInputStream()));

        //Establish connection
        sendToServer("HELO\n", dout);
        //RESPONDS: OK
        System.out.println("SERVER: " + receivedFromServer(brin));
        sendToServer("AUTH focal\n", dout);
        //RESPONDS: OK
        System.out.println("SERVER: " + receivedFromServer(brin));
        sendToServer("REDY\n", dout);
        //RESPONDS JOBN (not used)
        String firstJob = receivedFromServer(brin);
        System.out.println("SERVER: " + firstJob);

        //READ XML, ADD SERVER TO LIST
        //#region
        List<Server> listOfServers = new ArrayList<Server>();

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

        sendToServer("GETS All\n", dout);
        //RESPONDS DATAMSG
        List<ServerState> listOfServerStates = new ArrayList<ServerState>();
        String dataMsg= receivedFromServer(brin);
        System.out.println("SERVER: " + dataMsg);
        sendToServer("OK\n", dout);
        //RESPONDS DATA: Server States
        //#region
        //Extract number of data lines from 'Data message' and Print Servers
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
            //RESPOND .
            System.out.println("SERVER: " + serverStatesMsg);
        }
        //#endregion
        
        sendToServer("OK\n", dout);
        //RESPOND '.'
        System.out.println("SERVER: " + receivedFromServer(brin));


        //GET LIST OF LARGEST CORES
        Collections.sort(listOfServerStates);
        Integer maxCore = listOfServerStates.get(0).cores;
        List<ServerState> listOfMaxCores = new ArrayList<ServerState>();
        for (ServerState serverState : listOfServerStates) {
            if(serverState.cores == maxCore){
                listOfMaxCores.add(serverState);
            }
        }

        //GET FIRST LARGEST CORE SERVER TYPE ADDED TO LIST

        Collections.reverse(listOfMaxCores);
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
                    sendToServer("SCHD " + serverMsgArr[2] + " " + firstMaxCoreType + " " +
                    listFirstMaxType.get(serverId).serverId + "\n", dout);
                    //RESPONSE 'OK'
                    System.out.println("SERVER: " + receivedFromServer(brin));
                    serverId++;
                    if(serverId == listFirstMaxType.size()){
                        serverId = 0;
                    }
                    break;
                case "NONE":
                    firstLoop = false;
                    break;
                default:
            }
        }

        //Quit and close socket (End connection)
        sendToServer("QUIT\n", dout);
        System.out.println(brin.readLine());
        dout.close();
        s.close();
    }

    public static void sendToServer(String msg, DataOutputStream dout){
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
            return "SERVER MESSAGE ERROR";
        }
    }
}