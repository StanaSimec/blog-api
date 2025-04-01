package com.simec.blogApi.dao;

import com.simec.blogApi.model.Category;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Component
public class CategoryDaoImpl implements CategoryDao {

    private final JdbcTemplate jdbcTemplate;

    public CategoryDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Category> findByHeader(String header) {
        if (header == null) {
            return Optional.empty();
        }
        String sql = "SELECT id, header FROM category WHERE header = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new CategoryRowMapper(), header));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Category> findByArticleId(int articleId) {
        String sql = "SELECT category.id, category.header FROM category JOIN article ON category.id = article.category_id WHERE article.id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new CategoryRowMapper(), articleId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private static class CategoryRowMapper implements RowMapper<Category> {
        @Override
        public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Category.Builder()
                    .withId(rs.getInt("id"))
                    .withHeader(rs.getString("header"))
                    .build();
        }
    }
}
