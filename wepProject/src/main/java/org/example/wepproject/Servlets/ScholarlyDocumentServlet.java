package org.example.wepproject.Servlets;

import java.io.IOException;
import java.io.PrintWriter;

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

    public String getScholarlyDocument() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Annatoria</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>ANNATORIA</h1>\n" +
                "<h2>System Requirements Specification</h2>\n" +
                "<p><strong>Version:</strong> 1.0 | <strong>Date:</strong> June 2025</p>\n" +
                "<p><strong>Authors:</strong> Popa David-Tudor, Iancu Stefan Teodor <strong>Project:</strong> Web Technologies</p>\n" +
                "\n" +
                "<h3>Table of Contents</h3>\n" +
                "<ul>\n" +
                "    <li><a href=\"#intro\">1. Introduction</a></li>\n" +
                "    <li><a href=\"#overall\">2. General Description</a></li>\n" +
                "    <li><a href=\"#requirements\">3. Specific Requirements</a></li>\n" +
                "    <li><a href=\"#interfaces\">4. Interfaces</a></li>\n" +
                "    <li><a href=\"#performance\">5. Performance Requirements</a></li>\n" +
                "    <li><a href=\"#constraints\">6. Design Constraints</a></li>\n" +
                "</ul>\n" +
                "\n" +
                "<h2 id=\"intro\">1. Introduction</h2>\n" +
                "\n" +
                "<h3>1.1 Purpose of the Document</h3>\n" +
                "<p><strong>Annatoria</strong> is a platform dedicated to the management and sharing of multimedia content with annotation, organization, and statistical analysis functionalities, similar to Instagram, Unsplash, etc.</p>\n" +
                "\n" +
                "<h3>1.2 Scope</h3>\n" +
                "<p>Annatoria is a web application that enables users to:</p>\n" +
                "<ul>\n" +
                "    <li>Upload and manage multimedia content (images and videos)</li>\n" +
                "    <li>Annotate content with metadata and express opinions</li>\n" +
                "    <li>Organize content based on various criteria</li>\n" +
                "    <li>View statistics and export data</li>\n" +
                "    <li>Engage socially through comments and likes</li>\n" +
                "</ul>\n" +
                "\n" +
                "<h3>1.3 Definitions and Acronyms</h3>\n" +
                "<table border=\"1\">\n" +
                "    <tr><th>Term</th><th>Definition</th></tr>\n" +
                "    <tr><td>Feed</td><td>Personalized list of posts displayed to the user</td></tr>\n" +
                "    <tr><td>Tag</td><td>Descriptive label assigned to a post</td></tr>\n" +
                "    <tr><td>Metadata</td><td>Descriptive information about a post (category, year, description)</td></tr>\n" +
                "    <tr><td>RSS</td><td>Really Simple Syndication - format for content distribution</td></tr>\n" +
                "    <tr><td>CSV</td><td>Comma-Separated Values - format for exporting tabular data</td></tr>\n" +
                "    <tr><td>SVG</td><td>Scalable Vector Graphics - format for vector graphics</td></tr>\n" +
                "</table>\n" +
                "\n" +
                "<h2 id=\"overall\">2. General Description</h2>\n" +
                "\n" +
                "<h3>2.1 Product Perspective</h3>\n" +
                "<ul>\n" +
                "    <li><strong>Auth:</strong> Authentication and account management</li>\n" +
                "    <li><strong>Posts:</strong> Multimedia content management</li>\n" +
                "    <li><strong>Feed:</strong> Displaying and interacting with content</li>\n" +
                "    <li><strong>Statistics:</strong> Data analysis and export</li>\n" +
                "</ul>\n" +
                "\n" +
                "<h3>2.2 Product Functions</h3>\n" +
                "<h4>Main Use Cases:</h4>\n" +
                "<ul>\n" +
                "    <li>Create account and log in</li>\n" +
                "    <li>Upload and annotate multimedia content</li>\n" +
                "    <li>Navigate personalized feed</li>\n" +
                "    <li>Social interaction (likes, comments)</li>\n" +
                "    <li>View statistics and export data</li>\n" +
                "    <li>Manage personal profile</li>\n" +
                "</ul>\n" +
                "\n" +
                "<h3>2.3 User Characteristics</h3>\n" +
                "<p><strong>Target Users:</strong> Individuals interested in sharing and organizing multimedia content with a focus on artistic and creative aspects.</p>\n" +
                "\n" +
                "<h2 id=\"requirements\">3. Specific Requirements</h2>\n" +
                "\n" +
                "<h3>3.1 Functional Requirements - AUTH Module</h3>\n" +
                "\n" +
                "<p><strong>REQ-AUTH-001</strong><br>\n" +
                "<strong>Account Creation</strong><br>\n" +
                "<strong>Description:</strong> The system must allow users to create new accounts.<br>\n" +
                "<strong>Input:</strong> Username, email, password<br>\n" +
                "<strong>Constraints:</strong> Unique username, valid email<br>\n" +
                "<strong>Priority:</strong> High</p>\n" +
                "\n" +
                "<p><strong>REQ-AUTH-002</strong><br>\n" +
                "<strong>Login</strong><br>\n" +
                "<strong>Description:</strong> The system must allow authentication with username and password.<br>\n" +
                "<strong>Output:</strong> Active user session<br>\n" +
                "<strong>Priority:</strong> High</p>\n" +
                "\n" +
                "<p><strong>REQ-AUTH-003</strong><br>\n" +
                "<strong>Account Deletion</strong><br>\n" +
                "<strong>Description:</strong> Users must be able to delete their own accounts.<br>\n" +
                "<strong>Constraints:</strong> Action confirmation, cascading deletion of associated data<br>\n" +
                "<strong>Priority:</strong> Medium</p>\n" +
                "\n" +
                "<h3>3.2 Functional Requirements - POSTS Module</h3>\n" +
                "\n" +
                "<p><strong>REQ-POST-001</strong><br>\n" +
                "<strong>Multimedia Upload</strong><br>\n" +
                "<strong>Description:</strong> The system must allow uploading of images and videos.<br>\n" +
                "<strong>Constraints:</strong> Maximum file size 50MB, supported formats: JPG, PNG, MP4, AVI<br>\n" +
                "<strong>Priority:</strong> High</p>\n" +
                "\n" +
                "<p><strong>REQ-POST-002</strong><br>\n" +
                "<strong>Post Annotation</strong><br>\n" +
                "<strong>Description:</strong> Users must be able to add description, category, year of creation, and tags.<br>\n" +
                "<strong>Constraints:</strong> Maximum 50 tags per post<br>\n" +
                "<strong>Priority:</strong> High</p>\n" +
                "\n" +
                "<p><strong>REQ-POST-003</strong><br>\n" +
                "<strong>Import/Export Favorite Posts</strong><br>\n" +
                "<strong>Description:</strong> The system must allow import/export in JSON and XML formats.<br>\n" +
                "<strong>Priority:</strong> Low</p>\n" +
                "\n" +
                "<h3>3.3 Functional Requirements - FEED Module</h3>\n" +
                "\n" +
                "<p><strong>REQ-FEED-001</strong><br>\n" +
                "<strong>Personalized Feed</strong><br>\n" +
                "<strong>Description:</strong> The system must generate a personalized feed based on previous likes.<br>\n" +
                "<strong>Algorithm:</strong> Recommendations based on like history<br>\n" +
                "<strong>Priority:</strong> High</p>\n" +
                "\n" +
                "<p><strong>REQ-FEED-002</strong><br>\n" +
                "<strong>Sorting and Filtering</strong><br>\n" +
                "<strong>Description:</strong> Users must be able to sort by category, year of creation, and tags.<br>\n" +
                "<strong>Priority:</strong> High</p>\n" +
                "\n" +
                "<p><strong>REQ-FEED-003</strong><br>\n" +
                "<strong>Social Interaction</strong><br>\n" +
                "<strong>Description:</strong> Users must be able to like and comment on posts.<br>\n" +
                "<strong>Priority:</strong> High</p>\n" +
                "\n" +
                "<p><strong>REQ-FEED-004</strong><br>\n" +
                "<strong>Profile Viewing</strong><br>\n" +
                "<strong>Description:</strong> Users must be able to visit other users' profiles.<br>\n" +
                "<strong>Priority:</strong> Medium</p>\n" +
                "\n" +
                "<h3>3.4 Functional Requirements - STATISTICS Module</h3>\n" +
                "\n" +
                "<p><strong>REQ-STAT-001</strong><br>\n" +
                "<strong>Liked Posts Statistics</strong><br>\n" +
                "<strong>Description:</strong> The system must display the most liked post.<br>\n" +
                "<strong>Priority:</strong> Medium</p>\n" +
                "\n" +
                "<p><strong>REQ-STAT-002</strong><br>\n" +
                "<strong>Category Statistics</strong><br>\n" +
                "<strong>Description:</strong> The system must display the most commented post.<br>\n" +
                "<strong>Priority:</strong> Medium</p>\n" +
                "\n" +
                "<p><strong>REQ-STAT-003</strong><br>\n" +
                "<strong>Category Statistics</strong><br>\n" +
                "<strong>Description:</strong> The system must display the most active user.<br>\n" +
                "<strong>Priority:</strong> Medium</p>\n" +
                "\n" +
                "<p><strong>REQ-STAT-004</strong><br>\n" +
                "<strong>Data Export</strong><br>\n" +
                "<strong>Description:</strong> The system must allow exporting statistics in CSV and SVG formats.<br>\n" +
                "<strong>Priority:</strong> Medium</p>\n" +
                "\n" +
                "<p><strong>REQ-STAT-005</strong><br>\n" +
                "<strong>RSS Feed</strong><br>\n" +
                "<strong>Description:</strong> The system must generate an RSS feed for news and trends.<br>\n" +
                "<strong>Priority:</strong> Low</p>\n" +
                "\n" +
                "<h2 id=\"interfaces\">4. Interfaces</h2>\n" +
                "\n" +
                "<h3>4.1 User Interfaces</h3>\n" +
                "<ul>\n" +
                "    <li><strong>Login/Signup Page:</strong> Simple and intuitive form</li>\n" +
                "    <li><strong>Main Feed:</strong> Instagram-like layout with posts and filtering options</li>\n" +
                "    <li><strong>Profile Page:</strong> Gallery of own and saved posts</li>\n" +
                "    <li><strong>Upload Page:</strong> Form for uploading and annotating multimedia</li>\n" +
                "    <li><strong>Statistics Dashboard:</strong> Interactive charts and tables</li>\n" +
                "</ul>\n" +
                "\n" +
                "<h3>4.2 Hardware Interfaces</h3>\n" +
                "<p>The application will be accessible through standard web browsers on:</p>\n" +
                "<ul>\n" +
                "    <li>Desktop/laptop computers</li>\n" +
                "    <li>Mobile phones (responsive design)</li>\n" +
                "</ul>\n" +
                "\n" +
                "<h3>4.3 Software Interfaces</h3>\n" +
                "<ul>\n" +
                "    <li><strong>Database:</strong> JDBC interface for persistence management</li>\n" +
                "    <li><strong>Unsplash API:</strong> REST API for external content</li>\n" +
                "    <li><strong>File System:</strong> For storing multimedia files</li>\n" +
                "</ul>\n" +
                "\n" +
                "<h2 id=\"performance\">5. Performance Requirements</h2>\n" +
                "\n" +
                "<h3>5.1 Response Time</h3>\n" +
                "<ul>\n" +
                "    <li>Feed loading: maximum 3 seconds</li>\n" +
                "    <li>File upload: visible progress for files > 50MB</li>\n" +
                "    <li>Statistics generation: maximum 5 seconds</li>\n" +
                "</ul>\n" +
                "\n" +
                "<h3>5.2 Capacity</h3>\n" +
                "<ul>\n" +
                "    <li>Support for at least 100 concurrent users</li>\n" +
                "    <li>Storage of at least 10,000 posts</li>\n" +
                "    <li>Multimedia files up to 50MB each</li>\n" +
                "</ul>\n" +
                "\n" +
                "<h3>5.3 Scalability</h3>\n" +
                "<p>The architecture must allow scaling for a larger number of users through database optimization and caching implementation.</p>\n" +
                "\n" +
                "<h2 id=\"constraints\">6. Design Constraints</h2>\n" +
                "\n" +
                "<h3>6.1 Technologies Used</h3>\n" +
                "<ul>\n" +
                "    <li><strong>Backend:</strong> Java Servlets, JSP</li>\n" +
                "    <li><strong>Frontend:</strong> HTML5, CSS3, JavaScript</li>\n" +
                "    <li><strong>Database:</strong> ORACLE</li>\n" +
                "    <li><strong>Server:</strong> Apache Tomcat</li>\n" +
                "</ul>\n" +
                "\n" +
                "<h3>6.2 Security Requirements</h3>\n" +
                "<ul>\n" +
                "    <li>Validation of all user inputs</li>\n" +
                "    <li>Session-based authentication</li>\n" +
                "    <li>CSRF protection for sensitive forms</li>\n" +
                "    <li>Validation of file size and type for uploads</li>\n" +
                "</ul>\n" +
                "\n" +
                "<h3>6.3 Standards and Compliance</h3>\n" +
                "<ul>\n" +
                "    <li>Compliance with W3C standards for HTML and CSS</li>\n" +
                "    <li>Accessibility guidelines</li>\n" +
                "    <li>Responsive design for various screen sizes</li>\n" +
                "    <li>Cross-browser compatibility (Chrome, Firefox, Safari, Edge)</li>\n" +
                "</ul>\n" +
                "\n" +
                "<h3>Document Information</h3>\n" +
                "<table border=\"1\">\n" +
                "    <tr><td>Document Version</td><td>1.0</td></tr>\n" +
                "    <tr><td>Creation Date</td><td>June 2025</td></tr>\n" +
                "    <tr><td>Status</td><td>BETA</td></tr>\n" +
                "    <tr><td>Classification</td><td>Academic Project</td></tr>\n" +
                "</table>\n" +
                "</body>\n" +
                "</html>";
    }
}