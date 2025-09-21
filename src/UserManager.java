import java.io.*;

public class UserManager {
    private static final String USER_DATA_DIR = "data/users/";
    private static final String CREDENTIALS_FILE = "data/users.txt";

    public static boolean isUserExists(String username) {
        try {
            File file = new File(CREDENTIALS_FILE);
            if (!file.exists()) return false;

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equalsIgnoreCase(username)) {
                    reader.close();
                    return true;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean validateUser(String username, String password) {
        try {
            File file = new File(CREDENTIALS_FILE);
            if (!file.exists()) return false;

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].equalsIgnoreCase(username) && parts[1].equals(password)) {
                    reader.close();
                    return true;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean registerUser(String username, String password) {
        if (isUserExists(username)) return false;
        try {
            new File(USER_DATA_DIR).mkdirs();


            BufferedWriter writer = new BufferedWriter(new FileWriter(CREDENTIALS_FILE, true));
            writer.write(username + "," + password);
            writer.newLine();
            writer.close();

            String userDataFile = getUserDataFilePath(username);
            new File(userDataFile).getParentFile().mkdirs();


            DataManager initialData = new DataManager();
            initialData.saveDataToFile(userDataFile);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getUserDataFilePath(String username) {
        return USER_DATA_DIR + username + "_data.ser";
    }

    public static DataManager loadUserData(String username) {
        String userDataFile = getUserDataFilePath(username);
        DataManager dataManager = new DataManager();

        try {
            File file = new File(userDataFile);
            if (file.exists()) {
                dataManager.loadDataFromFile(userDataFile);
            } else {
                // If no data exists, load sample data for new user
                DataLoader.loadSampleData(dataManager);
                dataManager.saveDataToFile(userDataFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // If loading fails, create fresh data
            dataManager = new DataManager();
            DataLoader.loadSampleData(dataManager);
        }

        return dataManager;
    }

    public static void saveUserData(String username, DataManager dataManager) {
        try {
            dataManager.saveDataToFile(getUserDataFilePath(username));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}