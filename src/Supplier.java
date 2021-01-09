import java.util.UUID;

public class Supplier {

    private final int id;
    private final String name;
    private final int logisticId;

    public Supplier(int id,int numLogistics){
        this.id            = id;
        logisticId         = (int )(Math.random() * numLogistics + 1);
        name               = "SUPP_"+UUID.randomUUID().toString().substring(0,2);
    }

    public String getName(){
        return name;
    }
    @Override
    public String toString(){
        return id+","+name+","+logisticId;
    }
}
