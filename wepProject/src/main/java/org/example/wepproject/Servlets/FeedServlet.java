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


import jakarta.servlet.http.HttpSession;

@WebServlet("/feed")
public class FeedServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private  PostDAO postDAO;
    private  CategoryDAO categoryDAO;
    private static final Map<Long, String> categoryNames = new HashMap<>();
    private  MatrixDAO matrixDAO;

    @Override
    public void init() throws ServletException {
        postDAO = new PostDAO();
        categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.findAll();
        for (Category category : categories) {
            categoryNames.put(category.getId(), category.getName());
        }
        matrixDAO = new MatrixDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            resp.sendRedirect("login");
            return;
        }

        // Initialize or retrieve fetched post IDs from session
        @SuppressWarnings("unchecked")
        Set<Long> fetchedPostIds = (Set<Long>) session.getAttribute("fetchedPostIds");
        if (fetchedPostIds == null) {
            fetchedPostIds = new HashSet<>();
            session.setAttribute("fetchedPostIds", fetchedPostIds);
        }

        final Set<Long> finalFetchedPostIds = fetchedPostIds;

        List<Post> posts = new ArrayList<>();
        Map<String, Object> responseData = new HashMap<>();
        System.out.println("FeedServlet: Request received - Accept: " + req.getHeader("Accept") +
                ", Query: " + req.getQueryString() +
                ", SessionID: " + session.getId());

        try {
            Integer categoryId = req.getParameter("categoryId") != null ? Integer.parseInt(req.getParameter("categoryId")) : null;
            Integer creationYear = req.getParameter("creationYear") != null ? Integer.parseInt(req.getParameter("creationYear")) : null;
            Integer namedTagId = req.getParameter("namedTagId") != null ? Integer.parseInt(req.getParameter("namedTagId")) : null;
            Integer offset = req.getParameter("offset") != null ? Integer.parseInt(req.getParameter("offset")) : 0;
            Integer limit = req.getParameter("limit") != null ? Integer.parseInt(req.getParameter("limit")) : 5;
            boolean reset = req.getParameter("reset") != null && Boolean.parseBoolean(req.getParameter("reset"));

            if (offset < 0 || limit < 1 || limit > 100) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Invalid offset or limit"));
                return;
            }

            if (reset) {
                fetchedPostIds.clear();
                session.setAttribute("fetchedPostIds", fetchedPostIds);
            }

            var m = MatrixConvertor.toMatrix(matrixDAO.getMatrixFromFunction(
                    userId,
                    3, // best_friends
                    2, // random_friends
                    categoryId,
                    creationYear,
                    namedTagId
            ));
            var alg = new PageRanker(m);
            List<Long> postIds = Arrays.stream(alg.runAndGetRankedPostIds())
                    .mapToLong(i -> i)
                    .boxed()
                    .collect(Collectors.toList());

            System.out.println("FeedServlet: Retrieved postIds = " + postIds);

            List<Long> newPostIds = postIds.stream()
                    .filter(id -> !finalFetchedPostIds.contains(id))
                    .collect(Collectors.toList());

            // Apply pagination
            int totalPosts = newPostIds.size();
            List<Long> paginatedPostIds = newPostIds.stream()
                    .skip(offset)
                    .limit(limit)
                    .collect(Collectors.toList());

            // Update fetched post IDs
            fetchedPostIds.addAll(paginatedPostIds);
            session.setAttribute("fetchedPostIds", fetchedPostIds);

            // Fetch posts
            posts = paginatedPostIds.stream()
                    .map(id -> postDAO.findById(id))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // Convert posts to PostDTOs
            List<PostDTO> postDTOs = posts.stream()
                    .map(post -> PostDTO.PostToPostDTO(post, userId, getUsernameFromSessionOrDB(req, post.getAuthorId())))
                    .collect(Collectors.toList());

            String acceptHeader = req.getHeader("Accept");
            if (acceptHeader != null && acceptHeader.contains("application/json")) {
                // Handle AJAX request
                resp.setContentType("application/json");
                responseData.put("posts", postDTOs);
                responseData.put("categoryMap", categoryNames);
                responseData.put("totalPosts", totalPosts);
                responseData.put("offset", offset);
                responseData.put("limit", limit);
                responseData.put("hasMorePosts", offset + paginatedPostIds.size() < totalPosts);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "Posts retrieved successfully", responseData));
            } else {
                // Handle HTML request
                req.setAttribute("posts", postDTOs != null ? postDTOs : List.of());
                req.setAttribute("categoryNames", categoryNames);
                req.setAttribute("totalPosts", totalPosts);
                req.setAttribute("offset", offset);
                req.setAttribute("limit", limit);
                req.setAttribute("hasMorePosts", offset + paginatedPostIds.size() < totalPosts);
                req.getRequestDispatcher("/feed.jsp").forward(req, resp);
            }

        } catch (SQLException e) {
            System.err.println("FeedServlet: SQL error: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Posts not found"));
        } catch (NumberFormatException e) {
            System.err.println("FeedServlet: Invalid parameter format: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Invalid categoryId, creationYear, namedTagId, offset, or limit format"));
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