import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

class Medicine {
    String name, type;
    double price; int stock;

    Medicine(String name, String type, double price, int stock) {
        this.name = name; this.type = type; this.price = price; this.stock = stock;
    }

    @Override
    public String toString() {
        return "Medicine{name='" + name + "', type='" + type + "', price=" + price + ", stock=" + stock + "}";
    }
}

class Folder {
    String name;
    Map<String, Folder> subfolders = new HashMap<>();
    Map<String, Medicine> medicines = new HashMap<>();

    Folder(String name) { this.name = name; }

    void addSubfolder(String folderName) { subfolders.put(folderName, new Folder(folderName)); }
    void addMedicine(Medicine medicine) { medicines.put(medicine.name, medicine); }
    boolean removeMedicine(String name) { return medicines.remove(name) != null; }
    Medicine searchMedicine(String name) { return medicines.get(name); }
    String listMedicines() { return medicines.isEmpty() ? "No medicines.\n" : medicines.values().toString() + "\n"; }
    String listSubfolders() { return subfolders.isEmpty() ? "No subfolders.\n" : subfolders.keySet().toString() + "\n"; }
    Folder getSubfolder(String name) { return subfolders.get(name); }
}

public class SimpleFileSystemGUI extends JFrame {
    private Folder root = new Folder("root");
    private JTextArea outputArea = new JTextArea();
    private JTextField folderField = new JTextField(10);
    private JComboBox<String> folderComboBox = new JComboBox<>(new String[]{"Select Folder"});

    public SimpleFileSystemGUI() {
        setTitle("Simple File System for Medicine Management"); setSize(600, 400); setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); gbc.anchor = GridBagConstraints.CENTER;

        outputArea.setEditable(false); outputArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(outputArea); scrollPane.setPreferredSize(new Dimension(580, 150));

        JButton addFolderButton = new JButton("Add Folder"), switchFolderButton = new JButton("Manage Medicines");

        gbc.gridx = 0; gbc.gridy = 0; contentPanel.add(new JLabel("Folder:"), gbc);
        gbc.gridx = 1; contentPanel.add(folderField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; contentPanel.add(addFolderButton, gbc);
        gbc.gridx = 1; contentPanel.add(folderComboBox, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; contentPanel.add(scrollPane, gbc);
        gbc.gridy = 3; contentPanel.add(switchFolderButton, gbc);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), 
                        "Simple File System for Medicine Management", TitledBorder.CENTER, TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 16)));
        titlePanel.add(contentPanel, BorderLayout.CENTER);
        add(titlePanel); setLocationRelativeTo(null); setVisible(true);

        addFolderButton.addActionListener(e -> {
            String folderName = folderField.getText();
            if (!folderName.isEmpty()) {
                root.addSubfolder(folderName); folderComboBox.addItem(folderName);
                outputArea.append("Added folder: " + folderName + "\nTotal folders: " + root.subfolders.size() + "\n");
                folderField.setText("");
            } else outputArea.append("Folder name cannot be empty.\n");
        });

        switchFolderButton.addActionListener(e -> {
            String folder = (String) folderComboBox.getSelectedItem();
            if (folder != null && !folder.equals("Select Folder")) openAddMedicineWindow(folder);
            else outputArea.append("Please select a valid folder.\n");
        });
    }

    private void openAddMedicineWindow(String folderName) {
        JFrame addMedicineFrame = new JFrame("Manage Medicines in " + folderName);
        addMedicineFrame.setSize(400, 300); addMedicineFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addMedicineFrame.setLocationRelativeTo(null); addMedicineFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(5, 5, 5, 5);

        JTextField medicineField = new JTextField(10), typeField = new JTextField(10), priceField = new JTextField(10), stockField = new JTextField(10);
        gbc.gridx = 0; gbc.gridy = 0; addMedicineFrame.add(new JLabel("Medicine Name:"), gbc); gbc.gridx = 1; addMedicineFrame.add(medicineField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; addMedicineFrame.add(new JLabel("Type:"), gbc); gbc.gridx = 1; addMedicineFrame.add(typeField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; addMedicineFrame.add(new JLabel("Price:"), gbc); gbc.gridx = 1; addMedicineFrame.add(priceField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; addMedicineFrame.add(new JLabel("Stock:"), gbc); gbc.gridx = 1; addMedicineFrame.add(stockField, gbc);

        JButton addMedicineButton = new JButton("Add Medicine"), removeMedicineButton = new JButton("Remove Medicine"), searchMedicineButton = new JButton("Search Medicine");

        gbc.gridx = 0; gbc.gridy = 4; addMedicineFrame.add(addMedicineButton, gbc); gbc.gridx = 1; addMedicineFrame.add(removeMedicineButton, gbc);
        gbc.gridx = 0; gbc.gridy = 5; addMedicineFrame.add(searchMedicineButton, gbc);

        addMedicineButton.addActionListener(e -> addMedicine(folderName, medicineField.getText(), typeField.getText(), priceField.getText(), stockField.getText()));
        removeMedicineButton.addActionListener(e -> removeMedicine(folderName, JOptionPane.showInputDialog(addMedicineFrame, "Enter name of medicine to remove:")));
        searchMedicineButton.addActionListener(e -> searchMedicine(folderName, JOptionPane.showInputDialog(addMedicineFrame, "Enter name of medicine to search:")));

        addMedicineFrame.setVisible(true);
    }

    private void addMedicine(String folderName, String name, String type, String priceStr, String stockStr) {
        Folder folder = root.getSubfolder(folderName);
        if (folder != null) try {
            double price = Double.parseDouble(priceStr); int stock = Integer.parseInt(stockStr);
            Medicine med = new Medicine(name, type, price, stock); folder.addMedicine(med);
            outputArea.append("Added medicine: " + med + "\n");
        } catch (NumberFormatException e) { outputArea.append("Invalid price or stock.\n"); }
    }

    private void removeMedicine(String folderName, String name) {
        Folder folder = root.getSubfolder(folderName);
        if (folder != null && folder.removeMedicine(name)) outputArea.append("Removed medicine: " + name + "\n");
        else outputArea.append("Medicine not found: " + name + "\n");
    }

    private void searchMedicine(String folderName, String name) {
        Folder folder = root.getSubfolder(folderName);
        Medicine med = (folder != null) ? folder.searchMedicine(name) : null;
        outputArea.append((med != null) ? "Found medicine: " + med + "\n" : "Medicine not found: " + name + "\n");
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(SimpleFileSystemGUI::new); }
}
