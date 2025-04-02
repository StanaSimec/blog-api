package com.simec.blogApi.dao;

import com.simec.blogApi.model.Article;

import java.util.List;
import java.util.Optional;

public interface ArticleDao {
    int create(Article article);

    Optional<Article> findById(int id);

    void deleteById(int id);

    void update(Article article);

    List<Article> findAll();

    List<Article> findBySearchTerm(String searchTerm);
}
