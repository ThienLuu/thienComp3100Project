public class Server implements Comparable<Server>{
    String type;
    Integer serverId;
    String serverState;
    Integer curStartTime;
    Integer cores;
    Integer memory;
    Integer disk;
    Integer wJobs;
    Integer rJobs;

    public Server(String type,
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

    Integer getWJobs(){
        return wJobs;
    }

    @Override
    public int compareTo(Server ss) {
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
