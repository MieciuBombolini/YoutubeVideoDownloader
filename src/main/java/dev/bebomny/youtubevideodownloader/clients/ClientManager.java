package dev.bebomny.youtubevideodownloader.clients;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ClientManager {

    private final Map<String, FetchingClient> fetchingClients;

    public ClientManager() {
        fetchingClients = new ConcurrentHashMap<>();
        loadDefaults();
    }

    public void loadDefaults() {
        fetchingClients.put("android", new AndroidFetchingClient());
        fetchingClients.put("ios", new IOSFetchingClient());
        //fetchingClients.put("web", new WebFetchingClient());
    }

    public Optional<FetchingClient> getFetchingClient(String name) {
        return Optional.ofNullable(fetchingClients.get(name));
    }

    public void addFetchingClient(String name, FetchingClient client) {
        fetchingClients.put(name, client);
    }

    public Map<String, FetchingClient> getFetchingClients() {
        return fetchingClients;
    }
}
