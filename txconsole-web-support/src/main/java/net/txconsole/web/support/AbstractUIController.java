package net.txconsole.web.support;

import net.txconsole.core.InputException;
import net.txconsole.core.NotFoundException;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

public abstract class AbstractUIController extends AbstractController {

    protected final Strings strings;

    @Autowired
    public AbstractUIController(ErrorHandler errorHandler,
                                Strings strings) {
        super(errorHandler);
        this.strings = strings;
    }

    protected ResponseEntity<String> getMessageResponse(String message) {
        // Header
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/plain; charset=utf-8");
        // OK
        return new ResponseEntity<>(message, responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> onNotFoundException(Locale locale, NotFoundException ex) {
        // Returns a message to display to the user
        String message = ex.getLocalizedMessage(strings, locale);
        // OK
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InputException.class)
    public ResponseEntity<String> onInputException(Locale locale, InputException ex) {
        // Returns a message to display to the user
        String message = ex.getLocalizedMessage(strings, locale);
        // OK
        return getMessageResponse(message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> onException(Locale locale, Exception ex) throws Exception {
        // Ignores access errors
        if (ex instanceof AccessDeniedException) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Error message
        ErrorMessage error = errorHandler.handleError(locale, ex);
        // Returns a message to display to the user
        String message = strings.get(locale, "general.error.full", error.getMessage(), error.getUuid());
        // Ok
        return getMessageResponse(message);
    }

}
