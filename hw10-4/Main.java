import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Main {
    public static void main(String[] args) {
        Client client = new Client();
        client.read_input_file(args[0]);
    }
}


class Client{
    // Implement main logic in this class
    Window window = null;

    public void read_input_file(String file_path){
        try {
            File file = new File(file_path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                String[] tokens = st.split(" ");
                // Process input here
                if (tokens[0].equals("window")){
                    if (tokens[1].equals("IconWindow")){
                        window = new IconWindow();
                        window.setWindowImplementor(tokens[2]);
                    }
                    else if (tokens[1].equals("TransientWindow")){
                        window = new TransientWindow();
                        window.setWindowImplementor(tokens[2]);
                    }
                    else{
                        System.out.println("Input Error");
                    }
                }
                else{
                    if (tokens[0].equals("drawBorder")){
                        window.drawBorder();
                    }
                    else if (tokens[0].equals("drawCloseBox")){
                        window.drawCloseBox();
                    }
                    else{
                        System.out.println("Input Error");
                    }
                }
            }
            br.close();
        }
        catch (Exception e){
            System.out.println(e);
            System.out.println("Input Error");
        }
    }
}


// Window
class Window {
    WindowImplementor wimplt = null;

    void setWindowImplementor(String type){
        if(type.equals("XWindow")){
            wimplt = new XWindowImplemetor();
        }
        else if(type.equals("PMWindow")){
            wimplt = new PMWindowImplemetor();
        }
    }

    void drawBorder(){
        wimplt.drawText();
        wimplt.drawRectangle();
    }

    void drawCloseBox(){
        wimplt.drawRectangle();
    }
}


class IconWindow extends Window{
    void drawCloseBox(){
        // Not implemented
    }
}

class TransientWindow extends Window{
    void drawRectangle(){
        // Not implemented
    }
}



// WindowImplementor
interface WindowImplementor{
    void drawRectangle();
    void drawText();
}

class XWindowImplemetor implements WindowImplementor{
    public void drawRectangle(){
        System.out.println("XXXX");
    }
    public void drawText(){
        System.out.println("XWindow");
    }
}


class PMWindowImplemetor implements WindowImplementor{
    public void drawRectangle(){
        System.out.println("MMMM");
    }
    public void drawText(){
        System.out.println("PMWindow");
    }
}