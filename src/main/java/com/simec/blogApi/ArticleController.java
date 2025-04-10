package com.simec.blogApi;

import com.simec.blogApi.exception.ArticleNotFoundException;
import com.simec.blogApi.exception.CategoryNotFoundException;
import com.simec.blogApi.exception.TagNotFoundException;
import com.simec.blogApi.repository.ArticleRepository;
import com.simec.blogApi.validator.ArticleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArticleDTO> detail(@PathVariable Integer id) {
        validator.validateId(id);
        ArticleDTO articleDTO = addLinksForDetail(repository.findById(id));
        return ResponseEntity.status(HttpStatus.OK).body(articleDTO);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArticleDTO> create(@RequestBody ArticleDTO articleDTO) {
        validator.validateWithoutId(articleDTO);
        ArticleDTO createdArticleDTO = addLinksForDetail(repository.create(articleDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArticleDTO);
    }

    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        validator.validateId(id);
        repository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArticleDTO> update(@PathVariable Integer id, @RequestBody ArticleDTO articleDTO) {
        if (id != articleDTO.getId()) {
            throw new IllegalArgumentException("Provided ids does not match");
        }
        validator.validateWithId(articleDTO);

        ArticleDTO updatedArticleDTO = addLinksForDetail(repository.update(articleDTO));
        return ResponseEntity.status(HttpStatus.OK).body(updatedArticleDTO);
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

        ArticleDTO updatedArticleDTO = addLinksForDetail(repository.update(article));
        return ResponseEntity.status(HttpStatus.OK).body(updatedArticleDTO);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<ArticleDTO>> findAll(@RequestParam(required = false) String term) {
        Link selfLink = linkTo(ArticleController.class).withSelfRel();
        if (term == null || term.isBlank()) {
            List<ArticleDTO> articles = addLinksForList(repository.findAll());
            return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(articles, selfLink));
        }

        if (term.length() > 20) {
            throw new IllegalArgumentException("Search term cannot be longer than 20 characters");
        }

        List<ArticleDTO> articles = addLinksForList(repository.findBySearchTerm(term));
        return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(articles, selfLink));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleValidationException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler({ArticleNotFoundException.class, CategoryNotFoundException.class, TagNotFoundException.class})
    public ResponseEntity<String> handleNotFound(Throwable e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    private ArticleDTO addLinksForDetail(ArticleDTO articleDTO) {
        articleDTO.add(linkTo(methodOn(ArticleController.class).detail(articleDTO.getId())).withSelfRel());
        articleDTO.add(linkTo(ArticleController.class).withRel(IanaLinkRelations.COLLECTION));
        return articleDTO;
    }

    private List<ArticleDTO> addLinksForList(List<ArticleDTO> articleDTOList) {
        return articleDTOList.stream()
                .peek(a -> a.add(linkTo(methodOn(ArticleController.class).detail(a.getId())).withSelfRel()))
                .toList();
    }
}
