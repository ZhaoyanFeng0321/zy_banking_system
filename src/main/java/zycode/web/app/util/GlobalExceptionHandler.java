package zycode.web.app.util;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import javax.naming.OperationNotSupportedException;
import javax.security.auth.login.AccountNotFoundException;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiError> handleAccountNotFound(AccountNotFoundException ex, HttpServletRequest request) {
        logger.warn("Account not found at [{}]: {}", request.getRequestURI(), ex.getMessage());

        ApiError error = new ApiError(
                "Account not found.",
                "ERR_ACCOUNT_NOT_FOUND",
                404
        );

        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNoSuchElementException(NoSuchElementException ex, HttpServletRequest request) {
        logger.error("Element Not Found error at [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);

        ApiError error = new ApiError(
                "Something went wrong.",
                "ERR_INVALID_REQUEST",
                400
        );

        return ResponseEntity.status(400).body(error);
    }
    //UnsupportedOperationException
    @ExceptionHandler(OperationNotSupportedException.class)
    public ResponseEntity<ApiError> handleOperationNotSupportedException(OperationNotSupportedException ex, HttpServletRequest request) {
        logger.error("Unsupported Operation error at [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);

        ApiError error = new ApiError(
                "Something went wrong.",
                "ERR_INVALID_REQUEST",
                400
        );

        return ResponseEntity.status(400).body(error);
    }

    // Handles unauthorized (e.g., bad token, no login)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                "Unauthorized access. Please log in.",
                "ERR_UNAUTHORIZED",
                401
        );
        return ResponseEntity.status(401).body(error);
    }

    // Optional: If using method-level security (@PreAuthorize), handle AccessDenied
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                "You do not have permission to access this resource.",
                "ERR_FORBIDDEN",
                403
        );
        return ResponseEntity.status(403).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, HttpServletRequest request) {
        logger.error("Unexpected error at [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);

        ApiError error = new ApiError(
                "Something went wrong.",
                "ERR_INTERNAL_SERVER",
                500
        );

        return ResponseEntity.status(500).body(error);
    }
}
