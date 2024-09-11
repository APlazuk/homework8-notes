package pl.aplazuk.homework8notes.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.aplazuk.homework8notes.model.Note;
import pl.aplazuk.homework8notes.service.NoteService;

import java.util.List;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Note>> getNotes() {
        List<Note> notes = noteService.findAll();
        return !notes.isEmpty() ? ResponseEntity.ok(notes) : ResponseEntity.notFound().build();
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Note> getNote(@PathVariable Long id) {
        Note noteById = noteService.findById(id);
        return noteById != null ? ResponseEntity.ok(noteById) : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Note> saveNote(@RequestBody @Valid Note note) {
        Note savedNote = noteService.saveNote(note);
        return savedNote != null ? ResponseEntity.status(HttpStatus.CREATED).body(savedNote) : ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PutMapping(value = "/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> editNote(@RequestBody @Valid Note note) {
        noteService.editNote(note);
        return ResponseEntity.ok("Note with id " + note.getId() + " has been edited");
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteNote(@PathVariable Long id) {
        noteService.deleteNoteById(id);
        return ResponseEntity.ok().build();
    }
}
