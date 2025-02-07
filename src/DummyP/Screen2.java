/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package DummyP;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static java.lang.System.exit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author chandrakanth.shaji
 */
public class Screen2 extends javax.swing.JFrame {

    private String name;

    /**
     * Creates new form Screen2
     */
    public Screen2() {
        initComponents();
//        setupFrame();
    }

    public Screen2(Object[][] jsonRequestBodyTableData, String baseUrl, String method, String path, String name, DefaultTableModel headersTableModel,int previousState) {
//        setExtendedState(previousState);
        this.name = name;
        initComponents();
        setupFrame(previousState);
        if (jsonRequestBodyTableData != null) {
            for (Object[] row : jsonRequestBodyTableData) {
                if (row[1] == "String") {
                    row[1] = "String";
                }
            }
            jsonTable.setModel(new DefaultTableModel(jsonRequestBodyTableData, new String[]{
                "Field", "Data Type", "Positive Data", "Negative Data", "Error Message"
            }) {
                Class[] types = new Class[]{
                    java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
                };

                @Override
                public Class getColumnClass(int columnIndex) {
                    return types[columnIndex];
                }

                @Override
                public boolean isCellEditable(int row, int column) {

                    return column == 1 || column == 2 || column == 3 || column == 4; // Allow edits for relevant columns
                }
            });
            customizeTable();
        }else{
            jsonTable.setEnabled(false);
        }
        urlValueLabel.setText(baseUrl);
        methodValueLabel.setText(method.toUpperCase());
        //  to display the Headers in Screen 2.
        if (headersTableModel.getRowCount() != 0) {
            StringBuilder headersText = new StringBuilder();
            for (int i = 0; i < headersTableModel.getRowCount(); i++) {
                String headerName = (String) headersTableModel.getValueAt(i, 0);
                String headerValue = (String) headersTableModel.getValueAt(i, 1);
                headersText.append(headerName).append(": ").append(headerValue);
                if(i<headersTableModel.getRowCount()-1){
                    headersText.append(" , ").append("\n");
                }
            }
            headersValueLabel.setText(headersText.toString());
        } else {
            headersValueLabel.setText("No headers available");
        }
    }

    private JSONObject convertTableToJson(Object[][] jsonRequestBodyTableData) {
        JSONObject rootJson = new JSONObject(); // Root JSON object
        for (Object[] row : jsonRequestBodyTableData) {
            String field = (String) row[0];  // Field name (e.g., "customErrors[0].customErrorText")
            String dataType = (String) row[1];  // Data type (e.g., "String")
            Object data = row[2];  // Data (can be any type)

            // Split field by dot notation to find the hierarchy
            String[] keys = field.split("\\.");

            JSONObject currentJson = rootJson; // Start at the root
            for (int i = 0; i < keys.length - 1; i++) {
                String key = keys[i];

                // Check if it's an array (e.g., "customErrors[0]")
                if (key.matches(".*\\[\\d+\\]")) {
                    String arrayName = key.substring(0, key.indexOf("["));
                    int index = Integer.parseInt(key.substring(key.indexOf("[") + 1, key.indexOf("]")));

                    // Create or retrieve the JSONArray
                    if (!currentJson.has(arrayName)) {
                        currentJson.put(arrayName, new JSONArray());
                    }
                    JSONArray jsonArray = currentJson.getJSONArray(arrayName);

                    // Ensure the array has enough elements
                    while (jsonArray.length() <= index) {
                        jsonArray.put(new JSONObject());
                    }

                    currentJson = jsonArray.getJSONObject(index); // Move to the array element
                } else {
                    // Create or retrieve the nested object
                    if (!currentJson.has(key)) {
                        currentJson.put(key, new JSONObject());
                    }
                    currentJson = currentJson.getJSONObject(key); // Move to the nested object
                }
            }
            String finalKey = keys[keys.length - 1];
            if (dataType.equalsIgnoreCase("String")) {
                currentJson.put(finalKey, data.toString());
            } else if (dataType.equalsIgnoreCase("Integer")) {
                // Handle both integer and floating-point numbers
                if (data instanceof Integer) {
                    currentJson.put(finalKey, data);
                } else if (data.toString().contains(".")) {
                    currentJson.put(finalKey, Double.parseDouble(data.toString().trim()));
                } else {
                    currentJson.put(finalKey, Integer.parseInt(data.toString().trim()));
                }
            } else if (dataType.equalsIgnoreCase("Boolean")) {
                // Handle Boolean
                if (data instanceof Boolean) {
                    currentJson.put(finalKey, data);
                } else {
                    currentJson.put(finalKey, Boolean.parseBoolean(data.toString().trim().toLowerCase()));
                }
            } else {
                currentJson.put(finalKey, data.toString()); // Default to String if unrecognized
            }
        }
        return rootJson;
    }

    private static Object[][] extractTableData(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int rowCount = model.getRowCount();
        int colCount = model.getColumnCount();

        Object[][] data = new Object[rowCount][colCount];

        // Extract data from the table model
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                data[row][col] = model.getValueAt(row, col);
            }
        }
        return data;
    }

    // to ensure the frame opens maximized, Allow resizing, and set a default close operation
    private void setupFrame(int state) {
        setExtendedState(state);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void customizeTable() {
        // Get the "Data Type" column (index 1)
        TableColumn dataTypeColumn = jsonTable.getColumnModel().getColumn(1);
        // Create a combo box with options
        JComboBox<String> comboBox = new JComboBox<>(new String[]{"Integer", "Boolean", "String"});
        // Set the combo box as the editor for the column
        dataTypeColumn.setCellEditor(new DefaultCellEditor(comboBox));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        requestBodyScrollPane = new javax.swing.JScrollPane();
        jsonTable = new javax.swing.JTable();
        executeTestbtn = new javax.swing.JButton();
        exitBtn = new javax.swing.JButton();
        otherComponentsPanel = new javax.swing.JPanel();
        urlLabel = new javax.swing.JLabel();
        methodLabel = new javax.swing.JLabel();
        headersLabel = new javax.swing.JLabel();
        urlValueLabel = new javax.swing.JLabel();
        methodValueLabel = new javax.swing.JLabel();
        headersValueLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Request Field Data and Validations");

        jsonTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "Field", "Datatype", "Positive Data", "Negative Data", "Error Message"
            }
        ));
        jsonTable.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jsonTablePropertyChange(evt);
            }
        });
        requestBodyScrollPane.setViewportView(jsonTable);

        executeTestbtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        executeTestbtn.setText("Execute Test");
        executeTestbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                executeTestbtnActionPerformed(evt);
            }
        });

        exitBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        exitBtn.setText("Exit");
        exitBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitBtnActionPerformed(evt);
            }
        });

        otherComponentsPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        otherComponentsPanel.setForeground(new java.awt.Color(255, 255, 255));

        urlLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        urlLabel.setText("URL");

        methodLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        methodLabel.setText("Method");

        headersLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        headersLabel.setText("Headers");

        javax.swing.GroupLayout otherComponentsPanelLayout = new javax.swing.GroupLayout(otherComponentsPanel);
        otherComponentsPanel.setLayout(otherComponentsPanelLayout);
        otherComponentsPanelLayout.setHorizontalGroup(
            otherComponentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(otherComponentsPanelLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(otherComponentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(otherComponentsPanelLayout.createSequentialGroup()
                        .addComponent(headersLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(headersValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(otherComponentsPanelLayout.createSequentialGroup()
                        .addComponent(urlLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(urlValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 457, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(otherComponentsPanelLayout.createSequentialGroup()
                        .addComponent(methodLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(methodValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        otherComponentsPanelLayout.setVerticalGroup(
            otherComponentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(otherComponentsPanelLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(otherComponentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(urlLabel)
                    .addComponent(urlValueLabel))
                .addGap(18, 18, 18)
                .addGroup(otherComponentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(methodLabel)
                    .addComponent(methodValueLabel))
                .addGap(18, 18, 18)
                .addGroup(otherComponentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(headersLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(headersValueLabel))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(requestBodyScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(executeTestbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(exitBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(otherComponentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(otherComponentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(requestBodyScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(executeTestbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(exitBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void executeTestbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_executeTestbtnActionPerformed
        // TODO add your handling code here:
        Object[][] screen3TableData = null;
        System.out.println("Table row count: "+jsonTable.getRowCount());
        printTableData(jsonTable);
        
        if(jsonTable.getRowCount()== 0){
            screen3TableData = null;
        } else {
            Object[][] requestJson = extractTableData(jsonTable);
            try {
                screen3TableData = generateRequestBodies(name, jsonTable);
                System.out.println("Screen 3 row count = "+screen3TableData.length);
            } catch (Exception ex) {
                Logger.getLogger(Screen2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Screen3 screen3 = new Screen3(screen3TableData,getExtendedState());
        screen3.setVisible(true);
//        setVisible(false);
//        this.dispose();
    }//GEN-LAST:event_executeTestbtnActionPerformed

    private static String beautifyJson(JSONObject json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            Map<String, Object> map = jsonObjectToMap(json);
            return writer.writeValueAsString(map);
        } catch (Exception e) {
            return json.toString(); // Return the original string if it fails
        }
    }

    private static Map<String, Object> jsonObjectToMap(JSONObject jsonObject) {
        Map<String, Object> map = new HashMap<>();
        for (Object key : jsonObject.keySet()) {
            Object value = jsonObject.get((String) key);
            // Recursively handle nested JSONObjects
            if (value instanceof JSONObject) {
                value = jsonObjectToMap((JSONObject) value);
            } else if (value instanceof JSONArray) {
                value = jsonArrayToList((JSONArray) value);
            }
            map.put((String) key, value);
        }
        return map;
    }

    private static List<Object> jsonArrayToList(JSONArray jsonArray) {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);
            // Recursively handle nested JSONObjects
            if (value instanceof JSONObject) {
                value = jsonObjectToMap((JSONObject) value);
            } else if (value instanceof JSONArray) {
                value = jsonArrayToList((JSONArray) value);
            }
            list.add(value);
        }
        return list;
    }

     public static Object[][] generateRequestBodies(String name, JTable table) {
        List<Object[]> requestBodies = new ArrayList<>();

        // Step 1: Extract fields and their values from the JTable
        JSONObject defaultRequestBody = new JSONObject();
        List<String> fieldsWithMultipleValues = new ArrayList<>();
        List<List<Object>> valueCombinations = new ArrayList<>();

        int rowCount = table.getRowCount();

        for (int row = 0; row < rowCount; row++) {
            String fieldName = table.getValueAt(row, 0).toString(); // Field name
            String dataType = table.getValueAt(row, 1).toString(); // Data type (String, Integer, Boolean)
            String valuesStr = table.getValueAt(row, 2).toString(); // Positive values (comma-separated)

            String[] values = valuesStr.split(",");
            List<Object> parsedValues = new ArrayList<>();
            for (String value : values) {
                parsedValues.add(parseValue(dataType, value.trim()));
            }

            // Add default value (first value in the list) to the request body
            defaultRequestBody.put(fieldName, parsedValues.get(0));

            // Track fields with multiple values for generating combinations
            if (parsedValues.size() > 1) {
                fieldsWithMultipleValues.add(fieldName);
                valueCombinations.add(parsedValues);
            }
        }

        // Step 2: Generate all combinations of values for fields with multiple values
        if (fieldsWithMultipleValues.isEmpty()) {
            // If no field has multiple values, generate a single request body
            String testName = "Verify " + name + " with all default values";
            requestBodies.add(new Object[]{testName, defaultRequestBody.toString()});
        } else {
            generateCombinations(fieldsWithMultipleValues, valueCombinations, defaultRequestBody, name, requestBodies);
        }

        // Convert the list to an array and return
        return requestBodies.toArray(new Object[0][]);
    }

    // Helper method to parse values based on data type
    private static Object parseValue(String dataType, String value) {
        switch (dataType.toLowerCase()) {
            case "integer":
                return Integer.parseInt(value);
            case "boolean":
                return Boolean.parseBoolean(value);
            default:
                return value; // Default is String
        }
    }

    // Helper method to generate combinations
    private static void generateCombinations(List<String> fields, List<List<Object>> valueCombinations,
                                             JSONObject baseRequestBody, String name, List<Object[]> requestBodies) {
        int[] indices = new int[fields.size()];
        int totalCombinations = 1;

        for (List<Object> values : valueCombinations) {
            totalCombinations *= values.size();
        }

        for (int i = 0; i < totalCombinations; i++) {
            JSONObject requestBody = new JSONObject(baseRequestBody.toString());

            for (int fieldIndex = 0; fieldIndex < fields.size(); fieldIndex++) {
                String fieldName = fields.get(fieldIndex);
                Object value = valueCombinations.get(fieldIndex).get(indices[fieldIndex]);
                requestBody.put(fieldName, value);
            }

            String testName = "Verify " + name;
            for (int fieldIndex = 0; fieldIndex < fields.size(); fieldIndex++) {
                String fieldName = fields.get(fieldIndex);
                Object value = valueCombinations.get(fieldIndex).get(indices[fieldIndex]);
                testName += String.format(" with %s value %s", fieldName, value);
            }

            requestBodies.add(new Object[]{testName, requestBody.toString()});

            // Update indices for the next combination
            for (int j = fields.size() - 1; j >= 0; j--) {
                if (indices[j] < valueCombinations.get(j).size() - 1) {
                    indices[j]++;
                    break;
                } else {
                    indices[j] = 0;
                }
            }
        }
    }

    public static void printTableData(JTable table) {
        // Get the TableModel (this is where the data is stored)
        TableModel model = table.getModel();

        // Get the number of rows and columns
        int rowCount = model.getRowCount();
        int columnCount = model.getColumnCount();

        // Loop through the rows and columns to print the data
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                // Get the value from the model
                Object value = model.getValueAt(row, col);
                System.out.print(value + "\t");  // Print the value in the table
            }
            System.out.println();  // Move to the next line after each row
        }
    }
    
    private void jsonTablePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jsonTablePropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_jsonTablePropertyChange

    private void exitBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitBtnActionPerformed
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(null,"Are you sure want to exit?");
        if(confirm==0){
            exit(0);
        }
    }//GEN-LAST:event_exitBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        Object[][] tableData = {
            {"Field1", "String", "123", "", ""},
            {"Field2", "String", "abc", "", ""},
            {"Field3", "String", "", "", ""}
        };
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Screen2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Screen2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Screen2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Screen2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Screen2().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton executeTestbtn;
    private javax.swing.JButton exitBtn;
    private javax.swing.JLabel headersLabel;
    private javax.swing.JLabel headersValueLabel;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTable jsonTable;
    private javax.swing.JLabel methodLabel;
    private javax.swing.JLabel methodValueLabel;
    private javax.swing.JPanel otherComponentsPanel;
    private javax.swing.JScrollPane requestBodyScrollPane;
    private javax.swing.JLabel urlLabel;
    private javax.swing.JLabel urlValueLabel;
    // End of variables declaration//GEN-END:variables

}
