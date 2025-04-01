package com.simec.blogApi.model;

public class Category {
    private final int id;
    private final String header;

    public Category(Builder builder) {
        this.id = builder.id;
        this.header = builder.header;
    }

    public int getId() {
        return id;
    }

    public String getHeader() {
        return header;
    }

    public static class Builder {
        private int id = 0;
        private String header = "";

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withHeader(String header) {
            this.header = header;
            return this;
        }

        public Category build() {
            return new Category(this);
        }
    }
}
