import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


public class Quiz {
    public static void main(String[] args) {
        Monitor monitor = new Monitor(args);
        monitor.startMonitor();
    }
}


class Monitor {
    String filePath;
    int monitorTimeLength;
    List<Patient> patientList = new ArrayList<>();

    Monitor(String[] filePath){
        try{
            this.filePath = filePath[0];
            List<String> inputData = this.readInputFile(this.filePath);
            this.processInputData(inputData);
        }
        catch (Exception e){
            System.out.println("Input Error");
        }
    }

    void processInputData(List<String> inputData) {
        for (String line : inputData) {
            String[] words = line.split(" ");
            if (words[0].equals("patient")) {
                Patient patient = new Patient(words[1], Integer.parseInt(words[2]));
                this.patientList.add(patient);
            }
            else if (words.length == 1) {
                this.monitorTimeLength = Integer.parseInt(words[0]);
            }
            else {
                Patient patientLast = this.patientList.get(patientList.size() - 1);
                Device device = new Device(words[0], words[1], words[2], Integer.parseInt(words[3]), Integer.parseInt(words[4]));
                patientLast.devices.add(device);
            }
        }
    }

    void startMonitor(){
        // start monitor
        for (int i = 0; i <= this.monitorTimeLength; i++) {
            for (Patient patient : patientList) {
                patient.updateTime(i);
            }
        }

        // final database output
        // System.out.println("===== final database output =====");
        for (Patient patient : patientList) {
            patient.printDatabase();
        }

        // Test output data
        // System.out.println("===== Hello World =====");
        // System.out.println("monitorTimeLength: " + monitorTimeLength);
        // for (Patient patient : patientList) {
        //     System.out.println("Patient: " + patient.name + " " + patient.monitorFreq);
        //     System.out.println("Monitor Time: " + patient.monitorTimeList);
        //     for (Device device : patient.devices) {
        //         System.out.println("Device: " + device.type + " " + device.name + " " + device.dataPath + " " + device.safeLow + " " + device.safeHigh);
        //         System.out.println("Data: " + device.data);
        //     }
        // }  
    }

    List<String> readInputFile(String filepath) {
        List<String> result = new ArrayList<>();
        File fakeDataFile = new File(filepath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fakeDataFile));
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
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
        return result;
    }
}


class Patient {
    String name;
    int monitorFreq;
    List<Integer> monitorTimeList = new ArrayList<>();
    List<Device> devices = new ArrayList<>();

    Patient(String name, int monitorFreq) {
        this.name = name;
        this.monitorFreq = monitorFreq;
    }

    void updateTime(int time){
        if (time % this.monitorFreq == 0) {
            monitorTimeList.add(time);
            for (Device device : devices) {
                int dataCnt = time / (int) this.monitorFreq;
                
                if (dataCnt < device.data.size()) {
                    double dataValue = device.data.get(dataCnt);
                    if (dataValue == -1.0) {
                        System.out.println("[" + time + "] " + device.name + " fails");
                    }
                    else if (dataValue < device.safeLow || dataValue > device.safeHigh) {
                        System.out.printf("[%d] %s is in danger! Cause: %s %.1f\n", time, this.name, device.name, dataValue);
                    }
                }
                else{
                    System.out.println("[" + time + "] " + device.name + " fails");
                }
            }
        }
    }

    void printDatabase(){
        System.out.println("patient " + this.name);
        for (Device device : devices) {
            System.out.println(device.type + " " + device.name);
            int cnt = 0;
            for (Integer time: monitorTimeList) {
                if (cnt < device.data.size()) {
                    double dataValue = device.data.get(cnt);
                    System.out.println("[" + time + "] " + dataValue);
                    cnt++;
                }
                else{
                    System.out.println("[" + time + "] " + "-1.0");
                }
            }
        }
    }
}


class Device {
    String type;
    String name;
    String dataPath;
    double safeLow;
    double safeHigh;
    List<Double> data = new ArrayList<>();

    Device(String type, String name, String dataPath, double safeLow, double safeHigh) {
        this.type = type;
        this.name = name;
        this.dataPath = dataPath;
        this.safeLow = safeLow;
        this.safeHigh = safeHigh;
        this.readInputData(dataPath);
    }

    void readInputData(String filepath) {
        File fakeDataFile = new File(filepath);
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
}


