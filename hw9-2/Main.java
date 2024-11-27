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
    RDFReader reader;

    public Client(){
        this.reader = new RDFReader();
    }

    public void read_input_file(String file_path){
        try {
            File file = new File(file_path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                String[] tokens = st.split(" ");
                // Process input here
                if (tokens[0].equals("TeX")){
                    Converter converter = new TeXTextConverter();
                    reader.setConverter(converter);
                }
                else if (tokens[0].equals("TextWidget")){
                    Converter converter = new TextWidgetConverter();
                    reader.setConverter(converter);
                }
                else{
                    reader.addContent(tokens[0]);
                    reader.convert();
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


// ===== Reader and Document ===== 
class RDFReader{
    public Document document;
    public Converter converter;

    void setConverter(Converter converter){
        this.converter = converter;
    }

    void addContent(String content){
        this.document = new Document();
        for (int i = 0; i < content.length(); i++){
            if (content.charAt(i) == 'C'){
                document.addChar(new CharacterChar());
            }
            else if (content.charAt(i) == 'F'){
                document.addChar(new FontChange());
            }
            else if (content.charAt(i) == 'P'){
                document.addChar(new Paragraph());
            }
        }
    }

    void convert(){
        for (Char c : document.chars){
            c.acceptConverter(converter);
        }
        System.out.println();
    }

}

class Document{
    public List<Char> chars;

    public Document(){
        chars = new ArrayList<Char>();
    }

    public void addChar(Char c){
        chars.add(c);
    }
}


// ===== Char
interface Char{
    public void acceptConverter(Converter converter);
}

class CharacterChar implements Char{
    public void acceptConverter(Converter converter){
        converter.convertChar();
    }
}

class FontChange implements Char{
    public void acceptConverter(Converter converter){
        converter.convertFontChange();
    }
}

class Paragraph implements Char{
    public void acceptConverter(Converter converter){
        converter.convertParagraph();
    }
}


// ===== Converter =====
interface Converter{

    public void convertChar();
    public void convertFontChange();
    public void convertParagraph();

}

class TeXTextConverter implements Converter{
    public void convertChar(){
        System.out.print("c");
    }

    public void convertFontChange(){
        System.out.print("_");
    }

    public void convertParagraph(){
        System.out.print("|");
    }   
}

class TextWidgetConverter implements Converter{
    public void convertChar(){
        System.out.print("<Char>");
    }

    public void convertFontChange(){
        System.out.print("<Font>");
    }

    public void convertParagraph(){
        System.out.print("<Paragraph>");
    }   
}
