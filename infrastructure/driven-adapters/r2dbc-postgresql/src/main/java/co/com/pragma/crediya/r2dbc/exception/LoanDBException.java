package co.com.pragma.crediya.r2dbc.exception;

import co.com.pragma.crediya.model.loanapplication.LoanApplication;
import co.com.pragma.crediya.model.loanapplication.exception.LoanTypeNotExistsException;
import io.r2dbc.spi.R2dbcException;
import org.springframework.dao.DataIntegrityViolationException;
import reactor.core.Exceptions;

public class LoanDBException extends RuntimeException {

    LoanDBException() {
    }

    public static RuntimeException valideDBException(DataIntegrityViolationException exception, LoanApplication loanApplication) {
        DbError db = extractDbError(exception);

        if (isForeignKeyViolation(db)) {
            RuntimeException mapped = mapForeignKeyViolation(db.message(), loanApplication);
            if (mapped != null) return mapped;
        }

        return exception;
    }


    private static RuntimeException mapForeignKeyViolation(String msg, LoanApplication loanApplication) {
        if (contains(msg, "fk_solicitudes_tipo_prestamo")) {
            return new LoanTypeNotExistsException(String.valueOf(loanApplication.getLoanType()));
        }
        return null;
    }

    private static boolean isForeignKeyViolation(DbError db) {
        return "23503".equals(db.sqlState()) || containsForeignKeyKeyword(db.message());
    }

    private static DbError extractDbError(DataIntegrityViolationException exception) {
        Throwable root = Exceptions.unwrap(exception);
        R2dbcException r2 = findCause(root, R2dbcException.class);

        String sqlState = (r2 != null) ? r2.getSqlState() : null;

        String msg;
        if (r2 != null && r2.getMessage() != null) {
            msg = r2.getMessage();
        } else if (root.getMessage() != null) {
            msg = root.getMessage();
        } else {
            msg = exception.getMessage();
        }

        return new DbError(sqlState, msg);
    }


    private static boolean containsForeignKeyKeyword(String msg) {
        if (msg == null) return false;
        String m = msg.toLowerCase();
        return m.contains("foreign key")
                || m.contains("violates foreign key constraint");
    }

    private static boolean contains(String msg, String needle) {
        return msg != null && msg.contains(needle);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> T findCause(Throwable t, Class<T> type) {
        while (t != null && t.getCause() != t) {
            if (type.isInstance(t)) return (T) t;
            t = t.getCause();
        }
        return null;
    }

    private record DbError(String sqlState, String message) {
    }
}
