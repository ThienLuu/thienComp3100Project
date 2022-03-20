public class Server {
    String type;
    Integer limit;
    Integer bootupTime;
    Integer hourlyRate;
    Integer cores;
    Integer memory;
    Integer disk;

    public Server(String t,
                    Integer l, 
                    Integer b, 
                    Integer h, 
                    Integer c, 
                    Integer m, 
                    Integer d){
        type = t;
        limit = l;
        bootupTime = b;
        hourlyRate = h;
        cores = c;
        memory = m;
        disk = d;
    }

    //Method
}
