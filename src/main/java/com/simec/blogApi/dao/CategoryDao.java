package com.simec.blogApi.dao;

import com.simec.blogApi.model.Category;

import java.util.Optional;

public interface CategoryDao {
    Optional<Category> findByHeader(String header);

    Optional<Category> findByArticleId(int articleId);
}
