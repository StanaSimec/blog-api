package com.simec.blogApi.dao;

import com.simec.blogApi.model.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class ArticleDaoImpl implements ArticleDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ArticleDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int create(Article article) {
        String sql = "INSERT INTO article (header, content, created_at, updated_at, category_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, article.getHeader());
            ps.setString(2, article.getContent());
            ps.setTimestamp(3, new Timestamp(article.getCreatedAt().toEpochMilli()));
            ps.setTimestamp(4, new Timestamp(article.getUpdatedAt().toEpochMilli()));
            ps.setInt(5, article.getCategoryId());
            return ps;
        }, keyHolder);
        return (int) Objects.requireNonNull(keyHolder.getKeys()).get("id");
    }

    @Override
    public Optional<Article> findById(int id) {
        String sql = "SELECT id, header, content, created_at, updated_at, category_id FROM article WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new ArticleRowMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM article WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void update(Article article) {
        String sql = "UPDATE article SET header = ?, content = ?, updated_at = ?, category_id =? WHERE id = ?";
        jdbcTemplate.update(
                sql,
                article.getHeader(),
                article.getContent(),
                new Timestamp(article.getUpdatedAt().toEpochMilli()),
                article.getCategoryId(),
                article.getId());
    }

    @Override
    public List<Article> findAll() {
        String sql = "SELECT id, header, content, created_at, updated_at, category_id FROM article";
        try {
            return jdbcTemplate.query(sql, new ArticleRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }

    @Override
    public List<Article> findBySearchTerm(String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) {
            return List.of();
        }
        String sqlTerm = "%" + searchTerm + "%";
        String sql = """
                SELECT DISTINCT article.id, article.header, article.content, article.created_at, article.updated_at, article.category_id
                FROM article
                JOIN category
                ON article.category_id = category.id
                LEFT JOIN article_tag
                ON article.id = article_tag.article_id
                LEFT JOIN tag
                ON article_tag.tag_id = tag.id
                WHERE article.header ILIKE ?
                OR article.content ILIKE ?
                OR category.header ILIKE ?
                OR tag.header ILIKE ?""";
        try {
            return jdbcTemplate.query(sql, new ArticleRowMapper(), sqlTerm, sqlTerm, sqlTerm, sqlTerm);
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }

    private static class ArticleRowMapper implements RowMapper<Article> {
        @Override
        public Article mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Article.Builder()
                    .withId(rs.getInt("id"))
                    .withHeader(rs.getString("header"))
                    .withCreatedAt(rs.getTimestamp("created_at").toInstant())
                    .withContent(rs.getString("content"))
                    .withUpdatedAt(rs.getTimestamp("updated_at").toInstant())
                    .withCategoryId(rs.getInt("category_id"))
                    .build();
        }
    }
}
