import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class StudentManagementSystem extends JFrame {
    // List to store all students
    private ArrayList<Student> students = new ArrayList<>();
    // Table model and sorter for displaying students
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTable studentTable;
    // Input fields for student details
    private JTextField nameField, rollField, idField, deptField, cgpaField, emailField;
    // Label to show status messages
    private JLabel statusLabel;
    // Timer for clearing status messages
    private Timer statusTimer;

    // Constructor to set up the GUI
    public StudentManagementSystem() {
        // Set window title with my name
        setTitle("Student Management System - Mohammad Arfat Karim");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with a modern background color
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 248, 255)); // Alice Blue

        // Developer info panel with styled background
        JPanel devPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        devPanel.setBackground(new Color(70, 130, 180)); // Steel Blue
        devPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Developer details label
        JLabel devLabel = new JLabel("Developed by: Mohammad Arfat Karim, Dept: CST-5, Email: arafatkarim37@gmail.com");
        devLabel.setForeground(Color.WHITE);
        devLabel.setFont(new Font("Arial", Font.BOLD, 14));
        devPanel.add(devLabel);

        // Links panel
        JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        linksPanel.setBackground(new Color(70, 130, 180));

        // GitHub link
        JLabel githubLink = createStyledLinkLabel("GitHub", "https://github.com/Arfatkarim");
        linksPanel.add(githubLink);

        // LinkedIn link
        JLabel linkedinLink = createStyledLinkLabel("LinkedIn", "https://www.linkedin.com/in/mohammad-arafat-karim-004a5129b");
        linksPanel.add(linkedinLink);

        // Portfolio link
        JLabel portfolioLink = createStyledLinkLabel("Portfolio", "https://arfatkarim.netlify.app/");
        linksPanel.add(portfolioLink);

        devPanel.add(linksPanel);
        mainPanel.add(devPanel, BorderLayout.NORTH);

        // Input panel for entering student details
        JPanel inputPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180)), 
            "Enter Student Details", 
            0, 0, new Font("Arial", Font.BOLD, 14), 
            new Color(70, 130, 180)
        ));
        inputPanel.setBackground(Color.WHITE);

        // Add labels and text fields with styling
        Font labelFont = new Font("Arial", Font.PLAIN, 12);
        Font fieldFont = new Font("Arial", Font.PLAIN, 12);

        inputPanel.add(createStyledLabel("Name:", labelFont));
        nameField = createStyledTextField(fieldFont);
        inputPanel.add(nameField);

        inputPanel.add(createStyledLabel("Roll:", labelFont));
        rollField = createStyledTextField(fieldFont);
        inputPanel.add(rollField);

        inputPanel.add(createStyledLabel("ID No:", labelFont));
        idField = createStyledTextField(fieldFont);
        inputPanel.add(idField);

        inputPanel.add(createStyledLabel("Department:", labelFont));
        deptField = createStyledTextField(fieldFont);
        inputPanel.add(deptField);

        inputPanel.add(createStyledLabel("CGPA:", labelFont));
        cgpaField = createStyledTextField(fieldFont);
        inputPanel.add(cgpaField);

        inputPanel.add(createStyledLabel("Email:", labelFont));
        emailField = createStyledTextField(fieldFont);
        inputPanel.add(emailField);

        // Buttons for actions
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = createStyledButton("Add Student");
        JButton updateButton = createStyledButton("Update Student");
        JButton removeButton = createStyledButton("Remove Student");
        JButton clearButton = createStyledButton("Clear Fields");
        JButton sortButton = createStyledButton("Sort by Name");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(sortButton);

        inputPanel.add(new JLabel(""));
        inputPanel.add(buttonPanel);
        mainPanel.add(inputPanel, BorderLayout.WEST);

        // Table to display list of students
        String[] columns = {"Name", "Roll", "ID No", "Department", "CGPA", "Email"};
        tableModel = new DefaultTableModel(columns, 0);
        studentTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                // Alternate row colors for readability
                c.setBackground(row % 2 == 0 ? new Color(245, 245, 245) : Color.WHITE);
                return c;
            }
        };
        studentTable.setFont(new Font("Arial", Font.PLAIN, 12));
        studentTable.setRowHeight(25);
        studentTable.setGridColor(new Color(200, 200, 200));

        // Add sorter to table
        sorter = new TableRowSorter<>(tableModel);
        studentTable.setRowSorter(sorter);

        JScrollPane tableScrollPane = new JScrollPane(studentTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180)), 
            "Student List", 
            0, 0, new Font("Arial", Font.BOLD, 14), 
            new Color(70, 130, 180)
        ));
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Status label for messages
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(70, 130, 180));
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        // Initialize status timer
        statusTimer = new Timer();

        // Button action listeners
        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        removeButton.addActionListener(e -> removeStudent());
        clearButton.addActionListener(e -> clearFields());
        sortButton.addActionListener(e -> sortTableByName());

        // Table row selection listener to view/edit student details
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow >= 0) {
                // Convert view index to model index due to sorting
                int modelRow = studentTable.convertRowIndexToModel(selectedRow);
                // Fill input fields with selected student's details
                nameField.setText((String) tableModel.getValueAt(modelRow, 0));
                rollField.setText((String) tableModel.getValueAt(modelRow, 1));
                idField.setText((String) tableModel.getValueAt(modelRow, 2));
                deptField.setText((String) tableModel.getValueAt(modelRow, 3));
                cgpaField.setText((String) tableModel.getValueAt(modelRow, 4));
                emailField.setText((String) tableModel.getValueAt(modelRow, 5));
                setStatusMessage("Selected student. Edit and click 'Update Student' or 'Remove Student'.");
            }
        });

        add(mainPanel);
        setVisible(true);
    }

    // Helper method to create styled labels
    private JLabel createStyledLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(new Color(70, 130, 180));
        return label;
    }

    // Helper method to create styled text fields
    private JTextField createStyledTextField(Font font) {
        JTextField field = new JTextField();
        field.setFont(font);
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        return field;
    }

    // Helper method to create styled buttons
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(100, 149, 237)); // Cornflower Blue
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
        return button;
    }

    // Helper method to create styled clickable link labels
    private JLabel createStyledLinkLabel(String text, String url) {
        JLabel linkLabel = new JLabel(text);
        linkLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        linkLabel.setForeground(new Color(255, 215, 0)); // Gold for links
        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Hover effect
        linkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                linkLabel.setForeground(new Color(255, 255, 0)); // Yellow on hover
            }
            @Override
            public void mouseExited(MouseEvent e) {
                linkLabel.setForeground(new Color(255, 215, 0));
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                    setStatusMessage("Opened " + text + " link.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(StudentManagementSystem.this, 
                        "Could not open " + text + " link.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        return linkLabel;
    }

    // Helper method to set status message with timeout
    private void setStatusMessage(String message) {
        statusLabel.setText(message);
        // Cancel any existing timer
        statusTimer.cancel();
        statusTimer = new Timer();
        // Schedule timer to clear message after 3 seconds
        statusTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> statusLabel.setText("Ready"));
            }
        }, 3000);
    }

    // Helper method to sort table by name
    private void sortTableByName() {
        sorter.setSortKeys(java.util.Arrays.asList(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
        setStatusMessage("Table sorted by name.");
    }

    // Student class to store student details
    private class Student {
        String name, roll, id, dept, cgpa, email;

        Student(String name, String roll, String id, String dept, String cgpa, String email) {
            this.name = name;
            this.roll = roll;
            this.id = id;
            this.dept = dept;
            this.cgpa = cgpa;
            this.email = email;
        }
    }

    // Add a new student
    private void addStudent() {
        try {
            // Get input values
            String name = nameField.getText().trim();
            String roll = rollField.getText().trim();
            String id = idField.getText().trim();
            String dept = deptField.getText().trim();
            String cgpa = cgpaField.getText().trim();
            String email = emailField.getText().trim();

            // Check if any field is empty
            if (name.isEmpty() || roll.isEmpty() || id.isEmpty() || dept.isEmpty() || cgpa.isEmpty() || email.isEmpty()) {
                setStatusMessage("Error: Please fill all fields!");
                return;
            }

            // Validate CGPA
            double cgpaValue = Double.parseDouble(cgpa);
            if (cgpaValue < 0 || cgpaValue > 4.0) {
                setStatusMessage("Error: CGPA must be between 0 and 4.0!");
                return;
            }

            // Validate email (basic check)
            if (!email.contains("@") || !email.contains(".")) {
                setStatusMessage("Error: Invalid email format!");
                return;
            }

            // Check for duplicate roll or ID
            for (Student s : students) {
                if (s.roll.equals(roll) || s.id.equals(id)) {
                    setStatusMessage("Error: Roll or ID already exists!");
                    return;
                }
            }

            // Create and add new student
            Student student = new Student(name, roll, id, dept, cgpa, email);
            students.add(student);
            tableModel.addRow(new String[]{name, roll, id, dept, cgpa, email});
            clearFields();
            setStatusMessage("Student added successfully! Total students: " + students.size());
        } catch (NumberFormatException e) {
            setStatusMessage("Error: Invalid CGPA format!");
        } catch (Exception e) {
            setStatusMessage("Error: Something went wrong!");
        }
    }

    // Update an existing student
    private void updateStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            setStatusMessage("Error: Please select a student to update!");
            return;
        }

        try {
            // Get updated values
            String name = nameField.getText().trim();
            String roll = rollField.getText().trim();
            String id = idField.getText().trim();
            String dept = deptField.getText().trim();
            String cgpa = cgpaField.getText().trim();
            String email = emailField.getText().trim();

            // Check if any field is empty
            if (name.isEmpty() || roll.isEmpty() || id.isEmpty() || dept.isEmpty() || cgpa.isEmpty() || email.isEmpty()) {
                setStatusMessage("Error: Please fill all fields!");
                return;
            }

            // Validate CGPA
            double cgpaValue = Double.parseDouble(cgpa);
            if (cgpaValue < 0 || cgpaValue > 4.0) {
                setStatusMessage("Error: CGPA must be between 0 and 4.0!");
                return;
            }

            // Validate email (basic check)
            if (!email.contains("@") || !email.contains(".")) {
                setStatusMessage("Error: Invalid email format!");
                return;
            }

            // Convert view index to model index due to sorting
            int modelRow = studentTable.convertRowIndexToModel(selectedRow);

            // Check for duplicate roll or ID (excluding current student)
            for (int i = 0; i < students.size(); i++) {
                if (i != modelRow && (students.get(i).roll.equals(roll) || students.get(i).id.equals(id))) {
                    setStatusMessage("Error: Roll or ID already exists!");
                    return;
                }
            }

            // Update student details
            Student student = students.get(modelRow);
            student.name = name;
            student.roll = roll;
            student.id = id;
            student.dept = dept;
            student.cgpa = cgpa;
            student.email = email;

            // Update table row
            tableModel.setValueAt(name, modelRow, 0);
            tableModel.setValueAt(roll, modelRow, 1);
            tableModel.setValueAt(id, modelRow, 2);
            tableModel.setValueAt(dept, modelRow, 3);
            tableModel.setValueAt(cgpa, modelRow, 4);
            tableModel.setValueAt(email, modelRow, 5);

            clearFields();
            setStatusMessage("Student updated successfully!");
        } catch (NumberFormatException e) {
            setStatusMessage("Error: Invalid CGPA format!");
        } catch (Exception e) {
            setStatusMessage("Error: Something went wrong!");
        }
    }

    // Remove a student
    private void removeStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            setStatusMessage("Error: Please select a student to remove!");
            return;
        }

        // Confirm removal
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to remove this student?", 
            "Confirm Removal", 
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = studentTable.convertRowIndexToModel(selectedRow);
            students.remove(modelRow);
            tableModel.removeRow(modelRow);
            clearFields();
            setStatusMessage("Student removed successfully! Total students: " + students.size());
        }
    }

    // Clear all input fields
    private void clearFields() {
        nameField.setText("");
        rollField.setText("");
        idField.setText("");
        deptField.setText("");
        cgpaField.setText("");
        emailField.setText("");
        studentTable.clearSelection();
        setStatusMessage("Fields cleared. Ready to add or select a student.");
    }

    // Main method to run the program
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentManagementSystem());
    }
}