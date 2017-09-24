package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportDAO {

    private static String retrieveByGender(String timeBegin, String timeEnd, String gender) {
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
        return ans;
    }

    private static String retrieveByEmail(String timeBegin, String timeEnd, String school) {
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
        return ans;
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

    private static String retrieveThreeBreakdown(String timeBegin, String timeEnd, String year, String gender, String school) {
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
        return ans;
    }

    public static String notVeryBasicBreakdown(String[] text, String startTimeDate, String endTimeDate) {
        // initialize array
        String[] first, second, third;
        String[] year = "Year 2010 2011 2012 2013 2014".split(" ");                                    //5
        String[] gender = "Gender M F".split(" ");                                                     //2
        String[] school = "School economics sis socsc accoutancy business law".split(" ");             //6

        String used = "";

        switch (text[0]) {
            case "year":
                first = year;
                used += "year ";
                break;

            case "gender":
                first = gender;
                used += "gender ";
                break;

            case "school":
                first = school;
                used += "school ";
                break;

            default:
                first = null;
        }

        switch (text[1]) {
            case "year":
                second = year;
                used += "year ";
                break;

            case "gender":
                second = gender;
                used += "gender ";
                break;

            case "school":
                second = school;
                used += "school ";
                break;

            default:
                second = null;
        }

        switch (text[2]) {
            case "year":
                third = year;
                used += "year ";
                break;

            case "gender":
                third = gender;
                used += "gender ";
                break;

            case "school":
                third = school;
                used += "school ";
                break;

            default:
                third = null;
        }

        int total = used.split(" ").length;

        int firstL = (first.length - 1);
        int secondL = 1;
        int thirdL = 1;
        if (second != null) {
            secondL = (second.length - 1);
        }
        if (third != null) {
            thirdL = (third.length - 1);
        }

        //Number of rows to print
        int numberOfTimes = firstL;
        if (second != null) {
            numberOfTimes *= secondL;
        }
        if (third != null) {
            numberOfTimes *= thirdL;
        }

        // start string to return
        String returnThis = "<table border = 1>";
        for (int i = 1; i <= numberOfTimes; i++) {
            String currentLine = "<tr>";

            //First var to split by
            //if 1 trigger = 0
            //if 2/3 trigger = 1
            if (i % (secondL * thirdL) == total / 2) {
                currentLine += "<td rowspan =\"" + (secondL * thirdL) + "\">"
                        //Text for first col
                        + first[i / (secondL * thirdL) + total / 2]
                        + "</td>";
            }

            //if 2 trigger = 0
            //if 3 trigger = 1
            int trigger = total - 2;
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

            // run all the time
            System.out.print("Running " + i + "times\n\n\n");
            switch (total) {
                case 1:
                    switch (used) {
                        case "gender ":
                            currentLine += "<td>" + retrieveByGender(startTimeDate, endTimeDate, gender[i - 1]) + "</td>";
                            break;
                        case "school ":
                            currentLine += "<td>" + retrieveByEmail(startTimeDate, endTimeDate, school[i - 1]) + "</td>";
                            break;
                        default:
                            currentLine += "<td>" + retrieveByEmail(startTimeDate, endTimeDate, year[i - 1]) + "</td>";
                            break;
                    }
                    break;
                case 2:
                    String[] current = used.split(" ");
                    //Checking which variable is not selected
                    currentLine += "<td>";
                    int totalSum = 0;
                    if (current[0].equals("year") && current[1].equals("gender")) {
                        for (int j = 1; j < school.length; j++) {
                            totalSum += Integer.parseInt(retrieveThreeBreakdown(startTimeDate, endTimeDate, year[(int) Math.ceil((i - 0.01) / 2)], gender[(i - 1) % 2 + 1], school[j]));
                        }
                    } else if (current[0].equals("year") && current[1].equals("school")) {
                        for (int j = 1; j < gender.length; j++) {
                            totalSum += Integer.parseInt(retrieveThreeBreakdown(startTimeDate, endTimeDate, year[(int) (Math.ceil((i - 0.01) / 6))], gender[j], school[(i - 1) % 6 + 1]));
                        }
                    } else if (current[0].equals("school") && current[1].equals("gender")) {
                        for (int j = 1; j < year.length; j++) {
                            totalSum += Integer.parseInt(retrieveThreeBreakdown(startTimeDate, endTimeDate, year[j], gender[(i - 1) % 2 + 1], school[(int) Math.ceil((i - 0.01) / 2)]));
                        }
                    } else if (current[0].equals("school") && current[1].equals("year")) {
                        for (int j = 1; j < gender.length; j++) {
                            totalSum += Integer.parseInt(retrieveThreeBreakdown(startTimeDate, endTimeDate, year[(i - 1) % 5 + 1], gender[j], school[(int) Math.ceil((i - 0.01) / 5)]));
                        }
                    } else if (current[0].equals("gender") && current[1].equals("school")) {
                        for (int j = 1; j < year.length; j++) {
                            totalSum += Integer.parseInt(retrieveThreeBreakdown(startTimeDate, endTimeDate, year[j], gender[(int) Math.ceil((i - 0.01) / 6)], school[(i - 1) % 6 + 1]));
                        }
                    } else if (current[0].equals("gender") && current[1].equals("year")) {
                        for (int j = 1; j < school.length; j++) {
                            totalSum += Integer.parseInt(retrieveThreeBreakdown(startTimeDate, endTimeDate, year[(i - 1) % 5 + 1], gender[(int) Math.ceil((i - 0.01) / 6)], school[j]));
                        }
                    } else { // if (text[0].equals("gender") && text[1].equals("school"))
                        totalSum += -1;
                    }
                    currentLine += "" + totalSum;
                    break;
                default:
                    currentLine += "<td>";
                    if (text[0].equals("year") && text[1].equals("gender") && text[2].equals("school")) {
                        currentLine += retrieveThreeBreakdown(startTimeDate, endTimeDate, year[((int) Math.ceil((i - 0.01) / 12))], gender[((int) Math.ceil((i - 1) / 6)) % 2 + 1], school[(i - 1) % 6 + 1]);
                        /*currentLine += "<br>";
                        currentLine += " year: " + year[((int) Math.ceil((i - 0.01) / 12))];
                        currentLine += " gender: " + gender[(i - 1) % 2 + 1];
                        currentLine += " School: " + school[((int) Math.ceil((i - 1) / 2)) % 6 + 1];*/
                    } else if (text[0].equals("year") && text[2].equals("gender") && text[1].equals("school")) {
                        currentLine += retrieveThreeBreakdown(startTimeDate, endTimeDate, year[((int) Math.ceil((i - 0.01) / 12))], gender[(i - 1) % 2 + 1], school[((int) Math.ceil((i - 1) / 2)) % 6 + 1]);
                    } else if (text[1].equals("year") && text[0].equals("gender") && text[2].equals("school")) {
                        currentLine += retrieveThreeBreakdown(startTimeDate, endTimeDate, year[((int) Math.ceil((i - 1) / 6)) % 5 + 1], gender[((int) Math.ceil((i - 0.01) / 30))], school[(i - 1) % 6 + 1]);
                    } else if (text[1].equals("year") && text[2].equals("gender") && text[0].equals("school")) {
                        currentLine += retrieveThreeBreakdown(startTimeDate, endTimeDate, year[((int) Math.ceil((i - 1) / 2)) % 5 + 1], gender[(i - 1) % 2 + 1], school[((int) Math.ceil((i - 0.01) / 10))]);
                    } else if (text[2].equals("year") && text[1].equals("gender") && text[0].equals("school")) {
                        currentLine += retrieveThreeBreakdown(startTimeDate, endTimeDate, year[(i - 1) % 5 + 1], gender[((int) Math.ceil((i - 1) / 5)) % 2 + 1], school[((int) Math.ceil((i - 0.01) / 10))]);
                    } else//if (text[2].equals("year") && text[1].equals("gender") && text[0].equals("school"))
                    {
                        currentLine += retrieveThreeBreakdown(startTimeDate, endTimeDate, year[(i - 1) % 5 + 1], gender[((int) Math.ceil((i - 0.01) / 30))], school[((int) Math.ceil((i - 1) / 5)) % 6 + 1]);
                    }
                    currentLine += "</td>";
                    break;
            }

            //Ending
            currentLine += "</tr>";
            returnThis += currentLine;
        }
        returnThis += "</table>";
        return returnThis;
    }
}
