public class JobType {
    String type;
    Integer minRunTime;
    Integer maxRunTime;
    Integer populationRate;

    public JobType(String t, Integer min, Integer max, Integer p){
        type = t;
        minRunTime = min;
        maxRunTime = max;
        populationRate = p;
    }

    //Method get/set
}
