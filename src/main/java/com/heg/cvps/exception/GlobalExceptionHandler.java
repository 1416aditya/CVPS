package com.heg.cvps.exception;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Catch missing records (like when a validation gate lookup returns empty)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<LocalErrorResponse> handleNotFoundException(NoSuchElementException ex, WebRequest request) {
        LocalErrorResponse error = new LocalErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Record Not Found",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // 2. Catch bad logic arguments (like typing an invalid action state string)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<LocalErrorResponse> handleBadRequestException(IllegalArgumentException ex, WebRequest request) {
        LocalErrorResponse error = new LocalErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request Parameters",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 3. Global fallback to catch any other unhandled system failures safely
    @ExceptionHandler(Exception.class)
    public ResponseEntity<LocalErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        LocalErrorResponse error = new LocalErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

// =============================================================================
// 🆕 EMBEDDED STRUCTURED RESPONSE PAYLOAD (Bypasses Package Mismatches)
// =============================================================================
class LocalErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public LocalErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // Getters and Setters
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
}