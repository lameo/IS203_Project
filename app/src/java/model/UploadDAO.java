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
            preparedStatement = connection.prepareStatement("DROP DATABASE IF EXISTS data; create schema \"data\"; use \"data\"; create table demographics ( macaddress varchar(40) not null, name varchar(50) not null, password varchar(50) not null, email varchar(50) not null, gender char(1) not null, constraint demographics_pk primary key(macaddress) ); create table locationlookup ( locationid varchar(12) not null, locationname varchar(25) not null, constraint locationlookup_pk primary key(locationid) ); create table location ( timestamp datetime(6) not null,  macaddress varchar(40) not null,  locationid varchar(10) not null,  constraint location_pk primary key(macaddress, timestamp) ); create table stalkerMode ( macaddress varchar(40) not null,  locationid varchar(10) not null,  locationname varchar(25) not null, maxTimestamp datetime(6) not null, minTimestamp datetime(6) not null, minutesSpend int(4) not null, constraint location_pk primary key(macaddress, maxTimestamp) ); SHOW TABLES LIKE \"%demographic%\";");

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

    public static boolean importDemographics(String macAddress, String name, String password, String email, String gender) {

        boolean macValid = macAddress.length() == 40;
        boolean passwordValid = (password.length() > 8) && password.contains(" ");
        boolean genderValid = gender.equals("m") || gender.equals("f");

        String[] years = "2013 2014 2015 2016 2017".split(" ");
        String[] schools = "business accountancy sis economics law socsc".split(" ");
        boolean emailValid = false;
        for (String year : years) {
            for (String school : schools) {
                String tempo = year + "@" + school;
                if (email.contains(tempo)) {
                    emailValid = true;
                }
            }
        }

        if (macValid && passwordValid && genderValid && emailValid) {
            return false;
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("\"INSERT INTO demographic (?, ?, ?, ?, ?)");
            //set the parameters
            preparedStatement.setString(1, macAddress);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, gender);

            //execute SQL query
            resultSet = preparedStatement.executeQuery();

            //close connections
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String unzip(String zipFile, String outputDirectory) {
        String test = "";
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
                if (checkFileName(fileName)) { //unzip only the correct csv files
                    File newFile = new File(outputDirectory + File.separator + fileName);

                    //create all non exists folders else you will hit FileNotFoundException for compressed folder
                    new File(newFile.getParent()).mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile);

                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                    test += readCSV(outputDirectory + File.separator + fileName);                      
                }
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return test;        
    }

    public static boolean checkFileName(String fileName) {
        if (fileName.contains("demographics.csv")) {
            return true;
        } else if (fileName.contains("location.csv")) {
            return true;
        } else if (fileName.contains("location-lookup.csv")) {
            return true;
        }
        return false;
    }

    public static String readCSV(String filePath) {
        String test = "";
        try {
            CSVReader reader = new CSVReader(new FileReader(filePath));
            String[] columns;
            while ((columns = reader.readNext()) != null) {
                test = columns[0];
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return test;
    }
}
