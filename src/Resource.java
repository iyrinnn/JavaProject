// Resource.java
// This is the data model for a resource, including spaced repetition logic.

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class Resource implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID courseId;
    private UUID topicId;
    private String title;
    private String description;
    private ResourceType type;
    private String content;

    private double easinessFactor;
    private int interval;
    private int repetitions;
    private LocalDateTime nextReviewDate;
    private LocalDateTime lastReviewedDate;
    private List<Review> reviewHistory;

    public Resource(UUID courseId, UUID topicId, String title, String description, ResourceType type, String content) {
        this.id = UUID.randomUUID();
        this.courseId = courseId;
        this.topicId = topicId;
        this.title = title;
        this.description = description;
        this.type = type;
        this.content = content;

        this.easinessFactor = 2.0;
        this.interval = 0;
        this.repetitions = 0;
        this.nextReviewDate = LocalDateTime.now();
        this.reviewHistory = new ArrayList<>();
    }

    public UUID getId() { return id; }
    public UUID getCourseId() { return courseId; }
    public UUID getTopicId() { return topicId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public ResourceType getType() { return type; }
    public String getContent() { return content; }
    public LocalDateTime getNextReviewDate() { return nextReviewDate; }
    public LocalDateTime getLastReviewedDate() { return lastReviewedDate; }
    public List<Review> getReviewHistory() { return reviewHistory; }


    public void markReviewed(int recallRating) {
        reviewHistory.add(new Review(LocalDateTime.now(), recallRating));

        if (recallRating >= 3) {
            if (repetitions == 0) {
                interval = 1;
            } else if (repetitions == 1) {
                interval = 6;
            } else {
                interval = (int) Math.round(interval * easinessFactor);
            }
            repetitions++;
        } else {
            repetitions = 0;
            interval = 1;
        }

        if (recallRating < 3) {
            easinessFactor = easinessFactor - 0.2;
            if (easinessFactor < 1.3) {
                easinessFactor = 1.3;
            }
        } else {
            easinessFactor = easinessFactor + (0.1 - (5 - recallRating) * (0.08 + (5 - recallRating) * 0.02));
            if (easinessFactor > 2.5) {
                easinessFactor = 2.5;
            }
        }

        this.lastReviewedDate = LocalDateTime.now();
        this.nextReviewDate = lastReviewedDate.plusDays(interval);
    }
}
