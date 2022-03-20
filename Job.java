public class Job {
    //ID
    Integer number;
    String type;

    //Time (secs)
    Integer submissionTime;
    Integer estimatedRuntime;

    //Resource requirments
    Integer cpuCores;
    Integer memory;
    Integer disk;

    public Job(Integer n,
                String t,
                Integer s, 
                Integer e, 
                Integer c, 
                Integer m, 
                Integer d){
            
        number = n;
        type = t;
        submissionTime = s;
        estimatedRuntime = e;
        cpuCores = c;
        memory = m;
        disk = d;
    }

    //Method
    
}
