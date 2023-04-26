package session;

import java.util.Map;

import com.google.common.collect.Maps;

import model.User;

public class SessionDb {
    private static Map<String, User> sessionMap = Maps.newHashMap();

    public static String addSessionedUser(User user) {
        String sessionId = SessionGenerator.generate();
        sessionMap.put(sessionId, user);

        return sessionId;
    }

    public static User getUserBySessionId(String sessionId) {
        return sessionMap.get(sessionId);
    }

    public static void invalidate(String sessionId) {
        sessionMap.remove(sessionId);
    }


}
