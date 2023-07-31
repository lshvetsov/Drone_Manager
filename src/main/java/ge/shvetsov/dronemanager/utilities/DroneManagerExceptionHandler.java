package ge.shvetsov.dronemanager.utilities;

import ge.shvetsov.dronemanager.utilities.exception.ApplicationException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class DroneManagerExceptionHandler {

    public static final String MES1_SQL_CONSTRAINT = "Request validation hasn't passed";
    public static final Map<String, ApplicationException> exceptions = Map.of(
            "MS0", new ApplicationException("MS0", "General exception"),
            "MS1", new ApplicationException("MS1", "Drone not found"),
            "MS2", new ApplicationException("MS2", "Drone need charging"),
            "MS3", new ApplicationException("MS3", "Drone overload"),
            "MS4", new ApplicationException("MS4", "Drone is on mission"),
            "MS5", new ApplicationException("MS5", "Drone is not ready to be launched"),
            "MS6", new ApplicationException("MS6", "Impossible to receive the drone"),
            "MS7", new ApplicationException("MS7", "Order for the requested drone is not found"),
            "MS8", new ApplicationException("MS8", "Medication not found")
    );

    @ExceptionHandler({SQLIntegrityConstraintViolationException.class, HttpMessageNotReadableException.class,
            ConstraintViolationException.class})
    public ResponseEntity<String> handleIntegrityConstraintViolation() {
        return new ResponseEntity<>(MES1_SQL_CONSTRAINT, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<String> handleNotFound() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ApplicationException.class})
    public ResponseEntity<String> handleApplicationExceptions(Exception exception) {
        return new ResponseEntity<>(exception.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> handleOtherExceptions(Exception exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
