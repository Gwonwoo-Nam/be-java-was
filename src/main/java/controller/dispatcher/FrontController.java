package controller.dispatcher;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import controller.Controller;
import mapper.HandlerMapper;
import mapper.MappingInfoRepository;
import request.HttpRequest;
import response.HttpResponse;

public class FrontController {

    /**
     * 처리할 Controller를 조회해서 리턴합니다.
     * @param httpRequest
     * @return
     */


    /**
     * 들어온 요청을 적합한 Controller에게 위임한다.
     * @param httpRequest
     * @return
     */
    public String dispatch(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (!MappingInfoRepository.hasMapping(httpRequest)) {
            return httpRequest.getUrl();
        }

        Controller controller = MappingInfoRepository.getHandler(httpRequest);
        String viewName;
        try {
            viewName = controller.process(httpRequest, httpResponse);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                 InstantiationException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("지원하지 않는 요청입니다.");
        }

        return viewName;
    }

}
