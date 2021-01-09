import java.util.UUID;

public class Clinic {

    private final int id;
    private int demand;
    private final int logistic;
    private final String location;


    public Clinic(int id, int numLogistics){
        this.id                = id;
        location               = "CLINIC_"+UUID.randomUUID().toString().substring(0,6);
        this.demand            = (int )(Math.random() * 200 + 100);
        this.logistic          = (int )(Math.random() * numLogistics + 1);
    }
    public String getLoc(){
        return location;
    }
    public int getDemand(){
        if(demand <= 0) return 0;
        return demand;
    }

    public void decreaseDemand(int demand)
    {
        if(demand <=0) return;
        this.demand = demand;
    }

    @Override
    public String toString(){
        return id+","+location+","+demand+","+logistic;
    }
}
