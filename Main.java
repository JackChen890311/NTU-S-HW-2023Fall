import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Client client = new Client();
        client.read_input_file(args[0]);
    }
}


class Client{
    // Implement main logic in this class

    public Client(){
        
    }

    public void read_input_file(String file_path){
        try {
            File file = new File(file_path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                String[] tokens = st.split(" ");
                // Process input here
            }
            br.close();
        }
        catch (Exception e){
            System.out.println(e);
            System.out.println("Input Error");
        }
    }
}
