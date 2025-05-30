package org.example.wepproject.Servlets;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.example.wepproject.DAOs.PostDAO;
import org.example.wepproject.DTOs.ApiDTO;
import org.example.wepproject.DTOs.PostDTO;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@WebServlet("/import-saved-posts")
@MultipartConfig(maxFileSize = 50 * 1024 * 1024) // 50MB max
public class ImportSavedPostsServlet extends HttpServlet {
    private PostDAO postDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        postDAO = new PostDAO();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        if (userId == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "User not logged in"));
            return;
        }

        Part filePart = req.getPart("importFile");
        if (filePart == null || filePart.getSize() == 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "No file uploaded"));
            return;
        }

        String fileName = filePart.getSubmittedFileName();
        if (!fileName.endsWith(".json") && !fileName.endsWith(".xml")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Invalid file format. Use .json or .xml"));
            return;
        }

        try (InputStream inputStream = filePart.getInputStream()) {
            List<Long> postIds = new ArrayList<>();
            if (fileName.endsWith(".json")) {
                PostDTO[] posts = objectMapper.readValue(inputStream, PostDTO[].class);
                for (PostDTO post : posts) {
                    if (post.getId() != null) {
                        postIds.add(post.getId());
                    }
                }
            } else {
                postIds = parseXml(inputStream);
            }

            if (postIds.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "No valid post IDs found in file"));
                return;
            }

            int savedCount = 0;
            for (Long postId : postIds) {
                try {
                    if (postDAO.existsById(postId) && !postDAO.isSavedPostById(userId, postId)) {
                        postDAO.savePost(userId, postId);
                        savedCount++;
                    }
                } catch (SQLException e) {
                    System.err.println("Error saving post ID " + postId + ": " + e.getMessage());
                }
            }

            String message = savedCount > 0
                    ? String.format("Successfully imported %d post(s)", savedCount)
                    : "No new posts were imported (posts may not exist or are already saved)";
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", message));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Error importing posts: " + e.getMessage()));
        }
    }

    private List<Long> parseXml(InputStream inputStream) throws Exception {
        List<Long> postIds = new ArrayList<>();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(inputStream);

        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamReader.START_ELEMENT && "post".equals(reader.getLocalName())) {
                String id = reader.getAttributeValue(null, "id");
                if (id != null) {
                    try {
                        postIds.add(Long.parseLong(id));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid post ID: " + id);
                    }
                }
            }
        }
        reader.close();
        return postIds;
    }
}
