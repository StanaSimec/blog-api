package com.simec.blogApi.dao;

import com.simec.blogApi.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class TagDaoImpl implements TagDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TagDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Tag> findByHeader(String header) {
        if (header == null) {
            return Optional.empty();
        }
        String sql = "SELECT id, header FROM tag WHERE header = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new TagRowMapper(), header));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Tag> findByArticleId(int id) {
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
    public void assignTagToArticle(int tagId, int articleId) {
        String sql = "INSERT INTO article_tag(article_id, tag_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, articleId, tagId);
    }

    @Override
    public void removeTagFromArticle(int tagId, int articleId) {
        String sql = "DELETE FROM article_tag WHERE article_id = ? AND tag_id = ?";
        jdbcTemplate.update(sql, articleId, tagId);
    }

    @Override
    public List<Tag> findByHeaders(List<String> headers) {
        if (headers == null || headers.isEmpty()) return List.of();
        String marks = String.join(",", Collections.nCopies(headers.size(), "?"));
        String sql = String.format("SELECT id, header FROM tag WHERE header IN (%s)", marks);
        return jdbcTemplate.queryForStream(sql, new TagRowMapper(), headers.toArray()).toList();
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
