package webserver.enums;
public enum HttpStatus {
    OK(200,"OK"), FOUND(302,"Found"), BAD_REQUEST(400,"Bad Request"),
    FORBIDDEN(403,"Forbidden"), NOT_FOUND(404,"Not Found"), METHOD_NOT_ALLOWED(405,"Method Not Allowed");
    public final int code; public final String reason;
    HttpStatus(int c, String r){ code=c; reason=r; }
}