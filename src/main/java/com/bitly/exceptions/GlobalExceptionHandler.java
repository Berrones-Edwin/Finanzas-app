package com.bitly.exceptions;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

        private ProblemDetail createProblem(
                        HttpStatus status,
                        String detail,
                        HttpServletRequest request,
                        LinkedHashMap<String, String> errors

        ) {

                ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
                problem.setTitle(status.getReasonPhrase());
                problem.setProperty("path", request.getRequestURI());
                problem.setProperty("timestamp", LocalDateTime.now());

                if (errors != null && !errors.isEmpty()) {
                        problem.setProperty("errors", errors);
                }

                return problem;
        }

        private void addCommonProperties(ProblemDetail problem, WebRequest request) {

                problem.setProperty("timestamp", LocalDateTime.now());

                if (request instanceof ServletWebRequest servletWebRequest) {
                        problem.setProperty("path", servletWebRequest.getRequest().getRequestURI());
                }
        }

        @Override
        protected ResponseEntity<Object> handleMethodArgumentNotValid(
                        MethodArgumentNotValidException ex,
                        HttpHeaders headers,
                        HttpStatusCode status,
                        WebRequest request) {

                var errors = new LinkedHashMap<String, String>();

                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                ProblemDetail body = ex.getBody();
                body.setDetail("Validation failed");
                body.setProperty("errors", errors);

                return handleExceptionInternal(ex, body, headers, status, request);
        }

        @Override
        protected ResponseEntity<Object> handleExceptionInternal(
                        Exception ex,
                        Object body,
                        HttpHeaders headers,
                        HttpStatusCode statusCode,
                        WebRequest request) {

                if (statusCode.is5xxServerError()) {
                        log.error("Unhandled exception at {}", request.getDescription(false), ex);
                }

                if (body == null && !(ex instanceof ErrorResponse)) {

                        String detail = statusCode.is5xxServerError()
                                        ? "An unexpected error occurred"
                                        : ex.getMessage();

                        ProblemDetail problem = ProblemDetail.forStatusAndDetail(statusCode, detail);
                        HttpStatus status = HttpStatus.resolve(statusCode.value());

                        if (status != null) {
                                problem.setTitle(status.getReasonPhrase());
                        }

                        body = problem;
                }

                return super.handleExceptionInternal(ex, body, headers, statusCode, request);
        }

        @Override
        protected ResponseEntity<Object> createResponseEntity(
                        Object body,
                        HttpHeaders headers,
                        HttpStatusCode statusCode,
                        WebRequest request) {

                if (body instanceof ProblemDetail problem) {
                        addCommonProperties(problem, request);
                }

                return super.createResponseEntity(body, headers, statusCode, request);
        }

        @ExceptionHandler(InsufficientFundsException.class)
        public ResponseEntity<ProblemDetail> handleInsufficientFunds(
                        InsufficientFundsException ex,
                        HttpServletRequest request) {

                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(
                                createProblem(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage(), request, null));
        }

        @ExceptionHandler(TransactionTypeMismatchException.class)
        public ResponseEntity<ProblemDetail> handleTransactionTypeMismatch(
                        TransactionTypeMismatchException ex,
                        HttpServletRequest request) {

                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(
                                createProblem(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage(), request, null));
        }

        @ExceptionHandler({

                        CategoryAlreadyExistsException.class,
                        AccountAlreadyExistsException.class,
                        BudgetAlreadyExistsException.class

        })

        public ResponseEntity<ProblemDetail> handleConflict(
                        RuntimeException ex,
                        HttpServletRequest request) {

                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(
                                                createProblem(HttpStatus.CONFLICT, ex.getMessage(), request, null));
        }

        @ExceptionHandler(RefreshTokenException.class)
        public ResponseEntity<ProblemDetail> handleRefreshToken(
                        RefreshTokenException ex,
                        HttpServletRequest request) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(createProblem(HttpStatus.UNAUTHORIZED, ex.getMessage(), request, null));
        }

        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<ProblemDetail> handleNotFound(
                        EntityNotFoundException ex, HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(createProblem(HttpStatus.NOT_FOUND, ex.getMessage(), request, null));
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ProblemDetail> handleBadRequest(
                        IllegalArgumentException ex, HttpServletRequest request) {
                return ResponseEntity.badRequest()
                                .body(createProblem(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ProblemDetail> handleUnexpectedException(
                        Exception ex,
                        HttpServletRequest request) {

                if (ex instanceof ErrorResponse errorResponse) {

                        if (errorResponse.getStatusCode().is5xxServerError()) {
                                log.error("Unhandled error response at {} {}", request.getMethod(),
                                                request.getRequestURI(), ex);
                        }

                        ProblemDetail body = errorResponse.getBody();
                        body.setProperty("path", request.getRequestURI());
                        body.setProperty("timestamp", LocalDateTime.now());

                        return ResponseEntity.status(errorResponse.getStatusCode())
                                        .headers(errorResponse.getHeaders())
                                        .body(body);
                }

                log.error("Unhandled exception at {} {}", request.getMethod(), request.getRequestURI(), ex);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(
                                                createProblem(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                "An unexpected error occurred",
                                                                request,
                                                                null));
        }

}
