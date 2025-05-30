package org.example.wepproject.Servlets;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.wepproject.DAOs.CategoryDAO;
import org.example.wepproject.DAOs.PostDAO;
import org.example.wepproject.DAOs.UserDAO;
import org.example.wepproject.DTOs.ApiDTO;
import org.example.wepproject.DTOs.PostDTO;
import org.example.wepproject.Models.Post;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/feed")
public class FeedServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();
    private PostDAO postDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        postDAO = new PostDAO();
        categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        System.out.println("FeedServlet: Session userId = " + userId);

        if (userId == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        String acceptHeader = req.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains("application/json")) {
            // Handle AJAX request
            resp.setContentType("application/json");
            List<Post> posts = postDAO.findAll();
            List<PostDTO> postDTOs = posts.stream()
                    .map(post -> PostDTO.PostToPostDTO(post, userId, getUsernameFromSessionOrDB(req, post.getAuthorId())))
                    .collect(Collectors.toList());
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "Posts retrieved successfully", postDTOs));
        } else {
            // Handle HTML request
            List<Post> posts = postDAO.findAll();
            System.out.println(posts.size());
            req.setAttribute("posts", posts != null ? posts : List.of());
            req.getRequestDispatcher("/feed.jsp").forward(req, resp);
        }
    }

    private String getUsernameFromSessionOrDB(HttpServletRequest req, Long userId) {
        String username = (String) req.getSession().getAttribute("username");
        if (username != null && req.getSession().getAttribute("userId").equals(userId)) {
            return username;
        }
        return new UserDAO().findById(userId).getUsername();
    }
}