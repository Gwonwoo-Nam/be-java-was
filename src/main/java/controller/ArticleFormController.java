package controller;

import annotation.MethodType;
import annotation.RequestMapping;
import controller.Controller;
import request.HttpRequest;
import response.HttpResponse;

@RequestMapping(url = "/articles/form")
public class ArticleFormController extends Controller {
    @MethodType(value = "GET")
    public String showForm(HttpRequest httpRequest, HttpResponse httpResponse) {
        return "/qna/form.html";
    }
}
