package com.simec.blogApi.model;

import java.time.Instant;

public class Article {
    private final int id;
    private final String header;
    private final String content;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final int categoryId;

    private Article(Builder builder) {
        this.id = builder.id;
        this.header = builder.header;
        this.content = builder.content;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.categoryId = builder.categoryId;
    }

    public Integer getId() {
        return id;
    }

    public String getHeader() {
        return header;
    }

    public String getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public static class Builder {
        private int id = 0;
        private String header = "";
        private Instant createdAt = Instant.now();
        private String content = "";
        private Instant updatedAt = Instant.now();
        private int categoryId = 0;

        public Builder withId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder withHeader(String header) {
            this.header = header;
            return this;
        }

        public Builder withCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder withContent(String content) {
            this.content = content;
            return this;
        }

        public Builder withUpdatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder withCategoryId(int categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public Article build() {
            return new Article(this);
        }
    }
}
