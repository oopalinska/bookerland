package pl.oopalinska.bookerland.catalog.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.oopalinska.bookerland.catalog.application.port.CatalogUseCase;
import pl.oopalinska.bookerland.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CatalogController.class})
class CatalogControllerTest {
    @MockBean
    CatalogUseCase catalogUseCase;
    @Autowired
    CatalogController controller;

    @Test
    public void shouldGetAllBooks() {
        //given
        Book effectiveJava = new Book("Effective Java", 2005, new BigDecimal("99.00"), 50L);
        Book concurrency = new Book("Java Concurrency in Practice", 2006, new BigDecimal("99.00"), 50L);
        Mockito.when(catalogUseCase.findAll()).thenReturn(List.of(effectiveJava, concurrency));
        //when
        List<Book> all = controller.getAll(Optional.empty(), Optional.empty());
        //then
        assertEquals(2, all.size());
    }
}
