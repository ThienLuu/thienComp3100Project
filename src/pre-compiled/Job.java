public class Job {
    Integer submitTime;
    Integer jobId;
    Integer estRuntime;
    Integer core;
    Integer memory;
    Integer disk;
    Integer jobState;

    public Job(Integer submitTime,
                Integer jobId,
                Integer estRuntime,
                Integer core,
                Integer memory,
                Integer disk) {
        this.submitTime = submitTime;
        this.jobId = jobId;
        this.estRuntime = estRuntime;
        this.core = core;
        this.memory = memory;
        this.disk = disk;
        jobState = 0;
    }

    //METHOD: Set job state
    void setJobState(Integer state){
        jobState = state;
    }
}
