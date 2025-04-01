package com.simec.blogApi.repository;

import com.simec.blogApi.dao.TagDao;
import com.simec.blogApi.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TagRepositoryImpl implements TagRepository {

    private final TagDao dao;

    @Autowired
    public TagRepositoryImpl(TagDao dao) {
        this.dao = dao;
    }

    @Override
    public void assignTags(List<String> headers, int articleId) {
        List<Tag> currentTags = dao.findByArticleId(articleId);
        List<Tag> updatedTags = dao.findByHeaders(headers);

        List<Tag> tagsToRemove = currentTags.stream()
                .filter(tag -> !headers.contains(tag.getHeader()))
                .toList();

        if (!tagsToRemove.isEmpty()) {
            removeTags(tagsToRemove, articleId);
        }

        List<Tag> tagsToAdd = updatedTags.stream()
                .filter(t -> !currentTags.contains(t))
                .toList();

        if (!tagsToAdd.isEmpty()) {
            addTags(tagsToAdd, articleId);
        }
    }

    @Override
    public List<Tag> findByArticleId(int articleId) {
        return dao.findByArticleId(articleId);
    }

    private void addTags(List<Tag> tags, int articleId) {
        tags.forEach(t -> dao.assignTagToArticle(t.getId(), articleId));
    }


    private void removeTags(List<Tag> tags, int articleId) {
        tags.forEach(t -> dao.removeTagFromArticle(t.getId(), articleId));
    }
}
