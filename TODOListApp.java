import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.IOException;

public class TODOListApp extends JFrame {
    private DefaultListModel<String> plannedListModel;
    private DefaultListModel<String> inProgressListModel;
    private DefaultListModel<String> doneListModel;

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
        JButton updateButton = new JButton(("Update Planner"));

        addButton.addActionListener(e -> addCard());
        removeButton.addActionListener(e -> removeCard());
        updateButton.addActionListener(e -> updateButton());

        setLayout(new BorderLayout());

        JPanel buttonPanel = createButtonPanel(addButton, removeButton, updateButton);
        add(buttonPanel, BorderLayout.PAGE_START);

        // Create panels for the lists and add them to the CENTER region
        JPanel centerPanel = new JPanel(new GridLayout(1, 3));
        centerPanel.add(createPanel("Planned", plannedList));
        centerPanel.add(createPanel("In Progress", inProgressList));
        centerPanel.add(createPanel("Done", doneList));
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createPanel(String title, JList<String> list) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(new JScrollPane(list));
        return panel;
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
            protected Transferable createTransferable(JComponent c) {
                JList<String> source = (JList<String>) c;
                String selectedValue = source.getSelectedValue();
                return new StringTransferable(selectedValue);
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

        new DropTarget(list, new DropTargetAdapter(list));

        return list;
    }

    private JPanel createButtonPanel(JButton addButton, JButton removeButton, JButton updateButton) {
        JPanel panel = new JPanel(new FlowLayout()); // FlowLayout anstelle von GridLayout
        panel.add(addButton);
        panel.add(removeButton);
        panel.add(updateButton);

        // Setze die gewünschte Größe für die Buttons
        Dimension buttonSize = new Dimension(200, 30); // Beispielgröße, ersetze dies durch deine gewünschte Größe
        addButton.setPreferredSize(buttonSize);
        removeButton.setPreferredSize(buttonSize);
        updateButton.setPreferredSize(buttonSize);

        return panel;
    }
    private void addCard() {
        // Prompt the user for card name
        String cardName = JOptionPane.showInputDialog(this, "Enter card name:");

        // Prompt the user for ID
        String cardId = JOptionPane.showInputDialog(this, "Enter card ID:");

        // Check if both card name and ID are provided
        if (cardName != null && !cardName.isEmpty() && cardId != null && !cardId.isEmpty()) {
            // Concatenate card name and ID and add to the planned list
            String cardDetails = cardName + " (ID: " + cardId + ")";
            plannedListModel.addElement(cardDetails);
        } else {
            // Show an error message if either card name or ID is not provided
            JOptionPane.showMessageDialog(this, "Both card name and ID are required.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void removeCard() {

    }


    private void updateButton() {

    }

    private JList<String> getSelectedList() {
        Component[] components = getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JList) {
                return (JList<String>) component;
            }
        }
        // If no JList is found, show an error message or handle the situation accordingly
        JOptionPane.showMessageDialog(this, "Please select a list.", "Error", JOptionPane.ERROR_MESSAGE);
        return null;
    }

    private static class StringTransferable implements Transferable {
        private String data;

        public StringTransferable(String data) {
            this.data = data;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.stringFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(DataFlavor.stringFlavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (isDataFlavorSupported(flavor)) {
                return data;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }
    }

    private static class DropTargetAdapter extends DropTarget {
        private JList<String> targetList;

        public DropTargetAdapter(JList<String> targetList) {
            this.targetList = targetList;
        }

        @Override
        public void drop(DropTargetDropEvent dtde) {
            try {
                Transferable transferable = dtde.getTransferable();
                if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    String data = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                    DefaultListModel<String> model = (DefaultListModel<String>) targetList.getModel();
                    model.addElement(data);
                    dtde.dropComplete(true);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            dtde.rejectDrop();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TODOListApp().setVisible(true));
    }
}