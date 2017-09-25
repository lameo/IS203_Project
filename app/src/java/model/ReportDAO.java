package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportDAO {

    private static int retrieveByGender(String timeBegin, String timeEnd, String gender) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select count(DISTINCT l.macaddress) from location l, demographics d where  timestamp between ? and ? and l.macaddress = d.macaddress and gender = ?");

            //set the parameters
            preparedStatement.setString(1, timeBegin);
            preparedStatement.setString(2, timeEnd);
            preparedStatement.setString(3, gender);

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
        return Integer.parseInt(ans);
    }

    private static int retrieveByEmail(String timeBegin, String timeEnd, String school) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select count(DISTINCT l.macaddress) from location l, demographics d where  timestamp between ? and ? and l.macaddress = d.macaddress and email like ?");

            //set the parameters
            preparedStatement.setString(1, timeBegin);
            preparedStatement.setString(2, timeEnd);
            preparedStatement.setString(3, "%" + school + "%");

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
        return Integer.parseInt(ans);
    }
    
    private static int everyoneWithinTime(String timeBegin, String timeEnd) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select count(DISTINCT l.macaddress) from location l, demographics d where  timestamp between ? and ? and l.macaddress = d.macaddress");

            //set the parameters
            preparedStatement.setString(1, timeBegin);
            preparedStatement.setString(2, timeEnd);

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
        return Integer.parseInt(ans);
    }

    public static String retrieveTopKPopularPlaces(String time, String topK) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();
            //prepare a statement
            preparedStatement = connection.prepareStatement("select n.locationname, count(n.locationname) from (SELECT max(TIMESTAMP) as TIMESTAMP, macaddress FROM location WHERE timestamp BETWEEN ? AND (SELECT DATE_ADD(?,INTERVAL 15 MINUTE)) group by macaddress) l, location m, locationlookup n where l.macaddress = m.macaddress and m.timestamp = l.timestamp and m.locationid = n.locationid group by n.locationname order by count(n.locationname) desc limit ? ");

            //set the parameters
            preparedStatement.setString(1, time);
            preparedStatement.setString(2, time);
            int x = Integer.parseInt(topK);
            preparedStatement.setInt(3, x);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                //fencing
                if (!ans.equals("")) {
                    ans += "," + resultSet.getString(1) + "," + resultSet.getString(2);
                } else {
                    ans = resultSet.getString(1) + "," + resultSet.getString(2);
                }
            }

            //close connections
            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ans;
    }

    private static int retrieveThreeBreakdown(String timeBegin, String timeEnd, String year, String gender, String school) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();
            //prepare a statement
            preparedStatement = connection.prepareStatement("select count(DISTINCT l.macaddress) from location l, demographics d where  timestamp between ? and ? and l.macaddress = d.macaddress and gender = ? and email like ? and email like ?");

            //set the parameters
            preparedStatement.setString(1, timeBegin);
            preparedStatement.setString(2, timeEnd);
            preparedStatement.setString(3, gender);
            preparedStatement.setString(4, "%" + year + "%");
            preparedStatement.setString(5, "%" + school + "%");

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
        return Integer.parseInt(ans);
    }

    public static String notVeryBasicBreakdown(String[] text, String startTimeDate, String endTimeDate) {
        // initialize array
        String[] first, second, third;
        String[] year = "Year 2010 2011 2012 2013 2014".split(" ");                                    //5
        String[] gender = "Gender M F".split(" ");                                                     //2
        String[] school = "School economics sis socsc accountancy business law".split(" ");            //6

        String used = "";

        switch (text[0]) { //get from basicReport.jsp, can be year/gender/school
            case "year":
                first = year; //add year array into first
                used += "year "; //user chose year
                break;

            case "gender":
                first = gender; //add gender array into first
                used += "gender "; //user chose gender
                break;

            case "school":
                first = school; //add school array into first
                used += "school "; //user chose school
                break;

            default:
                first = null;
        }

        switch (text[1]) { //get from basicReport.jsp, can be year/gender/school/optional
            case "year":
                second = year; //add year array into second
                used += "year "; //user chose year
                break;

            case "gender":
                second = gender; //add gender array into second
                used += "gender "; //user chose gender
                break;

            case "school":
                second = school; //add school array into second
                used += "school "; //user chose school
                break;

            default:
                second = null; //user chose optional
        }

        switch (text[2]) { //get from basicReport.jsp, can be year/gender/school/optional
            case "year":
                third = year; //add year array into third
                used += "year "; //user chose year
                break;

            case "gender":
                third = gender; //add gender array into third
                used += "gender "; //user chose gender
                break;

            case "school":
                third = school; //add school array into third
                used += "school "; //user chose school
                break;

            default:
                third = null; //user chose optional
        }

        String[] usedArray = used.split(" "); //change from string into string array
        int totalOptions = usedArray.length; //check how many options has the user selected, can be 1 2 or 3

        int firstL = (first.length - 1); //to make sure the array doesn't have ArrayOutOfBoundException
        int secondL = 1;
        int thirdL = 1;
        if (second != null) { //if user chose a second option
            secondL = (second.length - 1); //to make sure the array doesn't have ArrayOutOfBoundException
        }
        if (third != null) { //if user chose a third option
            thirdL = (third.length - 1); //to make sure the array doesn't have ArrayOutOfBoundException
        }

        //Number of rows to print
        int numberOfRows = firstL;
        if (second != null) {
            numberOfRows *= secondL;
        }
        if (third != null) {
            numberOfRows *= thirdL;
        }

        //string to return to ReportServlet.java
        String returnThis = "<div class=\"container\">      <table class=\"table table-bordered\">";
        //Print table header
        returnThis += ("<thead><tr><th colspan = " + (totalOptions+2) + ">Report for " + startTimeDate + " to " + endTimeDate + "</th></tr>");
        for(String header : usedArray){ //can be year/gender/school
            returnThis += "<th>" + header.substring(0, 1).toUpperCase() + header.substring(1) + "</th>";
        }
        returnThis += "<th>Qty</th><th>Percentage</th></thead><tbody>";

        //for the percentage calculation later to compare the number in each category with the total number of possible users
        int totalBetweenTime = everyoneWithinTime(startTimeDate, endTimeDate);
        
        for (int i = 1; i <= numberOfRows; i++) {
            String currentLine = "<tr>";

            //first var to split by
            //if 1 trigger = 0
            //if 2/3 trigger = 1
            if (i % (secondL * thirdL) == totalOptions / 2) {
                currentLine += "<td rowspan =\"" + (secondL * thirdL) + "\">"
                        //Text for first col
                        + first[i / (secondL * thirdL) + totalOptions / 2]
                        + "</td>";
            }

            //if 2 trigger = 0
            //if 3 trigger = 1
            int trigger = totalOptions - 2;
            //Second var to split by
            if (second != null && i % (thirdL) == trigger) {
                currentLine += "<td rowspan =\"" + (thirdL) + "\">"
                        //text for second col
                        + second[(int) (i / (0.001 + thirdL) % (secondL)) + 1]
                        + "</td>";
            }

            //Third var to split by
            if (third != null) {
                currentLine += "<td>"
                        //text for third col
                        + third[(int) Math.ceil(i % (0.001 + thirdL))]
                        + "</td>";
            }

            //run all the time
            int value = -1;
            switch (totalOptions) {
                case 1: //user only choose 1 option
                    switch (used) { //check which option did the user choose
                        case "gender ":
                            value = retrieveByGender(startTimeDate, endTimeDate, gender[i]);
                            break;
                        case "school ":
                            value = retrieveByEmail(startTimeDate, endTimeDate, school[i]);
                            break;
                        case "year ":
                            value = retrieveByEmail(startTimeDate, endTimeDate, year[i]);
                            break;
                        default:
                            value = -2;
                            break;
                    }
                    break;
                case 2:
                    String[] current = usedArray;
                    //Checking which variable is not selected
                    int totalSum = 0;
                    if (current[0].equals("year") && current[1].equals("gender")) {
                        for (int j = 1; j < school.length; j++) {
                            totalSum += retrieveThreeBreakdown(startTimeDate, endTimeDate, year[(int) Math.ceil((i - 0.01) / 2)], gender[(i - 1) % 2 + 1], school[j]);
                        }
                    } else if (current[0].equals("year") && current[1].equals("school")) {
                        for (int j = 1; j < gender.length; j++) {
                            totalSum += retrieveThreeBreakdown(startTimeDate, endTimeDate, year[(int) (Math.ceil((i - 0.01) / 6))], gender[j], school[(i - 1) % 6 + 1]);
                        }
                    } else if (current[0].equals("school") && current[1].equals("gender")) {
                        for (int j = 1; j < year.length; j++) {
                            totalSum += retrieveThreeBreakdown(startTimeDate, endTimeDate, year[j], gender[(i - 1) % 2 + 1], school[(int) Math.ceil((i - 0.01) / 2)]);
                        }
                    } else if (current[0].equals("school") && current[1].equals("year")) {
                        for (int j = 1; j < gender.length; j++) {
                            totalSum += retrieveThreeBreakdown(startTimeDate, endTimeDate, year[(i - 1) % 5 + 1], gender[j], school[(int) Math.ceil((i - 0.01) / 5)]);
                        }
                    } else if (current[0].equals("gender") && current[1].equals("school")) {
                        for (int j = 1; j < year.length; j++) {
                            totalSum += retrieveThreeBreakdown(startTimeDate, endTimeDate, year[j], gender[(int) Math.ceil((i - 0.01) / 6)], school[(i - 1) % 6 + 1]);
                        }
                    } else if (current[0].equals("gender") && current[1].equals("year")) {
                        for (int j = 1; j < school.length; j++) {
                            totalSum += retrieveThreeBreakdown(startTimeDate, endTimeDate, year[(i - 1) % 5 + 1], gender[(int) Math.ceil((i - 0.01) / 6)], school[j]);
                        }
                    }
                    value = totalSum;
                    break;
                default:
                    if (text[0].equals("year") && text[1].equals("gender") && text[2].equals("school")) {
                        value = retrieveThreeBreakdown(startTimeDate, endTimeDate, year[((int) Math.ceil((i - 0.01) / 12))], gender[((int) Math.ceil((i - 1) / 6)) % 2 + 1], school[(i - 1) % 6 + 1]);
                    } else if (text[0].equals("year") && text[2].equals("gender") && text[1].equals("school")) {
                        value = retrieveThreeBreakdown(startTimeDate, endTimeDate, year[((int) Math.ceil((i - 0.01) / 12))], gender[(i - 1) % 2 + 1], school[((int) Math.ceil((i - 1) / 2)) % 6 + 1]);
                    } else if (text[1].equals("year") && text[0].equals("gender") && text[2].equals("school")) {
                        value = retrieveThreeBreakdown(startTimeDate, endTimeDate, year[((int) Math.ceil((i - 1) / 6)) % 5 + 1], gender[((int) Math.ceil((i - 0.01) / 30))], school[(i - 1) % 6 + 1]);
                    } else if (text[1].equals("year") && text[2].equals("gender") && text[0].equals("school")) {
                        value = retrieveThreeBreakdown(startTimeDate, endTimeDate, year[((int) Math.ceil((i - 1) / 2)) % 5 + 1], gender[(i - 1) % 2 + 1], school[((int) Math.ceil((i - 0.01) / 10))]);
                    } else if (text[2].equals("year") && text[1].equals("gender") && text[0].equals("school")) {
                        value = retrieveThreeBreakdown(startTimeDate, endTimeDate, year[(i - 1) % 5 + 1], gender[((int) Math.ceil((i - 1) / 5)) % 2 + 1], school[((int) Math.ceil((i - 0.01) / 10))]);
                    } else if (text[2].equals("year") && text[1].equals("gender") && text[0].equals("school")) {
                        value = retrieveThreeBreakdown(startTimeDate, endTimeDate, year[(i - 1) % 5 + 1], gender[((int) Math.ceil((i - 0.01) / 30))], school[((int) Math.ceil((i - 1) / 5)) % 6 + 1]);
                    }
                    break;
            }
            currentLine += "<td>" + value + "</td>";
            
            //Generating percentage
            currentLine += "<td>" + Math.round(value*100/totalBetweenTime) + "%</td>";
            
            //Ending
            currentLine += "</tr>";
            returnThis += currentLine;
        }
        returnThis += "</tbody></table></div>";
        return returnThis;
    }
}
