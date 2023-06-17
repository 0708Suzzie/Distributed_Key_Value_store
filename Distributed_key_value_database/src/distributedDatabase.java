import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.nio.file.Files;
import java.security.SecureRandom;
import java.io.*;
import java.util.Scanner;

public class distributedDatabase implements database{

    protected String fileName = "";
    protected String tempDatabasePath;

    protected Sync syncer;


    distributedDatabase() throws IOException{
        int filenum = randomNumGenerator();
        String directoryName = "/Distributed_key_value_database/temp_db" + filenum;
        File file = new File(directoryName);
        if(file.mkdir()){
            System.out.println("temp_db created");
        }
        else{
            System.out.println("\nRelaunch the programme. Unable to create temporary database");
        }

        File source = new File("/Distributed_key_value_database/dataWarehouse/hashFile.json");
        File destination = new File(directoryName + "/hashfile.json");

        try {
            Files.copy(source.toPath(), destination.toPath());
            tempDatabasePath = directoryName;
            syncer = new Sync(tempDatabasePath);
        }
        catch(IOException e){
            System.out.println("\nCannot get Hashfiles. Kindly Relaunch the programme or check if the hashfiles exist at source");

        }
    }

    public boolean createDatabase(){
        String dbName = getDbName();
        if(dbExists(dbName)){
            System.out.println("Database with such a name Already exists");
            return false;
        }

        JSONObject jsonObject1 = new JSONObject();
        try (FileWriter file1 = new FileWriter(syncer.hashFilePath + "/" + dbName + ".json") ){
            file1.write(jsonObject1.toJSONString());
            file1.flush();
            fileName = dbName;


            JSONParser jsonParser = new JSONParser();
            try {

                String path = syncer.hashFilePath + "/hashfile.json";
                JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(path));
                jsonObject.put(fileName, String.valueOf(syncer.generateHash(syncer.hashFilePath + "/" + dbName + ".json")));

                File currFile = new File(path);

                currFile.delete();

                try (FileWriter file = new FileWriter(path) ){
                    file.write(jsonObject.toJSONString());
                    file.flush();
                    syncer.replaceFileInMainDb("hashFile");
                    syncer.replaceFileInMainDb(dbName);
                    System.out.println("New db created");
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

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }



    }
    public boolean getDatabase(){
        String dbName = getDbName();
        if(!dbExists(dbName)){
            System.out.println("Database with such a name does not exist");
            return false;
        }
        if(fileName != ""){
            syncer.upSync(fileName);
            File file = new File(syncer.hashFilePath + "/" + fileName + ".json");
            file.delete();
        }

        fileName = dbName;
        File source = new File(syncer.mainDbPath + fileName + ".json");
        File destination = new File(syncer.hashFilePath + "/" + fileName + ".json");

        destination.delete();


        try {
            Files.copy(source.toPath(), destination.toPath());
        } catch (IOException e) {
            System.out.println("\ncannot import the desired db, kindly check if it exists at source");
            return false;

        }
        return true;
    }
    public boolean dropDatabase(){
        String dbName = getDbName();
        if(!dbExists(dbName)){
            System.out.println("Database with such a name does not exist");
            return false;
        }
        File file = new File(syncer.hashFilePath + "/" + dbName + ".json");
        file.delete();
        File file2 = new File(syncer.mainDbPath + dbName + ".json");
        file2.delete();

        JSONParser jsonParser = new JSONParser();
        try {

            String path = syncer.hashFilePath + "/hashfile.json";
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(path));
            jsonObject.remove(dbName);

            File currFile = new File(path);

            currFile.delete();

            try (FileWriter file1 = new FileWriter(path) ){
                file1.write(jsonObject.toJSONString());
                file1.flush();
                syncer.replaceFileInMainDb("hashFile");

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


    protected String getDbName(){
        System.out.println("Kindly enter the dbName");
        Scanner sc = new Scanner(System.in);
        String databaseName = sc.nextLine();
        return databaseName;
    }

    protected boolean dbExists(String dbname){
        syncer.refreshHashFile();
        JSONParser jsonParser1 = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser1.parse(new FileReader( syncer.hashFilePath +"/hashFile.json"));
            if(jsonObject.containsKey(dbname)){
                return true;
            }
            return false;

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


    }


    private int randomNumGenerator(){
        SecureRandom rand = new SecureRandom();

        int upperBound = 1000;
        int randomNumber = rand.nextInt(upperBound);

        return randomNumber;
    }

}
