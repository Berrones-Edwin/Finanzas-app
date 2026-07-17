package com.bitly.exceptions;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        private ProblemDetail createProblem(
                        HttpStatus status,
                        String detail,
                        HttpServletRequest request,
                        LinkedHashMap<String, String> errors

        ) {

                ProblemDetail problem = ProblemDetail.forStatus(status);
                problem.setTitle(status.getReasonPhrase());
                problem.setDetail(detail);
                problem.setProperty("errors", errors);
                problem.setProperty("path", request.getRequestURI());
                problem.setProperty("timestamp", LocalDateTime.now());

                if (errors != null && !errors.isEmpty()) {
                        problem.setProperty("errors", errors);
                }

                return problem;
        }

        @ExceptionHandler(InsufficientFundsException.class)
        public ResponseEntity<ProblemDetail> handleInsufficientFunds(
                        InsufficientFundsException ex,
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

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ProblemDetail> handleValidation(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest request) {

                var errors = new LinkedHashMap<String, String>();

                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                return ResponseEntity.badRequest().body(
                                createProblem(HttpStatus.BAD_REQUEST, "Validation failed", request, errors));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ProblemDetail> handleException(
                        Exception ex,
                        HttpServletRequest request) {

                log.error("Internal server error, message={}", ex.getMessage());

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(
                                                createProblem(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                "An unexpected error ocurred",
                                                                request,
                                                                null));
        }

}
