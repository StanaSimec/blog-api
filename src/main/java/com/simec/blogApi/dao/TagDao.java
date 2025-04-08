package com.simec.blogApi.dao;

import com.simec.blogApi.model.Tag;

import java.util.List;

public interface TagDao {
    List<Tag> findAllByArticleId(int id);

    void assignTagsToArticleId(List<Tag> tags, int articleId);

    List<Tag> findAllByHeaders(List<String> headers);

    void unassignTagsFromArticleId(List<Tag> tags, int articleId);
}
