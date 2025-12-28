package com.example.book.service;

import com.example.book.client.PricingClient;
import com.example.book.entity.Book;
import com.example.book.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;
    private final PricingClient pricingClient;

    public BookService(BookRepository bookRepository, PricingClient pricingClient) {
        this.bookRepository = bookRepository;
        this.pricingClient = pricingClient;
    }

    /**
     * Create a new book.
     * 
     * @param book Book to create
     * @return Created book
     */
    public Book createBook(Book book) {
        log.info("Creating book: {}", book.getTitle());
        return bookRepository.save(book);
    }

    /**
     * Get all books.
     * 
     * @return List of all books
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Get a book by ID.
     * 
     * @param id Book ID
     * @return Book if found
     */
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    /**
     * Borrow a book by ID.
     * Uses pessimistic locking to handle concurrent borrow requests.
     * Decrements stock and fetches the price from pricing service.
     * 
     * @param id Book ID
     * @return Map containing borrow result with book info and price
     */
    @Transactional
    public Map<String, Object> borrow(Long id) {
        log.info("Processing borrow request for book ID: {}", id);

        // Find book with pessimistic lock to prevent concurrent modifications
        Book book = bookRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        // Decrement stock (throws IllegalStateException if stock is 0)
        book.decrementStock();

        // Save the updated book
        bookRepository.save(book);

        // Get price from pricing service (with retry and circuit breaker)
        Double price = pricingClient.getBookPrice(id);

        log.info("Book '{}' borrowed successfully. Remaining stock: {}, Price: {}",
                book.getTitle(), book.getStock(), price);

        return Map.of(
                "bookId", book.getId(),
                "title", book.getTitle(),
                "author", book.getAuthor(),
                "remainingStock", book.getStock(),
                "price", price,
                "message", "Book borrowed successfully");
    }
}
