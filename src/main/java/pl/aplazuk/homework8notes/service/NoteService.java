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
    public void applyNewNoteContentByTitle(String title) {
        List<Note> notes = noteRepo.findAll();
        String randomContent = RandomStringUtils.randomAlphabetic(10);
        Optional<Note> optionalNote = notes.stream().filter(note -> title.equals(note.getTitle())).findFirst();
        optionalNote
                .ifPresent(note -> {
                    note.setContent(randomContent);
                    noteRepo.save(note);
                });
    }

    @Transactional
    public Note saveNote(Note note) {
        if (note.getId() == null) {
            return noteRepo.save(note);
        } else if (noteRepo.findById(note.getId()).isPresent()) {
            throw new NoteAlreadyExistsException("Note with given id already exists: " + note.getId());
        }
        return null;
    }

    @Transactional
    public void editNote(Note note) {
        Optional<Note> optionalNote = noteRepo.findById(note.getId());
        if (optionalNote.isPresent()) {
            noteRepo.save(note);
        } else {
            throw new NoteNotFoundException("No note has been found with id: " + note.getId());
        }
    }

    @Transactional
    public void deleteNoteById(Long id) {
        noteRepo.findById(id)
                .ifPresentOrElse(noteRepo::delete, () -> {
                    throw new NoteNotFoundException("No note has been found with id: " + id);
                });
    }
}
