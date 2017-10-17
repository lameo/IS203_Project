package model;

import au.com.bytecode.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UploadDAO {

    // Not working v
    public static void clearDatabase() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement(
                    "DROP DATABASE IF EXISTS 'data'; create database 'data'; use 'data'; create table demographics ( macaddress varchar(40) not null, name varchar(50) not null, password varchar(50) not null, email varchar(50) not null, gender char(1) not null, constraint demographics_pk primary key(macaddress) ); create table locationlookup ( locationid varchar(12) not null, locationname varchar(25) not null, constraint locationlookup_pk primary key(locationid) ); create table location ( timestamp varchar(20) not null,  macaddress varchar(40) not null,  locationid varchar(40) not null,  constraint location_pk primary key(macaddress, timestamp) );"
            );
            //nth to prepare
            //execute SQL query
            preparedStatement.executeQuery();

            //close connections
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void clearDemographics() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("truncate demographics;");

            //execute SQL query
            preparedStatement.executeUpdate();

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
            preparedStatement = connection.prepareStatement("truncate location;");

            //execute SQL query
            preparedStatement.executeUpdate();

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
            preparedStatement = connection.prepareStatement("truncate locationlookup;");

            //execute SQL query
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String[] retrieveLocationID() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select locationid from locationlookup;");

            //execute SQL query
            preparedStatement.executeQuery();

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ans += " " + resultSet.getString(1);
            }
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ans.split(" ");
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
            preparedStatement = connection.prepareStatement("select macaddress, email from demographics;");

            //execute SQL query
            preparedStatement.executeQuery();

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ans.add(resultSet.getString(1) + resultSet.getString(2));
            }
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ans;
    }
    
    public static boolean verifyPassword(String user, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select password from demographics where name like ?;");

            //execute SQL query
            preparedStatement.setString(1, user);
            preparedStatement.executeQuery();

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ans = resultSet.getString(1);
            }
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ans.equals(password);
    }

    private static ArrayList<String> retrieveMACDatePair() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<String> ans = new ArrayList<>();
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select TIMESTAMP, macaddress from location;");

            //execute SQL query
            preparedStatement.executeQuery();

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String tempo = resultSet.getString(1);
                tempo += resultSet.getString(2);
                ans.add(tempo);
            }
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ans;
    }

    private static ArrayList<String> retrieveLocationIDName() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<String> ans = new ArrayList<>();
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select * from locationlookup;");

            //execute SQL query
            preparedStatement.executeQuery();

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String tempo = resultSet.getString(1);
                tempo += resultSet.getString(2);
                ans.add(tempo);
            }
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
        final int batchSize = 30000;
        HashMap<Integer, String> errorMap = new HashMap<>();
        int lineNumber = 1;
        int count = 0;
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("insert into demographics (macaddress,name,password,email,gender) values(?,?,?,?,?)");

            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));

            CSVReader reader = new CSVReader(bufferReader);
            String[] columns;
            reader.readNext();
            while ((columns = reader.readNext()) != null) {
                lineNumber++;
                String errors = "";
                String macaddress = "";
                String name = "";
                String password = "";
                String email = "";
                String gender = "";

                //Mac Address
                if (!columns[0].isEmpty()) {
                    //if (macaddress != null || macaddress.length()==0 || !macaddress.isEmpty()) {
                    macaddress = columns[0].trim();
                    if (macaddress.length() != 40) {
                        errors += ", Invalid mac address";
                    }
                } else {
                    errors += ", Missing MAC address";
                }

                // Name
                if (!columns[1].isEmpty()) {
                    name = columns[1].trim();
                } else {
                    errors += ", Missing name";
                }

                // Password
                if (!columns[2].isEmpty()) {
                    password = columns[2].trim();
                    if (password.length() < 8 || password.contains(" ")) {
                        errors += ", Invalid password";
                    }
                } else {
                    errors += ", Missing password";
                }

                // Email
                if (!columns[3].isEmpty()) {
                    email = columns[3].trim();
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
                        if (!UserDAO.validateUsername(email.substring(0, email.indexOf("@"))) && !valid || email.contains("..")) {
                            errors += ", Invalid email";
                        }
                    } else {
                        errors += ", Invalid email";
                    }
                } else {
                    errors += ", Missing email";
                }

                // Gender
                if (!columns[4].isEmpty()) {
                    gender = columns[4];
                    if (!(gender.contains("M") || gender.contains("F"))) {
                        errors += ", Invalid gender";
                    }
                } else {
                    errors += ", Missing gender";
                }

                //set the parameters
                if (!errors.equals("")) {
                    errorMap.put(lineNumber, errors.substring(2));
                } else {
                    preparedStatement.setString(1, macaddress);
                    preparedStatement.setString(2, name);
                    preparedStatement.setString(3, password);
                    preparedStatement.setString(4, email);
                    preparedStatement.setString(5, gender);
                    preparedStatement.addBatch();
                }

                if (++count % batchSize == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearParameters();
                }

            }
            preparedStatement.executeBatch(); //insert remaining records
            reader.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return errorMap;
    }

    public static HashMap<Integer, String> updateLocationLookUp(String filePath) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        final int batchSize = 30000;
        HashMap<Integer, String> errorMap = new HashMap<>();
        int count = 0;
        try {
            ArrayList<String> previousLocationIDName = retrieveLocationIDName();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            CSVReader reader = new CSVReader(bufferReader);
            ArrayList<String[]> columns = new ArrayList<>();
            reader.readNext();
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("insert into locationlookup (locationid, locationname) values(?,?)");
            String[] arr;
            while ((arr = reader.readNext()) != null) {
                String locationID = arr[0];
                String semanticPlace = arr[1];
                columns.add(new String[]{locationID, semanticPlace});
            }

            for (int i = columns.size() - 1; i >= 0; i--) {
                String errorMsg = "";
                String[] tempo = columns.get(i);
                String locationID = tempo[0];
                String semanticPlace = tempo[1];

                //Duplicate
                if (previousLocationIDName.contains(locationID + semanticPlace)) {
                    errorMsg += ", Duplicate Row";
                } else {
                    previousLocationIDName.add(locationID + semanticPlace);
                }

                // Checking for LocationID
                if (locationID.equals("")) {
                    errorMsg += ", Missing locationID";
                } else {

                    try {
                        int xlocationID = Integer.parseInt(locationID);
                        if (locationID.length() != 10) {
                            errorMsg += ", Invalid locationID";
                        } else if (xlocationID < 0) {
                            errorMsg += ", Invalid locationID";
                        }
                    } catch (NumberFormatException e) {
                        errorMsg += ", Invalid locationID";
                    }
                }

                // Checking for semantic name
                if (semanticPlace.equals("")) {
                    errorMsg += ", Missing semantic place";
                } else {
                    boolean valid = false;
                    for (String level : "SMUSISB1 SMUSISL1 SMUSISL2 SMUSISL3 SMUSISL4 SMUSISL5".split(" ")) {
                        if (semanticPlace.contains(level)) {
                            valid = true;
                        }
                    }
                    if (!valid) {
                        errorMsg += ", invalid semantic place";
                    }
                }

                //set the parameters
                if (!errorMsg.equals("")) {
                    errorMap.put(2 + i, errorMsg.substring(2));
                } else {
                    preparedStatement.setString(1, locationID);
                    preparedStatement.setString(2, semanticPlace);
                    preparedStatement.addBatch();
                }

                if (++count % batchSize == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearParameters();
                }

            }
            preparedStatement.executeBatch(); //insert remaining records
            reader.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return errorMap;
    }

    public static HashMap<Integer, String> updateDemographics(String filePath) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        final int batchSize = 30000;
        HashMap<Integer, String> errorMap = new HashMap<>();
        int count = 0;
        try {
            ArrayList<String> previousMacEmail = retrieveMacEmail();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            CSVReader reader = new CSVReader(bufferReader);
            ArrayList<String[]> columns = new ArrayList<>();
            reader.readNext();
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("insert into demographics (macaddress,name,password,email,gender) values(?,?,?,?,?)");
            String[] arr;
            while ((arr = reader.readNext()) != null) {
                String macaddress = arr[0];
                String name = arr[1];
                String password = arr[2];
                String email = arr[3];
                String gender = arr[4];
                columns.add(new String[]{macaddress, name, password, email, gender});
            }

            for (int i = columns.size() - 1; i >= 0; i--) {

                String errorMsg = "";
                String[] tempo = columns.get(i);
                String macAddress = tempo[0];
                String name = tempo[1];
                String password = tempo[2];
                String email = tempo[3];
                String gender = tempo[4];
                System.out.println(macAddress + email);

                //Duplicate
                if (previousMacEmail.contains(macAddress + email)) {
                    errorMsg += ", Duplicate Row";
                } else {
                    previousMacEmail.add(macAddress + email);
                }

                //Mac Address
                if (!macAddress.equals("")) {
                    //if (macaddress != null || macaddress.length()==0 || !macaddress.isEmpty()) {
                    macAddress = macAddress.trim();
                    if (macAddress.length() != 40) {
                        errorMsg += ", Invalid mac address";
                    }
                } else {
                    errorMsg += ", Missing MAC address";
                }

                // Name
                if (!name.equals("")) {
                    name = name.trim();
                } else {
                    errorMsg += ", Missing name";
                }

                // Password
                if (password.equals("")) {
                    errorMsg += ", Missing password";
                } else {
                    password = password.trim();
                    if (password.length() < 8 || password.contains(" ")) {
                        errorMsg += ", Invalid password";
                    }
                }

                // Email
                if (email.equals("")) {
                    errorMsg += ", Missing email";
                } else {
                    email = email.trim();
                    String[] schools = "business socsc law sis accountancy economics".split(" ");
                    String[] years = "2013 2014 2015 2016 2017".split(" ");
                    boolean valid = false;
                    for (String school : schools) {
                        for (String year : years) {
                            String tempoo = year + "@" + school;
                            if (email.contains(tempoo)) {
                                valid = true;
                            }
                        }
                    }
                    if (email.contains("@")) {
                        if (!UserDAO.validateUsername(email.substring(0, email.indexOf("@"))) && !valid || email.contains("..")) {
                            errorMsg += ", Invalid email";
                        }
                    } else {
                        errorMsg += ", Invalid email";
                    }
                }

                // Gender
                if (gender.equals("")) {
                    errorMsg += ", Missing gender";
                } else {
                    gender = gender.trim();
                    if (!(gender.contains("M") || gender.contains("F"))) {
                        errorMsg += ", Invalid gender";
                    }
                }

                //set the parameters
                if (!errorMsg.equals("")) {
                    errorMap.put(i + 2, errorMsg.substring(2));
                } else {
                    preparedStatement.setString(1, macAddress);
                    preparedStatement.setString(2, name);
                    preparedStatement.setString(3, password);
                    preparedStatement.setString(4, email);
                    preparedStatement.setString(5, gender);
                    preparedStatement.addBatch();
                }

                if (++count % batchSize == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearParameters();
                }

            }
            preparedStatement.executeBatch(); //insert remaining records
            reader.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return errorMap;
    }

    public static HashMap<Integer, String> updateLocation(String filePath) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        final int batchSize = 30000;
        HashMap<Integer, String> errorMap = new HashMap<>();
        int count = 0;
        String[] locationIDs = retrieveLocationID();
        try {
            ArrayList<String> previousMacEmail = retrieveMACDatePair();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            CSVReader reader = new CSVReader(bufferReader);
            ArrayList<String[]> columns = new ArrayList<>();
            reader.readNext();
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("insert into location (timestamp, macaddress, locationid) values(?,?,?);");
            String[] arr;
            while ((arr = reader.readNext()) != null) {
                String timeDate = arr[0];
                String macAddress = arr[1];
                String locationID = arr[2];
                columns.add(new String[]{timeDate, macAddress, locationID});
            }

            for (int i = columns.size() - 1; i >= 0; i--) {
                String errorMsg = "";
                String[] tempo = columns.get(i);
                String timeDate = tempo[0];
                String macAddress = tempo[1];
                String locationID = tempo[2];
                System.out.println((timeDate + macAddress));

                
                //Duplicate
                if (previousMacEmail.contains(timeDate + macAddress)) {
                    errorMsg += ", Duplicate Row";
                } else {
                    previousMacEmail.add(timeDate + macAddress);
                }

                
                // Checking for timeDate
                if (timeDate.equals("")) {
                    errorMsg += ", Missing date & time";
                } else {
                    boolean valid = true;
                    // Length check
                    valid = valid && timeDate.length() == 19;
                    // Year bigger than 2013 & smaller or equal to 2017
                    valid = valid && (Integer.parseInt(timeDate.substring(0, 4)) > 2013) && (Integer.parseInt(timeDate.substring(0, 4)) <= 2017);
                    // Month bigger than 0 & smaller or equal to 12
                    valid = valid && (Integer.parseInt(timeDate.substring(5, 7)) > 0) && (Integer.parseInt(timeDate.substring(5, 7)) <= 12);
                    // Day bigger than 0 & smaller or equal to 12
                    valid = valid && (Integer.parseInt(timeDate.substring(8, 10)) > 0) && (Integer.parseInt(timeDate.substring(8, 10)) <= 31);
                    // Hour bigger or equal 0 & smaller or equal to 24
                    valid = valid && (Integer.parseInt(timeDate.substring(8, 10)) >= 0) && (Integer.parseInt(timeDate.substring(11, 13)) <= 23);
                    // Min bigger or equal 0 & smaller or equal to 59
                    valid = valid && (Integer.parseInt(timeDate.substring(8, 10)) >= 0) && (Integer.parseInt(timeDate.substring(14, 16)) <= 59);
                    // Second bigger or equal 0 & smaller or equal to 59
                    valid = valid && (Integer.parseInt(timeDate.substring(8, 10)) >= 0) && (Integer.parseInt(timeDate.substring(14, 16)) <= 59);
                    if (!valid) {
                        errorMsg += ", Invalid date & time";
                    }
                }

                
                //Mac Address
                if (macAddress.equals("")) {
                    errorMsg += ", Missing MAC address";
                } else {
                    //if (macaddress != null || macaddress.length()==0 || !macaddress.isEmpty()) {
                    macAddress = macAddress.trim();
                    if (macAddress.length() != 40) {
                        errorMsg += ", Invalid mac address";
                    }
                }

                
                // Checking for locationID
                if (locationID.equals("")) {
                    errorMsg += ", Missing locationID";
                } else {
                    locationID = locationID.trim();
                    boolean valid = false;
                    for (String location : locationIDs) {
                        if (locationID.contains(location)) {
                            valid = true;
                        }
                    }
                    if (!valid) {
                        errorMsg += ", invalid locationID";
                    }
                }

                //set the parameters
                if (!errorMsg.equals("")) {
                    errorMap.put(i + 2, errorMsg.substring(2));
                } else {
                    preparedStatement.setString(1, timeDate);
                    preparedStatement.setString(2, macAddress);
                    preparedStatement.setString(3, locationID);
                    preparedStatement.addBatch();
                }

                if (++count % batchSize == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearParameters();
                }

            }
            preparedStatement.executeBatch(); //insert remaining records
            reader.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return errorMap;
    }

    public static HashMap<Integer, String> readLookup(String filePath) {
        clearLookup();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        final int batchSize = 30000;
        HashMap<Integer, String> errorMap = new HashMap<>();
        int count = 0;
        int lineNumber = 1;
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("insert into locationlookup (locationid, locationname) values(?,?)");

            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));

            CSVReader reader = new CSVReader(bufferReader);
            String[] columns;
            reader.readNext();
            while ((columns = reader.readNext()) != null) {
                lineNumber++;
                String errors = "";
                int locationID = 0;
                String locationName = "";

                // Checking for LocationID
                if (!columns[0].isEmpty()) {
                    try {
                        locationID = Integer.parseInt(columns[0]);
                        if (locationID < 0) {
                            errors += ", Invalid locationID";
                        }
                    } catch (NumberFormatException e) {
                        errors += ", Invalid locationID";
                    }
                } else {
                    errors += ", Missing locationID";
                }

                // Checking for semantic name
                if (!columns[1].isEmpty()) {
                    locationName = columns[1];
                    boolean valid = false;
                    for (String level : "SMUSISB1 SMUSISL1 SMUSISL2 SMUSISL3 SMUSISL4 SMUSISL5".split(" ")) {
                        if (locationName.contains(level)) {
                            valid = true;
                        }
                    }
                    if (!valid) {
                        errors += ", invalid semantic place";
                    }
                } else {
                    errors += ", Missing semantic place";
                }

                //set the parameters
                if (!errors.equals("")) {
                    errorMap.put(lineNumber, errors.substring(2));
                } else {
                    preparedStatement.setInt(1, locationID);
                    preparedStatement.setString(2, locationName);
                    preparedStatement.addBatch();
                }

                if (++count % batchSize == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearParameters();
                }
            }
            preparedStatement.executeBatch(); //insert remaining records            
            reader.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return errorMap;
    }

    public static HashMap<Integer, String> readLocation(String filePath) {
        clearLocation();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        final int batchSize = 30000;
        HashMap<Integer, String> errorMap = new HashMap<>();
        int count = 0;
        int lineNumber = 1;
        String[] locationIDs = retrieveLocationID();
        ArrayList<String> macDatePair = retrieveMACDatePair();
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("insert into location (timestamp, macaddress, locationid) values(?,?,?);");

            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));

            CSVReader reader = new CSVReader(bufferReader);
            String[] columns;
            reader.readNext();
            while ((columns = reader.readNext()) != null) {
                lineNumber++;
                String errors = "";
                String timeDate = "";
                String macAddress = "";
                String locationID = "";

                // Checking for timeDate
                if (!columns[0].isEmpty()) {
                    timeDate = columns[0];
                    boolean valid = true;
                    // Length check
                    valid = valid && timeDate.length() == 19;
                    // Year bigger than 2013 & smaller or equal to 2017
                    valid = valid && (Integer.parseInt(timeDate.substring(0, 4)) > 2013) && (Integer.parseInt(timeDate.substring(0, 4)) <= 2017);
                    // Month bigger than 0 & smaller or equal to 12
                    valid = valid && (Integer.parseInt(timeDate.substring(5, 7)) > 0) && (Integer.parseInt(timeDate.substring(5, 7)) <= 12);
                    // Day bigger than 0 & smaller or equal to 12
                    valid = valid && (Integer.parseInt(timeDate.substring(8, 10)) > 0) && (Integer.parseInt(timeDate.substring(8, 10)) <= 31);
                    // Hour bigger or equal 0 & smaller or equal to 24
                    valid = valid && (Integer.parseInt(timeDate.substring(8, 10)) >= 0) && (Integer.parseInt(timeDate.substring(11, 13)) <= 23);
                    // Min bigger or equal 0 & smaller or equal to 59
                    valid = valid && (Integer.parseInt(timeDate.substring(8, 10)) >= 0) && (Integer.parseInt(timeDate.substring(14, 16)) <= 59);
                    // Second bigger or equal 0 & smaller or equal to 59
                    valid = valid && (Integer.parseInt(timeDate.substring(8, 10)) >= 0) && (Integer.parseInt(timeDate.substring(14, 16)) <= 59);
                    if (!valid) {
                        errors += ", Invalid date & time";
                    }
                } else {
                    errors += ", Missing date & time";
                }

                // Checking for macAddress
                if (!columns[1].isEmpty()) {
                    macAddress = columns[1];
                    if (macAddress.length() != 40) {
                        errors += ", Invalid mac address";
                    }
                } else {
                    errors += ", Missing MAC address";
                }

                // Checking for locationID
                if (!columns[2].isEmpty()) {
                    locationID = columns[2];
                    locationID = locationID.trim();
                    boolean valid = false;
                    for (String location : locationIDs) {
                        if (locationID.contains(location)) {
                            valid = true;
                        }
                    }
                    if (!valid) {
                        errors += ", invalid locationID";
                    }
                } else {
                    errors += ", Missing locationID";
                }

                //Checking for duplicates
                if (macDatePair.contains(timeDate + macAddress)) {
                    errors += ", Duplicate Row";
                } else {
                    macDatePair.add(timeDate + macAddress);
                }

                //set the parameters
                if (!errors.equals("")) {
                    errorMap.put(lineNumber, errors.substring(2));
                } else {
                    preparedStatement.setString(1, timeDate);
                    preparedStatement.setString(2, macAddress);
                    preparedStatement.setString(3, locationID);
                    preparedStatement.addBatch();
                }

                if (++count % batchSize == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearParameters();
                }
            }
            preparedStatement.executeBatch(); //insert remaining records            
            reader.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
