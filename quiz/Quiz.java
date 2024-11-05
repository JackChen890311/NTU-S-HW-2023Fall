import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


public class Quiz {

    public static void main(String[] args) {
        Hospital hospital = new Hospital("NTUH");
        Ward ward = new Ward(1);
        hospital.wards.add(ward);
        MonitoringSystem monitor = new MonitoringSystem("monitor");

        Quiz quiz = new Quiz();
        quiz.read_input_file(args[0], ward, monitor);

        monitor.read_factor();
        monitor.display();
    }
    
    void read_input_file(String file_path, Ward ward, MonitoringSystem monitor){
        try {
            File file = new File(file_path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                String[] tokens = st.split(" ");
                if (tokens.length == 1){
                    monitor.monitor_period = Integer.parseInt(tokens[0]);
                }
                else if (tokens.length == 3 && tokens[0].equals("patient")){
                    Patient p = new Patient(tokens[1], Integer.parseInt(tokens[2]));
                    ward.patients.add(p);
                    monitor.monitored_patients.add(p);
                }
                else if (tokens.length == 5){
                    Patient p = monitor.monitored_patients.get(monitor.monitored_patients.size() - 1);
                    p.safe_lower.add(Integer.parseInt(tokens[3]));
                    p.safe_upper.add(Integer.parseInt(tokens[4]));
                    
                    int patient_pos = p.attached_sensors.size();
                    Device d = new Device(tokens[0], tokens[1], tokens[2], p, patient_pos);
                    p.attached_sensors.add(d.sensor);
                    if (!monitor.device_order.containsKey(p.name)){
                        monitor.device_order.put(p.name, new ArrayList<>());
                    }
                    monitor.device_order.get(p.name).add(d);
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


class Ward {
    int ward_id;
    List<Patient> patients = new ArrayList<>();

    Ward(int ward_id){
        this.ward_id = ward_id;
    }
}


class Hospital {
    String name;
    List<Ward> wards = new ArrayList<>();

    Hospital(String name){
        this.name = name;
    }
}


class Factor {
    int timestamp;
    double value;

    Factor(int timestamp, double value) {
        this.timestamp = timestamp;
        this.value = value;
    }
}


class FactorDatabase {
    Map<String, Map<String, List<Factor>>> records = new HashMap<>();

    void add_factor(Patient patient, Device device, Factor factor){
        if (!this.records.containsKey(patient.name)){
            this.records.put(patient.name, new HashMap<>());
        }
        if (!this.records.get(patient.name).containsKey(device.name)){
            this.records.get(patient.name).put(device.name, new ArrayList<>());
        }
        this.records.get(patient.name).get(device.name).add(factor);
    }

    void display(Patient patient, Device device){
        System.out.println("patient " + patient.name);
        System.out.println(device.category + " " + device.name);
        if (this.records.get(patient.name).containsKey(device.name)){
            for (Factor factor : this.records.get(patient.name).get(device.name)){
                System.out.println("[" + factor.timestamp + "] " + factor.value);
            }
        }
    }
}


class MonitoringSystem{
    String name;
    String file_path;
    int monitor_period;
    FactorDatabase db = new FactorDatabase();
    List<Patient> monitored_patients = new ArrayList<>();
    Map<String, List<Device>> device_order = new HashMap<>();

    MonitoringSystem(String name){
        this.name = name;
    }

    void read_factor(){
        for (int time = 0; time <= this.monitor_period; time++){
            for (Patient patient : this.monitored_patients){
                if (time % patient.period == 0){
                    for (Device device : device_order.get(patient.name)){
                        int data_index = time / (int) patient.period;
                        String status = device.monitor(data_index);
                        double reading = device.measure(data_index);

                        if (status.equals("failed")){
                            System.out.println("[" + time + "] " + device.name + " fails");
                        }
                        else if (status.equals("danger")){
                            System.out.printf("[%d] %s is in danger! Cause: %s %.1f\n", time, patient.name, device.name, reading);
                        }
                        Factor factor = new Factor(time, reading);
                        this.db.add_factor(patient, device, factor);
                    }
                }    
            }
        }
    }

    void display(){
        for (Patient patient : this.monitored_patients){
            for (Device device : this.device_order.get(patient.name)){
                this.db.display(patient, device);
            }
        }
    }
}


class Patient {
    String name;
    int period;
    List<Integer> safe_lower = new ArrayList<>();
    List<Integer> safe_upper = new ArrayList<>();
    List<Sensor> attached_sensors = new ArrayList<>();

    Patient(String name, int period){
        this.name = name;
        this.period = period;
    }
}


class Sensor {
    String name;
    Patient attached_patient;
    List<Double> data = new ArrayList<>();

    Sensor(String name, Patient attached_patient, String dataset_file){
        this.name = name;
        this.attached_patient = attached_patient;
        this.read_data(dataset_file);
    }

    void read_data(String dataset_file) {
        File fakeDataFile = new File(dataset_file);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fakeDataFile));
            String line;
            while ((line = reader.readLine()) != null) {
                this.data.add(Double.parseDouble(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    double measure(int idx){
        if (idx < this.data.size()){
            return this.data.get(idx);
        }
        else{
            return -1;
        }
    }
}


class Device {
    String category;
    String name;
    List<Double> data = new ArrayList<>();
    Sensor sensor;
    Patient patient;
    int patient_pos;

    Device(String category, String name, String dataset_file, Patient patient, int patient_pos){
        this.category = category;
        this.name = name;
        this.sensor = new Sensor(name, patient, dataset_file);
        this.patient = patient;
        this.patient_pos = patient_pos;
    }

    double measure(int idx){
        return this.sensor.measure(idx);
    }

    String monitor(int idx){
        double value = this.sensor.measure(idx);
        if (value == -1){
            return "failed";
        }
        else if (value < this.patient.safe_lower.get(this.patient_pos) || value > this.patient.safe_upper.get(this.patient_pos)){
            return "danger";
        }
        else{
            return "safe";
        }
    }
}
