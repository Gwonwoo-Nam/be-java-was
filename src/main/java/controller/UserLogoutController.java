package controller;

import java.util.Optional;

import annotation.MethodType;
import annotation.RequestMapping;
import model.User;
import request.HttpRequest;
import request.HttpRequestUtils;
import response.HttpResponse;
import session.SessionDb;

@RequestMapping(url = "/users/logout")
public class UserLogoutController extends Controller {
    @MethodType(value = "GET")
    public String home(HttpRequest httpRequest, HttpResponse httpResponse) {
        //세션 무효화 코드
        Optional<String> sessionId = httpRequest.getSessionId();
        String parsedSessionId = HttpRequestUtils.parseSessionId(sessionId.get());
        SessionDb.invalidate(parsedSessionId);

        return "redirect:/";
    }
}
