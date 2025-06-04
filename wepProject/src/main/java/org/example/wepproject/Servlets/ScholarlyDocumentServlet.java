package org.example.wepproject.Servlets;

import java.io.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/scholarly")
public class ScholarlyDocumentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        out.println(getScholarlyDocument());

        out.flush();
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    public String getScholarlyDocument() throws IOException {
        return "<!DOCTYPE html>\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"\" xml:lang=\"\">\n" +
                "<head>\n" +
                "  <meta charset=\"utf-8\" />\n" +
                "  <meta name=\"generator\" content=\"pandoc\" />\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=yes\" />\n" +
                "  <title>README</title>\n" +
                "  <style>\n" +
                "    code{white-space: pre-wrap;}\n" +
                "    span.smallcaps{font-variant: small-caps;}\n" +
                "    div.columns{display: flex; gap: min(4vw, 1.5em);}\n" +
                "    div.column{flex: auto; overflow-x: auto;}\n" +
                "    div.hanging-indent{margin-left: 1.5em; text-indent: -1.5em;}\n" +
                "    /* The extra [class] is a hack that increases specificity enough to\n" +
                "       override a similar rule in reveal.js */\n" +
                "    ul.task-list[class]{list-style: none;}\n" +
                "    ul.task-list li input[type=\"checkbox\"] {\n" +
                "      font-size: inherit;\n" +
                "      width: 0.8em;\n" +
                "      margin: 0 0.8em 0.2em -1.6em;\n" +
                "      vertical-align: middle;\n" +
                "    }\n" +
                "    .display.math{display: block; text-align: center; margin: 0.5rem auto;}\n" +
                "  </style>\n" +
                "  <link rel=\"stylesheet\" href=\"style.css\" />\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1 id=\"annatoria\">Annatoria</h1>\n" +
                "<h2 id=\"overview\">Overview</h2>\n" +
                "<p>Annatoria is a web application for managing and sharing multimedia\n" +
                "content, focusing on annotation, organization, and statistical analysis.\n" +
                "It is inspired by platforms like Instagram and Unsplash, allowing users\n" +
                "to upload images and videos, annotate them with metadata, organize\n" +
                "content, view statistics, and engage socially through likes and\n" +
                "comments.</p>\n" +
                "<h2 id=\"feed-algorithm\">Feed algorithm</h2>\n" +
                "<p>Implemented PageRank on an Undirected Graph Algorithm developed by\n" +
                "Google. We generate posts as nodes with the purpose of “measuring” its\n" +
                "relative importance within the set. An edge to a post counts as a vote\n" +
                "of support. The PageRank of a post is defined recursively and depends on\n" +
                "the number and PageRank metric of all pages that link to it (“incoming\n" +
                "edges”). A post that is linked to by many posts with high PageRank\n" +
                "receives a high rank itself. 2 posts have an edge between them if they\n" +
                "have the same category or are posted by the same author.</p>\n" +
                "<p>So, our iterative way sounds like this: (<strong>pages</strong> =\n" +
                "<strong>posts</strong>)</p>\n" +
                "<p>At <strong>t = 0</strong> an initial probability is assumed:</p>\n" +
                "<p><img src=\"docs/images/iterative.png\" /></p>\n" +
                "<p>where user is the user that’s calling the algorithm and\n" +
                "p<strong>i</strong> is post i. 0 is page i at time 0 and\n" +
                "<strong>Score(u, p)</strong> is definedat the bottom.</p>\n" +
                "<p><img src=\"docs/images/computation.png\" /></p>\n" +
                "<p>d = usually 0.85 (probability to stop scrolling) N = number of posts\n" +
                "L(p<strong>j</strong>) = number of edges linked to post\n" +
                "<strong>j</strong> M(p<strong>i</strong>) = Set of posts that are linked\n" +
                "to post <strong>i</strong></p>\n" +
                "<p>The algorithm iterates over all nodes of the graph until</p>\n" +
                "<p><img src=\"docs/images/until.png\" /></p>\n" +
                "<p>epsilon - small constant for all i.</p>\n" +
                "<p><img src=\"docs/images/score1.png\" /> <img\n" +
                "src=\"docs/images/score2.png\" /></p>\n" +
                "<figure>\n" +
                "<img src=\"docs/images/example.svg\" alt=\"Example\" />\n" +
                "<figcaption aria-hidden=\"true\">Example</figcaption>\n" +
                "</figure>\n" +
                "<p>PageRank: <strong>https://en.wikipedia.org/wiki/PageRank</strong></p>\n" +
                "<h2 id=\"features\">Features</h2>\n" +
                "<ul>\n" +
                "<li><strong>Account Management</strong>: Create accounts, log in, and\n" +
                "delete accounts with authentication.</li>\n" +
                "<li><strong>Multimedia Upload</strong>: Upload images and videos (JPG,\n" +
                "PNG, MP4, AVI) with a 50MB file size limit.</li>\n" +
                "<li><strong>Content Annotation</strong>: Add descriptions, categories,\n" +
                "creation years, and up to 50 tags per post.</li>\n" +
                "<li><strong>Personalized Feed</strong>: View content based on user\n" +
                "preferences and likes, with sorting and filtering.</li>\n" +
                "<li><strong>Social Interaction</strong>: Like and comment on posts, and\n" +
                "view other users’ profiles.</li>\n" +
                "<li><strong>Statistics Dashboard</strong>: View insights on liked and\n" +
                "commented posts, active users, and export data in CSV and SVG\n" +
                "formats.</li>\n" +
                "<li><strong>RSS Feed</strong>: Generate feeds for news and trends.</li>\n" +
                "</ul>\n" +
                "<h2 id=\"system-requirements\">System Requirements</h2>\n" +
                "<h3 id=\"functional-requirements\">Functional Requirements</h3>\n" +
                "<ul>\n" +
                "<li><strong>AUTH Module</strong>:\n" +
                "<ul>\n" +
                "<li><strong>Account Creation</strong>: Create accounts with unique\n" +
                "username and valid email (High Priority)</li>\n" +
                "<li><strong>Login</strong>: Authenticate with username and password\n" +
                "(High Priority)</li>\n" +
                "<li><strong>Account Deletion</strong>: Delete accounts with confirmation\n" +
                "and data removal (Medium Priority)</li>\n" +
                "</ul></li>\n" +
                "<li><strong>POSTS Module</strong>:\n" +
                "<ul>\n" +
                "<li><strong>Multimedia Upload</strong>: Upload files with format and\n" +
                "size validation (High Priority)</li>\n" +
                "<li><strong>Post Annotation</strong>: Add metadata to posts (High\n" +
                "Priority)</li>\n" +
                "<li><strong>Import/Export Favorite Posts</strong>: Support JSON and XML\n" +
                "formats (Low Priority)</li>\n" +
                "</ul></li>\n" +
                "<li><strong>FEED Module</strong>:\n" +
                "<ul>\n" +
                "<li><strong>Personalized Feed</strong>: Generate feed based on like\n" +
                "history (High Priority)</li>\n" +
                "<li><strong>Sorting and Filtering</strong>: Sort by category, year, and\n" +
                "tags (High Priority)</li>\n" +
                "<li><strong>Social Interaction</strong>: Like and comment on posts (High\n" +
                "Priority)</li>\n" +
                "<li><strong>Profile Viewing</strong>: Visit other users’ profiles\n" +
                "(Medium Priority)</li>\n" +
                "</ul></li>\n" +
                "<li><strong>STATISTICS Module</strong>:\n" +
                "<ul>\n" +
                "<li><strong>Liked Posts Statistics</strong>: Display most liked post\n" +
                "(Medium Priority)</li>\n" +
                "<li><strong>Category Statistics</strong>: Display most commented post\n" +
                "and active user (Medium Priority)</li>\n" +
                "<li><strong>Data Export</strong>: Export statistics in CSV and SVG\n" +
                "formats (Medium Priority)</li>\n" +
                "<li><strong>RSS Feed</strong>: Generate feed for updates (Low\n" +
                "Priority)</li>\n" +
                "</ul></li>\n" +
                "</ul>\n" +
                "<h3 id=\"design-constraints\">Design Constraints</h3>\n" +
                "<ul>\n" +
                "<li><strong>Technologies</strong>:\n" +
                "<ul>\n" +
                "<li><strong>Backend</strong>: Java Servlets, JSP</li>\n" +
                "<li><strong>Frontend</strong>: HTML5, CSS3, JavaScript</li>\n" +
                "<li><strong>Database</strong>: ORACLE 23ai</li>\n" +
                "<li><strong>Server</strong>: Apache Tomcat</li>\n" +
                "</ul></li>\n" +
                "<li><strong>Security</strong>:\n" +
                "<ul>\n" +
                "<li>Validate all user inputs</li>\n" +
                "<li>Use session-based authentication</li>\n" +
                "<li>Implement CSRF protection</li>\n" +
                "<li>Validate file uploads</li>\n" +
                "</ul></li>\n" +
                "<li><strong>Standards</strong>:\n" +
                "<ul>\n" +
                "<li>Comply with W3C standards</li>\n" +
                "<li>Follow accessibility guidelines</li>\n" +
                "<li>Ensure responsive design</li>\n" +
                "<li>Support cross-browser compatibility (Chrome, Firefox, Safari,\n" +
                "Edge)</li>\n" +
                "</ul></li>\n" +
                "</ul>\n" +
                "<h2 id=\"usage\">Usage</h2>\n" +
                "<ul>\n" +
                "<li><strong>Sign Up/Login</strong>: Create or log in to an account.</li>\n" +
                "<li><strong>Upload Content</strong>: Add images or videos and annotate\n" +
                "with metadata.</li>\n" +
                "<li><strong>Explore Feed</strong>: Browse, like, comment, and filter\n" +
                "content.</li>\n" +
                "<li><strong>View Statistics</strong>: Access insights and export\n" +
                "data.</li>\n" +
                "<li><strong>Manage Profile</strong>: Update profile and view posts.</li>\n" +
                "</ul>\n" +
                "<h2 id=\"contributing\">Contributing</h2>\n" +
                "<p>Developed by Popa David-Tudor and Iancu Stefan-Teodor for academic\n" +
                "purposes. Submit pull requests or open issues for suggestions.</p>\n" +
                "<h2 id=\"license\">License</h2>\n" +
                "<p>For academic use only, not licensed for commercial purposes.</p>\n" +
                "<h2 id=\"document-information\">Document Information</h2>\n" +
                "<ul>\n" +
                "<li><strong>Version</strong>: 1.0</li>\n" +
                "<li><strong>Date</strong>: June 2025</li>\n" +
                "<li><strong>Status</strong>: BETA</li>\n" +
                "</ul>\n" +
                "<p><strong>Classification</strong>: Academic Project</p>\n" +
                "</body>\n" +
                "</html>\n";
    }
}