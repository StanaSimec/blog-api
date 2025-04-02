package com.simec.blogApi.dao;

import com.simec.blogApi.model.Tag;

import java.util.List;

public interface TagDao {
    List<Tag> findByArticleId(int id);

    void assignTagToArticle(int tagId, int articleId);

    List<Tag> findByHeaders(List<String> headers);

    void removeTagFromArticle(int tagId, int articleId);
}
