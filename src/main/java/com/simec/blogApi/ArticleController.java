package com.simec.blogApi;

import com.simec.blogApi.exception.ArticleNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ArticleController {

    private final ArticleRepository articleService;
    private final Validator validator;

    @Autowired
    public ArticleController(ArticleRepository articleService, Validator validator) {
        this.articleService = articleService;
        this.validator = validator;
    }

    @GetMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArticleDTO> detail(@PathVariable Integer id) {
        validator.validateId(id);
        ArticleDTO articleDTO = articleService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(articleDTO);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArticleDTO> create(@RequestBody ArticleDTO articleDTO) {
        validateArticle(articleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(articleService.create(articleDTO));
    }

    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        validator.validateId(id);
        articleService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Article was deleted");
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArticleDTO> update(@PathVariable Integer id, @RequestBody ArticleDTO articleDTO) {
        if (id != articleDTO.getId()) {
            throw new IllegalArgumentException("Provided ids does not match");
        }
        validator.validateId(id);
        validateArticle(articleDTO);
        return ResponseEntity.status(HttpStatus.OK).body(articleService.update(articleDTO));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleValidationException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(ArticleNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ArticleNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    private void validateArticle(ArticleDTO articleDTO) {
        validator.validateHeader(articleDTO.getHeader());
        validator.validateContent(articleDTO.getContent());
        validator.validateCategory(articleDTO.getCategory());
        validator.validateTags(articleDTO.getTags());
    }
}
