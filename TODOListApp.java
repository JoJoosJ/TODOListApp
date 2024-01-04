import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.sql.SQLException;
import java.util.List;

public class TODOListApp extends JFrame {
    private DefaultListModel<String> plannedListModel;
    private DefaultListModel<String> inProgressListModel;
    private DefaultListModel<String> doneListModel;
    Database db = new Database();

    public TODOListApp() {
        setTitle("TODOListApp");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        plannedListModel = new DefaultListModel<>();
        inProgressListModel = new DefaultListModel<>();
        doneListModel = new DefaultListModel<>();

        JList<String> plannedList = createList(plannedListModel);
        JList<String> inProgressList = createList(inProgressListModel);
        JList<String> doneList = createList(doneListModel);

        JButton addButton = new JButton("Add Card");
        JButton removeButton = new JButton("Remove Card");
        JButton ratingButton = new JButton(("Change Rating"));
        JButton showArchive = new JButton((("show Archive")));
        JButton showCurrent = new JButton((("show Current")));

        addButton.addActionListener(e -> addCard());
        removeButton.addActionListener(e -> {
            try {
                removeCard();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        ratingButton.addActionListener(e -> changeRating());
        showArchive.addActionListener(e -> showArchive());
        showCurrent.addActionListener(e -> updateButton());

        setLayout(new BorderLayout());

        JPanel buttonPanel = createButtonPanel(addButton, removeButton, ratingButton, showArchive, showCurrent);
        add(buttonPanel, BorderLayout.PAGE_START);


        JPanel centerPanel = new JPanel(new GridLayout(1, 3));
        centerPanel.add(createPanel("Planned", plannedList));
        centerPanel.add(createPanel("In Progress", inProgressList));
        centerPanel.add(createPanel("Done", doneList));
        add(centerPanel, BorderLayout.CENTER);
        updateButton();
    }

    private JPanel createPanel(String title, JList<String> list) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(new JScrollPane(list));
        return panel;
    }
    private JPanel createButtonPanel(JButton addButton, JButton removeButton, JButton ratingButton, JButton showArchive, JButton showCurrent) {
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(addButton);
        panel.add(removeButton);
        panel.add(ratingButton);
        panel.add(showArchive);
        panel.add(showCurrent);
        return panel;
    }
    private void addCard() {

        db.MSSQLconnect();

        String cardName = JOptionPane.showInputDialog(this, "Enter card name:");

        if (cardName != null && !cardName.isEmpty()) {
            plannedListModel.addElement(cardName);
            db.addEvent(cardName);
        } else {
            JOptionPane.showMessageDialog(this, "Card name is required.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        updateButton();
    }

    private void removeCard() throws SQLException {
        db.MSSQLconnect();
        String cardName = JOptionPane.showInputDialog(this, "Enter ID");

        if (cardName != null && !cardName.isEmpty()) {
            plannedListModel.removeElement(cardName);
            db.deleteCard(cardName);
        } else {
            JOptionPane.showMessageDialog(this, "ID is required.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        updateButton();
    }

    private void changeRating() {
        db.MSSQLconnect();
        String cardID = JOptionPane.showInputDialog(this, "Enter ID");
        String cardRating = JOptionPane.showInputDialog(this, "Enter Rating");

        if (cardID != null && !cardID.isEmpty() && cardRating != null && !cardRating.isEmpty()) {
            db.changeRating(cardID, cardRating);

        }else {
            JOptionPane.showMessageDialog(this, "ID and Rating are required", "Error", JOptionPane.ERROR_MESSAGE);
        }
        updateButton();
    }
    public void showArchive() {
        db.MSSQLconnect();

        String startDate = JOptionPane.showInputDialog(this, "Enter start Date in Form : <YYYY-MM-DD>","YYYY-MM-DD");

        plannedListModel.clear();
        inProgressListModel.clear();
        doneListModel.clear();

        //------------Schwierigkeiten-------------------------------
        java.sql.Date sqlDate = java.sql.Date.valueOf(startDate);
        List<String> archive = db.getCardsFromArchive(sqlDate);

        for (int i = 0; i < archive.size() -1; i++) {
            if (archive.get(i).equals("Planned")) {
                plannedListModel.addElement((String) archive.get(i +1));
            }
            if (archive.get(i).equals("InProgress")) {
                inProgressListModel.addElement((String) archive.get(i +1));
            }
            if (archive.get(i).equals("Done")) {
                doneListModel.addElement((String) archive.get(i +1));
            }
        }
    }

    private void updateButton() {

        plannedListModel.clear();
        inProgressListModel.clear();
        doneListModel.clear();

        db.MSSQLconnect();
        List<String> cardsPlanned = db.getCardsfromMSSQL("Planned");
        List<String> cardsInProgress = db.getCardsfromMSSQL("InProgress");
        List<String> cardsDone = db.getCardsfromMSSQL("Done");

        for (int i = 0; i <= cardsPlanned.size() -1; i++) {
            plannedListModel.addElement((String) cardsPlanned.get(i));
        }
        for (int i = 0; i <= cardsInProgress.size() -1; i++) {
            inProgressListModel.addElement((String) cardsInProgress.get(i));
        }
        for (int i = 0; i <= cardsDone.size() -1; i++) {
            doneListModel.addElement((String) cardsDone.get(i));
        }
    }


    private JList<String> createList(DefaultListModel<String> model) {
        JList<String> list = new JList<>(model);
        list.setDragEnabled(true);

        list.setTransferHandler(new TransferHandler() {
            @Override
            public int getSourceActions(JComponent c) {
                return TransferHandler.MOVE;
            }

            @Override
            protected void exportDone(JComponent source, Transferable data, int action) {
                if (action == TransferHandler.MOVE) {
                    JList<String> list = (JList<String>) source;
                    DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
                    int index = list.getSelectedIndex();
                    if (index != -1) {
                        model.remove(index);
                    }
                }
            }
        });

        return list;
    }

    private JList<String> getSelectedList() {
        Component[] components = getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JList) {
                return (JList<String>) component;
            }
        }
        JOptionPane.showMessageDialog(this, "Please select a list.", "Error", JOptionPane.ERROR_MESSAGE);
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TODOListApp().setVisible(true));
    }
}