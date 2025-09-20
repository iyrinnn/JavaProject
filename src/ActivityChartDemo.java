import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ActivityChartDemo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dashboard Activity Chart");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Map<LocalDate, Integer> mockActivity = generateMockActivity();
            ActivityChartPanel chartPanel = new ActivityChartPanel(mockActivity);

            frame.add(chartPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static Map<LocalDate, Integer> generateMockActivity() {
        Map<LocalDate, Integer> map = new HashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 365; i++) {
            LocalDate date = today.minusDays(i);
            int activityLevel = (int) (Math.random() * 5); // 0 to 4
            map.put(date, activityLevel);
        }
        return map;
    }
}

class ActivityChartPanel extends JPanel {
    private final Map<LocalDate, Integer> activityMap;

    public ActivityChartPanel(Map<LocalDate, Integer> activityMap) {
        this.activityMap = activityMap;
        setPreferredSize(new Dimension(900, 150));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int cellSize = 15;
        int padding = 2;
        int labelSpace = 20;
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(364);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw weekday labels
        for (int i = 0; i < 7; i++) {
            String day = DayOfWeek.of((i + 1) % 7 + 1).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(day, 0, i * (cellSize + padding) + cellSize);
        }

        // Draw month names and activity cells
        int currentMonth = -1;
        for (int col = 0; col < 53; col++) {
            for (int row = 0; row < 7; row++) {
                LocalDate date = startDate.plusDays(col * 7L + row);
                if (date.isAfter(today)) continue;

                int activity = activityMap.getOrDefault(date, 0);
                g2.setColor(getColorForActivity(activity));
                g2.fillRect(col * (cellSize + padding) + labelSpace, row * (cellSize + padding), cellSize, cellSize);

                // Month label
                if (row == 0) {
                    int month = date.getMonthValue();
                    if (month != currentMonth) {
                        currentMonth = month;
                        String monthName = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                        g2.setColor(Color.DARK_GRAY);
                        g2.drawString(monthName, col * (cellSize + padding) + labelSpace, 10);
                    }
                }
            }
        }
    }

    private Color getColorForActivity(int activity) {
        return switch (activity) {
            case 1 -> new Color(200, 200, 200); // light gray
            case 2 -> new Color(150, 150, 150); // gray
            case 3 -> new Color(100, 100, 100); // dark gray
            case 4 -> new Color(50, 50, 50);    // almost black
            default -> new Color(240, 240, 240); // white-ish
        };
    }
}
