package pl.oopalinska.bookerland.catalog.domain;

import java.util.List;

public interface  CatalogRepository {
    List<Book> findAll();
}
