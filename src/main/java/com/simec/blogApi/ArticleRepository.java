package com.simec.blogApi;

import com.simec.blogApi.dao.*;
import com.simec.blogApi.exception.ArticleNotFoundException;
import com.simec.blogApi.exception.CategoryNotFoundException;
import com.simec.blogApi.model.Article;
import com.simec.blogApi.model.Category;
import com.simec.blogApi.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ArticleRepository {

    private final ArticleDao articleDao;
    private final TagDao tagDao;
    private final CategoryDao categoryDao;

    @Autowired
    public ArticleRepository(ArticleDaoImpl articleDao, TagDaoImpl tagDao, CategoryDao categoryDao) {
        this.articleDao = articleDao;
        this.tagDao = tagDao;
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
        List<Tag> tagsToAdd = tagDao.findByHeaders(articleDTO.getTags());
        addTags(tagsToAdd, articleId);
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
        updateTags(articleDTO.getTags(), updatedArticle.getId());
        return findById(updatedArticle.getId());
    }

    public ArticleDTO findById(int id) {
        Article article = findArticleById(id);
        List<Tag> tags = tagDao.findByArticleId(id);
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

    private void addTags(List<Tag> tags, int articleId) {
        tags.forEach(t -> tagDao.assignTagToArticle(t.getId(), articleId));
    }

    private void removeTags(List<Tag> tags, int articleId) {
        tags.forEach(t -> tagDao.removeTagFromArticle(t.getId(), articleId));
    }

    private void updateTags(List<String> tagHeaders, int articleId) {
        List<Tag> currentTags = tagDao.findByArticleId(articleId);
        List<Tag> updatedTags = tagDao.findByHeaders(tagHeaders);

        List<Tag> tagsToRemove = currentTags.stream()
                .filter(tag -> !tagHeaders.contains(tag.getHeader()))
                .toList();

        if (!tagsToRemove.isEmpty()) {
            removeTags(tagsToRemove, articleId);
        }

        List<Tag> tagsToAdd = updatedTags.stream()
                .filter(t -> !currentTags.contains(t))
                .toList();

        if (!tagsToAdd.isEmpty()) {
            addTags(tagsToAdd, articleId);
        }
    }
}
