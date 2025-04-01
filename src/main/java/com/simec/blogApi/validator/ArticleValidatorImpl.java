package com.simec.blogApi.validator;

import com.simec.blogApi.ArticleDTO;
import com.simec.blogApi.dao.CategoryDao;
import com.simec.blogApi.dao.TagDao;
import com.simec.blogApi.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class ArticleValidatorImpl implements ArticleValidator {

    private final CategoryDao categoryDao;
    private final TagDao tagDao;

    @Autowired
    public ArticleValidatorImpl(CategoryDao categoryDao, TagDao tagDao) {
        this.categoryDao = categoryDao;
        this.tagDao = tagDao;
    }

    @Override
    public void validateWithoutId(ArticleDTO dto) {
        validateHeader(dto.getHeader());
        validateContent(dto.getContent());
        validateCategory(dto.getCategory());
        validateTags(dto.getTags());
    }

    @Override
    public void validateWithId(ArticleDTO dto) {
        validateId(dto.getId());
        validateWithoutId(dto);
    }

    @Override
    public void validateId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid id");
        }
    }

    private void validateHeader(String header) {
        int maxLength = 200;
        if (header == null || header.isBlank()) {
            throw new IllegalArgumentException("Header is required");
        }

        if (header.length() > maxLength) {
            throw new IllegalArgumentException("Header cannot be longer than 200 characters");
        }
    }

    private void validateContent(String content) {
        int maxLength = 500;
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content is required");
        }

        if (content.length() > maxLength) {
            throw new IllegalArgumentException("Content cannot be longer than 500 characters");
        }
    }

    private void validateCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category is required");
        }
        categoryDao.findByHeader(category).orElseThrow(() -> new IllegalArgumentException(
                String.format("Category '%s' does not exist", category)));
    }

    private void validateTags(List<String> tagList) {
        if (tagList == null) return;
        List<String> validatedTags = tagList.stream()
                .filter(Objects::nonNull)
                .toList();
        if (validatedTags.isEmpty()) return;

        List<String> savedTags = tagDao.findByHeaders(validatedTags).stream()
                .map(Tag::getHeader)
                .toList();

        for (String tag : tagList) {
            if (!savedTags.contains(tag)) {
                throw new IllegalArgumentException(String.format("Tag '%s' does not exist", tag));
            }
        }
    }
}
