// DataManager.java
// This class handles all data persistence and retrieval.

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DataManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String FILE_NAME = "smart_revision_data.ser";

    private List<Course> allCourses;
    private Map<UUID, Resource> allResources;

    public DataManager() {
        this.allCourses = new ArrayList<>();
        this.allResources = new HashMap<>();
    }

    public void addCourse(Course course) {
        allCourses.add(course);
    }

    public Course getCourseById(UUID courseId) {
        for (Course course : allCourses) {
            if (course.getId().equals(courseId)) {
                return course;
            }
        }
        return null;
    }

    public List<Course> getAllCourses() {
        return allCourses;
    }

    public void deleteCourse(UUID courseId) {
        allCourses.removeIf(course -> course.getId().equals(courseId));
    }

    public List<Resource> getAllResourcesInCourse(UUID courseId) {
        List<Resource> resources = new ArrayList<>();
        Course course = getCourseById(courseId);
        if (course != null) {
            for (Topic topic : course.getTopics()) {
                resources.addAll(topic.getResources());
            }
        }
        return resources;
    }

    public LocalDateTime getLastReviewDateInCourse(UUID courseId) {
        LocalDateTime latestDate = null;
        for (Resource resource : getAllResourcesInCourse(courseId)) {
            if (resource.getLastReviewedDate() != null) {
                if (latestDate == null || resource.getLastReviewedDate().isAfter(latestDate)) {
                    latestDate = resource.getLastReviewedDate();
                }
            }
        }
        return latestDate;
    }

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

    public Resource getResourceById(UUID resourceId) {
        return allResources.get(resourceId);
    }

    public Resource getFirstDueResourceInTopic(UUID topicId) {
        Topic topic = getTopicById(topicId);
        if (topic != null) {
            LocalDateTime now = LocalDateTime.now();
            for (Resource resource : topic.getResources()) {
                if (resource.getNextReviewDate() != null && now.isAfter(resource.getNextReviewDate())) {
                    return resource;
                }
            }
        }
        return null;
    }

    public Topic getTopicById(UUID topicId) {
        for (Course course : allCourses) {
            Topic topic = course.getTopicById(topicId);
            if (topic != null) {
                return topic;
            }
        }
        return null;
    }

    public List<Resource> getDueResources() {
        List<Resource> due = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (Resource resource : allResources.values()) {
            if (resource.getNextReviewDate() != null && now.isAfter(resource.getNextReviewDate())) {
                due.add(resource);
            }
        }
        return due;
    }

    public List<Resource> getDueResourcesByCourse(UUID courseId) {
        List<Resource> due = new ArrayList<>();
        Course course = getCourseById(courseId);
        if (course != null) {
            LocalDateTime now = LocalDateTime.now();
            for (Topic topic : course.getTopics()) {
                for (Resource resource : topic.getResources()) {
                    if (resource.getNextReviewDate() != null && now.isAfter(resource.getNextReviewDate())) {
                        due.add(resource);
                    }
                }
            }
        }
        return due;
    }

    public void saveData() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(allCourses);
            oos.writeObject(allResources);
        }
    }

    public void loadData() throws IOException, ClassNotFoundException {
        File file = new File(FILE_NAME);
        if (file.exists() && file.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                allCourses = (List<Course>) ois.readObject();
                allResources = (Map<UUID, Resource>) ois.readObject();
            }
        }
    }
}
