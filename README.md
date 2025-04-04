# Blogging Platform API

### Solution for the [blogging-platform-api](https://roadmap.sh/projects/blogging-platform-api) project from [roadmap.sh](https://roadmap.sh).

## Open project
```
$ git clone https://github.com/StanaSimec/blog-api.git
$ cd blogApi
```
## Set up environment variables
- Open src/main/java/resources/application.yml
- Set environment variables: 
  - spring.datasource.url
  - spring.datasource.username
  - spring.datasource.passsword

## How to run the app
```
.gradlew/ bootRun
```

## How to create posts
```
POST /posts
{
    "header": "Some great article header",
    "content": "Very long article content",
    "category": "Programming",
    "tags": ["SQL", "Java"] (optional)
}
```
## How to update posts with PUT
```
PUT /posts/1
{
    "id": 1,
    "header": "Some great article header",
    "content": "Better article content",
    "category": "Sport",
    "tags": ["Weekly news"] (optional)
}
```
## How to update posts with PATCH
```
PATCH /posts/1
{
    "id": 1,
    "content": "Better article content",
    "tags": ["Java", "Weekly news"] (optional)
}
```
## How to delete posts
```
DELETE /posts/1
```

## Find all posts
```
GET /posts
```

## Find post
```
GET /posts/1
```

## Search posts
```
GET /posts?term=searchterm
```
#### Note: Term searches by header, category and content

## Tech Stack
- Java
- Gradle
- Spring Boot Starter Web
- Spring Boot Starter JDBC
- PostgreSQL