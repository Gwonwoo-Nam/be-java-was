package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.dispatcher.FrontController;
import request.HttpRequest;
import request.HttpRequestBuilder;
import response.HttpResponse;
import view.ViewResolver;

public class RequestHandler implements Runnable {

    private static FrontController frontController = new FrontController();
    private static ViewResolver viewResolver = new ViewResolver();
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}",
                connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            HttpRequest httpRequest = HttpRequestBuilder.setup(br);

            HttpResponse httpResponse = new HttpResponse();
            String viewName = frontController.dispatch(httpRequest, httpResponse);

            byte[] responseMessage = viewResolver.mapView(viewName, httpResponse);

            DataOutputStream dos = new DataOutputStream(out);
            response(dos, responseMessage);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Response 메시지를 전송합니다.
     * @param dos
     * @param body
     */
    private void response(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
