package model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReportDAO {

    private static int retrieveByGender(String timeEnd, String gender) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select count(DISTINCT l.macaddress) "
                    + "from location l, demographics d "
                    + "where  timestamp between DATE_SUB(?,INTERVAL 15 MINUTE) and DATE_SUB(?,INTERVAL 1 SECOND) "
                    + "and l.macaddress = d.macaddress and gender = ?");

            //set the parameters
            preparedStatement.setString(1, timeEnd);
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

    private static int retrieveByEmail(String timeEnd, String school) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select count(DISTINCT l.macaddress) "
                    + "from location l, demographics d "
                    + "where timestamp between DATE_SUB(?,INTERVAL 15 MINUTE) and DATE_SUB(?,INTERVAL 1 SECOND) "
                    + "and l.macaddress = d.macaddress and email like ?");

            //set the parameters
            preparedStatement.setString(1, timeEnd);
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

    private static int everyoneWithinTime(String timeEnd) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select count(DISTINCT l.macaddress) "
                    + "from location l, demographics d "
                    + "where timestamp between DATE_SUB(?,INTERVAL 15 MINUTE) and DATE_SUB(?,INTERVAL 1 SECOND) "
                    + "and l.macaddress = d.macaddress");

            //set the parameters
            preparedStatement.setString(1, timeEnd);
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

    public static Map<Integer, String> retrieveTopKPopularPlaces(String time) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Map<Integer, String> map = new HashMap<>();
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();
            //prepare a statement
            preparedStatement = connection.prepareStatement("select n.locationname, count(n.locationname) "
                    + "from (SELECT max(TIMESTAMP) as TIMESTAMP, macaddress "
                    + "FROM location "
                    + "WHERE timestamp BETWEEN (SELECT DATE_SUB(?,INTERVAL 15 MINUTE)) AND (SELECT DATE_SUB(?,INTERVAL 1 SECOND)) "
                    + "group by macaddress) l, location m, locationlookup n "
                    + "where l.macaddress = m.macaddress and m.timestamp = l.timestamp and m.locationid = n.locationid "
                    + "group by n.locationname "
                    + "order by count(n.locationname) desc limit 30 ");

            //set the parameters
            preparedStatement.setString(1, time);
            preparedStatement.setString(2, time);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                if (map.containsKey(resultSet.getInt(2))) {
                    map.put(resultSet.getInt(2), map.get(resultSet.getInt(2)) + ", " + resultSet.getString(1));
                } else {
                    map.put(resultSet.getInt(2), resultSet.getString(1));
                }
            }

            //close connections
            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Map<Integer, ArrayList<String>> retrieveTopKNextPlaces(String inputTime, String locationName) {
        ArrayList<String> usersList = retrieveUserBasedOnLocation(inputTime, locationName); //to retrieve all users who are in a specific place in given a specific time frame and location
        Map<String, Integer> nextPlacesMap = new HashMap<>();
        for (int i = 0; i < usersList.size(); i++) { // loop through all the users retrieved from retrieveUserBasedOnLocation() mtd
            String location = retrieveTimelineForUser(usersList.get(i), inputTime); // retrieve the latest location user spends at least 5 min
            if (location != null && location.length() > 0) { // check if there is a location
                if (nextPlacesMap.get(location) == null) { //nextPlacesMap is empty
                    nextPlacesMap.put(location, 1); // to initialise nextPlacesMap to set a default value as 1
                } else { // nextPlacesMap is not empty
                    int currentQuantity = nextPlacesMap.get(location);
                    int addOnQuantity = currentQuantity + 1;
                    nextPlacesMap.put(location, addOnQuantity); // increment the counter if location appears for every different user from usersList
                }
            }
        }
        
        // TreeMap is sorted by keys
        Map<Integer, ArrayList<String>> ranking = new TreeMap<>(Collections.reverseOrder()); //sort keys in descending order    
        Set<String> locationKeys = nextPlacesMap.keySet(); // to retrieve all the keys(i.e location) from nextPlacesMap
        
        for(String location : locationKeys){
            int totalNumOfUsers = nextPlacesMap.get(location); //for each key(i.e location) in keys, retrieve the total number of users in the location
            ArrayList<String> allLocationList = ranking.get(totalNumOfUsers); //list is to group all the different locations with the same quantity 
            if(allLocationList==null || allLocationList.size()<0){ // when the ranking map is empty
                ArrayList<String> sameLocations = new ArrayList<>();                
                sameLocations.add(location);
                ranking.put(totalNumOfUsers, sameLocations);//to add all locations with the same quantity into map
            } else { // ranking map contains a list of locations
                allLocationList.add(location); // update the key(i.e location) into the list
                ranking.put(totalNumOfUsers, allLocationList); // update map
            }
        }
        
        return ranking;
    }

    //retrieve users who are in a specific place given a specific time frame in a specific location
    public static ArrayList<String> retrieveUserBasedOnLocation(String inputTime, String locationName) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<String> usersInSpecificPlace = new ArrayList<String>();

        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement to retrieve users who are in a specific place in a given time frame in a specific location
            preparedStatement = connection.prepareStatement("select distinct l.macaddress "
                    + "from location l, locationlookup llu "
                    + "where l.locationid = llu.locationid "
                    + "and timestamp between(SELECT DATE_SUB(? ,INTERVAL 15 MINUTE)) AND (SELECT DATE_SUB(? ,INTERVAL 1 SECOND)) "
                    + "and llu.locationname = ?");

            //set the parameters
            preparedStatement.setString(1, inputTime);
            preparedStatement.setString(2, inputTime);
            preparedStatement.setString(3, locationName);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String result = resultSet.getString(1); // retrieves the user 
                usersInSpecificPlace.add(result); // add into list to collate all users in the specific location
            }

            //close connections
            resultSet.close();
            preparedStatement.close();
            connection.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usersInSpecificPlace;
    }
    
    //retrieves the latest place user spends at least 5 mins 
    public static String retrieveTimelineForUser(String macaddress, String dateTime) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<String> locationTimestampList = new ArrayList<>();
        HashMap<String, Double> userCurrent = new HashMap<>();
        String currentPlace = ""; //latest place the user spends at least 5 mins

        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement to get location name and time given a specfic user and time 
            preparedStatement = connection.prepareStatement("select llu.locationname, l.timestamp "
                    + "from locationlookup llu, location l "
                    + "where macaddress = ? and timestamp BETWEEN (SELECT DATE_ADD(? ,INTERVAL 0 MINUTE)) AND (SELECT DATE_ADD(DATE_ADD(? ,INTERVAL 14 MINUTE), INTERVAL 59 SECOND)) "
                    + "and llu.locationid = l.locationid");

            //set the parameters
            preparedStatement.setString(1, macaddress);
            preparedStatement.setString(2, dateTime);
            preparedStatement.setString(3, dateTime);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String locationName = resultSet.getString(1); 
                String timestamp = resultSet.getString(2); 
                locationTimestampList.add(locationName);
                locationTimestampList.add(timestamp);
            }

            //close connections
            resultSet.close();
            preparedStatement.close();
            connection.close();
            
            //to get the total accumulated time for the given place
            double currentQuantity = 0.0;

            //arraylist locationTimestampList has locationname and timestamp in alternate order for 1 user only
            //Eg: location1, time1, location2, time2, location3, time3, .. etc
            for (int i = 0; i < locationTimestampList.size(); i += 2) { //for-loop to loop every location name added
                if (currentPlace == null || currentPlace.length() <= 0) {
                    currentPlace = locationTimestampList.get(i); // to initialise currentPlace to the one in arraylist at the start
                }
                if (i + 2 < locationTimestampList.size()) { //prevent arrayindexoutofbounds
                    String nextLocation = locationTimestampList.get(i + 2); //to retrieve the next immediate location after currentPlace
                    String date = locationTimestampList.get(i + 1); //to retrieve the corresponding date for currentPlace
                    String nextDate = locationTimestampList.get(i + 3); //to retrieve the date for nextLocation

                    DateFormat df = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss.SSSSSS");
                    
                    //to convert date and nextDate to Date objects
                    java.util.Date firstDateAdded = df.parse(date);
                    java.util.Date nextDateAdded = df.parse(nextDate);
                    
                    // to get the time the user stayed at currentPlace in seconds
                    /*
                    converting millis to seconds (by dividing by 1000)
                    use / 60 to get the minutes value
                    % 60 (remainder) to get the "seconds in minute
                    */
                    long diff = (nextDateAdded.getTime() - firstDateAdded.getTime());
                    double timeDiff = diff / 1000.0;
                    
                    if (currentPlace.equals(nextLocation)) { //if same location just add into currentQuantity
                        currentQuantity += timeDiff; // update the latest time                           
                    } else { //not the same location
                        currentQuantity += timeDiff; // update the latest time                          
                        if (currentQuantity >= 300) {
                            userCurrent.put(currentPlace, currentQuantity); //put into map currentPlace and the updated currentQuantity                        
                        }
                        currentPlace = nextLocation; //set the next place as current place
                        currentQuantity = 0; // reset currentQuantity   
                    }
                }
            }
            if (userCurrent.containsKey(currentPlace)) {
                return currentPlace; //to retrieve the sematic place for the user with the longest time (at least 5 mins)
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException ex) {
            Logger.getLogger(ReportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ""; //if the user does not have any sematic place with more than 5 mins, return empty string
    }

    private static int retrieveThreeBreakdown(String timeEnd, String year, String gender, String school) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();
            //prepare a statement
            preparedStatement = connection.prepareStatement("select count(DISTINCT l.macaddress) "
                    + "from location l, demographics d "
                    + "where timestamp between DATE_SUB(?,INTERVAL 15 MINUTE) and DATE_SUB(?,INTERVAL 1 SECOND) "
                    + "and l.macaddress = d.macaddress and gender = ? and email like ? and email like ?");

            //set the parameters
            preparedStatement.setString(1, timeEnd);
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

    public static String notVeryBasicBreakdown(String[] text, String endTimeDate) {
        // initialize array
        String[] first, second, third;  //category have name in their first value to know what does first, second or third variable contains
        String[] year = {"Year", "2013", "2014", "2015", "2016", "2017"};                              //5
        String[] gender = {"Gender", "M", "F"};                                                        //2
        String[] school = {"School", "economics", "sis", "socsc", "accountancy", "business", "law"};   //6

        String userInput = "";

        switch (text[0]) { //get from basicReport.jsp, can be year/gender/school
            case "year":
                first = year; //add year array into first
                userInput += "year "; //user chose year
                break;

            case "gender":
                first = gender; //add gender array into first
                userInput += "gender "; //user chose gender
                break;

            case "school":
                first = school; //add school array into first
                userInput += "school "; //user chose school
                break;

            default:
                first = null;
        }

        switch (text[1]) { //get from basicReport.jsp, can be year/gender/school/optional
            case "year":
                second = year; //add year array into second
                userInput += "year "; //user chose year
                break;

            case "gender":
                second = gender; //add gender array into second
                userInput += "gender "; //user chose gender
                break;

            case "school":
                second = school; //add school array into second
                userInput += "school "; //user chose school
                break;

            default:
                second = null; //user chose optional
        }

        switch (text[2]) { //get from basicReport.jsp, can be year/gender/school/optional
            case "year":
                third = year; //add year array into third
                userInput += "year "; //user chose year
                break;

            case "gender":
                third = gender; //add gender array into third
                userInput += "gender "; //user chose gender
                break;

            case "school":
                third = school; //add school array into third
                userInput += "school "; //user chose school
                break;

            default:
                third = null; //user chose optional
        }

        String[] userInputArray = userInput.split(" "); //change from string into string array
        int totalOptions = userInputArray.length; //check how many options has the user selected, can be 1 2 or 3

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

        //for the percentage calculation later to compare the number in each category with the total number of possible users
        int totalBetweenTime = everyoneWithinTime(endTimeDate);

        //Print table header
        returnThis += ("<thead><tr><th colspan = " + (totalOptions + 2) + ">Breakdown by " + userInput + " <br>Total: " + totalBetweenTime + "</th></tr>");
        for (String header : userInputArray) { //can be year/gender/school
            returnThis += "<th>" + header.substring(0, 1).toUpperCase() + header.substring(1) + "</th>";
        }
        returnThis += "<th>Qty</th><th>Percentage</th></thead><tbody>";

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
                    switch (userInput) { //check which option did the user choose
                        case "gender ":
                            value = retrieveByGender(endTimeDate, gender[i]);
                            break;
                        case "school ":
                            value = retrieveByEmail(endTimeDate, school[i]);
                            break;
                        case "year ":
                            value = retrieveByEmail(endTimeDate, year[i]);
                            break;
                        default:
                            value = -2;
                            break;
                    }
                    break;
                case 2: //user only choose 2 options
                    //Checking which variable is not selected
                    int totalSum = 0;
                    if (userInputArray[0].equals("year") && userInputArray[1].equals("gender")) {
                        for (int j = 1; j < school.length; j++) { //sum every school according to that year and gender, magic number is 2 (length of second input eg gender)
                            totalSum += retrieveThreeBreakdown(endTimeDate, year[(int) Math.ceil(i / 2.0)], gender[(i - 1) % 2 + 1], school[j]);
                        }
                    } else if (userInputArray[0].equals("year") && userInputArray[1].equals("school")) {
                        for (int j = 1; j < gender.length; j++) { //sum every gender according to that year and school, magic number is 6 (length of second input eg school)
                            totalSum += retrieveThreeBreakdown(endTimeDate, year[(int) Math.ceil((i / 6.0))], gender[j], school[(i - 1) % 6 + 1]);
                        }
                    } else if (userInputArray[0].equals("school") && userInputArray[1].equals("gender")) {
                        for (int j = 1; j < year.length; j++) { //sum every year according to that school and gender, magic number is 2 (length of second input eg gender)
                            totalSum += retrieveThreeBreakdown(endTimeDate, year[j], gender[(i - 1) % 2 + 1], school[(int) Math.ceil(i / 2.0)]);
                        }
                    } else if (userInputArray[0].equals("school") && userInputArray[1].equals("year")) {
                        for (int j = 1; j < gender.length; j++) { //sum every gender according to that school and year, magic number is 5 (length of second input eg year)
                            totalSum += retrieveThreeBreakdown(endTimeDate, year[(i - 1) % 5 + 1], gender[j], school[(int) Math.ceil(i / 5.0)]);
                        }
                    } else if (userInputArray[0].equals("gender") && userInputArray[1].equals("school")) {
                        for (int j = 1; j < year.length; j++) { //sum every year according to that gender and school, magic number is 6 (length of second input eg school)
                            totalSum += retrieveThreeBreakdown(endTimeDate, year[j], gender[(int) Math.ceil(i / 6.0)], school[(i - 1) % 6 + 1]);
                        }
                    } else if (userInputArray[0].equals("gender") && userInputArray[1].equals("year")) {
                        for (int j = 1; j < school.length; j++) { //sum every school according to that gender and year, magic number is 5 (length of second input eg year)
                            totalSum += retrieveThreeBreakdown(endTimeDate, year[(i - 1) % 5 + 1], gender[(int) Math.ceil(i / 5.0)], school[j]);
                        }
                    }
                    value = totalSum;
                    break;
                default: //user only choose 3 options
                    if (userInputArray[0].equals("year") && userInputArray[1].equals("gender") && userInputArray[2].equals("school")) { //same year 12 times, same gender 6 times, 6 different schools
                        value = retrieveThreeBreakdown(endTimeDate, year[(int) Math.ceil(i / 12.0)], gender[((int) Math.ceil((i - 1) / 6)) % 2 + 1], school[(i - 1) % 6 + 1]);
                    } else if (userInputArray[0].equals("year") && userInputArray[1].equals("school") && userInputArray[2].equals("gender")) { //same year 12 times, same school 2 times, 2 different gender
                        value = retrieveThreeBreakdown(endTimeDate, year[(int) Math.ceil(i / 12.0)], gender[(i - 1) % 2 + 1], school[((int) Math.ceil((i - 1) / 2)) % 6 + 1]);
                    } else if (userInputArray[0].equals("gender") && userInputArray[1].equals("year") && userInputArray[2].equals("school")) { //same gender 30 times, same year 6 times, 6 different school
                        value = retrieveThreeBreakdown(endTimeDate, year[((int) Math.ceil((i - 1) / 6)) % 5 + 1], gender[((int) Math.ceil(i / 30.0))], school[(i - 1) % 6 + 1]);
                    } else if (userInputArray[0].equals("gender") && userInputArray[1].equals("school") && userInputArray[2].equals("year")) { //same gender 30 times, same school 5 times, 5 different year
                        value = retrieveThreeBreakdown(endTimeDate, year[(i - 1) % 5 + 1], gender[(int) Math.ceil(i / 30.0)], school[((int) Math.ceil((i - 1) / 5)) % 6 + 1]);
                    } else if (userInputArray[0].equals("school") && userInputArray[1].equals("year") && userInputArray[2].equals("gender")) { //same school 10 times, same year 2 times, 2 different gender
                        value = retrieveThreeBreakdown(endTimeDate, year[((int) Math.ceil((i - 1) / 2)) % 5 + 1], gender[(i - 1) % 2 + 1], school[(int) Math.ceil(i / 10.0)]);
                    } else if (userInputArray[0].equals("school") && userInputArray[1].equals("gender") && userInputArray[2].equals("year")) { //same school 10 times, same gender 5 times, 5 different year
                        value = retrieveThreeBreakdown(endTimeDate, year[(i - 1) % 5 + 1], gender[((int) Math.ceil((i - 1) / 5)) % 2 + 1], school[(int) Math.ceil(i / 10.0)]);
                    }
                    break;
            }
            currentLine += "<td>" + value + "</td>";

            //Generating percentage
            currentLine += "<td>" + Math.round(value * 100.0 / totalBetweenTime) + "%</td>";

            //Ending
            currentLine += "</tr>";
            returnThis += currentLine;
        }
        returnThis += "</tbody></table></div>";
        return returnThis;
    }

    public static Map<ArrayList<String>, ArrayList<Integer>> retrieveTopKCompanions(String endTimeDate, String macaddress, int k) {
        ArrayList<String> UserLocationTimestamps = retrieveUserLocationTimestamps(macaddress, endTimeDate);
        System.out.println(UserLocationTimestamps);
        Map<ArrayList<String>, ArrayList<Integer>> result = new HashMap<ArrayList<String>, ArrayList<Integer>>();

        Map<String, Integer> CompanionColocations = new LinkedHashMap<String, Integer>();
        for (String UserLocationTimestamp : UserLocationTimestamps) {
            String[] LocationTimestamp = UserLocationTimestamp.split(",");
            String locationid = LocationTimestamp[0];
            String timestringStart = LocationTimestamp[1];
            String stringDuration = LocationTimestamp[2];
            ArrayList<String> CompanionLocationTimestamps = retrieveCompanionLocationTimestamps(macaddress, locationid, timestringStart, stringDuration);
            for (String CompanionLocationTimestamp : CompanionLocationTimestamps) {
                String[] LocationTimestampc = UserLocationTimestamp.split(",");
                String macaddressc = LocationTimestampc[0];
                int colocationTime = Integer.parseInt(LocationTimestamp[3]);
                if (!CompanionColocations.containsKey(macaddressc)) {
                    CompanionColocations.put(macaddressc, colocationTime);
                } else {
                    int colocationTime2 = CompanionColocations.get(macaddressc);
                    colocationTime2 += colocationTime;
                    CompanionColocations.put(macaddressc, colocationTime2);
                }
            }
            int rank = 0;
            int rankcolocationTime = 0;

            ArrayList<String> Companions = new ArrayList<String>();
            ArrayList<Integer> Companions2 = new ArrayList<Integer>();
            while (rank <= k) {
                for (Entry<String, Integer> entry : CompanionColocations.entrySet()) {
                    String macaddress2 = entry.getKey();
                    int colocationTime3 = entry.getValue();
                    if (!result.containsKey(macaddress2)) {
                        Companions.add(macaddress2);
                        if (colocationTime3 != rankcolocationTime) {
                            rankcolocationTime = colocationTime3;
                        } else {
                            rank += 1;
                            Companions2.add(rank, rankcolocationTime);
                            result.put(Companions, Companions2);
                        }
                    }

                }
            }

        }
        return result;
    }

    //retreive users in hashmap form, hashmap key is macaddress and hashmap value is array of email, locationid and timestamp
    public static ArrayList<String> retrieveUserLocationTimestamps(String macaddress, String endtimeDate) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        int duration = 0;
        ArrayList<String> UserLocationTimestamps = new ArrayList<String>();

        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select locationid, timestamp,DATE_SUB(?, INTERVAL 1 SECOND) "
                    + "from demographics d, location l"
                    + "where macaddress = ? and timestamp between DATE_SUB(?, INTERVAL 15 MINUTE)"
                    + "and DATE_SUB(?, INTERVAL 1 SECOND) order by timestamp");

            //set the parameters
            preparedStatement.setString(1, endtimeDate);
            preparedStatement.setString(2, macaddress);
            preparedStatement.setString(3, endtimeDate);
            preparedStatement.setString(3, endtimeDate);

            //execute SQL query
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                duration = 0;
                String locationid = resultSet.getString(1);
                Timestamp timestamp = resultSet.getTimestamp(2);
                Timestamp timestampEnd = resultSet.getTimestamp(3);
                if (resultSet.isLast()) {
                    duration = (int) (TimeUnit.MILLISECONDS.toMinutes(timestampEnd.getTime() - timestamp.getTime()));
                    if (duration > 5) {
                        duration = 5;
                    }
                    ans += "," + locationid + "," + timestamp + "," + duration;
                } else {
                    while (resultSet.next()) {
                        String locationidNext = resultSet.getString(1);
                        Timestamp timestampNext = resultSet.getTimestamp(2);
                        if (locationid != locationidNext) {
                            duration = (int) (TimeUnit.MILLISECONDS.toMinutes(timestampNext.getTime() - timestamp.getTime()));
                            if (duration > 5) {
                                duration += 5;
                            }
                            ans += "," + locationid + "," + timestamp + "," + duration;
                            //UserLocationTimestamps.add(ans);
                            break;
                            //if the next update location is same as the previous one
                        } else {
                            duration = (int) (TimeUnit.MILLISECONDS.toMinutes(timestampNext.getTime() - timestamp.getTime()));
                            if (duration > 5) {
                                duration = 5;
                                ans += "," + locationid + "," + timestamp + "," + duration;
                                //UserLocationTimestamps.add(ans);
                                break;
                            } else {
                                timestamp = timestampNext;
                                duration += (int) (TimeUnit.MILLISECONDS.toMinutes(timestampNext.getTime() - timestamp.getTime()));
                            }

                        }

                    }
                    UserLocationTimestamps.add(ans);
                }
                UserLocationTimestamps.add(ans);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //UserLocationTimestamps.add("1010110032"+","+"014-03-24 09:07:27.000000"+","+"1");
        return UserLocationTimestamps;
    }

    //retreive users in hashmap form, hashmap key is macaddress and hashmap value is array of email, locationid and timestamp
    public static ArrayList<String> retrieveCompanionLocationTimestamps(String userMacaddress, String locationid, String timestringStart, String stringDuration) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        int duration;
        int colocationTime = 0;
        Timestamp timestampStart = null;
        Timestamp timestampEnd = null;
        Date timedateEnd;
        String datestringBeforeStart = null;
        String timestringEnd = null;
        ArrayList<String> CompanionLocationTimestamps = new ArrayList<String>();

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date parsedStartDate = (Date) dateFormat.parse(timestringStart);
            timestampStart = new java.sql.Timestamp(parsedStartDate.getTime());
            duration = Integer.parseInt(stringDuration);
            //timeDateEnd = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(parsedStartDate);
            cal.add(Calendar.MINUTE, -5);
            datestringBeforeStart = dateFormat.format(cal.getTime());
            cal.setTime(parsedStartDate);
            cal.add(Calendar.MINUTE, duration);
            timestringEnd = dateFormat.format(cal.getTime());
            Date parsedEndDate = (Date) dateFormat.parse(timestringEnd);
            timestampEnd = new java.sql.Timestamp(parsedEndDate.getTime());
        } catch (Exception e) { //this generic but you can control another types of exception
            // look the origin of excption 
        }

        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select macaddress,"
                    + "TIMESTAMPDIFF(Minute,timestamp,?) as diff,timestamp, "
                    + "from location l"
                    + "where macaddress <> ? and timestamp between ? and ?"
                    + "and l.locationid=? order by l.macaddress, diff desc");

            //set the parameters
            preparedStatement.setString(1, timestringEnd);
            preparedStatement.setString(2, userMacaddress);
            preparedStatement.setString(3, datestringBeforeStart);
            preparedStatement.setString(4, timestringEnd);
            preparedStatement.setString(5, locationid);

            //execute SQL query
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String macaddress = resultSet.getString(1);
                int timeDiff = resultSet.getInt(2);
                Timestamp timestamp = resultSet.getTimestamp(3);
                if (resultSet.isLast()) {
                    colocationTime = timeDiff;
                    if (colocationTime > 5) {
                        colocationTime = 5;
                    }
                    ans += "," + macaddress + "," + locationid + "," + timestamp + "," + colocationTime;
                    CompanionLocationTimestamps.add(ans);
                } else {
                    while (resultSet.next()) {
                        String macaddressNext = resultSet.getString(1);
                        int timeDiffNext = resultSet.getInt(2);
                        Timestamp timestampNext = resultSet.getTimestamp(3);
                        if (macaddress.equals(macaddressNext)) {
                            colocationTime += timeDiff - timeDiffNext;
                            if (colocationTime > 5) {
                                colocationTime = 5;
                            }
                        } else {
                            colocationTime += timeDiffNext;
                            if (colocationTime > 5) {
                                colocationTime = 5;
                            }
                            break;
                        }
                        ans += "," + macaddress + "," + locationid + "," + timestamp + "," + colocationTime;
                        CompanionLocationTimestamps.add(ans);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return CompanionLocationTimestamps;
    }

}
