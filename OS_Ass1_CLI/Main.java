package OS_Ass1_CLI;

import java.io.File;
import java.util.Scanner;
import static OS_Ass1_CLI.Parser.parser;
/**
 *
 * @author Omar Mostafa
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        File file = new File("C:\\");// start in C directory
        Scanner input = new Scanner(System.in);
        String command = "";
        parser(file,command,input);
    }

}
