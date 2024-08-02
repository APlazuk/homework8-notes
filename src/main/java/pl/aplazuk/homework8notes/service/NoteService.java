package pl.aplazuk.homework8notes.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pl.aplazuk.homework8notes.model.Note;
import pl.aplazuk.homework8notes.repositories.NoteRepo;

import java.util.List;
import java.util.Optional;

@Service
public class NoteService {

    private final NoteRepo noteRepo;

    public NoteService(NoteRepo noteRepo) {
        this.noteRepo = noteRepo;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<Note> findAll() {
        return noteRepo.findAll();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Optional<Note> findById(Long id) {
        return noteRepo.findById(id);
    }

    @Transactional
    public Note saveNote(Note note) {
        return noteRepo.save(note);
    }

    @Transactional
    public void editNote(Note note) {
        noteRepo.save(note);
    }


}
