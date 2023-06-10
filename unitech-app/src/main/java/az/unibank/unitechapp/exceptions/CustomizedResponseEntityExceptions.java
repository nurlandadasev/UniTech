package az.unibank.unitechapp.exceptions;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Exception-Handling-for-rest-with-spring
 * https://www.baeldung.com/exception-handling-for-rest-with-spring
 */
@Slf4j
@RestController
@ControllerAdvice
public class CustomizedResponseEntityExceptions extends ResponseEntityExceptionHandler {


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public final ApplicationException handleAllException(Exception ex, WebRequest request){
        log.error("Exception body: \n",ex);
        return new BusinessException(ex.getMessage(),request.getDescription(false));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(BusinessException.class)
    public final BusinessException handleBusinessException(BusinessException ex, WebRequest request){
        log.error("Exception body: \n",ex);
        return ex;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> validationMessages = ex.getBindingResult().getFieldErrors().stream().map(f -> f.getField() + " must be " +f.getCode()).collect(Collectors.toList());
        log.error("org.springframework.web.bind.MethodArgumentNotValidException body: {}, \n{}",ex, ex.getMessage());
        BusinessException validation_failed = new BusinessException("Validation failed", validationMessages.toString());
        return new ResponseEntity<>(validation_failed, HttpStatus.BAD_REQUEST);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public final ApplicationException handleConstraintViolationException(ConstraintViolationException ex, WebRequest request){
        List<String> constraintViolationMessages = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());

        log.error("ConstraintViolationException body: {}, \n{}",ex, ex.getMessage());


        return new BusinessException("ConstraintViolationException",constraintViolationMessages.toString());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public final ApplicationException handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request){
        log.error("DataIntegrityViolationException body: {}, \n{}",ex, ex.getMessage());

        String message = NestedExceptionUtils.getMostSpecificCause(ex).getMessage();

        return new BusinessException(message,ex.getMessage());
    }


}
