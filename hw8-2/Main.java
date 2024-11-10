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
    Application application;

    public Client(){
        this.application = new Application();
    }

    public void read_input_file(String file_path){
        try {
            File file = new File(file_path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                String[] tokens = st.split(" ");
                if (tokens[0].equals("Draw")){
                    this.application.addDocument("DrawingDocument");
                }
                else if (tokens[0].equals("Text")){
                    this.application.addDocument("TextDocument");
                }
                else if (tokens[0].equals("Present")){
                    this.application.printDocuments();
                }
                else{
                    System.out.println("Input Error");
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


class Application{
    public List<Document> documentList;
    public DocumentFactory documentFactory;

    public Application(){
        this.documentList = new ArrayList<Document>();
        this.documentFactory = new DocumentFactory();
    }

    public void addDocument(String type){
        Document document = this.documentFactory.createDocument(type);
        this.documentList.add(document);
    }

    public void printDocuments(){
        for (Document document : this.documentList){
            System.out.println(document.getName());
        }
    }
}


class DocumentFactory{
    public Document createDocument(String type){
        if (type.equals("DrawingDocument")){
            return new DrawingDocument();
        }
        else if (type.equals("TextDocument")){
            return new TextDocument();
        }
        else{
            return null;
        }
    }
}


abstract class Document{
    protected String name;

    public String getName() {
        return this.name;
    }
}


class DrawingDocument extends Document{
    public DrawingDocument() {
        this.name = "DrawingDocument";
    }
}


class TextDocument extends Document{
    public TextDocument() {
        this.name = "TextDocument";
    }
}