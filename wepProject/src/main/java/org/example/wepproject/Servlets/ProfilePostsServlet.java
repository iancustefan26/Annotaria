package org.example.wepproject.Servlets;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.wepproject.DAOs.CommentDAO;
import org.example.wepproject.DAOs.LikeDAO;
import org.example.wepproject.DAOs.PostDAO;
import org.example.wepproject.DTOs.ApiDTO;
import org.example.wepproject.DTOs.PostDTO;
import org.example.wepproject.Models.Post;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


@WebServlet("/posts")
public class ProfilePostsServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private PostDAO postDAO;
    private LikeDAO likeDAO;
    private CommentDAO commentDAO;

    @Override
    public void init() throws ServletException {
        postDAO = new PostDAO();
        likeDAO = new LikeDAO();
        commentDAO = new CommentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            Long userId = (Long) req.getSession().getAttribute("userId");
            if (userId == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "User not logged in"));
                return;
            }

            List<Post> posts = postDAO.findByUserId(userId);

            List<PostDTO> postDTOs = posts != null ? posts.stream()
                    .map(PostDTO::PostToPostDTO).collect(Collectors.toList()) : List.of();

            objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "Posts retrieved successfully", postDTOs));
        } catch (Exception e) {
            System.out.println("Error retrieving posts: " + e.getMessage());
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Failed to retrieve posts: " + e.getMessage()));
        }
    }
}
