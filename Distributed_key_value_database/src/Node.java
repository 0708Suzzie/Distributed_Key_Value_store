import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;

public class Node extends distributedDatabase {

    Node() throws IOException {


    }

    public boolean setKey(){
        syncer.downSync(fileName);
        String key = getInputKey();
        String value = getInputValue();
        JSONParser jsonParser = new JSONParser();
        try {
            String path = tempDatabasePath + "/" + fileName + ".json";
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(path));
            jsonObject.put(key, value);

            File currFile = new File(path);

            currFile.delete();

            try (FileWriter file = new FileWriter(path) ){
                file.write(jsonObject.toJSONString());
                file.flush();
                syncer.upSync(fileName);
                return true;

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


    }

    public boolean getKey(){
        syncer.downSync(fileName);
        String key = getInputKey();
        JSONParser jsonParser = new JSONParser();
        try {
            String path = tempDatabasePath + "/" + fileName + ".json";
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(path));
            String value = (String) jsonObject.get(key);
            System.out.println("The value for the key: " + key + " is : " + value);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


        return false;
    }


    public boolean deleteKey(){

        syncer.downSync(fileName);
        String key = getInputKey();
        JSONParser jsonParser = new JSONParser();
        try {
            String path = tempDatabasePath + "/" + fileName + ".json";
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(path));
            jsonObject.remove(key);

            File currFile = new File(path);

            currFile.delete();

            try (FileWriter file = new FileWriter(path) ){
                file.write(jsonObject.toJSONString());
                file.flush();
                syncer.upSync(fileName);
                return true;

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


    }

    public boolean getAll(){
        syncer.downSync(fileName);
        JSONParser parser = new JSONParser();

        try {
            String path = tempDatabasePath + "/" + fileName + ".json";
            JSONObject json = (JSONObject) parser.parse(new FileReader(path));
            Set<String> keyset = json.keySet();
            Iterator<String> keys = keyset.iterator();
            while(keys.hasNext()){
                String key = keys.next();
                Object value = json.get(key);
                System.out.println( key +" : " + value);
            }
            return true;

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public boolean disableNode(){

        String filePath = tempDatabasePath;
        File file = new File(filePath);
        try{
            deleteDirectory(file);
            file.delete();
            System.out.println("temp_db deleted successfully.");
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }


    protected String getInputKey(){
        System.out.println("Kindly enter the key");
        Scanner sc = new Scanner(System.in);
        String str = sc.nextLine();
        return str;
    }
    protected String getInputValue(){
        System.out.println("Kindly enter the value");
        Scanner sc = new Scanner(System.in);
        String str = sc.nextLine();
        return str;
    }

    protected static void deleteDirectory(File file)
    {
        for (File subfile : file.listFiles()) {
            if (subfile.isDirectory()) {
                deleteDirectory(subfile);
            }
            subfile.delete();
        }
    }

}
