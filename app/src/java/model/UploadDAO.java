package model;

import au.com.bytecode.opencsv.CSVReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UploadDAO {

    public static boolean clearDatabase() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement(
                    "DROP DATABASE IF EXISTS data;\n"
                    + "create schema `data`;"
                    + "use `data`;"
                    + "create table demographics ( macaddress varchar(40) not null, name varchar(50) not null, password varchar(50) not null, email varchar(50) not null, gender char(1) not null, constraint demographics_pk primary key(macaddress) );"
                    + "create table locationlookup ( locationid varchar(12) not null, locationname varchar(25) not null, constraint locationlookup_pk primary key(locationid) );"
                    + "create table location ( timestamp varchar(20) not null,  macaddress varchar(40) not null,  locationid varchar(10) not null,  constraint location_pk primary key(macaddress, timestamp) );"
                    + "create table stalkerMode ( macaddress varchar(40) not null,  locationid varchar(10) not null,  locationname varchar(25) not null, maxTimestamp datetime(6) not null, minTimestamp datetime(6) not null, minutesSpend int(4) not null, constraint location_pk primary key(macaddress, maxTimestamp) );"
                    + "SHOW TABLES LIKE '%demographic%';");
            //set the parameters
            //nth to prepare
            //execute SQL query
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ans = resultSet.getString(1);
            }

            //close connections
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ans.equals("demographic");
    }

    public static void demographicsImport1(String fileLocation) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<String[]> emptyDataSet = new ArrayList<>();
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement(
                    "LOAD DATA LOCAL INFILE 'javax.servlet.context.tempdir.demographics.csv' INTO TABLE demographics FIELDS TERMINATED BY ',' LINES TERMINATED BY '\\r' IGNORE 1 LINES (macaddress, name, password, email, gender);"
            );
            //set the parameters
            preparedStatement.setString(1, fileLocation);

            //execute SQL query
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void demographicsImport2(String fileLocation) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<String[]> emptyDataSet = new ArrayList<>();
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement(
                    "LOAD DATA LOCAL INFILE 'javax.servlet.context/tempdir/demographics.csv' INTO TABLE demographics FIELDS TERMINATED BY ',' LINES TERMINATED BY '\\r' IGNORE 1 LINES (macaddress, name, password, email, gender);"
            );
            //set the parameters
            preparedStatement.setString(1, fileLocation);

            //execute SQL query
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void demographicsImport3(String fileLocation) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<String[]> emptyDataSet = new ArrayList<>();
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
             preparedStatement = connection.prepareStatement(
                    "LOAD DATA LOCAL INFILE 'javax/servlet/context/tempdir/demographics.csv' INTO TABLE demographics FIELDS TERMINATED BY ',' LINES TERMINATED BY '\\r' IGNORE 1 LINES (macaddress, name, password, email, gender);"
            );
            //set the parameters
            preparedStatement.setString(1, fileLocation);

            //execute SQL query
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void demographicsImport4(String fileLocation) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<String[]> emptyDataSet = new ArrayList<>();
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
             preparedStatement = connection.prepareStatement(
                    "LOAD DATA LOCAL INFILE 'javax\\servlet\\context\\tempdir\\demographics.csv' INTO TABLE demographics FIELDS TERMINATED BY ',' LINES TERMINATED BY '\\r' IGNORE 1 LINES (macaddress, name, password, email, gender);"
            );
            //set the parameters
            preparedStatement.setString(1, fileLocation);

            //execute SQL query
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void demographicsImport5(String fileLocation) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<String[]> emptyDataSet = new ArrayList<>();
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            //prepare a statement
             preparedStatement = connection.prepareStatement(
                    "LOAD DATA LOCAL INFILE 'javax.servlet.context\\tempdir\\demographics.csv' INTO TABLE demographics FIELDS TERMINATED BY ',' LINES TERMINATED BY '\\r' IGNORE 1 LINES (macaddress, name, password, email, gender);"
            );
            //set the parameters
            preparedStatement.setString(1, fileLocation);

            //execute SQL query
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String[]> demographicsNilChecking() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<String[]> emptyDataSet = new ArrayList<>();
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select  *  from  demographics where password is null or password like '' or name is null or name like '' or macaddress is null or macaddress like '' or gender is null or gender like '' or email is null or email like ''");
            //set the parameters
            //preparedStatement.setString(1, fileLocation);

            //execute SQL query
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                emptyDataSet.add(new String[]{resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)});
            }

            //close connections
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emptyDataSet;
    }
    
    public static void clearDemographics(){
        Connection connection = null;
        PreparedStatement preparedStatement = null;   
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("truncate demographics");

            //execute SQL query
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }         
    }
    
    public static void clearLocation(){
        Connection connection = null;
        PreparedStatement preparedStatement = null;   
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("truncate location");

            //execute SQL query
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }         
    }    
    
    public static void clearLookup(){
        Connection connection = null;
        PreparedStatement preparedStatement = null;   
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("truncate locationlookup");

            //execute SQL query
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }         
    }    

    public static void readDemographics(String filePath) {
        clearDemographics();     
        Connection connection = null;
        PreparedStatement preparedStatement = null;           
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("insert into demographics (macaddress,name,password,email,gender) values(?,?,?,?,?)");
            
            CSVReader reader = new CSVReader(new FileReader(filePath));
            String[] columns;
            while ((columns = reader.readNext()) != null) {
                //set the parameters
                preparedStatement.setString(1, columns[0]);
                preparedStatement.setString(2, columns[1]);
                preparedStatement.setString(3, columns[2]);
                preparedStatement.setString(4, columns[3]);
                preparedStatement.setString(5, columns[4]);                

                //execute SQL query
                preparedStatement.executeUpdate();                
            }
            reader.close();
            preparedStatement.close();
            connection.close();            
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void readLookup(String filePath) {
        clearLookup();     
        Connection connection = null;
        PreparedStatement preparedStatement = null;           
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("insert into locationlookup (locationid, locationname) values(?,?)");
            
            CSVReader reader = new CSVReader(new FileReader(filePath));
            String[] columns;
            while ((columns = reader.readNext()) != null) {
                //set the parameters
                preparedStatement.setString(1, columns[0]);
                preparedStatement.setString(2, columns[1]);               

                //execute SQL query
                preparedStatement.executeUpdate();                
            }
            reader.close();
            preparedStatement.close();
            connection.close();            
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readLocation(String filePath) {
        clearLocation();     
        Connection connection = null;
        PreparedStatement preparedStatement = null;           
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("insert into location (timestamp, macaddress, locationid) values(?,?,?)");
            
            CSVReader reader = new CSVReader(new FileReader(filePath));
            String[] columns;
            while ((columns = reader.readNext()) != null) {
                //set the parameters
                preparedStatement.setString(1, columns[0]);
                preparedStatement.setString(2, columns[1]);
                preparedStatement.setString(3, columns[2]);              

                //execute SQL query
                preparedStatement.executeUpdate();                
            }
            reader.close();
            preparedStatement.close();
            connection.close();            
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
