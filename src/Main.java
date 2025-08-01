import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Create an instance of the data manager to handle all data operations.
        DataManager dataManager = new DataManager();

        // Use SwingUtilities.invokeLater to ensure the GUI is created and updated
        // on the Event Dispatch Thread (EDT), which is a best practice for Swing.
        SwingUtilities.invokeLater(() -> {
            MainApplicationFrame frame = new MainApplicationFrame(dataManager);
            frame.setVisible(true);
        });
    }
}
