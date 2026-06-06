package com.tresorshautebretagne.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException e) {
        return error(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getDefaultMessage())
                .findFirst()
                .orElse("Données invalides");
        return error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException e) {
        return switch (e.getMessage()) {
            case "EMAIL_ALREADY_EXISTS"      -> error(HttpStatus.CONFLICT,   "Cette adresse email est déjà utilisée");
            case "EMAIL_NOT_VERIFIED"        -> error(HttpStatus.FORBIDDEN,  "Veuillez vérifier votre adresse email avant de vous connecter");
            case "VERIFICATION_TOKEN_EXPIRED"-> error(HttpStatus.GONE,       "Le lien de vérification a expiré. Demandez-en un nouveau.");
            case "INVALID_VERIFICATION_TOKEN"-> error(HttpStatus.BAD_REQUEST,"Lien de vérification invalide");
            case "EMAIL_ALREADY_VERIFIED"    -> error(HttpStatus.CONFLICT,   "Votre email est déjà vérifié");
            case "INVALID_REFRESH_TOKEN"     -> error(HttpStatus.UNAUTHORIZED,"Token de rafraîchissement invalide");
            case "REFRESH_TOKEN_EXPIRED"     -> error(HttpStatus.UNAUTHORIZED,"Session expirée, veuillez vous reconnecter");
            default                          -> {
                e.printStackTrace();
                yield error(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur est survenue");
            }
        };
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "error", message
        ));
    }
}
