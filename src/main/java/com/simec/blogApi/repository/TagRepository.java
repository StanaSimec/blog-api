package com.simec.blogApi.repository;

import com.simec.blogApi.model.Tag;

import java.util.List;

public interface TagRepository {
    void assignTags(List<String> tagHeaders, int articleId);

    List<Tag> findByArticleId(int articleId);
}
