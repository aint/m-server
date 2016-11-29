package ua.softgroup.matrix.server.supervisor.jersey.exception;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class JwtException extends RuntimeException {
    private static final long serialVersionUID = -1414052240073072641L;

    public JwtException(Throwable cause) {
        super(cause);
    }
}
