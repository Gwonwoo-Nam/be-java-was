package response;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;

public class HttpHeaders {

    private Map<String, String> headers = new HashMap<>();

    public void put(String key, String value) {
        headers.put(key, value);
    }

    public void parse(String headerString) {
        StringTokenizer st = new StringTokenizer(headerString, ":");
        headers.put(st.nextToken().trim(), st.nextToken().trim());
    }

    public int getContentLength() {
        if (headers.containsKey("Content-Length")) {
            return Integer.parseInt(headers.get("Content-Length"));
        }

        return 0;
    }

    public Optional<String> getSessionId() {
        return Optional.ofNullable(headers.get("Cookie"));
    }

    @Override
    public String toString() {
        StringBuffer headerString = new StringBuffer();
        headers.entrySet().stream()
                .forEach(e -> headerString.append(e.getKey()).append(": ").append(e.getValue())
                        .append("\r\n"));

        return headerString.append("\r\n").toString();
    }
}
