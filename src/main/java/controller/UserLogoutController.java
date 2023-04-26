package controller;

import annotation.MethodType;
import annotation.RequestMapping;
import request.HttpRequest;
import response.HttpResponse;
import session.SessionDb;

@RequestMapping(url = "/users/logout")
public class UserLogoutController extends Controller {
    @MethodType(value = "GET")
    public String home(HttpRequest httpRequest, HttpResponse httpResponse) {
        //세션 무효화 코드
        return "redirect:/";
    }
}
