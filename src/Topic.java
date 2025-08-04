import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class Topic implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String name;
    private String status;
    private LocalDateTime wentOnlineDate;
    private List<Resource> resources;

    public Topic(String name, String status) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.status = status;
        this.wentOnlineDate = LocalDateTime.now();
        this.resources = new ArrayList<>();
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public LocalDateTime getWentOnlineDate() { return wentOnlineDate; }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) { this.status = status; }
    public List<Resource> getResources() { return resources; }
    public void addResource(Resource resource) { resources.add(resource); }
}