import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DataLoader {

    public static void loadSampleData(DataManager dataManager) {
        System.out.println("Loading sample data...");

        // Clear existing data
        dataManager.getAllCourses().clear();

        // Create sample courses
        Course dataStructures = new Course("DATA STRUCTURE");
        Course oop = new Course("Object Oriented Programming");
        Course algorithms = new Course("Algorithms");
        Course database = new Course("Database Systems");
        Course networks = new Course("Computer Networks");

        // Add topics to Data Structures course
        Topic arrays = new Topic("Arrays", "New");
        Topic linkedLists = new Topic("Linked Lists", "Reviewed");
        Topic trees = new Topic("Trees", "Due");
        Topic graphs = new Topic("Graphs", "New");

        // Add topics to OOP course
        Topic inheritance = new Topic("Inheritance", "Reviewed");
        Topic polymorphism = new Topic("Polymorphism", "Due");
        Topic encapsulation = new Topic("Encapsulation", "New");

        // Add resources to topics
        addSampleResources(dataStructures, arrays, linkedLists, trees, graphs);
        addSampleResources(oop, inheritance, polymorphism, encapsulation);

        // Add review history
        addSampleReviewHistory(linkedLists);
        addSampleReviewHistory(trees);
        addSampleReviewHistory(inheritance);
        addSampleReviewHistory(polymorphism);

        // Set different review dates
        setReviewDates(arrays, 1);
        setReviewDates(linkedLists, -2);
        setReviewDates(trees, 0);
        setReviewDates(graphs, 7);
        setReviewDates(polymorphism, -1);

        // Add topics to courses
        dataStructures.addTopic(arrays);
        dataStructures.addTopic(linkedLists);
        dataStructures.addTopic(trees);
        dataStructures.addTopic(graphs);

        oop.addTopic(inheritance);
        oop.addTopic(polymorphism);
        oop.addTopic(encapsulation);

        algorithms.addTopic(new Topic("Sorting", "Due"));
        algorithms.addTopic(new Topic("Searching", "New"));

        database.addTopic(new Topic("SQL", "Reviewed"));
        networks.addTopic(new Topic("TCP/IP", "Due"));

        // Add courses to data manager
        dataManager.addCourse(dataStructures);
        dataManager.addCourse(oop);
        dataManager.addCourse(algorithms);
        dataManager.addCourse(database);
        dataManager.addCourse(networks);

        System.out.println("Sample data loaded successfully!");
    }

    private static void addSampleResources(Course course, Topic... topics) {
        for (Topic topic : topics) {
            topic.addResource(new Resource(course.getId(), topic.getId(),
                    "Lecture Notes", "Comprehensive notes", ResourceType.PDF,
                    "https://example.com/notes"));

            topic.addResource(new Resource(course.getId(), topic.getId(),
                    "Video Tutorial", "Detailed explanation", ResourceType.VIDEO,
                    "https://youtube.com/watch?v=sample"));
        }
    }

    private static void addSampleReviewHistory(Topic topic) {
        LocalDateTime now = LocalDateTime.now();
        topic.addReviewRecord(new Review(now.minusDays(30), 4));
        topic.addReviewRecord(new Review(now.minusDays(15), 3));
        topic.addReviewRecord(new Review(now.minusDays(5), 5));
    }

    private static void setReviewDates(Topic topic, int daysFromNow) {
        topic.setNextReviewDate(LocalDate.now().plusDays(daysFromNow));
    }
}