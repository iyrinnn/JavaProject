import java.io.Serializable;
import java.util.UUID;

public class Resource implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID courseId;
    private UUID topicId;
    private String name;
    private String description;
    private ResourceType type;
    private String url;

    public Resource(UUID courseId, UUID topicId, String name, String description, ResourceType type, String url) {
        this.id = UUID.randomUUID();
        this.courseId = courseId;
        this.topicId = topicId;
        this.name = name;
        this.description = description;
        this.type = type;
        this.url = url;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getCourseId() { return courseId; }
    public UUID getTopicId() { return topicId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public ResourceType getType() { return type; }
    public String getUrl() { return url; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setType(ResourceType type) { this.type = type; }
    public void setUrl(String url) { this.url = url; }
}
