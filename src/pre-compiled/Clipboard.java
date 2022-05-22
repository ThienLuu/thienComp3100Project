// //-=-firstCycle-=-
//             //#region
//             if(firstLoop){
//                 sendToServer("REDY\n", dout);
//                 //RESPONDS JOBN
//                 receivedFromServer(brin);

//                 //GET EACH SERVER CONFIGURATIONS
//                 sendToServer("GETS All\n", dout);
//                 String dataMsg= receivedFromServer(brin);
//                 sendToServer("OK\n", dout);
//                 //RESPONDS DATA: Server States
//                 //#region
//                 //Extract number of data lines from 'Data message' and Print Servers
//                 //Create an object for each server read and add to listOfServerStates
//                 String[] dataMsgArr = dataMsg.trim().split("\\s+");
//                 Integer noDataLines = Integer.parseInt(dataMsgArr[1]);
//                 for (int i = 0; i < noDataLines; i++) {
//                     String serverStatesMsg = receivedFromServer(brin);

//                     String[] serverMsgArr = serverStatesMsg.trim().split("\\s+");
//                     Server server = new Server(
//                         serverMsgArr[0],                    //Type
//                         Integer.parseInt(serverMsgArr[1]),  //ID
//                         serverMsgArr[2],  //State
//                         Integer.parseInt(serverMsgArr[3]),  //curStartTime
//                         Integer.parseInt(serverMsgArr[4]),  //Cores
//                         Integer.parseInt(serverMsgArr[5]),  //Memory
//                         Integer.parseInt(serverMsgArr[6]),  //Disk
//                         Integer.parseInt(serverMsgArr[7]), 
//                         Integer.parseInt(serverMsgArr[8])
//                     );
//                     listOfServers.add(server);
//                 }
//                 //#endregion

//                 sendToServer("OK\n", dout);
//                 receivedFromServer(brin);

//                 firstLoop = false;
//             }
//             //#endregion
//             //-=-firstCycle-=-


// for (Server server : listOfServers) {
                    //     if(Integer.parseInt(dsServerMsgArr[4]) <= server.cores){
                    //         //SCHEDULE JOB TO SERVER jobid/stype/sid
                    //         sendToServer("SCHD " + dsServerMsgArr[2] + " "
                    //         + server.type + " "
                    //         + server.serverId
                    //         + "\n", dout);
                    //         break;
                    //     }
                    // }