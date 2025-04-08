package com.simec.blogApi.dao;

import com.simec.blogApi.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TagDaoImpl implements TagDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TagDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Tag> findAllByArticleId(int id) {
        String sql = "SELECT tag.id, tag.header FROM tag " +
                "JOIN article_tag ON article_tag.tag_id = tag.id " +
                "WHERE article_tag.article_id = ?";
        try {
            return jdbcTemplate.query(sql, new TagRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }

    @Override
    public void assignTagsToArticleId(List<Tag> tags, int articleId) {
        if (tags.isEmpty()) return;

        String marks = String.join(",", Collections.nCopies(tags.size(), "(?,?)"));
        String sql = String.format("INSERT INTO article_tag(article_id, tag_id) VALUES %s", marks);

        List<Integer> ids = new ArrayList<>();
        for (Tag tag : tags) {
            ids.add(articleId);
            ids.add(tag.getId());
        }

        jdbcTemplate.update(sql, ids.toArray());
    }

    @Override
    public void unassignTagsFromArticleId(List<Tag> tags, int articleId) {
        if (tags.isEmpty()) return;

        String marks = String.join(",", Collections.nCopies(tags.size(), "?"));
        String sql = String.format("DELETE FROM article_tag WHERE article_id = ? AND tag_id IN (%s)", marks);

        List<Integer> ids = new ArrayList<>();
        ids.add(articleId);
        for (Tag tag : tags) {
            ids.add(tag.getId());
        }

        jdbcTemplate.update(sql, ids.toArray());
    }

    @Override
    public List<Tag> findAllByHeaders(List<String> headers) {
        if (headers == null || headers.isEmpty()) return List.of();
        String marks = String.join(",", Collections.nCopies(headers.size(), "?"));
        String sql = String.format("SELECT id, header FROM tag WHERE header IN (%s)", marks);
        return jdbcTemplate.query(sql, new TagRowMapper(), headers.toArray());
    }

    private static class TagRowMapper implements RowMapper<Tag> {
        @Override
        public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Tag.Builder()
                    .withId(rs.getInt("id"))
                    .withHeader(rs.getString("header"))
                    .build();
        }
    }
}
