import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class TimeLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID resourceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TimeLog(UUID resourceId, LocalDateTime startTime) {
        this.resourceId = resourceId;
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
