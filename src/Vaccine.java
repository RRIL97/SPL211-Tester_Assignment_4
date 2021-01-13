import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Vaccine {


    private final String id;
    private String generatedDate;
    private int supplier;
    private int quantity;
    private final int numSuppliers;

    public Vaccine(int id,int numSuppliers){
        this.id = (id < 10 ? "0" : "") + id;
        this.numSuppliers = numSuppliers;
        generateValues();
    }
    public void generateValues(){
        generatedDate = new SimpleDateFormat("yyyy-MM-"+id).format(Calendar.getInstance().getTime());
        supplier = (int )(Math.random() * numSuppliers + 1);
        quantity = (int )(Math.random() * 120 + 30);
    }
    public int getQuantity(){
        return quantity;
    }
    @Override
    public String toString(){
        return id+","+generatedDate+","+supplier+","+quantity;
    }
}
