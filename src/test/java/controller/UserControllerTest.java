package controller;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import db.Database;
import model.User;
import request.HttpRequest;
import response.HttpResponse;

class UserControllerTest {
    private UserJoinController userJoinController;

    @BeforeEach
    void setup() {
        userJoinController = new UserJoinController();
    }

    @Test
    @DisplayName("유저를 추가한다.")
    void requestMappingRootTest() {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.initRequestLine("POST /users/create HTTP/1.1");
        httpRequest.setBody("userId=poro&password=123&name=pororo&email=ngw7617%40naver.com");
        userJoinController.join(httpRequest, new HttpResponse());

        User user = Database.findUserById("poro").get();


        assertThat(user.getUserId()).isEqualTo("poro");
    }

    @Test
    @DisplayName("회원 가입 요청 시 유저 객체를 생성하고 Root로 리다이렉트를 반환한다.")
    void userJoin() {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setBody("GET /user/create?userId=poro&password=123&name=pororo&email=ngw7617%40naver.com HTTP/1.1");

        //TODO : 유저를 저장하지 않으므로 유저에 대한 테스트가 없음.
        assertThat(userJoinController.join(httpRequest, new HttpResponse())).isEqualTo("redirect:/");
    }
}
