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
    public void assignTagsByHeadersToArticleId(List<String> headers, int articleId) {
        List<Tag> currentTags = dao.findAllByArticleId(articleId);
        List<Tag> updatedTags = dao.findAllByHeaders(headers);

        List<Tag> tagsToUnassign = currentTags.stream()
                .filter(tag -> !headers.contains(tag.getHeader()))
                .toList();

        if (!tagsToUnassign.isEmpty()) {
            dao.unassignTagsFromArticleId(tagsToUnassign, articleId);
        }

        List<Tag> tagsToAdd = updatedTags.stream()
                .filter(t -> !currentTags.contains(t))
                .toList();

        if (!tagsToAdd.isEmpty()) {
            dao.assignTagsToArticleId(tagsToAdd, articleId);
        }
    }

    @Override
    public List<Tag> findByArticleId(int articleId) {
        return dao.findAllByArticleId(articleId);
    }
}
