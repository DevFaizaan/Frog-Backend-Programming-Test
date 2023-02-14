package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void testGetAllBooks() throws Exception {
        // arrange
        Book book = new Book();
        book.setTitle("The Great Gatsby");
        book.setAuthor("F. Scott Fitzgerald");
        book.setPublicationYear(1925);
        bookRepository.save(book);

        Book book2 = new Book();
        book2.setTitle("The Great Gatsby 2");
        book2.setAuthor("F. Scott Fitzgerald");
        book2.setPublicationYear(1930);
        bookRepository.save(book2);

        // act
        MvcResult result = mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andReturn();

        // assert
        List<Book> books = Arrays.asList(
                new ObjectMapper().readValue(result.getResponse().getContentAsString(), Book[].class)
        );
        assertEquals(2, books.size());

        assertEquals("The Great Gatsby", books.get(0).getTitle());
        assertEquals("F. Scott Fitzgerald", books.get(0).getAuthor());
        assertEquals(1925, books.get(0).getPublicationYear().intValue());

        assertEquals("The Great Gatsby 2", books.get(1).getTitle());
        assertEquals("F. Scott Fitzgerald", books.get(1).getAuthor());
        assertEquals(1930, books.get(1).getPublicationYear().intValue());
    }

    @Test
    public void testGetBookById() throws Exception {
        // arrange
        Book book = new Book();
        book.setTitle("The Great Gatsby");
        book.setAuthor("F. Scott Fitzgerald");
        book.setPublicationYear(1925);
        Book savedBook = bookRepository.save(book);

        // act
        MvcResult result = mockMvc.perform(get("/books/" + savedBook.getId()))
                .andExpect(status().isOk())
                .andReturn();

        // assert
        Book returnedBook = new ObjectMapper().readValue(result.getResponse().getContentAsString(), Book.class);
        assertEquals("The Great Gatsby", returnedBook.getTitle());
        assertEquals("F. Scott Fitzgerald", returnedBook.getAuthor());
        assertEquals(1925, returnedBook.getPublicationYear().intValue());
    }

    @Test
    public void testCreateBook() throws Exception {
        // arrange
        Book book = new Book();
        book.setTitle("The Great Gatsby");
        book.setAuthor("F. Scott Fitzgerald");
        book.setPublicationYear(1925);

        // act
        MvcResult result = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(book)))
                .andExpect(status().isOk())
                .andReturn();

        // assert
        Book savedBook = new ObjectMapper().readValue(result.getResponse().getContentAsString(), Book.class);
        assertNotNull(savedBook.getId());
        assertEquals("The Great Gatsby", savedBook.getTitle());
        assertEquals("F. Scott Fitzgerald", savedBook.getAuthor());
        assertEquals(1925, savedBook.getPublicationYear().intValue());
    }

    @Test
    public void testUpdateBook() throws Exception {
        // arrange
        Book book = new Book();
        book.setTitle("The Great Gatsby");
        book.setAuthor("F. Scott Fitzgerald");
        book.setPublicationYear(1925);
        Book savedBook = bookRepository.save(book);

        // act
        savedBook.setTitle("The Great Gatsby (Updated)");
        MvcResult result = mockMvc.perform(put("/books/" + savedBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(savedBook)))
                .andExpect(status().isOk())
                .andReturn();

        // assert
        Book updatedBook = new ObjectMapper().readValue(result.getResponse().getContentAsString(), Book.class);
        assertEquals(savedBook.getId(), updatedBook.getId());
        assertEquals("The Great Gatsby (Updated)", updatedBook.getTitle());
        assertEquals("F. Scott Fitzgerald", updatedBook.getAuthor());
        assertEquals(1925, updatedBook.getPublicationYear().intValue());
    }

    @Test
    public void testDeleteBook() throws Exception {
        // arrange
        Book book = new Book();
        book.setTitle("The Great Gatsby");
        book.setAuthor("F. Scott Fitzgerald");
        book.setPublicationYear(1925);
        Book savedBook = bookRepository.save(book);
        mockMvc.perform(delete("/books/" + savedBook.getId()))
                .andExpect(status().isOk());

        // assert
        Optional<Book> deletedBook = bookRepository.findById(savedBook.getId());
        assertFalse(deletedBook.isPresent());
    }

}