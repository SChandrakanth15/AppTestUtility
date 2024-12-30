/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package DummyP;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.json.JSONArray;
import org.json.JSONException;
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
        setupFrame();
    }

    public Screen2(Object[][] jsonRequestBodyTableData, String baseUrl, String method, String path, String name, DefaultTableModel headersTableModel) {
        this.name = name;
        initComponents();
        setupFrame();
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
                    java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class
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
        }
        urlTextField.setText(baseUrl);
        methodTextField.setText(method.toUpperCase());
        //  to display the Headers in Screen 2.
        if (headersTableModel.getRowCount() != 0) {
            StringBuilder headersText = new StringBuilder();
            for (int i = 0; i < headersTableModel.getRowCount(); i++) {
                String headerName = (String) headersTableModel.getValueAt(i, 0);
                String headerValue = (String) headersTableModel.getValueAt(i, 1);
                headersText.append(headerName).append(": ").append(headerValue).append("\n");
            }
            headersTextArea.setText(headersText.toString());
        } else {
            headersTextArea.setText("Headers not available");
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
    private void setupFrame() {
        setResizable(true);
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        urlTextField = new javax.swing.JTextField();
        methodTextField = new javax.swing.JTextField();
        headersScrollPane = new javax.swing.JScrollPane();
        headersTextArea = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        tableScrollPane = new javax.swing.JScrollPane();
        jsonTable = new javax.swing.JTable();
        executeTestbtn = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("URL");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Method");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Headers");

        urlTextField.setText("URL");

        methodTextField.setText("Method");
        methodTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                methodTextFieldActionPerformed(evt);
            }
        });

        headersTextArea.setEditable(false);
        headersTextArea.setColumns(20);
        headersTextArea.setRows(5);
        headersScrollPane.setViewportView(headersTextArea);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Request Field Data and Validations");

        jsonTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
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
        tableScrollPane.setViewportView(jsonTable);

        executeTestbtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        executeTestbtn.setText("Execute Test");
        executeTestbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                executeTestbtnActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setText("Exit");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(23, 23, 23)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(urlTextField)
                            .addComponent(methodTextField)
                            .addComponent(headersScrollPane)))
                    .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(executeTestbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(urlTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(methodTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(headersScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(executeTestbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void methodTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_methodTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_methodTextFieldActionPerformed

    private void executeTestbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_executeTestbtnActionPerformed
        // TODO add your handling code here:
        Object[][] requestJson = extractTableData(jsonTable);
        JSONObject requestBodyJsonObject = convertTableToJson(requestJson);
        System.out.println(requestBodyJsonObject);
        System.out.println("the length of the json is : " + requestJson.length);

        Object[][] screen3TableData = new Object[requestJson.length][6];
        for (int i = 0; i < requestJson.length; i++) {
            String fieldName = (String) requestJson[i][0]; // Assuming [0] is always a String
            Object testDataObject = requestJson[i][2]; // Positive data
            String testData = (testDataObject != null) ? testDataObject.toString() : "";
            String testName = String.format("Verify %s with %s value %s", name, fieldName, testData);

            screen3TableData[i][0] = i + 1; // SL (serial number)
            screen3TableData[i][1] = testName; // Test Name
            screen3TableData[i][2] = requestBodyJsonObject;// Req Body (entire JSON)
            screen3TableData[i][3] = ""; // Res Code (to be filled in Screen3)
            screen3TableData[i][4] = ""; // Res Body (to be filled in Screen3)
            screen3TableData[i][5] = ""; // Test Result (to be filled in Screen3)
        }

        Screen3 screen3 = new Screen3(screen3TableData);
        screen3.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_executeTestbtnActionPerformed

    private void jsonTablePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jsonTablePropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_jsonTablePropertyChange

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
            new Screen2(tableData, "http://example.com", "GET", "/path", "name", null).setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton executeTestbtn;
    private javax.swing.JScrollPane headersScrollPane;
    private javax.swing.JTextArea headersTextArea;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTable jsonTable;
    private javax.swing.JTextField methodTextField;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JTextField urlTextField;
    // End of variables declaration//GEN-END:variables

}
