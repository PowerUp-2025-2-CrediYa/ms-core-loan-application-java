package co.com.pragma.crediya.api.helper;


import co.com.pragma.crediya.model.loanapplication.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ExceptionHelper {

    public static HttpStatus resolveStatus(Throwable ex) {

        if (ex instanceof org.springframework.web.server.ResponseStatusException rse) {
            return HttpStatus.valueOf(rse.getStatusCode().value());
        }
        if (ex instanceof InvalidAmountRangeException
                || ex instanceof InvalidLoanException
                || ex instanceof InvalidLoanTermException
                || ex instanceof LoanTypeNotExistsException
        ) {
            return HttpStatus.CONFLICT;
        }
        if (ex instanceof UserNotExistsException) {
            return HttpStatus.UNPROCESSABLE_ENTITY;
        }

        if (ex instanceof InvalidLoanException || ex instanceof BadRequestException) {
            return HttpStatus.BAD_REQUEST;
        }

        if (JsonErrorMessageFactory.isJsonDecodeError(ex)) {
            return HttpStatus.BAD_REQUEST; // 400
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public static Throwable unwrap(Throwable ex) {
        if (ex == null) return null;
        Throwable unwrapped = reactor.core.Exceptions.unwrap(ex);
        Throwable mostSpecific = org.springframework.core.NestedExceptionUtils.getMostSpecificCause(unwrapped);
        return (mostSpecific != null ? mostSpecific : unwrapped);
    }

}
