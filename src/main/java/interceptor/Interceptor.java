package interceptor;

import exception.UserInfoException;
import java.lang.reflect.Method;
import java.util.Optional;
import model.User;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.HttpRequest;
import request.HttpRequestUtils;
import response.HttpResponse;
import session.SessionDb;

public class Interceptor implements MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(Interceptor.class);

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
        throws Throwable {
        if (method.getName().contains("process")) {
            try {
                prehandle(obj, method, args);
            } catch (UserInfoException e) {
                logger.debug("현재 세션이 유효하지 않아 초기화됩니다.");
                return "redirect:/";
            }
            Object object = proxy.invokeSuper(obj, args);
            posthandle(obj, method);
            return object;
        }

        return proxy.invokeSuper(obj, args);
    }

    private void prehandle(Object obj, Method method, Object[] args) {
        logger.debug("실행 전 - 현재 실행 클래스 : {}, 메소드 : {}", obj.getClass().getSuperclass().toString(), method.getName() );
        checkCurrentSession((HttpRequest) args[0], (HttpResponse) args[1]);
    }

    private void posthandle(Object obj, Method method) {
        logger.debug("실행 후 - 현재 실행 클래스 : {}, 메소드 : {}", obj.getClass().getSuperclass().toString(), method.getName() );
    }
    /**
     * 모든 컨트롤러가 실행될 때 현재 쿠키를 세션에서 확인하고 유효하지 않으면 무효화합니다.
     * @param httpRequest
     * @param httpResponse
     * @throws UserInfoException
     */
    private void checkCurrentSession(HttpRequest httpRequest, HttpResponse httpResponse) throws UserInfoException {
        Optional<String> sessionId = httpRequest.getSessionId();
        //Home 아이콘에 비회원 Or 회원 ID를 표시해주는 기능
        httpResponse.setModelAttribute("loginedUserId", "비회원");
        if (!sessionId.isEmpty()) {
            httpResponse.setModelAttribute("hasSession", new Boolean(true));
            String parsedSessionId = HttpRequestUtils.parseSessionId(sessionId.get());

            User loginedUser = SessionDb.getUserBySessionId(parsedSessionId);

            //유효한 세션이 아닌 경우 쿠키를 초기화
            if (loginedUser == null) {
                //유효 세션이 아니면 요청에 보내고 있는 쿠키 무효화하기
                httpResponse.addHeader("Set-Cookie", String.format("sid=%s; Path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT;", parsedSessionId));
                throw new UserInfoException("세션이 만료되었습니다.");
            }

            httpResponse.setModelAttribute("loginedUserId", loginedUser.getUserId());
        }
        else {
            httpResponse.setModelAttribute("hasNoSession", new Boolean(true));
        }
    }
}
