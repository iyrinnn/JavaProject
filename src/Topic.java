import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class Topic implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String name;
    private String status;
    private LocalDate wentOnlineDate;
    private LocalDate nextReviewDate;
    private List<Resource> resources;
    private List<Review> reviewHistory; // ✅ NEW

    public Topic(String name, String status) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.status = status;
        this.wentOnlineDate = LocalDate.now();
        this.nextReviewDate = wentOnlineDate.plusDays(7); // default next review after 7 days
        this.resources = new ArrayList<>();
        this.reviewHistory = new ArrayList<>(); // ✅ initialize
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public LocalDate getWentOnlineDate() { return wentOnlineDate; }
    public LocalDate getNextReviewDate() { return nextReviewDate; }

    public void setName(String name) { this.name = name; }
    public void setStatus(String status) { this.status = status; }
    public void setNextReviewDate(LocalDate nextReviewDate) { this.nextReviewDate = nextReviewDate; }

    public boolean isDue() {
        return nextReviewDate != null && !nextReviewDate.isAfter(LocalDate.now());
    }

    public List<Resource> getResources() { return resources; }
    public void addResource(Resource resource) { resources.add(resource); }

    // ✅ New methods for topic review history
    public List<Review> getReviewHistory() { return reviewHistory; }
    public void addReviewRecord(Review record) { reviewHistory.add(record); }
}
