package org.example.wepproject.Servlets;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.wepproject.DAOs.CategoryDAO;
import org.example.wepproject.DAOs.MatrixDAO;
import org.example.wepproject.DAOs.PostDAO;
import org.example.wepproject.DAOs.UserDAO;
import org.example.wepproject.DTOs.ApiDTO;
import org.example.wepproject.DTOs.PostDTO;
import org.example.wepproject.Helpers.PageRank.MatrixConvertor;
import org.example.wepproject.Helpers.PageRank.PageRanker;
import org.example.wepproject.Models.Category;
import org.example.wepproject.Models.Post;
import org.example.wepproject.Models.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/*
TODO:
    - SEARCH BAR TO WORK
    - LEADERBOARD IN LOW LEFT
    - PHONE
    - SQL INJECTION FOR LOGIN
 */

@WebServlet("/feed")
public class FeedServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();
    private PostDAO postDAO;
    private CategoryDAO categoryDAO;
    private static Map<Long, String> categoryNames;
    private Map<String, Object> responseData;
    private MatrixDAO matrixDAO;

    @Override
    public void init() throws ServletException {
        postDAO = new PostDAO();
        categoryDAO = new CategoryDAO();
        categoryNames = new HashMap<>();
        List<Category> categories = categoryDAO.findAll();
        for (Category category : categories) {
            categoryNames.put(category.getId(), category.getName());
        }
        responseData = new HashMap<>();
        matrixDAO = new MatrixDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");

        if (userId == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        List<Post> posts = new ArrayList<>();
        System.out.println("FeedServlet: Request received - Accept: " + req.getHeader("Accept") +
                ", Query: " + req.getQueryString() +
                ", SessionID: " + req.getSession().getId());
        try {
            // Read query parameters
            Integer categoryId = req.getParameter("categoryId") != null ? Integer.parseInt(req.getParameter("categoryId")) : null;
            Integer creationYear = req.getParameter("creationYear") != null ? Integer.parseInt(req.getParameter("creationYear")) : null;
            Integer namedTagId = req.getParameter("namedTagId") != null ? Integer.parseInt(req.getParameter("namedTagId")) : null;

            var m = MatrixConvertor.toMatrix(matrixDAO.getMatrixFromFunction(
                    userId, // Use actual user ID
                    3, // best_friends
                    2, // random_friends
                    categoryId, // category id
                    creationYear, // creation year
                    namedTagId // named tag id
            ));
            var alg = new PageRanker(m);
            List<Long> postIds = Arrays.stream(alg.runAndGetRankedPostIds()).mapToLong(i -> i).boxed().toList();
            System.out.println("FeedServlet: Retrieved postIds = " + postIds);
            posts = postIds.stream()
                    .map(id -> {
                        Post post = postDAO.findById(id);
                        return post;
                    })
                    .filter(Objects::nonNull)
                    .toList();

        } catch (SQLException e) {
            System.err.println("FeedServlet: SQL error: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Database error: " + e.getMessage()));
            return;
        } catch (NumberFormatException e) {
            System.err.println("FeedServlet: Invalid parameter format: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Invalid categoryId, creationYear, or namedTagId format"));
            return;
        }

        // Convert posts to PostDTOs for both JSON and HTML paths
        List<PostDTO> postDTOs = posts.stream()
                .map(post -> PostDTO.PostToPostDTO(post, userId, getUsernameFromSessionOrDB(req, post.getAuthorId())))
                .collect(Collectors.toList());

        String acceptHeader = req.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains("application/json")) {
            // Handle AJAX request
            resp.setContentType("application/json");
            responseData.put("posts", postDTOs);
            responseData.put("categoryMap", categoryNames);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "Posts retrieved successfully", responseData));
        } else {
            // Handle HTML request
            req.setAttribute("posts", postDTOs != null ? postDTOs : List.of());
            req.setAttribute("categoryNames", categoryNames);
            req.getRequestDispatcher("/feed.jsp").forward(req, resp);
        }
    }

    private String getUsernameFromSessionOrDB(HttpServletRequest req, Long userId) {
        String username = (String) req.getSession().getAttribute("username");
        if (username != null && req.getSession().getAttribute("userId").equals(userId)) {
            return username;
        }
        User user = new UserDAO().findById(userId);
        return user != null ? user.getUsername() : "Unknown";
    }
}