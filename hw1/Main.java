import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Handler handler = new Handler();
        handler.read_input_file(args[0]);
    }
}


class Handler{
    Composition composition;

    public Handler(){
        this.composition = new Composition();
    }

    public void read_input_file(String file_path){
        try {
            File file = new File(file_path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                String[] tokens = st.split(" ");
                if (tokens[0].equals("Text")){
                    Text text = new Text(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), tokens[5]);
                    this.composition.addComponent(text);
                }
                else if (tokens[0].equals("GraphicalElement")){
                    GraphicalElement graphicalElement = new GraphicalElement(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), tokens[5]);
                    this.composition.addComponent(graphicalElement);
                }
                else if (tokens[0].equals("ChangeSize")){
                    this.composition.findComponent(Integer.parseInt(tokens[1])).changeSize(Integer.parseInt(tokens[2]));
                }
                else if (tokens[0].equals("Require")){
                    if (tokens[1].equals("SimpleComposition")){
                        this.composition.compose(new SimpleComposition());
                    }
                    else if (tokens[1].equals("TexComposition")){
                        this.composition.compose(new TexComposition());
                    }
                    else if (tokens[1].equals("ArrayComposition")){
                        this.composition.compose(new ArrayComposition());
                    }
                    else{
                        System.out.println("Input Error");
                    }
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


abstract class Component{
    int id;
    int naturalSize;
    int shrinkability;
    int stretchability;

    public void changeSize(int newSize){
        if (newSize < shrinkability || stretchability < newSize){
            System.out.println("component " + id + " failed to change size");
        }
        else{
            this.naturalSize = newSize;
            System.out.println("component " + id + " size changed to " + newSize);
        }
    }

    public abstract void display();

}


class Text extends Component{
    String content;

    public Text(int id, int naturalSize, int shrinkability, int stretchability, String content){
        this.id = id;
        this.naturalSize = naturalSize;
        this.shrinkability = shrinkability;
        this.stretchability = stretchability;
        this.content = content;
    }

    public void display(){
        System.out.printf("[" + naturalSize + "]" + content);
    }
}


class GraphicalElement extends Component{
    String path;

    public GraphicalElement(int id, int naturalSize, int shrinkability, int stretchability, String path){
        this.id = id;
        this.naturalSize = naturalSize;
        this.shrinkability = shrinkability;
        this.stretchability = stretchability;
        this.path = path;
    }

    public void display(){
        System.out.printf("[" + naturalSize + "]" + path);
    }
}


class Composition{
    List<Component> componentList = new ArrayList<>();

    public void compose(Startegy startegy){
        startegy.compose(componentList);
    }

    public void addComponent(Component component){
        componentList.add(component);
    }

    public Component findComponent(int id){
        for (Component component : componentList){
            if (component.id == id){
                return component;
            }
        }
        return null;
    }
}


interface Startegy{
    public abstract void compose(List<Component> componentList);
}

class SimpleComposition implements Startegy{
    public void compose(List<Component> componentList){
        for (Component component : componentList){
            component.display();
            System.out.println();
        }

    }
}

class TexComposition implements Startegy{
    public void compose(List<Component> componentList){
        for (Component component : componentList){
            component.display();
            if (component instanceof Text){
                Text text = (Text) component;
                if (text.content.equals("<ParagraphEnd>")){
                    System.out.println();
                }
                else{
                    System.out.print(" ");
                }
            }
            else{
                System.out.print(" ");
            }
        }

    }
}

class ArrayComposition implements Startegy{
    public void compose(List<Component> componentList){
        for (int i = 0; i < componentList.size(); i+=3){
            for (int j = i; j < i+3; j++){
                if (j < componentList.size()){
                    componentList.get(j).display();
                    if (j != i+2 && j != componentList.size()-1){
                        System.out.print(" ");
                    }
                }
            }
            if (i+3 < componentList.size()){
                System.out.println();
            }
        }
    }
}