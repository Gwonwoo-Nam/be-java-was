package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import controller.dispatcher.FrontController;
import controller.user.UserJoinController;
import request.HttpRequest;

class FrontControllerTest {

    private FrontController frontController;

    @BeforeEach
    void setup() {
        frontController = new FrontController();
    }

    @Nested
    @DisplayName("URL 경로에 따라 적합한 Controller에 할당합니다.")
    class GetHandlerTest {
        @Test
        @DisplayName("Root(/) URL로 요청을 보냈을 때 HomeController가 처리한다.")
        void requestMappingRootTest() {
            HttpRequest httpRequest = new HttpRequest();
            httpRequest.initRequestLine("GET / HTTP/1.1");

            assertThat(frontController.getHandler(httpRequest)).isInstanceOf(HomeController.class);
        }

        @Test
        @DisplayName("/users/create는 UserJoingController가 처리한다.")
        void requestMappingTest() {
            HttpRequest httpRequest = new HttpRequest();
            httpRequest.initRequestLine("POST /users/create HTTP/1.1");

            assertThat(frontController.getHandler(httpRequest)).isInstanceOf(UserJoinController.class);
        }
    }


}
