package controller.user;

import annotation.MethodType;
import annotation.RequestMapping;
import controller.Controller;
import request.HttpRequest;
import response.HttpResponse;

@RequestMapping(url = "/users/form")
public class UserFormController extends Controller {
    @MethodType(value = "GET")
    public String showForm(HttpRequest httpRequest, HttpResponse httpResponse) {
        return "/user/form.html";
    }
}
