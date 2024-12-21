import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;


public class Quiz {
    public static void main(String[] args) {
        Client client = new Client();
        client.read_input_file(args[0]);
    }
}


class Client{
    // Implement main logic in this class
    private HashMap<String, Component> componentMap;

    public Client(){
        this.componentMap = new HashMap<String, Component>();
    }

    public void read_input_file(String file_path){
        try {
            File file = new File(file_path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                String[] tokens = st.split(" ");
                // Process input here
                // System.out.print("=====");
                // System.out.print(st);
                // System.out.println("=====");
                if (tokens[0].equals("create")){
                    String name = tokens[1];
                    double power = Double.parseDouble(tokens[2]);
                    double cost = Double.parseDouble(tokens[3]);
                    Chassis chassis = new Chassis(name, power, cost);
                    this.componentMap.put(name, chassis);
                }
                else if (tokens[0].equals("add")){
                    String type = tokens[1];
                    String cptName = tokens[2];
                    double power = Double.parseDouble(tokens[3]);
                    double cost = Double.parseDouble(tokens[4]);
                    String parentName = tokens[5];

                    Component parent = this.componentMap.get(parentName);
                    Component child;
                    if (type.equals("bus")){
                        child = new Buses(cptName, power, cost);
                    }
                    else if (type.equals("floppy")){
                        child = new Floppies(cptName, power, cost);
                    }
                    else{
                        // System.out.println("input error");
                        continue;
                    }
                    ((Chassis)parent).addComponent(child);
                    this.componentMap.put(cptName, child);
                }
                else if (tokens[0].equals("get")){
                    String name = tokens[1];
                    int index = Integer.parseInt(tokens[2]);
                    Component cpt = this.componentMap.get(name);

                    if (cpt instanceof Chassis){
                        Iterator it = ((Chassis)cpt).createIterator();
                        if (index >= it.getSize()){
                            System.out.println("Index " + index + " out of bound of " + cpt.name);
                            continue;
                        }
                        int i = 0;
                        while (it.hasNext()){
                            Component subcpt = it.next();
                            if (i == index){
                                System.out.println(cpt.name + ":" + subcpt.name);
                                break;
                            }
                            i += 1;
                        }
                    }
                    else{
                        System.out.println(cpt.name + " does not support command get");
                        continue;
                    }
                }
                else if (tokens[0].equals("print")){
                    String name = tokens[1];
                    Component cpt = this.componentMap.get(name);
                    if (cpt instanceof Chassis){
                        cpt.print();
                        Iterator it = ((Chassis)cpt).createIterator();
                        while (it.hasNext()){
                            Component subcpt = it.next();
                            System.out.print(cpt.name + ":");
                            subcpt.print();
                        }
                    }
                    else{
                        cpt.print();
                    }
                }
                else if (tokens[0].equals("sumOfPowerConsumption")){
                    String name = tokens[1];
                    Component cpt = this.componentMap.get(name);
                    if (cpt instanceof Chassis){
                        System.out.println(cpt.getPower());
                    }
                    else{
                        System.out.println(cpt.name + " does not support command sumOfPowerConsumption");
                        continue;
                    }
                }
                else if (tokens[0].equals("sumOfAdditionCost")){
                    String name = tokens[1];
                    Component cpt = this.componentMap.get(name);
                    if (cpt instanceof Chassis){
                        System.out.println(cpt.getCost());
                    }
                    else{
                        System.out.println(cpt.name + " does not support command sumofAdditionCost");
                        continue;
                    }
                }
                else{
                    System.out.println("input error");
                    continue;
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


// ===== Components =====
abstract class Component{
    String name;
    protected double power;
    protected double cost;

    public Component(String name, double power, double cost){
        this.name = name;
        this.power = power;
        this.cost = cost;
    }

    double getPower(){
        return this.power;
    }

    double getCost(){
        return this.cost;
    }

    void print(){
        System.out.println(this.name + " (" + this.power + ", " + this.cost + ")");
    }
}


class Buses extends Component{
    public Buses(String name, double power, double cost){
        super(name, power, cost);
    }
}

class Floppies extends Component{
    public Floppies(String name, double power, double cost){
        super(name, power, cost);
    }
}

class Chassis extends Component{
    List<Component> componentList = new ArrayList<Component>();

    public Chassis(String name, double power, double cost){
        super(name, power, cost);
    }

    double getPower(){
        double sum = this.power;
        for (Component cpt : this.componentList){
            sum += cpt.getPower();
        }
        return sum;
    }

    double getCost(){
        double sum = this.cost;
        for (Component cpt : this.componentList){
            sum += cpt.getCost();
        }
        return sum;
    }

    void addComponent(Component cpt){
        this.componentList.add(cpt);
    }

    Iterator createIterator(){
        return new ChassisIterator(this);
    }
}

// ===== Iterator =====

interface Iterator{
    Component next();
    boolean hasNext();
    int getSize();
}


class ChassisIterator implements Iterator{
    private Chassis chassis;
    private int index;
    private int size;

    public ChassisIterator(Chassis chassis){
        this.chassis = chassis;
        this.index = 0;
        this.size = this.chassis.componentList.size();
    }

    public Component next(){
        if (this.hasNext()){
            Component component = this.chassis.componentList.get(this.index);
            this.index += 1;
            return component;
        }
        return null;
    }

    public boolean hasNext(){
        return this.index < this.chassis.componentList.size();
    }

    public int getSize(){
        return this.size;
    }    
}   