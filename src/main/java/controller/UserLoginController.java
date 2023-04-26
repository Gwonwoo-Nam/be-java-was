package controller;

import java.util.Map;

import annotation.ExceptionHandler;
import annotation.MethodType;
import annotation.RequestMapping;
import db.Database;
import exception.UserInfoException;
import model.User;
import request.HttpRequest;
import request.HttpRequestUtils;
import response.HttpResponse;
import session.SessionDb;

@RequestMapping(url = "/users/login")
public class UserLoginController extends Controller {

    @MethodType(value = "GET")
    public String showForm(HttpRequest httpRequest, HttpResponse httpResponse) {
        return "/user/login.html";
    }
    @MethodType(value = "POST")
    public String login(HttpRequest httpRequest, HttpResponse httpResponse) {
        Map<String, String> params = HttpRequestUtils.parseQueryParams(httpRequest.getBody());
        String userInputPassword = params.get("password");
        String userId = params.get("userId");

        User user = Database.findUserById(userId).orElseThrow(() -> {
            httpResponse.setModelAttribute("errorMessage", "존재하지 않는 유저입니다.");
            throw new UserInfoException();
        });

        if (user.validate(userInputPassword)){
            String session = SessionDb.addSessionedUser(user);
            httpResponse.addHeader("Set-cookie", String.format("sid=%s; Path=/", session));
            return "redirect:/";
        }
        httpResponse.setModelAttribute("errorMessage", "비밀번호가 틀렸습니다.");
        throw new UserInfoException();
    }

    @ExceptionHandler(exception = "UserInfoException.class")
    public String failLogin(HttpRequest httpRequest, HttpResponse httpResponse) {
        return "/user/login_failed.html";
    }
}
