package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class initializeSLOCA {

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
        for(String year: years){
            for(String school: schools){
                String tempo = year + "@" + school;
                if(email.contains(tempo)){
                    emailValid = true;
                }
            }
        }
        
        if(macValid && passwordValid && genderValid && emailValid){
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
}

