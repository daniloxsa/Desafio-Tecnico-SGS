package br.com.sergipetech.solicitacao_api.exceptions;

import br.com.sergipetech.solicitacao_api.services.exception.InvalidDocumentException;
import br.com.sergipetech.solicitacao_api.services.exception.ResourceNotFoundException;
import br.com.sergipetech.solicitacao_api.services.exception.StatusTransactionError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException exception, HttpServletRequest request) {
        String error = "Recurso não encontrado";
        HttpStatus status = HttpStatus.NOT_FOUND;

        StandardError err = new StandardError(Instant.now(), status.value(), error, exception.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);

    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> database(DataIntegrityViolationException exception, HttpServletRequest request) {
        String error = "Erro em executar operação em banco de dados";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        StandardError err = new StandardError(Instant.now(), status.value(), error, exception.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);

    }


    @ExceptionHandler(StatusTransactionError.class)
    public ResponseEntity<StandardError> transactionStatus(StatusTransactionError exception, HttpServletRequest request) {
        String error = exception.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        StandardError err = new StandardError(Instant.now(), status.value(), error, exception.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);

    }

    @ExceptionHandler(InvalidDocumentException.class)
    public ResponseEntity<StandardError> invalidDocument(InvalidDocumentException exception, HttpServletRequest request) {
        String error = "Erro em validar a veracidade das credencias";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        StandardError err = new StandardError(Instant.now(), status.value(), error, exception.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> invalidDocument(MethodArgumentNotValidException exception, HttpServletRequest request) {
        String error = "Erro de validação dos dados";
        String message = "";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        for (FieldError errors : exception.getBindingResult().getFieldErrors()) {
            message += errors.getField() + ": " + errors.getDefaultMessage() + "; ";
        }

        StandardError err = new StandardError(Instant.now(), status.value(), error, message, request.getRequestURI());
        return ResponseEntity.status(status).body(err);

    }
}

