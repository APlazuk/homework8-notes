package pl.aplazuk.homework8notes.service;

import org.apache.commons.lang3.RandomStringUtils;
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
    public Note findById(Long id) {
        return noteRepo.findById(id).orElseThrow(() -> new NoteNotFoundException("No note has been found with id: " + id));
    }

    @Transactional
    public void applyNewNoteTitleByAuthor(String author) {
        List<Note> notes = noteRepo.findAll();
        String randomTitle = RandomStringUtils.randomAlphabetic(10);
        notes.stream().filter(note -> note.getAuthor().equals(author)).findFirst()
                .ifPresent(note -> {
                    note.setTitle(randomTitle);
                    noteRepo.save(note);
                });
    }

    @Transactional
    public Note saveNote(Note note) {
        return noteRepo.save(note);
    }

    @Transactional
    public void editNote(Note note) {
        Optional<Note> optionalNote = noteRepo.findById(note.getId());
        optionalNote
                .ifPresentOrElse(noteRepo::save, () -> {
                    throw new NoteNotFoundException("No note has been found with id: " + note.getId());
                });
    }

    @Transactional
    public void deleteNote(Long id) {
        noteRepo.findById(id)
                .ifPresentOrElse(noteRepo::delete, () -> {
                    throw new NoteNotFoundException("No note has been found with id: " + id);
                });
    }
}
