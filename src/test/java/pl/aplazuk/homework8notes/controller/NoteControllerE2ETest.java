package pl.aplazuk.homework8notes.controller;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import pl.aplazuk.homework8notes.model.Note;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class NoteControllerE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    Flyway flyway;

    @Test
    public void shouldReturnSelectedNotes() {
        ResponseEntity<List<Note>> exchange = restTemplate.exchange("http://localhost:" + port + "/notes",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<Note>>() {
                }
        );
        List<Note> actual = exchange.getBody();
        assertFalse(actual.isEmpty());
        assertEquals("Bartek", actual.get(actual.size() - 1).getAuthor());
        assertEquals("Zawsze mam 100% pokrycia testami", actual.get(actual.size() - 1).getContent());
        assertEquals("koduje z testami", actual.get(actual.size() - 1).getTitle());
        assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }

    @Test
    public void shouldReturnSelectedNoteById() {
        ResponseEntity<Note> exchange = restTemplate.exchange("http://localhost:" + port + "/notes/4",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                Note.class
        );
        Note actual = exchange.getBody();
        assertEquals("Bartek", actual.getAuthor());
        assertEquals("Zawsze mam 100% pokrycia testami", actual.getContent());
        assertEquals("koduje z testami", actual.getTitle());
        assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }

    @Test
    public void shouldAddNewNote() {
        Note note = new Note();
        note.setAuthor("new author");
        note.setTitle("new title");
        note.setContent("new content");
        note.setCreationDate(LocalDateTime.now());
        HttpEntity httpEntity = new HttpEntity(note);

        ResponseEntity<Note> exchange = restTemplate.exchange("http://localhost:" + port + "/notes/add",
                HttpMethod.POST,
                httpEntity,
                Note.class
        );

        assertEquals(HttpStatus.CREATED, exchange.getStatusCode());
    }

    @Test
    public void shouldEditNote() {
        Note note = new Note();
        note.setId(1L);
        note.setAuthor("new author");
        note.setTitle("new title");
        note.setContent("new content");
        note.setCreationDate(LocalDateTime.now());
        HttpEntity httpEntity = new HttpEntity(note);

        ResponseEntity<String> exchange = restTemplate.exchange("http://localhost:" + port + "/notes/edit",
                HttpMethod.PUT,
                httpEntity,
                String.class
        );
        assertEquals(HttpStatus.OK, exchange.getStatusCode());
        assertEquals("Note with id 1 has been edited", exchange.getBody());
    }

    @Test
    public void shouldDeleteNote() {
        ResponseEntity<String> exchange = restTemplate.exchange("http://localhost:" + port + "/notes/delete/1",
                HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }

    @AfterEach
    public void cleanup() {
        flyway.clean();
        flyway.migrate();
    }

}