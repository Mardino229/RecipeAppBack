package com.nidas.recipesapp.controller.advice;

import com.nidas.recipesapp.Exception.EmailAlreadyExistsException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ApplicationControllerAdvice {

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(value = ExpiredJwtException.class)
    public @ResponseBody ProblemDetail badCredentialsException(final ExpiredJwtException exception) {
        ApplicationControllerAdvice.log.error(exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(FORBIDDEN, "Token expired");
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(FORBIDDEN)
    public @ResponseBody ProblemDetail handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        ApplicationControllerAdvice.log.error(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(FORBIDDEN, e.getMessage());
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(value = AuthorizationDeniedException.class)
    public @ResponseBody ProblemDetail badCredentialsException(final AuthorizationDeniedException exception) {
        ApplicationControllerAdvice.log.error(exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(FORBIDDEN, "Vous n'ètes pas autorisé à effectué cette action");
    }

//    @ResponseStatus(FORBIDDEN)
//    @ExceptionHandler(value = DataIntegrityViolationException.class)
//    public @ResponseBody ProblemDetail badCredentialsException(final DataIntegrityViolationException exception) {
//        ApplicationControllerAdvice.log.error(exception.getMessage(), exception);
//        return ProblemDetail.forStatusAndDetail(FORBIDDEN, "Cet email ou ce pseudo existe déja");
//    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(value = BadCredentialsException.class)
    public @ResponseBody ProblemDetail badCredentialsException(final BadCredentialsException exception) {
        ApplicationControllerAdvice.log.error(exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(UNAUTHORIZED, "Email ou mot de passe incorrect");
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(value = {NoSuchElementException.class, MalformedJwtException.class, })
    public @ResponseBody ProblemDetail badCredentialsException(NoSuchElementException exception) {
        ApplicationControllerAdvice.log.error(exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(UNAUTHORIZED, "Token invalide");
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(value = Exception.class)
    public Map<String, String> exceptionHandler() {
        return Map.of("message", "exception");
    }

}


