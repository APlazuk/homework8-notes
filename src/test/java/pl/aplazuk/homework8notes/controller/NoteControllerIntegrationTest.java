package pl.aplazuk.homework8notes.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.aplazuk.homework8notes.model.Note;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NoteControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    Flyway flyway;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void shouldReturnSelectedNoteById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/notes/1"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andReturn();

        Note actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Note.class);
        assertNotNull(actual);
        assertEquals(1, actual.getId());
        assertEquals("Kasia", actual.getAuthor());
        assertEquals("Nie wiem co to testy", actual.getContent());
        assertEquals("testy", actual.getTitle());
    }

    @Test
    public void shouldReturn404StatusWhenGetNoteById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/notes/10"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()))
                .andReturn();

        int actual = mvcResult.getResponse().getStatus();

        assertNotNull(actual);
        assertEquals(HttpStatus.NOT_FOUND.value(), actual);
    }

    @Test
    public void shouldAddNewNote() throws Exception {
        Note note = new Note();
        note.setAuthor("new author");
        note.setTitle("new title");
        note.setContent("new content");
        note.setCreationDate(LocalDateTime.now());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/notes/add")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(note)))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()))
                .andReturn();

        Note actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Note.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(5, actual.getId());
    }

    @Test
    public void shouldReturn409StatusWhenAddNewNoteExists() throws Exception {
        Note note = new Note();
        note.setId(1L);
        note.setAuthor("new author");
        note.setTitle("new title");
        note.setContent("new content");
        note.setCreationDate(LocalDateTime.now());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/notes/add")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(note)))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CONFLICT.value()))
                .andReturn();

        String actual = mvcResult.getResolvedException().getMessage();
        assertNotNull(actual);
        assertEquals("Note with given id already exists: 1", actual);
    }

    @Test
    public void shouldReturn400StatusWhenAddNoteWithoutMandatoryFields() throws Exception {
        Note note = new Note();
        note.setAuthor("new author");
        note.setTitle("new title");
        note.setContent("new content");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/notes/add")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(note)))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        String actual = objectMapper.readValue(content, new TypeReference<List<String>>() {
        }).get(0);

        assertEquals("Published date cannot be null", actual);
    }

    @Test
    public void shouldEditNote() throws Exception {
        Note note = new Note();
        note.setId(1L);
        note.setAuthor("new author");
        note.setTitle("new title");
        note.setContent("new content");
        note.setCreationDate(LocalDateTime.now());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/notes/edit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(note)))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andReturn();

        String actual = mvcResult.getResponse().getContentAsString();
        assertNotNull(actual);
        assertEquals("Note with id 1 has been edited", actual);
    }

    @Test
    public void shouldReturn404StatusWhenEditNote() throws Exception {
        Note note = new Note();
        note.setId(7L);
        note.setAuthor("new author");
        note.setTitle("new title");
        note.setContent("new content");
        note.setCreationDate(LocalDateTime.now());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/notes/edit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(note)))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()))
                .andReturn();

        String actual = mvcResult.getResolvedException().getMessage();
        assertNotNull(actual);
        assertEquals("No note has been found with id: 7", actual);
    }

    @Test
    public void shouldReturn400StatusWhenEditNoteWithoutMandatoryFields() throws Exception {
        Note note = new Note();
        note.setId(1L);
        note.setTitle("new title");
        note.setCreationDate(LocalDateTime.now());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/notes/edit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(note)))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        String actual = objectMapper.readValue(content, new TypeReference<List<String>>() {
        }).get(0);

        assertEquals("Please provide valid note author", actual);
    }

    @Test
    public void shouldDeleteNote() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/notes/delete/1"))
                .andReturn();
        int actual = mvcResult.getResponse().getStatus();

        assertEquals(HttpStatus.OK.value(), actual);
    }

    @Test
    public void shouldReturn404StatusWhenDeleteNote() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/notes/delete/7"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()))
                .andReturn();

        String actual = mvcResult.getResolvedException().getMessage();
        assertNotNull(actual);
        assertEquals("No note has been found with id: 7", actual);
    }

    @AfterEach
    public void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

}