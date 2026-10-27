package cl.jimenez.aguilera.caso1.controller;

import cl.jimenez.aguilera.caso1.dto.RespuestaError;
import cl.jimenez.aguilera.caso1.exception.OperacionInvalidaException;
import cl.jimenez.aguilera.caso1.exception.RecursoNoEncontradoException;
import cl.jimenez.aguilera.caso1.exception.StockInsuficienteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<RespuestaError> manejarStockInsuficiente(StockInsuficienteException ex) {
        Map<String, Object> detalles = new HashMap<>();
        detalles.put("nombreProducto", ex.getNombreProducto());
        detalles.put("cantidadSolicitada", ex.getCantidadSolicitada());
        detalles.put("stockDisponible", ex.getStockDisponible());

        RespuestaError error = new RespuestaError(ex.getMessage(), HttpStatus.CONFLICT.value(), detalles);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<RespuestaError> manejarRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        RespuestaError error = new RespuestaError(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(OperacionInvalidaException.class)
    public ResponseEntity<RespuestaError> manejarOperacionInvalida(OperacionInvalidaException ex) {
        RespuestaError error = new RespuestaError(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaError> manejarValidaciones(MethodArgumentNotValidException ex) {
        Map<String, Object> errores = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errores.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        RespuestaError error = new RespuestaError("Error de validacion en los datos enviados", HttpStatus.BAD_REQUEST.value(), errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaError> manejarExcepcionGeneral(Exception ex) {
        RespuestaError error = new RespuestaError("Ha ocurrido un error interno en el servidor: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
