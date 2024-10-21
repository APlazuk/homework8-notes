package pl.aplazuk.homework8notes.controller;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.aplazuk.homework8notes.service.NoteAlreadyExistsException;
import pl.aplazuk.homework8notes.service.NoteNotFoundException;

import java.util.List;

@RestControllerAdvice
public class NoteExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleException(MethodArgumentNotValidException validException) {
        List<String> errors = validException.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(NoteAlreadyExistsException.class)
    public ResponseEntity<String> handleNoteAlreadyExistsException(NoteAlreadyExistsException validException) {
        String errorMessage = validException.getMessage();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
    }

    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<String> handleNoteNotFoundEException(NoteNotFoundException validException) {
        String errorMessage = validException.getMessage();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }
}
