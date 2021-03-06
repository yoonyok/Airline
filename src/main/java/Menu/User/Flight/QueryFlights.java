package main.java.Menu.User.Flight;

import main.java.Menu.Main;
import main.java.Menu.User.Passenger.Passenger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryFlights {
    public static JPanel panel;
    private GridBagConstraints c;

    private JLabel errorInFindLabel;

    private JComboBox departureAirportIDComboBox;
    private JComboBox arrivalAirportIDComboBox;
    private JComboBox minMaxCB;
    private JComboBox departingAPCB;

    private JCheckBox arrivalAirportCB;
    private JCheckBox departingAirportCB;
    private JCheckBox arrivalDateCB;
    private JCheckBox arrivalTimeCB;
    private JCheckBox departingDateCB;
    private JCheckBox departingTimeCB;

    private Boolean isAAChecked = false;
    private Boolean isDAChecked = false;
    private Boolean isADChecked = false;
    private Boolean isATChecked = false;
    private Boolean isDDChecked = false;
    private Boolean isDTChecked = false;

    private Boolean isAASelected = false;
    private Boolean isDASelected = false;

    private JScrollPane scrollPane;
    private JTable table;

    private JTextField reserveFlight;
    private String reservedFlightNo = "";

    private JLabel invalidFlightNoLabel;

    private String[] columns = new String[] {
            "Flight Number", "Cost", "Depart From", "Departure Date", "Departure Time",
            "Arrive In", "Arrival Date", "Arrival Time", "Seats Remaining"};

    private Object[][] data;

    private List<String> filterColumns = new ArrayList<String>();
    private List<String> departingAirports;

    private String select_clause = "select ";
    private String from_clause = "from flights ";
    private String where_clause = "";
    private String arriving_airport = "";
    private String selected_arrival = "";
    private String departing_airport = "";
    private String selected_departure = "";
    private String minMaxSelection = "";
    private String departingAPSelection = "";

    private Boolean select_triggered = false;
    private Boolean where_triggered = false;


    public void init() {
        panel = new JPanel();
        c = new GridBagConstraints();
        panel.setLayout(new GridBagLayout());
        Main.frame.add(panel);

        /**
         * Departing Airport Dropdown (where airportid_depart = ...)
         */

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        JLabel departureAirportLabel = new JLabel("Select Departing Airport");
        panel.add(departureAirportLabel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        departureAirportIDComboBox = new JComboBox();
        departureAirportIDComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object selected = departureAirportIDComboBox.getSelectedItem();
                setDASelected(selected);
            }
        });
        generateDepartureDropDown();
        panel.add(departureAirportIDComboBox, c);

        /**
         * Arriving Airport Dropdown (where airportid_arrive = ...)
         */

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 0;
        JLabel arrivalAirportLabel = new JLabel("Select Arriving Airport");
        panel.add(arrivalAirportLabel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 0;
        arrivalAirportIDComboBox = new JComboBox();
        arrivalAirportIDComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object selected = arrivalAirportIDComboBox.getSelectedItem();
                setAASelected(selected);
            }
        });
        generateArrivalDropDown();
        panel.add(arrivalAirportIDComboBox, c);

        /**
         * Back button layout
         */
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 5;
        c.gridy = 0;
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.setVisible(false);
                Passenger.panel.setVisible(true);
            }
        });
        panel.add(backButton, c);

        /**
         * Filtering Checkboxes (select ...)
         */
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        arrivalAirportCB = new JCheckBox("Arriving Airport");
        arrivalAirportCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (arrivalAirportCB.isSelected()) {
                    isAAChecked = true;
                    filterColumns.add("Arriving In");
                    select_clause += "airportid_arrive, ";
                } else {
                    isAAChecked = false;
                    filterColumns.remove("Arriving In");
                    select_clause = select_clause.replace("airportid_arrive, ", "");
                }
            }
        });
        panel.add(arrivalAirportCB, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        departingAirportCB = new JCheckBox("Departing Airport");
        departingAirportCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (departingAirportCB.isSelected()) {
                    isDAChecked = true;
                    filterColumns.add("Depart From");
                    select_clause += "airportid_depart, ";
                } else {
                    isDAChecked = false;
                    filterColumns.remove("Depart From");
                    select_clause = select_clause.replace("airportid_depart, ", "");
                }
            }
        });
        panel.add(departingAirportCB, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        arrivalDateCB = new JCheckBox("Arriving Date");
        arrivalDateCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (arrivalDateCB.isSelected()) {
                    isADChecked = true;
                    filterColumns.add("Arrival Date");
                    select_clause += "date_arrive, ";
                } else {
                    isADChecked = false;
                    filterColumns.remove("Arrival Date");
                    select_clause = select_clause.replace("date_arrive, ", "");
                }
            }
        });
        panel.add(arrivalDateCB, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 2;
        departingDateCB = new JCheckBox("Departing Date");
        departingDateCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (departingDateCB.isSelected()) {
                    isDDChecked = true;
                    filterColumns.add("Departure Date");
                    select_clause += "date_depart, ";
                } else {
                    isDDChecked = false;
                    filterColumns.remove("Departure Date");
                    select_clause = select_clause.replace("date_depart, ", "");
                }
            }
        });
        panel.add(departingDateCB, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 1;
        arrivalTimeCB = new JCheckBox("Arriving Time");
        arrivalTimeCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (arrivalTimeCB.isSelected()) {
                    isATChecked = true;
                    filterColumns.add("Arrival Time");
                    select_clause += "time_arrive, ";
                } else {
                    isATChecked = false;
                    filterColumns.remove("Arrival Time");
                    select_clause = select_clause.replace("time_arrive, ", "");
                }
            }
        });
        panel.add(arrivalTimeCB, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 2;
        departingTimeCB = new JCheckBox("Departing Time");
        departingTimeCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (departingTimeCB.isSelected()) {
                    isDTChecked = true;
                    filterColumns.add("Departure Time");
                    select_clause += "time_depart, ";
                } else {
                    isDTChecked = false;
                    filterColumns.remove("Departure Time");
                    select_clause = select_clause.replace("time_depart, ", "");
                }
            }
        });
        panel.add(departingTimeCB, c);

        /**
         * Aggregated Search for Price
         */
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 5;
        JLabel findTheFlightLabel = new JLabel("Find the flight with the");
        panel.add(findTheFlightLabel, c);
        generateMinMaxDropDown();
        c.gridx = 2;
        JLabel findThePriceLabel = new JLabel("price departing from");
        panel.add(findThePriceLabel, c);
        generateDepartingDropDown();

        /**
         * Find Button for Price Search
         */
        c.gridx = 5;
        JButton findButton = new JButton("Find");
        findButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (priceSearchIsValid()) {
                    resetErrorMessages();
                    executePriceFindQuery();
                }
                else {
                    errorInFindLabel.setText("Invalid Search. Please Try Again.");
                }
            }
        });
        panel.add(findButton, c);

        /**
         * Error Message for Find
         */
        c.gridx = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 6;
        errorInFindLabel = new JLabel("");
        panel.add(errorInFindLabel, c);

        /**
         * Flight Table Title with filter suffix
         */
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 7;
        JLabel flightDetailLabel = new JLabel("All Flights");
        panel.add(flightDetailLabel, c);

        /**
         * Filter Search Button
         */
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 5;
        c.gridy = 2;
        JButton filterButton = new JButton("Filter Search");
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSelectTrigger();
                if (!select_triggered && !where_triggered) {
                    resetErrorMessages();
                    displayAllFlightDetails();
                } else {
                    resetErrorMessages();
                    filterSearch();
                }
            }
        });
        panel.add(filterButton, c);
        displayAllFlightDetails();

        /**
         * Enter Flight Number Text Field
         */
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = table.getRowHeight();
        c.gridwidth = 3;
        reserveFlight = new JTextField("Enter Flight Number...");
        panel.add(reserveFlight, c);

        /**
         * Reserve Flight Button
         */
        c.fill = GridBagConstraints.HORIZONTAL;
        JButton reserveButton = new JButton("Reserve Flight");
        reserveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reservedFlightNo = reserveFlight.getText();
                if (isFlightNoValid(reservedFlightNo)) {
                    panel.setVisible(false);
                    FlightReserver fr = new FlightReserver();
                    fr.init(reservedFlightNo);
                }
            }
        });
        invalidFlightNoLabel = new JLabel();
        c.gridx = 0;
        c.gridy = table.getRowHeight() + 10;
        panel.add(invalidFlightNoLabel, c);
        c.gridx = 5;
        c.gridy = table.getRowHeight();
        panel.add(reserveButton, c);
    }

    private void resetErrorMessages() {
        invalidFlightNoLabel.setText("");
        errorInFindLabel.setText("");
    }

    private void executePriceFindQuery() {
        try {
            ResultSet results = Main.myStat.executeQuery("select flight_no, cost, airportid_depart, date_depart, " +
                    "time_depart, airportid_arrive, date_arrive, time_arrive, available_seats " + from_clause +
                    "where cost = (select " + minMaxSelection + from_clause + "where airportid_depart = " + "\'" +
                    departingAPSelection + "\'" + ") " + "and airportid_depart = " + "\'" + departingAPSelection + "\'");

            int rowCount = 0;
            if (results.last()) {
                rowCount = results.getRow();
                results.beforeFirst();
            }

            data = new Object[rowCount][columns.length];
            int j = 0;

            while (results.next()) {
                for (int i = 0; i < columns.length; i++) {
                    data[j][i] = results.getObject(i + 1);
                }
                j++;
            }
            resetWhereConditions();
            refreshTable(columns);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean priceSearchIsValid() {
        return (!minMaxSelection.equals("") && !departingAPSelection.equals(""));
    }

    private void generateDepartingDropDown() {
        c.gridx = 3;
        departingAPCB = new JComboBox();
        departingAPCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(departingAPCB.getSelectedItem() != "- select airport -") {
                    departingAPSelection = departingAPCB.getSelectedItem().toString();
                }
                else {
                    departingAPSelection = "";
                }
            }
        });
        for (String s : departingAirports) {
            departingAPCB.addItem(s);
        }
        panel.add(departingAPCB, c);
    }

    private void generateMinMaxDropDown() {
        c.gridx = 1;
        minMaxCB = new JComboBox();
        minMaxCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (minMaxCB.getSelectedItem().equals("maximum")) {
                    minMaxSelection = "MAX(cost) ";
                }
                else if (minMaxCB.getSelectedItem().equals("minimum")) {
                    minMaxSelection = "MIN(cost) ";
                }
                else {
                    minMaxSelection = "";
                }
            }
        });
        minMaxCB.addItem("- select -");
        minMaxCB.addItem("maximum");
        minMaxCB.addItem("minimum");
        panel.add(minMaxCB, c);
    }

    private void generateDepartureDropDown() {
        try {
            ResultSet departureSet = Main.myStat.executeQuery("select airportid_depart from flights");

            departingAirports = new ArrayList<String>();
            departingAirports.add("- select airport -");

            while (departureSet.next()) {
                String result = departureSet.getString("airportid_depart");
                if (!departingAirports.contains(result)) {
                    departingAirports.add(result);
                }
            }
            for (int i = 0; i < departingAirports.size(); i++) {
                departureAirportIDComboBox.addItem(departingAirports.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateArrivalDropDown() {
        try {
            ResultSet arrivalSet = Main.myStat.executeQuery("select airportid_arrive from flights");

            List<String> arrivingAirports = new ArrayList<String>();
            arrivingAirports.add("- select airport -");

            while (arrivalSet.next()) {
                String result = arrivalSet.getString("airportid_arrive");
                if (!arrivingAirports.contains(result)) {
                    arrivingAirports.add(result);
                }
            }

            for (int i = 0; i < arrivingAirports.size(); i++) {
                arrivalAirportIDComboBox.addItem(arrivingAirports.get(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayAllFlightDetails() {
        try {
            ResultSet allDetailsSet = Main.myStat.executeQuery("select flight_no, cost, airportid_depart, date_depart, time_depart, airportid_arrive, date_arrive, time_arrive, available_seats from flights");

            int rowCount = 0;
            if (allDetailsSet.last()) {
                rowCount = allDetailsSet.getRow();
                allDetailsSet.beforeFirst();
            }

            data = new Object[rowCount][columns.length];
            int j = 0;

            while (allDetailsSet.next()) {
                for (int i = 0; i < columns.length; i++) {
                    data[j][i] = allDetailsSet.getObject(i + 1);
                }
                j++;
            }
            refreshTable(columns);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filterSearch() {
        String[] filterArray;

        formatSelectClause();
        formatWhereClause();

        String sql = select_clause + from_clause + where_clause;
        //System.out.println(sql);
        try {
            ResultSet filteredSet = Main.myStat.executeQuery(sql);

            filterArray = selectOutputColumns();

            int rowCount = 0;
            if (filteredSet.last()) {
                rowCount = filteredSet.getRow();
                filteredSet.beforeFirst();
            }

            data = new Object[rowCount][filterArray.length];
            int j = 0;

            while (filteredSet.next()) {
                for (int i = 0; i < filterArray.length; i++) {
                    data[j][i] = filteredSet.getObject(i + 1);
                }
                j++;
            }
            filterColumns.clear();
            resetSelectConditions();
            resetWhereConditions();
            refreshTable(filterArray);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String[] selectOutputColumns() {
        String[] filterArray;
        if (select_triggered || where_triggered) {
            if (select_triggered && where_triggered) {
                filterArray = Arrays.copyOf(filterColumns.toArray(), filterColumns.toArray().length, String[].class);
            } else if (select_triggered) {
                filterArray = Arrays.copyOf(filterColumns.toArray(), filterColumns.toArray().length, String[].class);
            } else {
                filterArray = columns;
            }
        }
        else {
            filterArray = columns;
        }
        return filterArray;
    }


    private void formatSelectClause() {
        if (!select_triggered) {
            select_clause += "flight_no, cost, airportid_depart, date_depart, time_depart, airportid_arrive, date_arrive, time_arrive, available_seats ";
        }
        else {
            select_clause = select_clause.substring(0, select_clause.length() - 2);
            select_clause += " ";
        }
    }

    private void formatWhereClause() {
        if (isAASelected && isDASelected) {
            where_clause = "where " + arriving_airport + " = " + "\'" + selected_arrival + "\'" +
                    " and " + departing_airport + " = " + "\'" + selected_departure + "\'";
        } else if (isAASelected) {
            where_clause = "where " + arriving_airport + " = " + "\'" + selected_arrival + "\'";
        } else if (isDASelected) {
            where_clause = "where " + departing_airport + " = " + "\'" + selected_departure + "\'";
        }
    }

    private void resetSelectConditions() {
        arrivalAirportCB.setSelected(false);
        departingAirportCB.setSelected(false);
        arrivalDateCB.setSelected(false);
        arrivalTimeCB.setSelected(false);
        departingDateCB.setSelected(false);
        departingTimeCB.setSelected(false);
        isAAChecked = false;
        isADChecked = false;
        isATChecked = false;
        isDAChecked = false;
        isDDChecked = false;
        isDTChecked = false;
        select_clause = "select ";
    }

    private void resetWhereConditions() {
        minMaxCB.setSelectedIndex(0);
        departingAPCB.setSelectedIndex(0);
        arrivalAirportIDComboBox.setSelectedIndex(0);
        departureAirportIDComboBox.setSelectedIndex(0);
        where_triggered = false;
        where_clause = "";
    }

    private void refreshTable(String[] columnNames) {
        if (scrollPane != null) {
            panel.remove(scrollPane);
        }
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 8;
        c.gridwidth = 10;
        c.gridheight = 2;
        table = new JTable(data, columnNames);
        scrollPane = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        panel.add(scrollPane, c);
        panel.revalidate();
        panel.repaint();
    }

    private boolean isFlightNoValid(String reserved) {
        try {
            ResultSet result = Main.myStat.executeQuery("select flight_no from flights where flight_no = " + "\'" + reserved + "\'");

            if (result.isBeforeFirst() && result.next() && result.getString("flight_no").equals(reserved)) {
                invalidFlightNoLabel.setText("");
                return true;
            } else {
                invalidFlightNoLabel.setText("Invalid flight number, please try again");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setSelectTrigger() {
        select_triggered = isAAChecked || isDAChecked || isADChecked || isATChecked || isDDChecked || isDTChecked;
    }

    public void setDASelected(Object selected) {
        String selectedString = selected.toString();
        if (!selectedString.equals("- select airport -")) {
            isDASelected = true;
            where_triggered = true;
            selected_departure = selectedString;
            departing_airport = "airportid_depart";
        }
        else {
            isDASelected = false;
            where_triggered = false;
            selected_departure = "";
            departing_airport = "";
        }
    }

    public void setAASelected(Object selected) {
        String selectedString = selected.toString();
        if (!selectedString.equals("- select airport -")) {
            isAASelected = true;
            where_triggered = true;
            selected_arrival = selectedString;
            arriving_airport = "airportid_arrive";
        }
        else {
            isAASelected = false;
            where_triggered = false;
            selected_arrival = "";
            arriving_airport = "";
        }
    }
}
