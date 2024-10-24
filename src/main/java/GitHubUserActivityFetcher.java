import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class GitHubUserActivityFetcher {

    private static final String GITHUB_API_URL = "https://api.github.com/users/%s/events";
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    // Fetch user events with pagination support, up to a limit (50 events)
    public List<GitHubEvent> fetchUserEvents(String username, int limit) throws IOException {
        List<GitHubEvent> allEvents = new ArrayList<>();
        int page = 1;

        while (allEvents.size() < limit) {
            String url = String.format(GITHUB_API_URL, username, page);
            Request request = new Request.Builder()
                    .url(url)
                    .header("Accept", "application/vnd.github.v3+json") // GitHub API version
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.code() == 404) {
                    System.out.println("User not found or no public events available for the user: " + username);
                    return List.of();  // Return an empty list if no events are found
                }

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code: " + response);
                }

                // Parse the response body using Gson
                GitHubEvent[] eventsArray = gson.fromJson(response.body().string(), GitHubEvent[].class);
                List<GitHubEvent> eventsList = Arrays.asList(eventsArray);

                // Break the loop if there are no more events (API returns an empty array)
                if (eventsList.isEmpty()) {
                    break;
                }

                // Add events to the list, but keep the list size to the limit (50 events)
                allEvents.addAll(eventsList);
                page++;
            }
        }

        // Trim to the limit if more than 50 events were fetched
        return allEvents.size() > limit ? allEvents.subList(0, limit) : allEvents;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Prompt the user to enter a GitHub username
        System.out.print("Enter GitHub username: ");
        String username = scanner.nextLine();

        // Fetch and display GitHub events for the entered username
        GitHubUserActivityFetcher fetcher = new GitHubUserActivityFetcher();
        try {
            List<GitHubEvent> events = fetcher.fetchUserEvents(username, 50); // Limit to 50 events
            events.forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("Failed to fetch user events: " + e.getMessage());
        }
    }
}