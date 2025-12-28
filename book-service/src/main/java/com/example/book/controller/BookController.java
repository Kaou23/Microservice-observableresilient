package com.example.book.controller;

import com.example.book.entity.Book;
import com.example.book.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Create a new book.
     * 
     * @param book Book to create
     * @return Created book with 201 status
     */
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book createdBook = bookService.createBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    /**
     * Get all books.
     * 
     * @return List of all books
     */
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    /**
     * Get a book by ID.
     * 
     * @param id Book ID
     * @return Book if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    /**
     * Borrow a book by ID.
     * Decrements stock and returns price.
     * 
     * @param id Book ID
     * @return Borrow result with book info and price
     */
    @PostMapping("/{id}/borrow")
    public ResponseEntity<Map<String, Object>> borrowBook(@PathVariable Long id) {
        Map<String, Object> result = bookService.borrow(id);
        return ResponseEntity.ok(result);
    }
}
