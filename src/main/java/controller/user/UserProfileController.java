package controller.user;

import java.util.Optional;

import annotation.MethodType;
import annotation.RequestMapping;
import controller.Controller;
import model.User;
import request.HttpRequest;
import request.HttpRequestUtils;
import response.HttpResponse;
import session.SessionDb;

@RequestMapping(url = "/users/profile")
public class UserProfileController extends Controller {
    @MethodType(value = "GET")
    public String profile(HttpRequest httpRequest, HttpResponse httpResponse) {
        Optional<String> sessionId = httpRequest.getSessionId();
        String parsedSessionId = HttpRequestUtils.parseSessionId(sessionId.get());
        User user = SessionDb.getUserBySessionId(parsedSessionId);

        httpResponse.setModelAttribute("user", user);

        return "/user/profile.html";
    }
}
