import org.json.JSONObject;
 
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class TestMain {



    public static int numSuccessTest = 0;
    public static int numFailedTests = 0;
    public static void main(String [] args)
    {

     try {
         Scanner in = new Scanner(System.in);
         System.out.println("Input 1 to generate tests");
         System.out.println("Input 2 to run tests");

         int option = in.nextInt();
         if(option == 1) {
             ArrayList<String> testCasesGenerated = new ArrayList<>();

             for (int i = 0; i < 100; i++) {
                 System.out.println("Generated Test Number " + i );
                 Tester s = new Tester();
                 s.generateConfig();
                 s.generateOrders();
                 JSONObject result = s.executePythonAndParseResults(1);
                 testCasesGenerated.add(result.toString());
             }
             System.out.println("Wrote tests to Tests.txt" );
             Files.write(Paths.get("Tests.txt"), testCasesGenerated, Charset.defaultCharset());

         }else{

            Tester testRunner = new Tester();
            testRunner.runTests();
            System.out.println("Passed | " + numSuccessTest);
            System.out.println("Failed | " + numFailedTests);
         }
         }catch(Exception e){
         e.printStackTrace();
     }
     }
}
