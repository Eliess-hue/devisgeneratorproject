package fr.devisgenerator.devisgenerator.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExists(
            UserAlreadyExistsException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentials(
            InvalidCredentialsException ex) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<String> handleClientNotFound(
            ClientNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(QuoteNotFoundException.class)
    public ResponseEntity<String> handleQuoteNotFound(
            QuoteNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(QuoteLineNotFoundException.class)
    public ResponseEntity<String> handleQuoteLineNotFound(
            QuoteLineNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(InvalidQuoteLineException.class)
    public ResponseEntity<String> handleInvalidQuoteLine(
            InvalidQuoteLineException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(
            AccessDeniedException ex) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {

        log.error("Unexpected server error", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Une erreur inattendue s'est produite");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(
            MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(message);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {

        Throwable cause = ex.getCause();

        while (cause != null) {

            if (cause instanceof org.hibernate.exception.ConstraintViolationException sqlEx) {

                String constraintName = sqlEx.getConstraintName();

                if ("uk_clients_email_user".equals(constraintName)) {
                    log.warn("Email déjà utilisé pour ce client");

                    return ResponseEntity
                            .status(HttpStatus.CONFLICT)
                            .body("Cet email est déjà utilisé pour ce client");
                }

                log.warn("Contrainte SQL non gérée : {}", constraintName);

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Conflit de données");
            }

            cause = cause.getCause();
        }

        log.warn("Violation d'intégrité non reconnue", ex);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Conflit de données");
    }

    @ExceptionHandler(PdfGenerationException.class)
    public ResponseEntity<String> handlePdfGenerationException(
            PdfGenerationException ex) {

        log.error("Erreur lors de la génération du PDF", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }

}