public class ServerState implements Comparable<ServerState>{
    String type;
    Integer serverId;
    String serverState;
    Integer curStartTime;
    Integer cores;
    Integer memory;
    Integer disk;
    Integer wJobs;
    Integer rJobs;

    public ServerState(String type,
                    Integer serverId,
                    String serverState,
                    Integer curStartTime,
                    Integer cores, 
                    Integer memory, 
                    Integer disk,
                    Integer wJobs,
                    Integer rJobs){
        this.type = type;
        this.serverId = serverId;
        this.serverState = serverState;
        this.curStartTime = curStartTime;
        this.cores = cores;
        this.memory = memory;
        this.disk = disk;
        this.wJobs = wJobs;
        this.rJobs = rJobs;
    }

    @Override
    public int compareTo(ServerState ss) {
        if(this.cores == ss.cores){
            return 0;
        }
        else if(this.cores < ss.cores){
            return 1;
        }
        else{
            return -1;
        }
    }
}
