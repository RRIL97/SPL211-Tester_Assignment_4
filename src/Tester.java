
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

public class Tester {

    private String format = "";
    private final ArrayList<Vaccine> vaccines = new ArrayList<>();
    private final ArrayList<Supplier> suppliers = new ArrayList<>();
    private final ArrayList<Clinic> clinics = new ArrayList<>();
    private final ArrayList<Logistic> logistics = new ArrayList<>();

    private String config;
    private String orders;

    public void generateConfig() {
        try {
            int numVaccines = (int) (Math.random() * 5 + 2);
            ;
            int numSuppliers = (int) (Math.random() * 5 + 2);
            ;
            int numClinics = (int) (Math.random() * 5 + 2);
            int numLogistics = (int) (Math.random() * 5 + 2);

            config = "";
            format = format + numVaccines + "," + numSuppliers + "," + numClinics + "," + numLogistics;
            config += (format) + ("\n");

            for (int i = 1; i <= numVaccines; i++) {
                Vaccine vac = new Vaccine(i, numSuppliers - 1);
                vaccines.add(vac);
            }
            Collections.shuffle(vaccines);
            for (int i = 0; i < vaccines.size(); i++)
                config += vaccines.get(i).toString() + ("\n");

            for (int i = 0; i < numSuppliers; i++) {
                Supplier sup = new Supplier(i, numLogistics);
                suppliers.add(sup);
                config += sup.toString() + ("\n");
            }
            for (int i = 0; i < numClinics; i++) {
                Clinic clin = new Clinic(i, numLogistics);
                clinics.add(clin);
                config += (clin.toString()) + ("\n");
            }
            for (int i = 0; i < numLogistics; i++) {
                Logistic log = new Logistic(i + 1);
                logistics.add(log);
                config += log.toString() + ("\n");
            }
            Files.write(Paths.get("config.txt"), config.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String addOrders() {
        String supply = "";

        int sizeVaccines = vaccines.size() + 1;
        int numTimesToAddVaccines = ((int) (Math.random() * 6) + 1);
        for (int i = 0; i < numTimesToAddVaccines; i++) {
            Supplier sup = suppliers.get((int) (Math.random() * (suppliers.size() - 1) + 1));
            String supplierName = sup.getName();
            int numSupplyToAdd = (int) (Math.random() * 60 + 30);
            String temp = "";
            if(sizeVaccines < 10 )
                temp= supplierName + "," + numSupplyToAdd + "," + new SimpleDateFormat("yyyy-MM-0" + (sizeVaccines++)).format(Calendar.getInstance().getTime());
            else  temp =  supplierName + "," + numSupplyToAdd + "," + new SimpleDateFormat("yyyy-MM-" + (sizeVaccines++)).format(Calendar.getInstance().getTime());

            supply += (temp) + ("\n");
        }

        return supply;

    }

    public void generateOrders() {
        try {
            orders = "";
            //Adds supply
            orders += addOrders();

            //Send supply
            int totalSupply = 0;
            for (Vaccine current : vaccines)
                totalSupply += current.getQuantity();

            int numTimesToSendSupply = ((int) (Math.random() * clinics.size()) + 1);

            for (int i = 0; i < numTimesToSendSupply; i++) {
                Clinic clnc = clinics.get((int) (Math.random() * (clinics.size() - 1) + 1));
                String clinicName = clnc.getLoc();
                int numSupplyToSend = (int) ((int) (Math.random() * (clnc.getDemand()) + 2));
                if (totalSupply > numSupplyToSend) {
                    String temp = clinicName + "," + numSupplyToSend;
                    orders += (temp) + ("\n");
                    clnc.decreaseDemand(clnc.getDemand() - numSupplyToSend - 1);
                    totalSupply = totalSupply - numSupplyToSend;
                }
            }

            Files.write(Paths.get("orders.txt"), orders.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String getDbSummary(Statement stat, String dbName) {
        String summary = "";
        try {
            ResultSet rs;
            if (dbName.equals("vaccines"))
                rs = stat.executeQuery("select * from " + dbName + " order by date;");
            else
                rs = stat.executeQuery("select * from " + dbName + ";");

            JSONArray json = new JSONArray();
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                int numColumns = rsmd.getColumnCount();
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= numColumns; i++) {
                    String column_name = rsmd.getColumnName(i);
                    Object t_Obj = rs.getObject(column_name);
                    obj.put(column_name, t_Obj);
                }
                json.put(obj);
            }
            summary = json.toString();
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return summary;
    }

    public JSONObject executePythonAndParseResults(int testNumber) {
        JSONObject testSummary = new JSONObject();
        try {
            File db = new File("database.db");
            if (db.exists())
                db.delete();
            Process p = Runtime.getRuntime().exec("python3 main.py config.txt orders.txt output.txt");
            p.waitFor();

            Class.forName("org.sqlite.JDBC");

            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement stat = conn.createStatement();

            String dbSummary = "";
            dbSummary += (getDbSummary(stat, "clinics")) + "\n";
            dbSummary += (getDbSummary(stat, "logistics")) + "\n";
            dbSummary += (getDbSummary(stat, "suppliers")) + "\n";
            dbSummary += (getDbSummary(stat, "vaccines"));


            ArrayList<String> outputFile = (ArrayList<String>) Files.readAllLines(Paths.get("output.txt"));

            testSummary.put("dbSummary", dbSummary);
            testSummary.put("orderFile", orders.toString());
            testSummary.put("configFile", config.toString());
            testSummary.put("outputFile", outputFile.toString());
            stat.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return testSummary;
    }

    static void writeToFile(String input, String fileName) {
        try {

            try (PrintStream out = new PrintStream(new FileOutputStream(fileName))) {
                out.print(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean resultEqual(JSONObject first, JSONObject second) {

        String firstStr = first.toString();
        String secondStr = second.toString();

        for (int i = 1; i < 10; i++) {
            firstStr = firstStr.replaceAll("2021-01-" + i, "2021-01-0" + i);

            secondStr = secondStr.replaceAll("2021-01-" + i, "2021-01-0" + i);

        }
        for (int i = 0; i < 10; i++) {

            firstStr = firstStr.replaceAll("2021-01-01" + i, "2021-01-1" + i);
            secondStr = secondStr.replaceAll("2021-01-01" + i, "2021-01-1" + i);
        }

        firstStr = firstStr.replaceAll("\\\\\\\\n", "");
        secondStr = secondStr.replaceAll("\\\\\\\\n", "");

        System.out.println(firstStr.toString() + "\n" + secondStr);
        return firstStr.equals(secondStr);
    }

    void runTests() {
        try {
            ArrayList<String> readTests = (ArrayList<String>) Files.readAllLines(Paths.get("Tests.txt"));

            int currentTest = 1;
            for (String s : readTests) {
                JSONObject test = new JSONObject(s);
                String configFile = (String) test.get("configFile");
                System.out.println("\r\nConfig File:\r" + configFile + "\n");
                String orderFile = (String) test.get("orderFile");
                System.out.println("\r\nOrder File:\r" + orderFile + "\n");
                writeToFile(configFile, "config.txt");
                for (int i = 1; i < 10; i++) {
                    orderFile = orderFile.replaceAll("2021-01-" + i, "2021-01-0" + i);

                    orderFile = orderFile.replaceAll("2021-01-" + i, "2021-01-0" + i);

                }
                for (int i = 0; i < 10; i++) {

                    orderFile = orderFile.replaceAll("2021-01-01" + i, "2021-01-1" + i);
                    orderFile = orderFile.replaceAll("2021-01-01" + i, "2021-01-1" + i);
                }
                writeToFile(orderFile, "orders.txt");

                System.out.println("Initiating Test | " + currentTest + "\n----------------------");

                orders = orderFile;
                config = configFile;

                JSONObject result = executePythonAndParseResults(currentTest);
                if (resultEqual(test, result)) {
                    TestMain.numSuccessTest++;
                    System.out.println("Passed Test " + currentTest);

                } else {
                    TestMain.numFailedTests++;
                    System.out.println("Failed Test " + currentTest);
                    System.exit(0);

                }
                currentTest++;
                System.out.println("----------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
