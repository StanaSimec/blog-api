package com.simec.blogApi.model;

import java.util.Objects;

public class Tag {
    private final int id;
    private final String header;

    private Tag(Builder builder) {
        this.id = builder.id;
        this.header = builder.header;
    }

    public int getId() {
        return id;
    }

    public String getHeader() {
        return header;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return id == tag.id && Objects.equals(header, tag.header);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, header);
    }

    public static class Builder {
        private int id = 0;
        private String header = "";

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withHeader(String name) {
            this.header = name;
            return this;
        }

        public Tag build() {
            return new Tag(this);
        }
    }
}
