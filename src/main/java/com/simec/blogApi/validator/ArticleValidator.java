package com.simec.blogApi.validator;

import com.simec.blogApi.ArticleDTO;

public interface ArticleValidator {
    void validateWithoutId(ArticleDTO dto);

    void validateWithId(ArticleDTO dto);

    void validateId(Integer id);
}
