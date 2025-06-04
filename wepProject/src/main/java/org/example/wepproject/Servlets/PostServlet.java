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
import org.example.wepproject.Exceptions.PostNotFoundException;
import org.example.wepproject.Models.Category;
import org.example.wepproject.Models.Post;
import org.example.wepproject.Models.User;

import java.io.IOException;
import java.sql.SQLException;

import static org.example.wepproject.DTOs.PostDTO.PostToPostDTO;

@WebServlet("/post")
public class PostServlet extends HttpServlet {
    private PostDAO postDAO;
    private ObjectMapper objectMapper;
    private UserDAO userDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        postDAO = new PostDAO();
        objectMapper = new ObjectMapper();
        userDAO = new UserDAO();
        categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (req.getSession().getAttribute("userId") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "User not logged in"));
            return;
        }
        try {
            Long loggedInUserId = Long.parseLong(req.getSession().getAttribute("userId").toString());
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Post id required"));
                return;
            }
            Long postId = Long.parseLong(idParam);
            Post post = postDAO.findById(postId);
            if (!post.getAuthorId().equals(loggedInUserId)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "User does not have permission to delete another user's post"));
                return;
            }
            postDAO.deleteByIdWithQuerry(postId);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "Post deleted"));
        } catch (PostNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Post not found"));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Invalid post id"));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", e.getMessage()));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");

        Long userId = (Long) req.getSession().getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect("/login");
            return;
        }

        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                req.setAttribute("error", "Post ID is required");
                req.getRequestDispatcher("/post.jsp").forward(req, resp);
                return;
            }

            Long postId = Long.parseLong(idParam);
            Post post = postDAO.findById(postId);
            User author = userDAO.findById(post.getAuthorId());
            PostDTO postDTO = PostDTO.PostToPostDTO(post, userId, author.getUsername());
            boolean isOwnProfile = post.getAuthorId().equals(userId);
            Category category = categoryDAO.findById(post.getCategoryId());

            req.setAttribute("post", postDTO);
            req.setAttribute("isOwnProfile", isOwnProfile);
            req.setAttribute("categoryName", category != null ? category.getName() : null);
            req.getRequestDispatcher("/post.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            System.err.println("Invalid post ID format: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            req.setAttribute("error", "Invalid post ID format");
            req.getRequestDispatcher("/post.jsp").forward(req, resp);
        } catch (PostNotFoundException e) {
            System.err.println("Post not found: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            req.setAttribute("error", "Post not found");
            req.getRequestDispatcher("/post.jsp").forward(req, resp);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            req.setAttribute("error", "Internal server error: " + e.getMessage());
            req.getRequestDispatcher("/post.jsp").forward(req, resp);
        }
    }
}