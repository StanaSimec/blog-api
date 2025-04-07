package com.simec.blogApi.dao;

import com.simec.blogApi.model.Tag;

import java.util.List;

public interface TagDao {
    List<Tag> findAllByArticleId(int id);

    void assignTagToArticle(int tagId, int articleId);

    List<Tag> findAllByHeaders(List<String> headers);

    void removeTagFromArticle(int tagId, int articleId);
}
