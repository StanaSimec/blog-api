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

    @Override
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

    @Override
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

    @Override
    public ArticleDTO findById(int id) {
        Article article = findArticleById(id);
        List<Tag> tags = tagRepository.findByArticleId(id);
        Category category = categoryDao.findByArticleId(article.getId())
                .orElseThrow(() -> new CategoryNotFoundException(
                        String.format("Category for article id: %d was not found", article.getId())));
        return toDTO(article, tags, category);
    }

    @Override
    public void deleteById(int id) {
        Article article = findArticleById(id);
        articleDao.deleteById(article.getId());
    }

    @Override
    public List<ArticleDTO> findAll() {
        return articleDao.findAll().stream()
                .map(a -> {
                    List<Tag> tags = tagRepository.findByArticleId(a.getId());
                    Category category = findCategoryByArticleId(a.getId());
                    return toDTO(a, tags, category);
                })
                .toList();
    }

    @Override
    public List<ArticleDTO> findBySearchTerm(String searchTerm) {
        return articleDao.findBySearchTerm(searchTerm).stream()
                .map(a -> {
                    List<Tag> tags = tagRepository.findByArticleId(a.getId());
                    Category category = findCategoryByArticleId(a.getId());
                    return toDTO(a, tags, category);
                })
                .toList();
    }

    private Category findCategoryByArticleId(int articleId) {
        return categoryDao.findByArticleId(articleId)
                .orElseThrow(() -> new CategoryNotFoundException(
                        String.format("Category for article id: %d was not found", articleId)));
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
