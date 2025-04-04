package com.simec.blogApi;

import com.simec.blogApi.exception.ArticleNotFoundException;
import com.simec.blogApi.exception.CategoryNotFoundException;
import com.simec.blogApi.exception.TagNotFoundException;
import com.simec.blogApi.repository.ArticleRepository;
import com.simec.blogApi.validator.ArticleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class ArticleController {

    private final ArticleRepository repository;
    private final ArticleValidator validator;

    @Autowired
    public ArticleController(ArticleRepository repository, ArticleValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @GetMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArticleDTO> detail(@PathVariable Integer id) {
        validator.validateId(id);
        return ResponseEntity.status(HttpStatus.OK).body(repository.findById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArticleDTO> create(@RequestBody ArticleDTO articleDTO) {
        validator.validateWithoutId(articleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.create(articleDTO));
    }

    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        validator.validateId(id);
        repository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Article was deleted");
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArticleDTO> update(@PathVariable Integer id, @RequestBody ArticleDTO articleDTO) {
        if (id != articleDTO.getId()) {
            throw new IllegalArgumentException("Provided ids does not match");
        }
        validator.validateWithId(articleDTO);
        return ResponseEntity.status(HttpStatus.OK).body(repository.update(articleDTO));
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArticleDTO> patch(@PathVariable Integer id, @RequestBody ArticleDTO articleDTO) {
        validator.validateId(id);
        ArticleDTO article = repository.findById(id);

        if (articleDTO.getHeader() != null) {
            article.setHeader(articleDTO.getHeader());
        }

        if (articleDTO.getContent() != null) {
            article.setContent(articleDTO.getContent());
        }

        if (articleDTO.getCategory() != null) {
            article.setCategory(articleDTO.getCategory());
        }

        if (articleDTO.getTags() != null) {
            article.setTags(articleDTO.getTags());
        }

        validator.validateWithId(article);
        return ResponseEntity.status(HttpStatus.OK).body(repository.update(article));
    }

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ArticleDTO>> findAll(@RequestParam(required = false) String term) {
        if (term == null || term.isBlank()) {
            return ResponseEntity.status(HttpStatus.OK).body(repository.findAll());
        }

        if (term.length() > 100) {
            throw new IllegalArgumentException("Search term cannot be longer than 100 characters");
        }

        return ResponseEntity.status(HttpStatus.OK).body(repository.findBySearchTerm(term));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleValidationException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler({ArticleNotFoundException.class, CategoryNotFoundException.class, TagNotFoundException.class})
    public ResponseEntity<String> handleNotFound(Throwable e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
