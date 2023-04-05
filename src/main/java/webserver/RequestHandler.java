package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.UserController;
import model.User;
import util.HttpRequest;
import util.HttpRequestUtils;


public class RequestHandler implements Runnable {
    private UserController userController = new UserController();
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            HttpRequest httpRequest = new HttpRequest();
            httpRequest.init(br.readLine());
            logger.debug("http Request : {}", httpRequest);

            String requestHeader;
            while (!(requestHeader = br.readLine()).equals("")) {
                logger.debug("header : {}", requestHeader);
            }

            String view = userController.requestMapping(httpRequest);

            mapView(out, view);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * View의 위치에 있는 파일을 찾아 HTTP 응답으로 보냅니다.
     * @param out
     * @param view
     * @throws IOException
     */
    private void mapView(OutputStream out, String view) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);

        //Redirect 키워드가 있는 경우 redirect 헤더로 작성
        if (view.startsWith("redirect:")) {
            response302Header(dos, view);
            return;
        }

        //그 외의 경우 200 OK 응답을 반환
        byte[] body = mapStaticOrTemplateFiles(view, dos);
        responseBody(dos, body);
    }

    /**
     * css, js, fonts는 static 폴더에서 파일을 탐색하여 반환합니다. (content header를 text/css로 변경해서 보냅니다.)
     * 나머지(html 파일)는 templates 폴더에서 탐색합니다.
     * @param view
     * @param dos
     * @return
     * @throws IOException
     */
    private byte[] mapStaticOrTemplateFiles(String view, DataOutputStream dos) throws IOException {
        //Static 파일 경로에서 탐색
        if (view.startsWith("/css") || view.startsWith("/js") || view.startsWith("/fonts")) {
            byte[] body = Files.readAllBytes(new File("src/main/resources/static" + view).toPath());
            response200StyleHeader(dos, body.length);
            return body;
        }
        //나머지는 Template 파일에서 탐색
        byte[] body = Files.readAllBytes(new File("src/main/resources/templates" + view).toPath());
        response200Header(dos, body.length);
        return body;
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            //헤더 작성
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");

            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void response200StyleHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            //헤더 작성
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");

            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String view) {
        try {
            //헤더 작성
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + view.split("redirect:")[1] + "\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
