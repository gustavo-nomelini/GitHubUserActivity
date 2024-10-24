public class GitHubEvent {
    String type;
    Payload payload;

    static class Payload{
        String action;
    }

    @Override
    public String toString() {
        // Handle events based on their type
        switch (type) {
            case "WatchEvent":
                return "Event: " + type + ", Action: " + (payload != null ? payload.action : "N/A");

            case "PushEvent":
                return "Event: " + type + ", Commit pushed.";

            case "CreateEvent":
                return "Event: " + type + ", Repository or branch created.";

            // Add more cases for other event types if needed
            default:
                return "Event: " + type;
        }
    }
}
