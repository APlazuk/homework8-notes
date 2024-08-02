package pl.aplazuk.homework8notes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.aplazuk.homework8notes.model.Note;

@Repository
public interface NoteRepo extends JpaRepository<Note, Long> {
}
