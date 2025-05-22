package org.example.wepproject.Servlets;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.wepproject.DAOs.PostDAO;
import org.example.wepproject.DTOs.ApiDTO;
import org.example.wepproject.DTOs.PostDTO;
import org.example.wepproject.Models.Post;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/feed")
public class FeedServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();
    private PostDAO postDAO = new PostDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            Long userId = (Long) req.getSession().getAttribute("userId");
            System.out.println("FeedServlet: Session userId = " + userId);

            List<Post> posts = postDAO.findAll();
            List<PostDTO> postDTOs = posts.stream().map(PostDTO::PostToPostDTO).collect(Collectors.toList());

            objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "Posts retrieved successfully", postDTOs));
        } catch (Exception e) {
            System.out.println("FeedServlet: Error = " + e.getMessage());
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Failed to retrieve posts: " + e.getMessage()));
        }
    }

    private String getUsernameFromSessionOrDB(HttpServletRequest req, Long userId) {
        String username = (String) req.getSession().getAttribute("username");
        if (username != null && req.getSession().getAttribute("userId").equals(userId)) {
            return username;
        }
        return "User" + userId;
    }
}
