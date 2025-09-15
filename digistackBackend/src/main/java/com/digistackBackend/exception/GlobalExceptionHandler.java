package com.digistackBackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorDetails> buildResponse(Exception ex, WebRequest we, HttpStatus status) {
        return new ResponseEntity<>(
                new ErrorDetails(ex.getMessage(), we.getDescription(false)),
                status
        );
    }

    // 🔐 User / Auth Exceptions
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> handleUserAlreadyExists(UserAlreadyExistsException ex, WebRequest we) {
        return buildResponse(ex, we, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleUserNotFound(UserNotFoundException ex, WebRequest we) {
        return buildResponse(ex, we, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorDetails> handleInvalidCredentials(InvalidCredentialsException ex, WebRequest we) {
        return buildResponse(ex, we, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorDetails> handleUnauthorized(UnauthorizedException ex, WebRequest we) {
        return buildResponse(ex, we, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorDetails> handleForbidden(ForbiddenException ex, WebRequest we) {
        return buildResponse(ex, we, HttpStatus.FORBIDDEN);
    }

    // 📊 Quota / Usage Exceptions
    @ExceptionHandler(QuotaExceededException.class)
    public ResponseEntity<ErrorDetails> handleQuotaExceeded(QuotaExceededException ex, WebRequest we) {
        return buildResponse(ex, we, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(MonthlyLimitExceededException.class)
    public ResponseEntity<ErrorDetails> handleMonthlyLimitExceeded(MonthlyLimitExceededException ex, WebRequest we) {
        return buildResponse(ex, we, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(QuotaNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleQuotaNotFound(QuotaNotFoundException ex, WebRequest we) {
        return buildResponse(ex, we, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(QuotaPersistenceException.class)
    public ResponseEntity<ErrorDetails> handleQuotaPersistence(QuotaPersistenceException ex, WebRequest we) {
        return buildResponse(ex, we, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 🔑 Keyword / Cache Exceptions
    @ExceptionHandler(KeywordNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleKeywordNotFound(KeywordNotFoundException ex, WebRequest we) {
        return buildResponse(ex, we, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(KeywordCacheException.class)
    public ResponseEntity<ErrorDetails> handleKeywordCache(KeywordCacheException ex, WebRequest we) {
        return buildResponse(ex, we, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidKeywordException.class)
    public ResponseEntity<ErrorDetails> handleInvalidKeywordFormat(InvalidKeywordException ex, WebRequest we) {
        return buildResponse(ex, we, HttpStatus.BAD_REQUEST);
    }

    // 🌍 External API Exceptions
    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorDetails> handleExternalApi(ExternalApiException ex, WebRequest we) {
        return buildResponse(ex, we, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(ExternalApiRateLimitException.class)
    public ResponseEntity<ErrorDetails> handleExternalApiRateLimit(ExternalApiRateLimitException ex, WebRequest we) {
        return buildResponse(ex, we, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(ExternalApiAuthException.class)
    public ResponseEntity<ErrorDetails> handleExternalApiAuth(ExternalApiAuthException ex, WebRequest we) {
        return buildResponse(ex, we, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ExternalApiResponseException.class)
    public ResponseEntity<ErrorDetails> handleExternalApiResponse(ExternalApiResponseException ex, WebRequest we) {
        return buildResponse(ex, we, HttpStatus.BAD_GATEWAY);
    }

    // ⚙️ System Exceptions
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ErrorDetails> handleDatabase(DatabaseException ex, WebRequest we) {
        return buildResponse(ex, we, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CacheUnavailableException.class)
    public ResponseEntity<ErrorDetails> handleCacheUnavailable(CacheUnavailableException ex, WebRequest we) {
        return buildResponse(ex, we, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorDetails> handleServiceUnavailable(ServiceUnavailableException ex, WebRequest we) {
        return buildResponse(ex, we, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorDetails> handleInternalServer(InternalServerException ex, WebRequest we) {
        return buildResponse(ex, we, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 🔄 Generic fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGeneric(Exception ex, WebRequest we) {
        return buildResponse(ex, we, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFound(ResourceNotFoundException ex, WebRequest we){
        return buildResponse(ex,we,HttpStatus.NOT_FOUND);
    }
}
