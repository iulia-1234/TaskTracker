package com.tracker.tasktracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@SuppressWarnings("unused")
public class GlobalExceptionHandler {

    private static final String GENERIC_ERROR_MESSAGE = "An unexpected error occurred";

    public String formatErrorCode(Exception exception) {
        return exception.getClass().getSimpleName();
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiExceptions(ApiException apiException) {
        return ResponseEntity
                .status(apiException.getHttpStatus())
                .body(new ErrorResponse(
                        apiException.getMessage(),
                        formatErrorCode(apiException)
                ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        String message = String.format(
                "Invalid value '%s' for parameter '%s'. Expected type: %s",
                exception.getValue(),
                exception.getName(),
                exception.getRequiredType() != null ? exception.getRequiredType().getSimpleName() : "unknown"
                );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        message,
                        formatErrorCode(exception)
                ));
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ErrorResponse> handleMissingPathVariableException(MissingPathVariableException exception) {
        String message = String.format(
                "Missing required path variable '%s'",
                exception.getVariableName()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        message,
                        formatErrorCode(exception)
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No validation errors found"));
        String message = String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        message,
                        formatErrorCode(exception)
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleEnumsFromHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getMostSpecificCause();
        String message = "Malformed JSON request";
        if (cause != null && cause.getClass().getSimpleName().equals("InvalidFormatException")) {
            try {
                Object value = cause.getClass().getMethod("getValue").invoke(cause);
                String field = "unknown";
                try {
                    Object pathList = cause.getClass().getMethod("getPath").invoke(cause);
                    if (pathList instanceof java.util.List<?> path && !path.isEmpty()) {
                        Object firstRef = path.getFirst();
                        try {
                            field = (String) firstRef.getClass().getMethod("getFieldName").invoke(firstRef);
                        } catch (Exception ignored) {}
                    }
                } catch (Exception ignored) {}
                if ("unknown".equals(field)) {
                    String msg = cause.getMessage();
                    if (msg != null && msg.contains("[\"")) {
                        int start = msg.indexOf("[\"") + 2;
                        int end = msg.indexOf("\"]", start);
                        if (start >= 2 && end > start) {
                            field = msg.substring(start, end);
                        }
                    }
                }
                message = String.format("Invalid value '%s' for field '%s'", value, field);
            } catch (Exception ignored) {
                message = cause.getMessage();
            }
        } else if (cause != null && cause.getClass().getSimpleName().equals("DateTimeParseException")) {
            try {
                Object value = cause.getClass().getMethod("getParsedString").invoke(cause);
                String field = "unknown";
                Throwable parent = exception.getCause();
                while (parent != null) {
                    if (parent.getClass().getSimpleName().equals("JsonMappingException")) {
                        try {
                            Object pathList = parent.getClass().getMethod("getPath").invoke(parent);
                            if (pathList instanceof java.util.List<?> path && !path.isEmpty()) {
                                Object firstRef = path.getFirst();
                                try {
                                    field = (String) firstRef.getClass().getMethod("getFieldName").invoke(firstRef);
                                } catch (Exception ignored) {}
                            }
                        } catch (Exception ignored) {}
                        break;
                    }
                    parent = parent.getCause();
                }
                if ("unknown".equals(field)) {
                    message = String.format(
                            "Invalid date value '%s'. Expected format: yyyy-MM-dd'T'HH:mm:ss",
                            value
                    );
                } else {
                    message = String.format(
                            "Invalid date value '%s' for field '%s'. Expected format: yyyy-MM-dd'T'HH:mm:ss",
                            value,
                            field
                    );
                }

            } catch (Exception ignored) {
                message = cause.getMessage();
            }
        } else if (cause != null) {
            message = cause.getMessage();
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message, formatErrorCode(exception)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedExceptions(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        GENERIC_ERROR_MESSAGE,
                        formatErrorCode(exception)
                ));
    }

}
