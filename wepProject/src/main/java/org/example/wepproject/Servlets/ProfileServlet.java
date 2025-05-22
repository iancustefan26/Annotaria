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
import org.example.wepproject.Exceptions.PostNotFoundException;
import org.example.wepproject.Exceptions.UserNotFoundException;
import org.example.wepproject.Models.Post;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import org.example.wepproject.DAOs.LikeDAO;
import org.example.wepproject.DAOs.CommentDAO;
import org.example.wepproject.Models.User;
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private PostDAO postDAO;
    private UserDAO userDAO;
    private LikeDAO likeDAO;
    private CommentDAO commentDAO;
    private ObjectMapper objectMapper;
    @Override
    public void init() {
        postDAO = new PostDAO();
        userDAO = new UserDAO();
        likeDAO = new LikeDAO();
        commentDAO = new CommentDAO();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        System.out.println("hello from delete profile servlet");

        Object userIdObj = req.getSession().getAttribute("userId");
        if (userIdObj == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "User not logged in"));
            return;
        }

        try {
            Long profileUserId = Long.parseLong(userIdObj.toString());
            userDAO.deleteById(profileUserId);

            req.getSession().invalidate();
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "User deleted"));
        } catch (UserNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "User not found: " + e.getMessage()));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Server error: " + e.getMessage()));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (req.getSession().getAttribute("userId") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        boolean isOwnProfile = true;
        try {
            Long loggedInUserId = (Long) req.getSession().getAttribute("userId");
            Long profileUserId = loggedInUserId; // Default to viewing own profile

            // Check if we're visiting another user's profile via the id parameter
            String userIdParam = req.getParameter("userId");
            if (userIdParam != null && !userIdParam.isEmpty()) {
                try {
                    profileUserId = Long.parseLong(userIdParam);
                    isOwnProfile = profileUserId.equals(loggedInUserId);

                    User profileUser = userDAO.findById(profileUserId);

                    // pass user info to the jsp
                    req.setAttribute("profileUser", profileUser);
                } catch (NumberFormatException e) {
                    req.setAttribute("error", "Invalid user ID");
                    req.getRequestDispatcher("/error.jsp").forward(req, resp);
                    return;
                }catch (UserNotFoundException e) {
                    req.setAttribute("error", "User not found");
                    req.getRequestDispatcher("/error.jsp").forward(req, resp);
                    return;
                }
                catch (Exception e) {
                    req.setAttribute("error", "Error finding user: " + e.getMessage());
                    req.getRequestDispatcher("/error.jsp").forward(req, resp);
                    return;
                }
            }
            // Get the user's posts
            List<Post> posts = postDAO.findByUserId(profileUserId);

            long postCount = posts.size();

            List<PostDTO> postDTOs = posts != null ? posts.stream()
                    .map(PostDTO::PostToPostDTO).collect(Collectors.toList()) : List.of();
            req.setAttribute("posts", postDTOs);
            req.setAttribute("postCount", postCount);
            req.setAttribute("isOwnProfile", isOwnProfile);

            req.getRequestDispatcher("/profile.jsp").forward(req, resp);
        } catch (PostNotFoundException e) {
            //
            List<PostDTO> postDTOs = List.of();
            req.setAttribute("posts", postDTOs);
            req.setAttribute("postCount", 0);
            req.setAttribute("isOwnProfile", isOwnProfile);
            req.getRequestDispatcher("/profile.jsp").forward(req, resp);
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            req.setAttribute("error", "Failed to load profile: " + e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }

    // add delete account
}
