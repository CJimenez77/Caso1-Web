package cl.jimenez.aguilera.caso1.exception;

public class OperacionInvalidaException extends RuntimeException {

    public OperacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
