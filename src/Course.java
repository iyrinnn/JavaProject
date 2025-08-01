// Course.java
// This is the data model for a course.

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String name;
    private List<Topic> topics;

    public Course(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.topics = new ArrayList<>();
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public List<Topic> getTopics() { return topics; }

    public void setName(String name) { this.name = name; }
    public void addTopic(Topic topic) { topics.add(topic); }
    public void deleteTopic(UUID topicId) { topics.removeIf(topic -> topic.getId().equals(topicId)); }

    public Topic getTopicById(UUID topicId) {
        for (Topic topic : topics) {
            if (topic.getId().equals(topicId)) {
                return topic;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
