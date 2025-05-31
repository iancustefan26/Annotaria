package org.example.wepproject.Servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.wepproject.DAOs.PostDAO;
import org.example.wepproject.DAOs.UserDAO;
import org.example.wepproject.DTOs.ApiDTO;
import org.example.wepproject.DTOs.PostDTO;
import org.example.wepproject.Exceptions.UserNotFoundException;
import org.example.wepproject.Models.Post;
import org.example.wepproject.Models.User;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/export-saved-posts")
public class ExportSavedPostsServlet extends HttpServlet {
    private PostDAO postDAO;
    private UserDAO userDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        postDAO = new PostDAO();
        userDAO = new UserDAO();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        if (userId == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "User not logged in"));
            return;
        }

        String format = req.getParameter("format");
        if (format == null || (!format.equals("json") && !format.equals("xml"))) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Invalid format specified"));
            return;
        }

        try {
            List<Post> savedPosts = postDAO.findAllSavedPostsByUserId(userId);
            List<PostDTO> postDTOs = savedPosts.stream()
                    .map(post -> {
                        try {
                            User author = userDAO.findById(post.getAuthorId());
                            PostDTO dto = PostDTO.PostToPostDTO(post, userId, author.getUsername());
                            System.out.println("Exporting post ID=" + dto.getId() + ", mediaType=" + dto.getMediaType());
                            return dto;
                        } catch (UserNotFoundException e) {
                            throw new RuntimeException("Error converting post to DTO: " + post.getId(), e);
                        }
                    })
                    .collect(Collectors.toList());

            if (format.equals("json")) {
                resp.setContentType("application/json");
                resp.setHeader("Content-Disposition", "attachment; filename=saved_posts.json");
                objectMapper.writeValue(resp.getWriter(), postDTOs);
            } else {
                resp.setContentType("application/xml");
                resp.setHeader("Content-Disposition", "attachment; filename=saved_posts.xml");
                String xml = generateXml(postDTOs);
                System.out.println("Generated XML: " + xml);
                resp.getWriter().write(xml);
            }
        } catch (SQLException e) {
            System.err.println("Export error: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Database error: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("Export error: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Unexpected error: " + e.getMessage()));
        }
    }

    private String generateXml(List<PostDTO> posts) throws Exception {
        StringWriter stringWriter = new StringWriter();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(stringWriter);

        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("savedPosts");

        for (PostDTO post : posts) {
            writer.writeStartElement("post");
            writer.writeAttribute("id", post.getId().toString());

            writeElement(writer, "authorId", post.getAuthorId().toString());
            writeElement(writer, "authorUsername", post.getAuthorUsername());
            if (post.getCategoryId() != null) {
                writeElement(writer, "categoryId", post.getCategoryId().toString());
            }
            writeElement(writer, "mediaBlobBase64", post.getMediaBlobBase64());
            writeElement(writer, "externalMediaUrl", post.getExternalMediaUrl());
            if (post.getCreationYear() != null) {
                writeElement(writer, "creationYear", post.getCreationYear().toString());
            }
            if (post.getDatePosted() != null) {
                writeElement(writer, "datePosted", post.getDatePosted().toString());
            }
            writeElement(writer, "description", post.getDescription());
            writeElement(writer, "likeCount", String.valueOf(post.getLikeCount()));
            writeElement(writer, "commentCount", String.valueOf(post.getCommentCount()));
            writeElement(writer, "isOwnPost", post.getIsOwnPost().toString());
            writeElement(writer, "isSaved", post.getIsSaved().toString());
            writeElement(writer, "isLiked", post.getIsLiked().toString());
            writeElement(writer, "mediaType", post.getMediaType());

            writer.writeEndElement();
        }

        writer.writeEndElement();
        writer.writeEndDocument();
        writer.flush();
        writer.close();
        return stringWriter.toString();
    }

    private void writeElement(XMLStreamWriter writer, String name, String value) throws Exception {
        if (value != null) {
            writer.writeStartElement(name);
            writer.writeCharacters(value);
            writer.writeEndElement();
        }
    }
}