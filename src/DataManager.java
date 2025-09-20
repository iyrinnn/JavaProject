import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * DataManager handles persistence and retrieval of Courses, Topics, and Resources.
 * Review is based on Topics, not individual Resources.
 */
public class DataManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Course> allCourses;
    private Map<UUID, Resource> allResources; // Map to store all resources for quick lookup

    public DataManager() {
        this.allCourses = new ArrayList<>();
        this.allResources = new HashMap<>();
    }

    // Add a new course
    public void addCourse(Course course) {
        allCourses.add(course);
    }

    // Get course by ID (null if not found)
    public Course getCourseById(UUID courseId) {
        for (Course course : allCourses) {
            if (course.getId().equals(courseId)) {
                return course;
            }
        }
        return null;
    }

    // Get all courses
    public List<Course> getAllCourses() {
        return allCourses;
    }

    // Delete course by ID
    public void deleteCourse(UUID courseId) {
        allCourses.removeIf(course -> course.getId().equals(courseId));
        // Consider removing resources belonging to that course from allResources here if needed
    }

    // Get topic by ID searching all courses (null if not found)
    public Topic getTopicById(UUID topicId) {
        for (Course course : allCourses) {
            Topic topic = course.getTopicById(topicId);
            if (topic != null) {
                return topic;
            }
        }
        return null;
    }

    // Get all topics that are due for review
    public List<Topic> getDueTopics() {
        List<Topic> dueTopics = new ArrayList<>();
        for (Course course : allCourses) {
            for (Topic topic : course.getTopics()) {
                if (topic.isDue()) {
                    dueTopics.add(topic);
                }
            }
        }
        return dueTopics;
    }

    // Get all next review dates from topics
    public List<LocalDate> getAllTopicReviewDates() {
        List<LocalDate> reviewDates = new ArrayList<>();
        for (Course course : allCourses) {
            for (Topic topic : course.getTopics()) {
                LocalDate nextReview = topic.getNextReviewDate();
                if (nextReview != null) {
                    reviewDates.add(nextReview);
                }
            }
        }
        return reviewDates;
    }

    // Get course for a given topic
    public Course getCourseForTopic(Topic topic) {
        for (Course course : allCourses) {
            if (course.getTopics().contains(topic)) {
                return course;
            }
        }
        return null;
    }

    // Add a resource to a topic and update resource map
    public void addResourceToTopic(UUID courseId, UUID topicId, Resource resource) {
        Course course = getCourseById(courseId);
        if (course != null) {
            Topic topic = course.getTopicById(topicId);
            if (topic != null) {
                topic.addResource(resource);
                allResources.put(resource.getId(), resource);
            }
        }
    }

    // Convenience method to get resource by its ID (or null if not found)
    public Resource getResourceById(UUID resourceId) {
        return allResources.get(resourceId);
    }

    // Optional: Get all resources
    public Collection<Resource> getAllResources() {
        return allResources.values();
    }

    // Save data to specific file
    public void saveDataToFile(String fileName) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(allCourses);
            oos.writeObject(allResources);
        }
    }

    // Load data from specific file
    public void loadDataFromFile(String fileName) throws IOException, ClassNotFoundException {
        File file = new File(fileName);
        if (file.exists() && file.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                allCourses = (List<Course>) ois.readObject();
                allResources = (Map<UUID, Resource>) ois.readObject();
            }
        }
    }

    // For backward compatibility - save to default file
    public void saveData() throws IOException {
        saveDataToFile("smart_revision_data.ser");
    }

    // For backward compatibility - load from default file
    public void loadData() throws IOException, ClassNotFoundException {
        loadDataFromFile("smart_revision_data.ser");
    }
}