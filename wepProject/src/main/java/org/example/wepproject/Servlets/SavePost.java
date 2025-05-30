package org.example.wepproject.Servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.wepproject.DAOs.*;
import org.example.wepproject.DTOs.ApiDTO;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/save")
public class SavePost extends HttpServlet {
    private PostDAO postDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        postDAO = new PostDAO();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Long userId = (Long) req.getSession().getAttribute("userId");
        if (userId == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "User not logged in"));
            return;
        }

        try {
            SaveRequest request = objectMapper.readValue(req.getReader(), SaveRequest.class);
            Long postId = request.getPostId();
            boolean save = request.isSave();

            if (save) {
                if (postDAO.isSavedPostById(userId, postId)) {
                    objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Post already saved"));
                    return;
                }
                postDAO.savePost(userId, postId);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "Post saved"));
            } else {
                if (!postDAO.isSavedPostById(userId, postId)) {
                    objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Post not saved"));
                    return;
                }
                postDAO.unsavePost(userId, postId);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "Post unsaved"));
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Database error: " + e.getMessage()));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Server error: " + e.getMessage()));
        }
    }

    private static class SaveRequest {
        private Long postId;
        private boolean save;

        public Long getPostId() { return postId; }
        public void setPostId(Long postId) { this.postId = postId; }
        public boolean isSave() { return save; }
        public void setSave(boolean save) { this.save = save; }
    }
}
