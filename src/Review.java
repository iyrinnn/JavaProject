// Review.java
// This is the data model for a single review session.

import java.io.Serializable;
import java.time.LocalDateTime;

class Review implements Serializable {
    private LocalDateTime timestamp;
    private int recallRating;

    public Review(LocalDateTime timestamp, int recallRating) {
        this.timestamp = timestamp;
        this.recallRating = recallRating;
    }

    @Override
    public String toString() {
        return "Reviewed on " + timestamp.toString() + " with rating " + recallRating;
    }
}
