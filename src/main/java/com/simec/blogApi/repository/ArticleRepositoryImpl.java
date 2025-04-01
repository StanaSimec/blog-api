package com.simec.blogApi.repository;

import com.simec.blogApi.ArticleDTO;
import com.simec.blogApi.dao.ArticleDao;
import com.simec.blogApi.dao.CategoryDao;
import com.simec.blogApi.exception.ArticleNotFoundException;
import com.simec.blogApi.exception.CategoryNotFoundException;
import com.simec.blogApi.model.Article;
import com.simec.blogApi.model.Category;
import com.simec.blogApi.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class ArticleRepositoryImpl implements ArticleRepository {

    private final ArticleDao articleDao;
    private final TagRepository tagRepository;
    private final CategoryDao categoryDao;

    @Autowired
    public ArticleRepositoryImpl(ArticleDao articleDao, TagRepository tagRepository, CategoryDao categoryDao) {
        this.articleDao = articleDao;
        this.tagRepository = tagRepository;
        this.categoryDao = categoryDao;
    }

    public ArticleDTO create(ArticleDTO articleDTO) {
        Category category = findCategoryByHeader(articleDTO.getCategory());

        Article article = new Article.Builder()
                .withHeader(articleDTO.getHeader())
                .withContent(articleDTO.getContent())
                .withCreatedAt(Instant.now())
                .withUpdatedAt(Instant.now())
                .withCategoryId(category.getId())
                .build();
        int articleId = articleDao.create(article);
        tagRepository.assignTags(articleDTO.getTags(), articleId);
        return findById(articleId);
    }

    public ArticleDTO update(ArticleDTO articleDTO) {
        Article article = findArticleById(articleDTO.getId());
        Category category = findCategoryByHeader(articleDTO.getCategory());

        Article updatedArticle = new Article.Builder()
                .withId(article.getId())
                .withHeader(articleDTO.getHeader())
                .withContent(articleDTO.getContent())
                .withCreatedAt(article.getCreatedAt())
                .withUpdatedAt(Instant.now())
                .withCategoryId(category.getId())
                .build();
        articleDao.update(updatedArticle);
        tagRepository.assignTags(articleDTO.getTags(), updatedArticle.getId());
        return findById(updatedArticle.getId());
    }

    public ArticleDTO findById(int id) {
        Article article = findArticleById(id);
        List<Tag> tags = tagRepository.findByArticleId(id);
        Category category = categoryDao.findByArticleId(article.getId())
                .orElseThrow(() -> new CategoryNotFoundException(
                        String.format("Category for article id: %d was not found", article.getId())));
        return toDTO(article, tags, category);
    }

    public void deleteById(int id) {
        Article article = findArticleById(id);
        articleDao.deleteById(article.getId());
    }

    private Category findCategoryByHeader(String header) {
        return categoryDao.findByHeader(header).
                orElseThrow(() -> new CategoryNotFoundException(
                        String.format("Category '%s' was not found", header)));
    }

    private Article findArticleById(int id) {
        return articleDao.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException(String.format("Article with id %d not found", id)));
    }

    private ArticleDTO toDTO(Article article, List<Tag> tags, Category category) {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(article.getId());
        dto.setContent(article.getContent());
        dto.setHeader(article.getHeader());
        dto.setCategory(category.getHeader());
        dto.setCreatedAt(article.getCreatedAt());
        dto.setUpdatedAt(article.getUpdatedAt());
        dto.setTags(tags.stream()
                .map(Tag::getHeader)
                .toList());
        return dto;
    }
}
