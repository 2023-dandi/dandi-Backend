package dandi.dandi.auth.exception;

public class UnauthorizedException extends RuntimeException {

    private UnauthorizedException(String message) {
        super(message);
    }

    public static UnauthorizedException invalid() {
        return new UnauthorizedException("유효하지 않은 토큰입니다.");
    }

    public static UnauthorizedException expired() {
        return new UnauthorizedException("만료된 토큰입니다.");
    }
}