import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Sync {

    protected String hashFilePath;
    protected String mainDbPath = "/Users/rissrani/Distributed_key_value_database/dataWarehouse/";

    Sync(String hashFilePath) {
        this.hashFilePath = hashFilePath;
    }

    protected boolean upSync(String dbName) {
        int result = checkHashChange(dbName);
        if (result == 0) {
            String newHash = String.valueOf(generateHash(hashFilePath + "/hashfile.json"));
            replaceKey(dbName, newHash);
            replaceFileInMainDb("hashFile");
            replaceFileInMainDb(dbName);
            System.out.println("Files synced successfully");
            return true;

        } else if (result == 1) {
            System.out.println("Conflicting files. changes have been made in the main file, cannot merge");
            return true;
        }
        else{
            System.out.println("The database doesn't exist there anymore, so Creating a copy in the main db with the data here");
            return true;
        }

    }

    protected boolean downSync(String dbName) {
        int result = checkHashChange(dbName);
        if (result == 1) {
            File source = new File(mainDbPath+dbName+".json");
            File destination = new File(hashFilePath + "/" + dbName+".json");

            destination.delete();


            try {
                Files.copy(source.toPath(), destination.toPath());
                System.out.println("Database Synced Successfully");

            } catch (IOException e) {
                System.out.println("\nCannot downSync current database. Kindly Relaunch the programme or check if the hashfiles exists at source");

            }
            return  true;

        } else if (result == 0) {
            System.out.println("Database Synced Successfully");
            return true;
        }
        else{
            System.out.println("The Database Doesn't exist there anymore");
        }

        return false;
    }


    protected void refreshHashFile() {

        File source = new File(mainDbPath+"hashFile.json");
        File destination = new File(String.format("%s/hashfile.json", hashFilePath));

        destination.delete();


        try {
            Files.copy(source.toPath(), destination.toPath());
        } catch (IOException e) {
            System.out.println("\nCannot refresh Hashfiles. Kindly Relaunch the programme or check if the hashfiles exists at source");

        }

    }


    protected int checkHashChange(String dbName) {
        String dbHash;
        JSONParser jsonParser1 = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser1.parse(new FileReader( hashFilePath +"/hashFile.json"));
            try {
                dbHash = (String) jsonObject.get(dbName);
            }
            catch (NullPointerException np){
                throw new RuntimeException();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        refreshHashFile();
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(hashFilePath +"/hashFile.json"));
            try {
                String val = (String) jsonObject.get(dbName);
                if(Objects.equals(val, dbHash)){
                    return 0;
                }
                else{
                    return 1;
                }
            }
            catch (NullPointerException np){
                return -1;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    protected boolean replaceKey(String dbname, String dbHash) {
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(hashFilePath + "/hashfile.json"));
            jsonObject.remove(dbname);
            jsonObject.put(dbname, dbHash);

            File currFile = new File(hashFilePath + "/hashfile.json");

            currFile.delete();

            try (FileWriter file = new FileWriter(hashFilePath + "/hashfile.json") ){
                file.write(jsonObject.toJSONString());
                file.flush();
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

    protected int generateHash(String dbfullpath){
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(dbfullpath));
            int hash = jsonObject.toString().hashCode();
            return hash;

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean replaceFileInMainDb(String dbName){
        File destination = new File(mainDbPath + dbName+".json");
        File source = new File(hashFilePath + "/" +dbName+".json");

        destination.delete();


        try {
            Files.copy(source.toPath(), destination.toPath());
            return true;
        } catch (IOException e) {
            System.out.println("\nCannot refresh files in main DB. Kindly Relaunch the programme or check if the files exists at source");

        }

        return false;
    }
}

