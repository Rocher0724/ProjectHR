package haru.com.hr;

/**
 * Created by myPC on 2017-04-17.
 */

public class HTTP_ResponseCode {
    public static final int CODE_OK = 200;
    public static final int CODE_CREATED = 201;
    public static final int CODE_DELETE = 201;
    public static final int CODE_BAD_REQUEST = 400; // 필수입력값 누락
    public static final int CODE_Unauthorized = 401; // 토큰 만료 or 잘못된 토큰
    public static final int CODE_NOT_FOUND = 404;
    public static final int CODE_CONFLICT = 409;
    public static final int CODE_INTERNAL_SERVER_ERROR = 500;
}
