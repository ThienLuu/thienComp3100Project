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



                    // sendToServer("GETS Capable " +  dsServerMsgArr[4] +
                    //                     " " + dsServerMsgArr[5] +
                    //                     " " + dsServerMsgArr[6] +
                    //                     "\n", dout);
                    // String dataMsg= receivedFromServer(brin);
                    // sendToServer("OK\n", dout);
                    // //RESPONDS DATA: Server States
                    // //#region
                    // //Extract number of data lines from 'Data message' and Print Servers
                    // //Create an object for each server read and add to listOfServerStates
                    // String[] dataMsgArr = dataMsg.trim().split("\\s+");
                    // Integer noDataLines = Integer.parseInt(dataMsgArr[1]);
                    // for (int i = 0; i < noDataLines; i++) {
                    //     String serverStatesMsg = receivedFromServer(brin);

                    //     String[] serverMsgArr = serverStatesMsg.trim().split("\\s+");
                    //     Server serverState = new Server(
                    //         serverMsgArr[0],                    //Type
                    //         Integer.parseInt(serverMsgArr[1]),  //ID
                    //         serverMsgArr[2],  //State
                    //         Integer.parseInt(serverMsgArr[3]),  //curStartTime
                    //         Integer.parseInt(serverMsgArr[4]),  //Cores
                    //         Integer.parseInt(serverMsgArr[5]),  //Memory
                    //         Integer.parseInt(serverMsgArr[6]),  //Disk
                    //         Integer.parseInt(serverMsgArr[7]), 
                    //         Integer.parseInt(serverMsgArr[8])
                    //     );
                    //     listOfCapable.add(serverState);
                    // }

                    // sendToServer("OK\n", dout);
                    // receivedFromServer(brin);

                    // sendToServer("REDY\n", dout);
                    // //RESPONDS JOBN
                    // receivedFromServer(brin);
                    
                    // Boolean capableServerInactiveCheck = true;
                    // for (Server server : listOfCapable) {
                    //     if(!server.serverState.equals("inactive")){
                    //         capableServerInactiveCheck = false;
                    //     }
                    //     if(server.serverState.equals("booting") ||
                    //                 server.serverState.equals("idle")){
                    //         sendToServer("SCHD " + dsServerMsgArr[2] + " "
                    //                         + server.type + " "
                    //                         + server.serverId
                    //                         + "\n", dout);
                    //         break;
                    //     }
                    // }

                    // //Schedule job on biggest capable server if all server is inactive
                    // if(capableServerInactiveCheck){
                    //     sendToServer("SCHD " + dsServerMsgArr[2] + " "
                    //                     + listOfCapable.get(listOfCapable.size() - 1).type + " "
                    //                     + listOfCapable.get(listOfCapable.size() - 1).serverId
                    //                     + "\n", dout);
                    // }
                    
                    // //RESPONSE 'OK'
                    // receivedFromServer(brin);


                    //replicated FC
                    // Job job = new Job(
                    //     Integer.parseInt(dsServerMsgArr[1]),
                    //     Integer.parseInt(dsServerMsgArr[2]),
                    //     Integer.parseInt(dsServerMsgArr[3]),
                    //     Integer.parseInt(dsServerMsgArr[4]),
                    //     Integer.parseInt(dsServerMsgArr[5]),
                    //     Integer.parseInt(dsServerMsgArr[6]));

                    // for (Server server : listOfServers) {
                    //     if(job.core <= server.cores &&
                    //         job.memory <= server.memory &&
                    //         job.disk <= server.disk){
                    //         sendToServer("SCHD " + dsServerMsgArr[2] + " "
                    //                         + server.type + " "
                    //                         + server.serverId
                    //                         + "\n", dout);
                    //         break;
                    //     }
                    // }
                    // //RESPONSE 'OK'
                    // receivedFromServer(brin);