import java.util.UUID;

public class Logistic {

    private final int id;
    private final String name;
    private final int count_received;
    private final int count_sent;

    public Logistic(int id){
        this.id = id;
        this.name = "LOGIS_"+UUID.randomUUID().toString().substring(0,2);
        this.count_received = 0;
        this.count_sent     = 0;
    }

    @Override
    public String toString(){
        return id+","+name+","+count_received+","+count_sent;
    }
}
