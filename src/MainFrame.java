import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

// The main window: subject table, input form, buttons, what-if box and summary
public class MainFrame extends JFrame {
    AttendanceManager manager = new AttendanceManager();
    DefaultTableModel model;
    JTable table;

    JTextField nameField = new JTextField();
    JComboBox<String> typeBox = new JComboBox<>(new String[]{"Theory", "Lab"});
    JTextField attendedField = new JTextField();
    JTextField totalField = new JTextField();
    JTextField remainingField = new JTextField();
    JTextField skipField = new JTextField(5);

    JLabel detailLabel = new JLabel("Select a subject to see its predictions");
    JLabel whatIfLabel = new JLabel(" ");
    JLabel summaryLabel = new JLabel(" ");

    MainFrame() {
        super("Attendance Predictor - How Many Classes Can I Miss?");
        manager.setSubjects(FileManager.load());

        // ----- table -----
        String[] cols = {"Subject", "Type", "Attended", "Total", "Attendance %", "Can miss now", "Status"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> showDetails());

        // colour the Status column: green / yellow / red
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String s = String.valueOf(v);
                comp.setForeground(Color.BLACK);
                if (s.equals("SAFE")) comp.setBackground(new Color(144, 238, 144));
                else if (s.equals("WARNING")) comp.setBackground(new Color(255, 235, 130));
                else comp.setBackground(new Color(255, 160, 160));
                setHorizontalAlignment(CENTER);
                return comp;
            }
        });

        // ----- input form (right side) -----
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 8));
        form.add(new JLabel("Subject name:"));      form.add(nameField);
        form.add(new JLabel("Type:"));              form.add(typeBox);
        form.add(new JLabel("Classes attended:"));  form.add(attendedField);
        form.add(new JLabel("Total classes held:")); form.add(totalField);
        form.add(new JLabel("Classes remaining:")); form.add(remainingField);

        JButton addBtn = new JButton("Add Subject");
        JButton presentBtn = new JButton("Mark Present");
        JButton absentBtn = new JButton("Mark Absent");
        JButton deleteBtn = new JButton("Delete");
        addBtn.addActionListener(e -> addSubject());
        presentBtn.addActionListener(e -> mark(true));
        absentBtn.addActionListener(e -> mark(false));
        deleteBtn.addActionListener(e -> deleteSubject());

        JPanel buttons = new JPanel(new GridLayout(2, 2, 5, 5));
        buttons.add(addBtn);
        buttons.add(deleteBtn);
        buttons.add(presentBtn);
        buttons.add(absentBtn);

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.add(form, BorderLayout.NORTH);
        top.add(buttons, BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout());
        right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        right.setPreferredSize(new Dimension(300, 0));
        right.add(top, BorderLayout.NORTH);

        // ----- bottom: what-if, details, summary -----
        JButton simBtn = new JButton("Simulate");
        simBtn.addActionListener(e -> whatIf());
        JPanel whatIfPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        whatIfPanel.add(new JLabel("What if I skip the next"));
        whatIfPanel.add(skipField);
        whatIfPanel.add(new JLabel("classes?"));
        whatIfPanel.add(simBtn);
        whatIfPanel.add(whatIfLabel);

        detailLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        detailLabel.setPreferredSize(new Dimension(0, 95));
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 8, 10));
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createTitledBorder("Prediction"));
        bottom.add(whatIfPanel, BorderLayout.NORTH);
        bottom.add(detailLabel, BorderLayout.CENTER);
        bottom.add(summaryLabel, BorderLayout.SOUTH);

        // ----- put everything in the window -----
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(right, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        refreshTable();
        setSize(980, 560);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Reloads the table from the manager
    void refreshTable() {
        int selected = table.getSelectedRow();
        model.setRowCount(0);
        for (Subject s : manager.getSubjects()) {
            model.addRow(new Object[]{
                    s.getName(), s.getType(), s.getAttended(), s.getTotal(),
                    String.format("%.1f", s.getPercentage()), s.safeAbsencesNow(), s.getStatus()
            });
        }
        if (selected >= 0 && selected < model.getRowCount()) {
            table.setRowSelectionInterval(selected, selected);
        }
        updateSummary();
        showDetails();
    }

    void updateSummary() {
        if (manager.getSubjects().isEmpty()) {
            summaryLabel.setText("No subjects added yet");
            return;
        }
        Subject w = manager.getWeakest();
        summaryLabel.setText(String.format("Overall attendance: %.1f%%    |    Weakest subject: %s (%.1f%%)",
                manager.getOverallPercentage(), w.getName(), w.getPercentage()));
    }

    // Shows the predictions for the selected subject
    void showDetails() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= manager.getSubjects().size()) {
            detailLabel.setText("Select a subject to see its predictions");
            return;
        }
        Subject s = manager.get(row);
        String text = "<html><b>" + s.getName() + "</b> (" + s.getType() + ", needs "
                + (int) s.getRequiredPercent() + "%)<br>";
        text += String.format("Current attendance: %.1f%%<br>", s.getPercentage());
        text += "Classes you can miss right now: " + s.safeAbsencesNow() + "<br>";
        if (s.getRemaining() == 0) {
            text += "No remaining classes entered.";
        } else if (s.canStillMiss() < 0) {
            text += "Target not possible this semester, even if you attend all "
                    + s.getRemaining() + " remaining classes.";
        } else {
            text += "Of the remaining " + s.getRemaining() + " classes: attend at least "
                    + s.mustAttendOfRemaining() + ", you can miss " + s.canStillMiss() + ".";
        }
        if (s.classesInARowNeeded() > 0) {
            text += "<br>Attend " + s.classesInARowNeeded() + " classes in a row to reach "
                    + (int) s.getRequiredPercent() + "%.";
        }
        detailLabel.setText(text + "</html>");
    }

    // Reads the form, checks it and adds a subject
    void addSubject() {
        String name = nameField.getText().trim().replace(",", " ");
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a subject name");
            return;
        }
        try {
            int a = Integer.parseInt(attendedField.getText().trim());
            int t = Integer.parseInt(totalField.getText().trim());
            int r = Integer.parseInt(remainingField.getText().trim());
            if (a < 0 || t < 1 || r < 0) {
                JOptionPane.showMessageDialog(this, "Numbers cannot be negative, and total must be at least 1");
                return;
            }
            if (a > t) {
                JOptionPane.showMessageDialog(this, "Attended classes cannot be more than total classes");
                return;
            }
            if (typeBox.getSelectedItem().equals("Lab")) manager.add(new LabSubject(name, a, t, r));
            else manager.add(new Subject(name, a, t, r));

            FileManager.save(manager.getSubjects());
            nameField.setText("");
            attendedField.setText("");
            totalField.setText("");
            remainingField.setText("");
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter whole numbers in the number boxes");
        }
    }

    // Marks the selected subject present or absent for one class
    void mark(boolean present) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a subject in the table first");
            return;
        }
        if (present) manager.get(row).markPresent();
        else manager.get(row).markAbsent();
        FileManager.save(manager.getSubjects());
        refreshTable();
    }

    void deleteSubject() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a subject in the table first");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Delete " + manager.get(row).getName() + "?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            manager.remove(row);
            FileManager.save(manager.getSubjects());
            refreshTable();
        }
    }

    // What-if simulator: shows the new % if the next n classes are skipped
    void whatIf() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a subject in the table first");
            return;
        }
        try {
            int n = Integer.parseInt(skipField.getText().trim());
            if (n < 0) {
                JOptionPane.showMessageDialog(this, "Enter 0 or more classes");
                return;
            }
            Subject s = manager.get(row);
            double p = s.whatIfSkip(n);
            String verdict = p >= s.getRequiredPercent() ? "still OK" : "BELOW the required %";
            whatIfLabel.setText(String.format("  New attendance: %.1f%% (%s)", p, verdict));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a whole number");
        }
    }
}
