package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReportDAO {

    public static ArrayList<String> getSemanticPlaces() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<String> list = new ArrayList<>();
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select distinct locationname from locationlookup order by locationname asc");

            //execute SQL query
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getString(1));
            }

            //close connections
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<String> getAllMacaddress() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<String> list = new ArrayList<>();
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select distinct macaddress from demographics");

            //execute SQL query
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getString(1));
            }

            //close connections
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

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
        Map<Integer, String> map = new TreeMap<>(Collections.reverseOrder());
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
                    + "group by n.locationname");

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

        for (String location : locationKeys) {
            int totalNumOfUsers = nextPlacesMap.get(location); //for each key(i.e location) in keys, retrieve the total number of users in the location
            ArrayList<String> allLocationList = ranking.get(totalNumOfUsers); //list is to group all the different locations with the same quantity
            if (allLocationList == null || allLocationList.size() < 0) { // when the ranking map is empty
                ArrayList<String> sameLocations = new ArrayList<>();
                sameLocations.add(location);
                ranking.put(totalNumOfUsers, sameLocations);//to add all locations with the same quantity into map
            } else { // ranking map contains a list of locations
                allLocationList.add(location); // update the key(i.e location) into the list
                Collections.sort(allLocationList);
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

    //retrieves the latest sematic place user spends at least 5 mins
    public static String retrieveTimelineForUser(String macaddress, String dateTime) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        ArrayList<String> locationTimestampList = new ArrayList<>();
        String currentPlace = ""; //latest place the user spends at least 5 mins
        String spentMoreThan5Minutes = "";

        DateFormat df = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

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
            for (int i = 0; i < locationTimestampList.size(); i += 2) { //loop every location name added
                if (currentPlace == null || currentPlace.length() <= 0) {
                    currentPlace = locationTimestampList.get(i); // to set currentPlace to first location from locationTimestampList at the start
                }
                //prevent arrayindexoutofbounds and to get all the locations before the last location in locationTimestampList
                if ((i + 2) < locationTimestampList.size()) {
                    String nextLocation = locationTimestampList.get(i + 2); //to retrieve the next immediate location after currentPlace
                    String date = locationTimestampList.get(i + 1); //to retrieve the corresponding date for currentPlace
                    String nextDate = locationTimestampList.get(i + 3); //to retrieve the date for nextLocation

                    //to convert date and nextDate to Date objects
                    java.util.Date firstDateAdded = df.parse(date);
                    java.util.Date nextDateAdded = df.parse(nextDate);

                    long diff = (nextDateAdded.getTime() - firstDateAdded.getTime()); // to get the time the user stayed at currentPlace in milliseconds
                    double timeDiff = diff / 1000.0; // to get the time the user stayed at currentPlace in seconds
                    currentQuantity += timeDiff; // update the latest time
                    if (!currentPlace.equals(nextLocation)) { //if different location check if time is more than 5 mins
                        if (currentQuantity >= 300) {
                            spentMoreThan5Minutes = currentPlace;
                        }
                        currentPlace = nextLocation; //set the next place as current place
                        currentQuantity = 0; // reset time to re-count the time for nextLocation
                    }
                } else { //reach the end
                    String lastDate = locationTimestampList.get(i + 1); //to retrieve the corresponding date for last location in locationTimestampList

                    java.util.Date lastDateTimeAdded = df.parse(lastDate);
                    java.util.Date endDateTime = df.parse(dateTime);

                    //Calendar object to add 14min 59s to dateTime given from user
                    //Eg: time is 11:00:00; after using Calendar object time is 11:14:59
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(endDateTime); //to use the dateTime given by input as the base
                    cal.add(Calendar.MINUTE, 14);
                    cal.add(Calendar.SECOND, 59);
                    endDateTime = cal.getTime(); //assign the added 14min 59s to endDateTime

                    long diff = (endDateTime.getTime() - lastDateTimeAdded.getTime()); //get the time difference between the last location to the max 15min window
                    double timeDiff = diff / 1000.0; //to get time in seconds

                    // based on wiki, will assume user spend the rest of his/her time there, update the latest time
                    currentQuantity += timeDiff;
                }
            }
            if (currentQuantity >= 300) { //if it is the same place all the way in the users time line
                spentMoreThan5Minutes = currentPlace;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException ex) {
            Logger.getLogger(ReportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return spentMoreThan5Minutes; //return sematic place with more than 5 minutes or if the user does not have any sematic place with more than 5 mins, return empty string
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
        String[] year = {"year", "2013", "2014", "2015", "2016", "2017"};                              //5
        String[] gender = {"gender", "M", "F"};                                                        //2
        String[] school = {"school", "accountancy", "business", "economics", "law", "sis", "socsc"};   //6

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

        int secondL = 1;
        if (second != null) { //if user choose a second option
            secondL = (second.length - 1); //to make sure the array doesn't have ArrayOutOfBoundException
        }
        int thirdL = 1;
        if (third != null) { //if user choose a third option
            thirdL = (third.length - 1); //to make sure the array doesn't have ArrayOutOfBoundException
        }


        //string to return to ReportServlet.java
        String returnThis = "<div class=\"container\">      <table class=\"table table-bordered\">";

        //for the percentage calculation later to compare the number in each category with the total number of possible users
        int totalBetweenTime = everyoneWithinTime(endTimeDate);

        //Print table header
        returnThis += ("<thead><tr><th colspan = " + (totalOptions + 2) + ">Breakdown by: " + userInput + " <br>Total user found: " + totalBetweenTime + "</th></tr>");
        for (String header : userInputArray) { //can be year/gender/school
            returnThis += "<th>" + proper(header) + "</th>";
        }
        returnThis += "<th>Qty</th><th>Percentage</th></thead><tbody>";

        //Arraylist for first
        int r1Count = 0;
        ArrayList<Integer> firstVarSplit = null;
        if (second != null) {
            firstVarSplit = notVeryBasicBreakdownJson(first[0].split(" "), endTimeDate);
        }
        
        
        int r2Count = 0;
        ArrayList<Integer> secondVarSplit = null;
        if (second != null) {
            secondVarSplit = notVeryBasicBreakdownJson((first[0] + " " + second[0]).split(" "), endTimeDate);
        }
        
        
        int r3Count = 0;
        ArrayList<Integer> thirdVarSplit = notVeryBasicBreakdownJson(userInputArray, endTimeDate);

        for (int i = 1; i <= thirdVarSplit.size(); i++) {
            //Stating of first row
            String currentLine = "<tr>";
            
            
            //Text for first col
            if (i % (secondL * thirdL) == totalOptions / 2) {       //checks whether to print this row (happens when there is 3 var)
                //first var to split by
                //if one var trigger = 0
                //if two or three var trigger = 1
                currentLine += "<td rowspan =\""
                        + (secondL * thirdL) + "\">"
                        + proper(first[i / (secondL * thirdL) + totalOptions / 2]);
                if (second != null) {
                    currentLine += "<br>Qty: "
                            + firstVarSplit.get(r1Count) + "<br>"
                            + Math.round(firstVarSplit.get(r1Count++) * 100.0 / totalBetweenTime)
                            + "%</td>";
                }
                currentLine += "</td>";
            }

            
            int trigger = totalOptions - 2;
            //text for second col
            if (second != null && i % (thirdL) == trigger) {        //checks whether to print this row (happens when there is 2 var)
                //Second var to split by
                //if two var trigger = 0
                //if three var trigger = 1
                currentLine += "<td rowspan =\"" + (thirdL) + "\">"
                        + proper(second[(int) (i / (0.001 + thirdL) % (secondL)) + 1]);
                if (third != null) {
                    currentLine += "<br>Qty: "
                            + secondVarSplit.get(r2Count)
                            + "<br>"
                            + Math.round(secondVarSplit.get(r2Count++) * 100.0 / totalBetweenTime)
                            + "%</td>";
                }
                currentLine += "</td>";
            }
            

            //text for third col
            if (third != null) {
                //Third var to split by
                currentLine += "<td>"
                        + proper(third[(int) Math.ceil(i % (0.001 + thirdL))])
                        + "</td>";
            }
            
            //Third var qty
            currentLine += "<td>" + thirdVarSplit.get(r3Count) + "</td>";
            
            //Third var percentage
            currentLine += "<td>" + Math.round(thirdVarSplit.get(r3Count++) * 100.0 / totalBetweenTime) + "%</td>";
            
            //Ending
            currentLine += "</tr>";
            returnThis += currentLine;
        }
        returnThis += "</tbody></table></div>";
        return returnThis;
    }

    public static ArrayList<Integer> notVeryBasicBreakdownJson(String[] text, String endTimeDate) {
        // initialize array
        String[] first = null;  //category have name in their first value to know what does first, second or third variable contains
        String[] second = null;  //category have name in their first value to know what does first, second or third variable contains
        String[] third = null;  //category have name in their first value to know what does first, second or third variable contains
        String[] year = {"Year", "2013", "2014", "2015", "2016", "2017"};                              //5
        String[] gender = {"Gender", "M", "F"};                                                        //2
        String[] school = {"School", "accountancy", "business", "economics", "law", "sis", "socsc"};   //6

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

        try {
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

        } catch (ArrayIndexOutOfBoundsException e) {

        } catch (NullPointerException e1) {

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
        ArrayList<Integer> ans = new ArrayList<>();

        for (int i = 1; i <= numberOfRows; i++) {
            String temp = "";

            //first var to split by
            //if 1 trigger = 0
            //if 2/3 trigger = 1
            if (i % (secondL * thirdL) == totalOptions / 2) {
                temp = first[0]
                        + " "
                        + first[i / (secondL * thirdL) + totalOptions / 2];
            }

            //if 2 trigger = 0
            //if 3 trigger = 1
            int trigger = totalOptions - 2;
            //Second var to split by
            if (second != null && i % (thirdL) == trigger) {
                temp += " "
                        + second[0]
                        + " "
                        + second[(int) (i / (0.001 + thirdL) % (secondL)) + 1];
            }

            if (third != null) {
                temp += " "
                        + third[0]
                        + " "
                        + third[(int) Math.ceil(i % (0.001 + thirdL))];
            }

            //Third var to split by
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
            ans.add(value);
        }
        return ans;
    }

    public static Map<Double, ArrayList<String>> retrieveTopKCompanions(String endTimeDate, String macaddress) {
        ArrayList<String> UserLocationTimestamps = retrieveUserLocationTimestamps(macaddress, endTimeDate);

        ArrayList<String> Companions = new ArrayList<String>();
        ArrayList<String> CompanionLocationTimestamps = null;

        Map<String, Double> CompanionColocations = new HashMap<String, Double>();
        Map<Double, ArrayList<String>> SortedList = new TreeMap<Double, ArrayList<String>>(Collections.reverseOrder());

        for (int i = 0; i < UserLocationTimestamps.size(); i += 1) {
            String UserLocationTimestamp = UserLocationTimestamps.get(i);
            String[] LocationTimestamp = UserLocationTimestamp.split(",");

            String locationid = LocationTimestamp[0];
            String timestringStart = LocationTimestamp[1];
            String timestringEnd = LocationTimestamp[2];

            Companions = retreiveCompanionMacaddresses(macaddress, locationid, timestringStart, timestringEnd);
            CompanionLocationTimestamps = retrieveCompanionLocationTimestamps(Companions, locationid, timestringStart, timestringEnd);

            if (CompanionLocationTimestamps != null) {
                for (int j = 0; j < CompanionLocationTimestamps.size(); j += 1) {
                    String CompanionLocationTimestamp = CompanionLocationTimestamps.get(j);
                    String[] LocationTimestampc = CompanionLocationTimestamp.split(",");
                    String macaddressc = LocationTimestampc[0];
                    double colocationTime = Double.parseDouble(LocationTimestampc[3]);

                    if (CompanionColocations.containsKey(macaddressc)) {
                        double colocationTime2 = CompanionColocations.get(macaddressc);
                        colocationTime += colocationTime2;
                        //CompanionColocations.remove(macaddressc);
                        CompanionColocations.put(macaddressc, colocationTime);

                    } else {
                        CompanionColocations.put(macaddressc, colocationTime);
                    }

                }
            }
        }

        Set<String> macaddressess = CompanionColocations.keySet();

        for (String macaddress2 : macaddressess) {
            double colocationTime3 = CompanionColocations.get(macaddress2);
            String email = retrieveEmailByMacaddress(macaddress2);
            if (email == null || email.length() <= 0) {
                email = "No email found";
            }
            ArrayList<String> companions = SortedList.get(colocationTime3); //null or something
            if (companions == null || companions.size() <= 0) {
                ArrayList<String> companionsList = new ArrayList<String>();
                companionsList.add(macaddress2 + "," + email);
                SortedList.put(colocationTime3, companionsList);
            } else {

                companions.add(macaddress2 + "," + email);
                SortedList.put(colocationTime3, companions);
            }
        }
        return SortedList;
    }

    //retreive users in hashmap form, hashmap key is macaddress and hashmap value is array of email, locationid and timestamp
    public static ArrayList<String> retrieveUserLocationTimestamps(String macaddress, String endtimeDate) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String ans = "";
        double duration = 0;

        ArrayList<String> UserLocationTimestamps = new ArrayList<String>();
        ArrayList<String> locations = new ArrayList<String>();

        String timestring = null;
        int timeStartIndex = -1;
        java.util.Date timedateEnd = null;

        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select locationid, timestamp from location where macaddress = ? and timestamp between DATE_SUB(?, INTERVAL 15 MINUTE) and DATE_SUB(?, INTERVAL 1 SECOND) order by timestamp");

            //set the parameters
            preparedStatement.setString(1, macaddress);
            preparedStatement.setString(2, endtimeDate);
            preparedStatement.setString(3, endtimeDate);

            //execute SQL query
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String locationid = resultSet.getString(1);
                timestring = resultSet.getString(2);
                locations.add(locationid);
                locations.add(timestring);
            }

            //close connections
            resultSet.close();
            preparedStatement.close();
            connection.close();

            for (int i = 0; i < locations.size(); i += 2) {
                String locationid = locations.get(i); //find first location id
                timestring = locations.get(i + 1); //find first time string

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Date timestamp = dateFormat.parse(timestring);//convert time string to Date format
                java.util.Date timestampEnd = dateFormat.parse(endtimeDate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(timestampEnd);
                cal.add(Calendar.SECOND, -1);

                timedateEnd = cal.getTime();
                timestampEnd = null;

                if (locations.size() <= i + 2) { //if last pair of location and time
                    duration = (timedateEnd.getTime() - timestamp.getTime()) / (1000.0);

                    if (duration > 300.0) {
                        cal.setTime(timestamp);
                        cal.add(Calendar.MINUTE, 5);
                        //timeDateEnd is 5 minutes after timeDateStart
                        timestampEnd = cal.getTime();
                    } else {
                        timestampEnd = timedateEnd;
                    }
                    if (timeStartIndex > -1) {
                        java.util.Date timestampStart = dateFormat.parse(locations.get(timeStartIndex));
                        timestamp = timestampStart;;
                    }
                    ans += locationid + "," + dateFormat.format(timestamp) + "," + dateFormat.format(timestampEnd) + ",";
                    UserLocationTimestamps.add(ans);
                    ans = "";
                } else if (locations.size() > i + 2) {
                    String locationidNext = locations.get(i + 2);
                    String timestringNext = locations.get(i + 3);
                    java.util.Date timestampNext = dateFormat.parse(timestringNext);

                    if (!locationid.equals(locationidNext)) {
                        duration = (double) (timestampNext.getTime() - timestamp.getTime()) / (1000.0);
                        if (timeStartIndex > -1) {
                            java.util.Date timestampStart = dateFormat.parse(locations.get(timeStartIndex));
                            timestamp = timestampStart;
                        }
                        ans += locationid + "," + dateFormat.format(timestamp) + "," + dateFormat.format(timestampNext) + ",";
                        UserLocationTimestamps.add(ans);
                        duration = 0;
                        ans = "";
                        timeStartIndex = -1;
                        //UserLocationTimestamps.add(ans);
                        //if the next update location is same as the previous one
                    } else if (locationid.equals(locationidNext)) {
                        if (timeStartIndex == -1) {
                            timeStartIndex = i + 1;
                        }
                        duration = (double) (timestampNext.getTime() - timestamp.getTime()) / (1000.0);
                        if (duration > 300.0) {
                            cal.setTime(timestamp);
                            cal.add(Calendar.MINUTE, 5);
                            //timeDateEnd is 5 minutes after timeDateStart
                            timestampEnd = cal.getTime();
                            java.util.Date timestampStart = dateFormat.parse(locations.get(timeStartIndex));
                            ans += locationidNext + "," + dateFormat.format(timestampStart) + "," + dateFormat.format(timestampEnd) + ",";
                            UserLocationTimestamps.add(ans);
                            duration = 0;
                            ans = "";
                            timeStartIndex = -1;
                            //UserLocationTimestamps.add(ans);
                        } else if (duration <= 300) {
                            duration += (timestampNext.getTime() - timestamp.getTime()) / (1000.0);
                        }

                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException ex) {
            Logger.getLogger(ReportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        //UserLocationTimestamps.add("1010110032"+","+"014-03-24 09:07:27.000000"+","+"1");
        return UserLocationTimestamps;
    }

    public static ArrayList<String> retreiveCompanionMacaddresses(String userMacaddress, String locationid, String timestringStart, String timestringEnd) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        ArrayList<String> Companions = new ArrayList<String>();

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date timeStart = dateFormat.parse(timestringStart);

            Calendar cal = Calendar.getInstance();
            cal.setTime(timeStart);
            cal.add(Calendar.MINUTE, -5);
            String timestringBeforeStart = dateFormat.format(cal.getTime());

            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select distinct macaddress from location where macaddress <> ? and locationid= ? and timestamp between ? and ?");

            //set the parameters
            preparedStatement.setString(1, userMacaddress);
            preparedStatement.setString(2, locationid);
            preparedStatement.setString(3, timestringBeforeStart);
            preparedStatement.setString(4, timestringEnd);

            //execute SQL query
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Companions.add(resultSet.getString(1));
            }

            //close connections
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException ex) {
            Logger.getLogger(ReportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        //UserLocationTimestamps.add("1010110032"+","+"014-03-24 09:07:27.000000"+","+"1");
        return Companions;
    }

    //retreive users in hashmap form, hashmap key is macaddress and hashmap value is array of email, locationid and timestamp
    public static ArrayList<String> retrieveCompanionLocationTimestamps(ArrayList<String> Companions, String locationid, String timestringStart, String timestringEnd) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String ans = "";

        ArrayList<String> CompanionLocationTimestamps = new ArrayList<String>();

        double colocationTime = 0;
        double tmp = 0;
        double duration = 0;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date timeStart = dateFormat.parse(timestringStart);
            java.util.Date timeEnd = dateFormat.parse(timestringEnd);
            Calendar cal = Calendar.getInstance();
            cal.setTime(timeStart);
            cal.add(Calendar.MINUTE, -5);
            String timestringBeforeStart = dateFormat.format(cal.getTime());
            duration = (timeEnd.getTime() - timeStart.getTime()) / 1000.0;

            for (int j = 0; j < Companions.size(); j += 1) {
                String macaddress = Companions.get(j);
                colocationTime = 0;
                ans = "";
                boolean CorrectTimestring = false;

                //get a connection to database
                connection = ConnectionManager.getConnection();

                //prepare a statement
                preparedStatement = connection.prepareStatement("select timestamp, locationid, TIMESTAMPDIFF(second,timestamp,?) as diff from location where macaddress = ? and timestamp between ? and ? order by timestamp,diff desc");

                //set the parameters
                preparedStatement.setString(1, timestringEnd);
                preparedStatement.setString(2, macaddress);
                preparedStatement.setString(3, timestringBeforeStart);
                preparedStatement.setString(4, timestringEnd);

                //execute SQL query
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String timestring = resultSet.getString(1);
                    String location = resultSet.getString(2);
                    int timeDiff = resultSet.getInt(3);
                    //CompanionLocationTimestamps.add("test sql " + macaddress + "" + timestring + "" + location + "" + timeDiff);
                    java.util.Date timestamp = dateFormat.parse(timestring);//convert time string to Date format
                    double gap = (double) (timeDiff - duration);
                    if (resultSet.isLast()) {
                        if (location.equals(locationid)) {
                            if (timestamp.before(timeStart)) {
                                if (timeDiff > 300) {
                                    colocationTime = (300 - gap);
                                } else {
                                    colocationTime = timeDiff - gap;
                                }
                                //CompanionLocationTimestamps.add(macaddress + "first and last location time before start " + "," + gap + "," + colocationTime + "," + timeDiff);

                            } else if (!timestamp.before(timeStart)) {
                                if (timeDiff > 300) {
                                    colocationTime += 300;
                                } else {
                                    colocationTime = timeDiff;
                                }
                                //CompanionLocationTimestamps.add(macaddress + "last location time " + colocationTime + "," + timeDiff);

                            }

                            ans = macaddress + "," + locationid + "," + timestamp + "," + colocationTime + ",";
                            CompanionLocationTimestamps.add(ans);
                            ans = "";
                            colocationTime = 0;
                            CorrectTimestring = false;
                        }
                    }

                    while (resultSet.next()) {
                        String locationNext = resultSet.getString(2);
                        int timeDiffNext = resultSet.getInt(3);
                        String timestringNext = resultSet.getString(1);
                        java.util.Date timestampNext = dateFormat.parse(timestringNext);//convert time string to Date format
                        gap = (double) (timeDiffNext - duration);

                        //check if the previous location is correct or current location is correct
                        if (location.equals(locationid) || CorrectTimestring) {
                            //java.util.Date timestampNext = dateFormat.parse(timestringNext);//convert time string to Date format
                            //if time stamp before time start
                            if (timestamp.before(timeStart)) {
                                //CompanionLocationTimestamps.add(macaddress + "before start " +timeDiff+","+timeDiffNext+ ","+timestring);
                                //if timestamp is the last one before time start and location is correct
                                if (timestampNext.after(timeStart) && location.equals(locationid)) {
                                    //if current and next location is the same
                                    if (location.equals(locationNext)) {
                                        tmp = timeDiff - timeDiffNext;
                                        if (tmp > 300) {
                                            colocationTime = (300 - gap);
                                        } else {
                                            colocationTime = duration - timeDiffNext;
                                        }
                                        //CompanionLocationTimestamps.add(macaddress + "same location time before start " + "," + colocationTime + "," + tmp + "," + duration + "," + gap);
                                        CorrectTimestring = true;
                                    } else if (!location.equals(locationNext)) {
                                        tmp = timeDiff - timeDiffNext;
                                        if (tmp > 300) {
                                            colocationTime = (300 - gap);
                                        } else {
                                            colocationTime = duration - timeDiffNext;
                                        }
                                        //CompanionLocationTimestamps.add(macaddress + "diff location time before start " + colocationTime + "," + tmp);
                                        ans = macaddress + "," + locationid + "," + timestampNext + "," + colocationTime + ",";
                                        CompanionLocationTimestamps.add(ans);
                                        ans = "";
                                        colocationTime = 0;
                                        CorrectTimestring = false;
                                    }
                                }
                                //if timestamp is after time start
                            } else if (!timestamp.before(timeStart)) {
                                //CompanionLocationTimestamps.add(macaddress + "timestamp after time start" + timestring);
                                //if current and next location is the same
                                if (location.equals(locationNext) && location.equals(locationid)) {

                                    tmp = timeDiff - timeDiffNext;
                                    if (tmp > 300) {
                                        colocationTime += 300;
                                    } else {
                                        colocationTime += tmp;
                                    }
                                    CorrectTimestring = true;
                                    //CompanionLocationTimestamps.add(macaddress + "same location " + colocationTime + "," + tmp + "," + timeDiff + "," + timeDiffNext + "," + timestring + ",");
                                    //if current location is different from next one and is correct location
                                } else if (!location.equals(locationNext) && location.equals(locationid)) {
                                    tmp = timeDiff - timeDiffNext;
                                    if (tmp > 300) {
                                        colocationTime += 300;
                                    } else {
                                        colocationTime += tmp;
                                    }
                                    //CompanionLocationTimestamps.add(macaddress + "diff location " + colocationTime + "," + tmp + "," + timeDiff + "," + timeDiffNext + "," + timestring + "," + timestringNext);
                                    ans = macaddress + "," + locationid + "," + timestampNext + "," + colocationTime + ",";
                                    CompanionLocationTimestamps.add(ans);
                                    ans = "";
                                    colocationTime = 0;
                                    CorrectTimestring = false;
                                    //if previous location is correct and is not correct location
                                }
                            }
                        }
                        if (resultSet.isLast()) {
                            if (locationNext.equals(locationid)) {
                                if (timestamp.before(timeStart)) {
                                    if (timeDiffNext > 300) {
                                        colocationTime = (300 - gap);
                                    } else {
                                        colocationTime += timeDiffNext - gap;
                                    }
                                    //CompanionLocationTimestamps.add(macaddress + "last location time before start " + duration + "," + colocationTime + "," + gap + "," + timeDiffNext);
                                    //if last location is correct location and not before start timestamp  
                                } else if (!timestamp.before(timeStart)) {
                                    if (timeDiffNext > 300) {
                                        colocationTime += 300;
                                    } else {
                                        colocationTime += timeDiffNext;
                                    }
                                    //CompanionLocationTimestamps.add(macaddress + "last location time " + colocationTime + "," + timeDiff);

                                }

                                ans = macaddress + "," + locationid + "," + timestampNext + "," + colocationTime + ",";
                                CompanionLocationTimestamps.add(ans);
                                ans = "";
                                colocationTime = 0;
                                CorrectTimestring = false;
                            }
                        }
                        timestring = timestringNext;
                        location = locationNext;
                        timeDiff = timeDiffNext;
                        timestamp = dateFormat.parse(timestring);
                    }

                }
                //close connections
                resultSet.close();
                preparedStatement.close();
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException ex) {
            Logger.getLogger(ReportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return CompanionLocationTimestamps;
    }

    //retrieve user's email by macaddress
    public static String retrieveEmailByMacaddress(String macaddress) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();
            //prepare a statement
            preparedStatement = connection.prepareStatement("SELECT email from demographics WHERE macaddress = ?");

            //set the parameters
            preparedStatement.setString(1, macaddress);

            resultSet = preparedStatement.executeQuery();

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

    private static String proper(String line) {
        if (line == null) {
            return null;
        }
        if (line.length() == 1) {
            return line.toUpperCase();
        }
        return line.substring(0, 1).toUpperCase() + line.substring(1);
    }

}
