package com.simec.blogApi.repository;

import com.simec.blogApi.ArticleDTO;

public interface ArticleRepository {
    ArticleDTO findById(int id);

    ArticleDTO create(ArticleDTO articleDTO);

    ArticleDTO update(ArticleDTO articleDTO);

    void deleteById(int id);
}
