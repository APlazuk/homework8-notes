package pl.aplazuk.homework8notes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.aplazuk.homework8notes.model.Note;
import pl.aplazuk.homework8notes.repositories.NoteRepo;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    private final NoteRepo noteRepo = mock(NoteRepo.class);
    private final NoteService noteService = new NoteService(noteRepo);

    @Captor
    private ArgumentCaptor<Note> noteCaptor;

    @BeforeEach
    public void setUpRepository() {
        Note note1 = new Note();
        note1.setAuthor("author1");
        note1.setTitle("title1");
        note1.setContent("content1");
        note1.setId(1L);

        Note note2 = new Note();
        note2.setAuthor("author2");
        note2.setTitle("title2");
        note2.setContent("content2");
        note2.setId(2L);

        doReturn(Optional.of(note1)).when(noteRepo).findById(1L);

        List<Note> notes = List.of(note1, note2);
        doReturn(notes).when(noteRepo).findAll();
    }

    @Test
    public void shouldReturnSelectedNoteById() {
        //when
        Note note = noteService.findById(1L);
        //then
        assertNotNull(note);
        assertEquals(note.getAuthor(), "author1");
        assertEquals(note.getTitle(), "title1");
        assertEquals(note.getContent(), "content1");
        assertEquals(note.getId(), 1L);
        assertDoesNotThrow(() -> {
            noteService.findById(1L);
        });
    }

    @Test
    public void shouldThrowExceptionAndNotReturnAnyNoteById() {
        //when
        NoteNotFoundException noteNotFoundException = assertThrows(NoteNotFoundException.class, () -> {
            noteService.findById(3L);
        });

        //then
        assertEquals("No note has been found with id: 3", noteNotFoundException.getMessage());
    }

    @Test
    public void shouldUpdateSelectedNoteWithRandomContentByTitle() {
        //when
        noteService.applyNewNoteContentByTitle("title1");

        //then
        verify(noteRepo, times(1)).save(noteCaptor.capture());
        Note actual = noteCaptor.getValue();
        assertNotNull(actual);
        assertEquals("title1", actual.getTitle());
        assertNotEquals("content1", actual.getContent());
        assertEquals(10, actual.getContent().length());
    }


    @Test
    public void shouldNotUpdateAnyNoteWithRandomContentByTitle() {
        //when
        noteService.applyNewNoteContentByTitle("title3");

        verify(noteRepo, times(0)).save(noteCaptor.capture());
    }

    @Test
    public void shouldUpdateSelectedNote() {
        //given
        Note note = new Note();
        note.setAuthor("new author");
        note.setTitle("new title");
        note.setContent("new content");
        note.setId(1L);

        //when
        noteService.editNote(note);

        //then
        verify(noteRepo, times(1)).save(noteCaptor.capture());
        Note actual = noteCaptor.getValue();
        assertNotNull(actual);
        assertEquals("new author", actual.getAuthor());
        assertEquals("new title", actual.getTitle());
        assertEquals("new content", actual.getContent());
        assertNotEquals(10, actual.getContent().length());
    }

    @Test
    public void shouldThrowExceptionAndNotUpdateAnyNote() {
        //given
        Note note = new Note();
        note.setId(3L);

        //when
        NoteNotFoundException noteNotFoundException = assertThrows(NoteNotFoundException.class, () -> {
            noteService.editNote(note);
        });

        //then
        assertEquals("No note has been found with id: 3", noteNotFoundException.getMessage());
    }

    @Test
    public void shouldDeleteSelectedNoteById() {
        //when
        noteService.deleteNoteById(1L);

        //then
        verify(noteRepo, times(1)).findById(1L);
        verify(noteRepo, times(1)).delete(noteCaptor.capture());
        Note actual = noteCaptor.getValue();
        assertEquals("title1", actual.getTitle());
        assertEquals("content1", actual.getContent());
        assertEquals("author1", actual.getAuthor());
    }

    @Test
    public void shouldThrowExceptionAndNotDeleteAnyNoteById() {
        //when
        NoteNotFoundException noteNotFoundException = assertThrows(NoteNotFoundException.class, () -> {noteService.deleteNoteById(3L);});

        //then
        assertEquals("No note has been found with id: 3", noteNotFoundException.getMessage());
    }
}