package model;

import au.com.bytecode.opencsv.CSVReader;
import java.util.zip.ZipInputStream;
import java.sql.PreparedStatement;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.util.zip.ZipEntry;
import java.sql.SQLException;
import java.util.ArrayList;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.io.File;

public class UploadDAO {

    private static void clearDemographics() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("truncate demographics");

            //execute SQL query
            preparedStatement.executeUpdate();

            //close connections
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void clearLocation() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("truncate location");

            //execute SQL query
            preparedStatement.executeUpdate();

            //close connections
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void clearLookup() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("truncate locationlookup");

            //execute SQL query
            preparedStatement.executeUpdate();

            //close connections
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String retrieveLocationID() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        //ArrayList<String> ans = new ArrayList<>();
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select locationid from locationlookup");

            //execute SQL query
            preparedStatement.executeQuery();

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ans += " " + resultSet.getString(1);
                //System.out.println(resultSet.getString(1));

                //ans.add(resultSet.getString(1));
            }

            //close connections
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(ans.contains("1010100009"));
        System.out.println(ans);
        return ans;
    }

    private static ArrayList<String> retrieveMacEmail() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<String> ans = new ArrayList<>();
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select macaddress, email from demographics");

            //execute SQL query
            preparedStatement.executeQuery();

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ans.add(resultSet.getString(1) + resultSet.getString(2));
            }

            //close connections
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ans;
    }

    public static ArrayList<String> retrieveMACDatePair() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<String> ans = new ArrayList<>();
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select distinct timestamp, macaddress from location");

            //execute SQL query
            preparedStatement.executeQuery();

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ans.add((resultSet.getString(1) + resultSet.getString(2)));
            }

            //close connections
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ans;
    }

    public static HashMap<Integer, String> readDemographics(String filePath) {
        clearDemographics();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        final int batchSize = 2000;
        HashMap<Integer, String> errorMap = new HashMap<>();
        int lineNumber = 1;
        int count = 0;
        int successful = 0;
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();
            connection.setAutoCommit(false);

            //prepare a statement
            preparedStatement = connection.prepareStatement("insert into demographics (macaddress,name,password,email,gender) values(?,?,?,?,?)");

            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));

            CSVReader reader = new CSVReader(bufferReader);
            String[] columns;
            reader.readNext();
            while ((columns = reader.readNext()) != null) {
                lineNumber++;
                String errorMsg = "";
                String macaddress = columns[0];
                String name = columns[1];
                String password = columns[2];
                String email = columns[3];
                String gender = columns[4];

                if (macaddress.isEmpty()) {
                    errorMsg += ", Missing mac address";
                    errorMap.put(lineNumber, errorMsg.substring(2));
                    continue;
                }

                if (name.isEmpty()) {
                    errorMsg += ", Missing name";
                    errorMap.put(lineNumber, errorMsg.substring(2));
                    continue;
                }

                if (password.isEmpty()) {
                    errorMsg += ", Missing password";
                    errorMap.put(lineNumber, errorMsg.substring(2));
                    continue;
                }

                if (email.isEmpty()) {
                    errorMsg += ", Missing email";
                    errorMap.put(lineNumber, errorMsg.substring(2));
                    continue;
                }

                if (gender.isEmpty()) {
                    errorMsg += ", Missing gender";
                    errorMap.put(lineNumber, errorMsg.substring(2));
                    continue;
                }

                //Mac address
                macaddress = macaddress.trim();
                if (macaddress.length() != 40) {
                    errorMsg += ", Invalid mac address";
                }

                // Name
                name = name.trim();

                // Password
                password = password.trim();
                if (password.length() < 8 || password.contains(" ")) {
                    errorMsg += ", Invalid password";
                }
                
                // Email
                email = email.trim();
                String[] schools = "business socsc law sis accountancy economics".split(" ");
                String[] years = "2013 2014 2015 2016 2017".split(" ");
                boolean valid = false;
                for (String school : schools) {
                    for (String year : years) {
                        String tempo = year + "@" + school;
                        if (email.contains(tempo)) {
                            valid = true;
                        }
                    }
                }
                if (email.contains("@")) {
                    if (!UserDAO.validateUsername(email.substring(0, email.lastIndexOf("@"))) || !valid || email.contains("..")) {
                        errorMsg += ", Invalid email";
                    }
                } else {
                    errorMsg += ", Invalid email";
                }

                // Gender
                gender = gender.trim();
                if (!(gender.equalsIgnoreCase("M") || gender.equalsIgnoreCase("F"))) {
                    errorMsg += ", Invalid gender";
                }

                //set the parameters
                if (!errorMsg.equals("")) {
                    errorMap.put(lineNumber, errorMsg.substring(2));
                } else {
                    successful++;
                    preparedStatement.setString(1, macaddress);
                    preparedStatement.setString(2, name);
                    preparedStatement.setString(3, password);
                    preparedStatement.setString(4, email);
                    preparedStatement.setString(5, gender);
                    preparedStatement.addBatch();
                }

                if (++count % batchSize == 0) {
                    preparedStatement.executeBatch();
                    connection.commit();
                    preparedStatement.clearParameters();
                }
            }
            preparedStatement.executeBatch(); //insert remaining records
            connection.commit();

            //close connections
            reader.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        errorMap.put(Integer.MAX_VALUE, "" + successful);
        return errorMap;
    }

    public static HashMap<Integer, String> readLookup(String filePath) {
        clearLookup();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        final int batchSize = 300;
        HashMap<Integer, String> errorMap = new HashMap<>();
        int lineNumber = 1;
        int count = 0;
        int successful = 0;
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();
            connection.setAutoCommit(false);

            //prepare a statement
            preparedStatement = connection.prepareStatement("insert into locationlookup (locationid, locationname) values(?,?)");

            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));

            CSVReader reader = new CSVReader(bufferReader);
            reader.readNext();
            String[] columns;
            while ((columns = reader.readNext()) != null) {
                System.out.println(columns[0] );
                lineNumber++;
                System.out.println(lineNumber);
                String errorMsg = "";
                int locationID = 0;

                // Checking for LocationID
                if (columns[0].isEmpty()) {
                    errorMsg += ", Missing locationID";
                    errorMap.put(lineNumber, errorMsg.substring(2));
                    continue;
                }
                try {
                    locationID = Integer.parseInt(columns[0].trim());
                    if (locationID < 0) {
                        errorMsg += ", Invalid locationID";
                    }
                } catch (NumberFormatException e) {
                    errorMsg += ", Invalid locationID";
                }

                // Checking for semantic name
                if (columns[1].isEmpty()) {
                    errorMsg += ", Missing semantic place";
                    errorMap.put(lineNumber, errorMsg.substring(2));
                    continue;
                }
                String locationName = columns[1];
                locationName = locationName.trim();
                boolean valid = false;
                for (String level : "SMUSISB1 SMUSISL1 SMUSISL2 SMUSISL3 SMUSISL4 SMUSISL5".split(" ")) {
                    if (locationName.contains(level)) {
                        valid = true;
                    }
                }
                if (!valid) {
                    errorMsg += ", Invalid semantic place";
                }

                //set the parameters
                if (!errorMsg.equals("")) {
                    errorMap.put(lineNumber, errorMsg.substring(2));
                } else {
                    successful++;
                    preparedStatement.setInt(1, locationID);
                    preparedStatement.setString(2, locationName);
                    preparedStatement.addBatch();
                }

                if (++count % batchSize == 0) {
                    preparedStatement.executeBatch();
                    connection.commit();
                    preparedStatement.clearParameters();
                }

            }
            preparedStatement.executeBatch(); //insert remaining records
            connection.commit();

            //close connections
            reader.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        errorMap.put(Integer.MAX_VALUE, "" + successful);
        return errorMap;
    }

    public static HashMap<Integer, String> readLocation(String filePath) {
        clearLocation();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        final int batchSize = 10000;
        int count = 0;
        int successful = 0;
        String locationIDs = retrieveLocationID();
        //ArrayList<String> locationIDs = retrieveLocationID();
        HashMap<String, String> checking = new HashMap<>();
        HashMap<Integer, String> errorMap = new HashMap<>();
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();
            connection.setAutoCommit(false);

            //prepare a statement
            preparedStatement = connection.prepareStatement("insert into location (timestamp, macaddress, locationid) values(?,?,?)");

            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            CSVReader reader = new CSVReader(bufferReader);

            List<String[]> rows = reader.readAll();

            for (int i = rows.size() - 1; i > 0; i--) {
                String errorMsg = "";
                String[] row = rows.get(i);
                String timeDate = row[0];
                String macaddress = row[1];
                String locationID = row[2];

                if (timeDate.isEmpty()) {
                    errorMsg += ", Missing date and time";
                    errorMap.put(i + 1, errorMsg.substring(2));
                    continue;
                }

                if (macaddress.isEmpty()) {
                    errorMsg += ", Missing mac address";
                    errorMap.put(i + 1, errorMsg.substring(2));
                    continue;
                }

                if (locationID.isEmpty()) {
                    errorMsg += ", Missing locationID";
                    errorMap.put(i + 1, errorMsg.substring(2));
                    continue;
                }

                // Checking for timeDate
                timeDate = timeDate.trim();
                boolean validDate = true;
                // Length check
                validDate = validDate && timeDate.length() == 19;
                // Year bigger than 2013 & smaller or equal to 2017
                validDate = validDate && (Integer.parseInt(timeDate.substring(0, 4)) > 2013) && (Integer.parseInt(timeDate.substring(0, 4)) <= 2017);
                // Month bigger than 0 & smaller or equal to 12
                validDate = validDate && (Integer.parseInt(timeDate.substring(5, 7)) > 0) && (Integer.parseInt(timeDate.substring(5, 7)) <= 12);
                // Day bigger than 0 & smaller or equal to 12
                validDate = validDate && (Integer.parseInt(timeDate.substring(8, 10)) > 0) && (Integer.parseInt(timeDate.substring(8, 10)) <= 31);
                // Hour bigger or equal 0 & smaller or equal to 24
                validDate = validDate && (Integer.parseInt(timeDate.substring(11, 13)) >= 0) && (Integer.parseInt(timeDate.substring(11, 13)) <= 23);
                // Min bigger or equal 0 & smaller or equal to 59
                validDate = validDate && (Integer.parseInt(timeDate.substring(14, 16)) >= 0) && (Integer.parseInt(timeDate.substring(14, 16)) <= 59);
                // Second bigger or equal 0 & smaller or equal to 59
                validDate = validDate && (Integer.parseInt(timeDate.substring(17, 19)) >= 0) && (Integer.parseInt(timeDate.substring(17, 19)) <= 59);
                if (!validDate) {
                    errorMsg += ", Invalid date & time";
                }

                // Checking for macaddress
                macaddress = macaddress.trim();
                if (macaddress.length() != 40) {
                    errorMsg += ", Invalid mac address";
                }

                // Checking for locationID
                locationID = locationID.trim();
                if (!locationIDs.contains(locationID)) {
                    errorMsg += ", Invalid locationID";
                }

                //Checking for duplicates
                if (checking.containsKey(timeDate + macaddress)) {
                    errorMsg += ", Duplicate Row";
                }

                //set the parameters
                if (!errorMsg.equals("")) {
                    errorMap.put(i + 1, errorMsg.substring(2));
                } else {
                    successful++;
                    preparedStatement.setString(1, timeDate);
                    preparedStatement.setString(2, macaddress);
                    preparedStatement.setString(3, locationID);
                    checking.put(timeDate + macaddress, "1");
                    preparedStatement.addBatch();
                }

                if (++count % batchSize == 0) {
                    preparedStatement.executeBatch();
                    connection.commit();
                    preparedStatement.clearParameters();
                }
            }
            preparedStatement.executeBatch(); //insert remaining records
            connection.commit();

            //close connections
            reader.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        errorMap.put(Integer.MAX_VALUE, "" + successful);
        return errorMap;
    }

    public static HashMap<Integer, String> updateDemographics(String filePath) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        final int batchSize = 2000;
        int count = 0;
        int successful = 0;

        ArrayList<String> previousMacEmail = retrieveMacEmail();
        HashMap<Integer, String> errorMap = new HashMap<>();

        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();
            connection.setAutoCommit(false);

            //prepare a statement
            preparedStatement = connection.prepareStatement("insert into demographics (macaddress,name,password,email,gender) values(?,?,?,?,?)");

            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            CSVReader reader = new CSVReader(bufferReader);

            List<String[]> rows = reader.readAll();

            for (int i = rows.size() - 1; i > 0; i--) {

                String errorMsg = "";
                String[] row = rows.get(i);
                String macaddress = row[0];
                String name = row[1];
                String password = row[2];
                String email = row[3];
                String gender = row[4];

                if (macaddress.isEmpty()) {
                    errorMsg += ", Missing mac address";
                    errorMap.put(i + 1, errorMsg.substring(2));
                    continue;
                }

                if (name.isEmpty()) {
                    errorMsg += ", Missing name";
                    errorMap.put(i + 1, errorMsg.substring(2));
                    continue;
                }

                if (password.isEmpty()) {
                    errorMsg += ", Missing password";
                    errorMap.put(i + 1, errorMsg.substring(2));
                    continue;
                }

                if (email.isEmpty()) {
                    errorMsg += ", Missing email";
                    errorMap.put(i + 1, errorMsg.substring(2));
                    continue;
                }

                if (gender.isEmpty()) {
                    errorMsg += ", Missing gender";
                    errorMap.put(i + 1, errorMsg.substring(2));
                    continue;
                }

                //Mac address
                macaddress = macaddress.trim();
                if (macaddress.length() != 40) {
                    errorMsg += ", Invalid mac address";
                }

                // Name
                name = name.trim();

                // Password
                password = password.trim();
                if (password.length() < 8 || password.contains(" ")) {
                    errorMsg += ", Invalid password";
                }

                // Email
                email = email.trim();
                String[] schools = "business socsc law sis accountancy economics".split(" ");
                String[] years = "2013 2014 2015 2016 2017".split(" ");
                boolean valid = false;
                for (String school : schools) {
                    for (String year : years) {
                        String tempo = year + "@" + school;
                        if (email.contains(tempo)) {
                            valid = true;
                        }
                    }
                }
                if (email.contains("@")) {
                    if (!UserDAO.validateUsername(email.substring(0, email.indexOf("@"))) || !valid || email.contains("..")) {
                        errorMsg += ", Invalid email";
                    }
                } else {
                    errorMsg += ", Invalid email";
                }

                // Gender
                gender = gender.trim();
                if (!(gender.equalsIgnoreCase("M") || gender.equalsIgnoreCase("F"))) {
                    errorMsg += ", Invalid gender";
                }

                //Duplicate
                if (previousMacEmail.contains(macaddress + email)) {
                    errorMsg += ", Duplicate Row";
                }

                //set the parameters
                if (!errorMsg.equals("")) {
                    errorMap.put(i + 1, errorMsg.substring(2));
                } else {
                    successful++;
                    preparedStatement.setString(1, macaddress);
                    preparedStatement.setString(2, name);
                    preparedStatement.setString(3, password);
                    preparedStatement.setString(4, email);
                    preparedStatement.setString(5, gender);
                    previousMacEmail.add(macaddress + email);
                    preparedStatement.addBatch();
                }

                if (++count % batchSize == 0) {
                    preparedStatement.executeBatch();
                    connection.commit();
                    preparedStatement.clearParameters();
                }

            }
            preparedStatement.executeBatch(); //insert remaining records
            connection.commit();

            //close connections
            reader.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        errorMap.put(Integer.MAX_VALUE, "" + successful);
        return errorMap;
    }

    public static HashMap<Integer, String> updateLocation(String filePath) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        final int batchSize = 10000;
        int count = 0;
        int successful = 0;
        String locationIDs = retrieveLocationID();
        //ArrayList<String> locationIDs = retrieveLocationID();

        ArrayList<String> previousMacEmail = retrieveMACDatePair();
        HashMap<Integer, String> errorMap = new HashMap<>();

        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();
            connection.setAutoCommit(false);

            //prepare a statement
            preparedStatement = connection.prepareStatement("insert into location (timestamp, macaddress, locationid) values(?,?,?)");

            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            CSVReader reader = new CSVReader(bufferReader);

            List<String[]> rows = reader.readAll();

            for (int i = rows.size() - 1; i > 0; i--) {
                String errorMsg = "";
                String[] row = rows.get(i);
                String timeDate = row[0];
                String macaddress = row[1];
                String locationID = row[2];

                if (timeDate.isEmpty()) {
                    errorMsg += ", Missing date and time";
                    errorMap.put(i + 1, errorMsg.substring(2));
                    continue;
                }

                if (macaddress.isEmpty()) {
                    errorMsg += ", Missing mac address";
                    errorMap.put(i + 1, errorMsg.substring(2));
                    continue;
                }

                if (locationID.isEmpty()) {
                    errorMsg += ", Missing locationID";
                    errorMap.put(i + 1, errorMsg.substring(2));
                    continue;
                }

                // Checking for timeDate
                timeDate = timeDate.trim();
                boolean validDate = true;
                // Length check
                validDate = validDate && timeDate.length() == 19;
                // Year bigger than 2013 & smaller or equal to 2017
                validDate = validDate && (Integer.parseInt(timeDate.substring(0, 4)) > 2013) && (Integer.parseInt(timeDate.substring(0, 4)) <= 2017);
                // Month bigger than 0 & smaller or equal to 12
                validDate = validDate && (Integer.parseInt(timeDate.substring(5, 7)) > 0) && (Integer.parseInt(timeDate.substring(5, 7)) <= 12);
                // Day bigger than 0 & smaller or equal to 12
                validDate = validDate && (Integer.parseInt(timeDate.substring(8, 10)) > 0) && (Integer.parseInt(timeDate.substring(8, 10)) <= 31);
                // Hour bigger or equal 0 & smaller or equal to 24
                validDate = validDate && (Integer.parseInt(timeDate.substring(11, 13)) >= 0) && (Integer.parseInt(timeDate.substring(11, 13)) <= 23);
                // Min bigger or equal 0 & smaller or equal to 59
                validDate = validDate && (Integer.parseInt(timeDate.substring(14, 16)) >= 0) && (Integer.parseInt(timeDate.substring(14, 16)) <= 59);
                // Second bigger or equal 0 & smaller or equal to 59
                validDate = validDate && (Integer.parseInt(timeDate.substring(17, 19)) >= 0) && (Integer.parseInt(timeDate.substring(17, 19)) <= 59);
                if (!validDate) {
                    errorMsg += ", Invalid date & time";
                }

                // Checking for macaddress
                macaddress = macaddress.trim();
                if (macaddress.length() != 40) {
                    errorMsg += ", Invalid mac address";
                }

                // Checking for locationID
                locationID = locationID.trim();
                if (!locationIDs.contains(locationID)) {
                    errorMsg += ", Invalid locationID";
                }

                //Duplicate
                String macDatePair = timeDate + ".0" + macaddress; //sql code retrieval added a .0 behind
                if (previousMacEmail.contains(macDatePair)) {
                    errorMsg += ", Duplicate Row";
                }

                //set the parameters
                if (!errorMsg.equals("")) {
                    errorMap.put(i + 1, errorMsg.substring(2));
                } else {
                    successful++;
                    preparedStatement.setString(1, timeDate);
                    preparedStatement.setString(2, macaddress);
                    preparedStatement.setString(3, locationID);
                    previousMacEmail.add(macDatePair);
                    preparedStatement.addBatch();
                }

                if (++count % batchSize == 0) {
                    preparedStatement.executeBatch();
                    connection.commit();
                    preparedStatement.clearParameters();
                }
            }
            preparedStatement.executeBatch(); //insert remaining records
            connection.commit();

            //close connections
            reader.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        errorMap.put(Integer.MAX_VALUE, "" + successful);
        return errorMap;
    }

    public static String unzip(String zipFile, String outputDirectory) {
        String fileExist = "";
        byte[] buffer = new byte[1024];
        try {
            File folder = new File(outputDirectory);
            if (!folder.exists()) { //create output directory is not exists
                folder.mkdir();
            }

            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile)); //get the zip file content
            ZipEntry ze = zis.getNextEntry(); //get the zipped file list entry

            while (ze != null) {
                String fileName = ze.getName();
                if (checkFileName(fileName) != null && checkFileName(fileName).length() > 0) { //unzip only the correct csv files
                    fileExist += " " + fileName;
                    File newFile = new File(outputDirectory + File.separator + fileName);

                    //create all non exists folders else you will hit FileNotFoundException for compressed folder
                    new File(newFile.getParent()).mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile);

                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return fileExist;
    }

    public static String checkFileName(String fileName) {
        if (fileName.contains("demographics.csv")) {
            return "demographics.csv";
        } else if (fileName.contains("location.csv")) {
            return "location.csv";
        } else if (fileName.contains("location-lookup.csv")) {
            return "location-lookup.csv";
        }
        return "";
    }
}
