package controller;

import java.util.Map;

import annotation.MethodType;
import annotation.RequestMapping;
import controller.Controller;
import db.Database;
import model.User;
import request.HttpRequest;
import request.HttpRequestUtils;
import response.HttpResponse;

@RequestMapping(url = "/users/create")
public class UserJoinController extends Controller {
    /**
     * httpRequest의 쿼리 파라미터 map을 넘겨받아 User 객체를 생성합니다.
     * DB와의 연결은 아직 구현되지 않았습니다.
     * 회원 가입 후 home으로 redirect하는 view를 리턴합니다.
     * @param httpRequest
     * @return view
     */
    @MethodType(value = "GET")
    public String joinForm(HttpRequest httpRequest, HttpResponse httpResponse) {
        return "/users/form.html";
    }

    @MethodType(value = "POST")
    public String join(HttpRequest httpRequest, HttpResponse httpResponse) {
        Map<String, String> params = HttpRequestUtils.parseQueryParams(httpRequest.getBody());

        User user = new User(params.get("userId"), params.get("password"), params.get("name"),
                params.get("email"));
        Database.addUser(user);

        return "redirect:/";
    }
}
