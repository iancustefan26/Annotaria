# Annatoria

## Overview

Annatoria is a web application for managing and sharing multimedia content, focusing on annotation, organization, and statistical analysis. It is inspired by platforms like Instagram and Unsplash, allowing users to upload images and videos, annotate them with metadata, organize content, view statistics, and engage socially through likes and comments.

## Features

- **Account Management**: Create accounts, log in, and delete accounts with authentication.
- **Multimedia Upload**: Upload images and videos (JPG, PNG, MP4, AVI) with a 50MB file size limit.
- **Content Annotation**: Add descriptions, categories, creation years, and up to 50 tags per post.
- **Personalized Feed**: View content based on user preferences and likes, with sorting and filtering.
- **Social Interaction**: Like and comment on posts, and view other users' profiles.
- **Statistics Dashboard**: View insights on liked and commented posts, active users, and export data in CSV and SVG formats.
- **RSS Feed**: Generate feeds for news and trends.

## System Requirements

### Functional Requirements

- **AUTH Module**:
  - **Account Creation**: Create accounts with unique username and valid email (High Priority)
  - **Login**: Authenticate with username and password (High Priority)
  - **Account Deletion**: Delete accounts with confirmation and data removal (Medium Priority)
- **POSTS Module**:
  - **Multimedia Upload**: Upload files with format and size validation (High Priority)
  - **Post Annotation**: Add metadata to posts (High Priority)
  - **Import/Export Favorite Posts**: Support JSON and XML formats (Low Priority)
- **FEED Module**:
  - **Personalized Feed**: Generate feed based on like history (High Priority)
  - **Sorting and Filtering**: Sort by category, year, and tags (High Priority)
  - **Social Interaction**: Like and comment on posts (High Priority)
  - **Profile Viewing**: Visit other users' profiles (Medium Priority)
- **STATISTICS Module**:
  - **Liked Posts Statistics**: Display most liked post (Medium Priority)
  - **Category Statistics**: Display most commented post and active user (Medium Priority)
  - **Data Export**: Export statistics in CSV and SVG formats (Medium Priority)
  - **RSS Feed**: Generate feed for updates (Low Priority)

### Performance Requirements

- Feed loading within 3 seconds
- Visible upload progress for files over 50MB
- Statistics generation within 5 seconds
- Support at least 100 concurrent users and 10,000 posts

### Design Constraints

- **Technologies**:
  - **Backend**: Java Servlets, JSP
  - **Frontend**: HTML5, CSS3, JavaScript
  - **Database**: ORACLE
  - **Server**: Apache Tomcat
- **Security**:
  - Validate all user inputs
  - Use session-based authentication
  - Implement CSRF protection
  - Validate file uploads
- **Standards**:
  - Comply with W3C standards
  - Follow accessibility guidelines
  - Ensure responsive design
  - Support cross-browser compatibility (Chrome, Firefox, Safari, Edge)


## Usage

- **Sign Up/Login**: Create or log in to an account.
- **Upload Content**: Add images or videos and annotate with metadata.
- **Explore Feed**: Browse, like, comment, and filter content.
- **View Statistics**: Access insights and export data.
- **Manage Profile**: Update profile and view posts.

## Contributing

Developed by Popa David-Tudor and Iancu Stefan Teodor for academic purposes. Submit pull requests or open issues for suggestions.

## License

For academic use only, not licensed for commercial purposes.

## Document Information

- **Version**: 1.0
- **Date**: June 2025
- **Status**: BETA

**Classification**: Academic Project


