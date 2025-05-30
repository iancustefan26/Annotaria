package org.example.wepproject.Servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.wepproject.DAOs.*;
import org.example.wepproject.DTOs.ApiDTO;
import org.example.wepproject.DTOs.PostDTO;
import org.example.wepproject.Exceptions.PostNotFoundException;
import org.example.wepproject.Exceptions.UserNotFoundException;
import org.example.wepproject.Models.Category;
import org.example.wepproject.Models.Post;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.example.wepproject.Models.User;
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private PostDAO postDAO;
    private UserDAO userDAO;
    private LikeDAO likeDAO;
    private CommentDAO commentDAO;
    private ObjectMapper objectMapper;
    private CategoryDAO categoryDAO;
    @Override
    public void init() {
        postDAO = new PostDAO();
        userDAO = new UserDAO();
        likeDAO = new LikeDAO();
        commentDAO = new CommentDAO();
        objectMapper = new ObjectMapper();
        categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Object userIdObj = req.getSession().getAttribute("userId");
        if (userIdObj == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "User not logged in"));
            return;
        }
        try {
            Long profileUserId = Long.parseLong(userIdObj.toString());
            System.out.println("hello from delete profile servlet");
            userDAO.deleteById(profileUserId);

            req.getSession().invalidate();
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "User deleted"));
        } catch (UserNotFoundException e) {

            System.out.println("user not found" + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "User not found: " + e.getMessage()));
        } catch (Exception e) {

            System.out.println("exception" + e.getMessage());
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
        List<Category> categoryDAOs;
        categoryDAOs = categoryDAO.findAll();
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
                } catch (UserNotFoundException e) {
                    req.setAttribute("error", "User not found");
                    req.getRequestDispatcher("/error.jsp").forward(req, resp);
                    return;
                } catch (Exception e) {
                    req.setAttribute("error", "Error finding user: " + e.getMessage());
                    req.getRequestDispatcher("/error.jsp").forward(req, resp);
                    return;
                }
            }
            List<Post> posts;
            Long saved;
            if (isOwnProfile) {
                saved =  Long.parseLong(req.getParameter("saved") == null ? "0" : req.getParameter("saved"));
                if(saved == 0){
                    posts = postDAO.findByUserId(profileUserId);
                }
                else{
                    posts = postDAO.findAllSavedPostsByUserId(profileUserId);
                }
            }else{
                posts = postDAO.findByUserId(profileUserId);
                saved = 0L;
            }
            // Get the user's posts
            long postCount = posts.size();

            User author = userDAO.findById(profileUserId);

            List<PostDTO> postDTOs = posts != null ? posts.stream()
                    .map(post -> PostDTO.PostToPostDTO(post, loggedInUserId, author.getUsername()))
                    .collect(Collectors.toList()) : List.of();

            req.setAttribute("posts", postDTOs);
            req.setAttribute("postCount", postCount);
            req.setAttribute("isOwnProfile", isOwnProfile);
            req.setAttribute("categories", categoryDAOs);
            req.setAttribute("saved", saved == 1 ? true : false);

            req.getRequestDispatcher("/profile.jsp").forward(req, resp);
        } catch (PostNotFoundException e) {
            //
            List<PostDTO> postDTOs = List.of();
            req.setAttribute("posts", postDTOs);
            req.setAttribute("postCount", 0);
            req.setAttribute("isOwnProfile", isOwnProfile);
            req.setAttribute("categories", categoryDAOs);
            req.getRequestDispatcher("/profile.jsp").forward(req, resp);
        } catch (RuntimeException e) {
            System.out.println("error runtime exception in profile servlet : " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            req.setAttribute("error", "Failed to load profile: " + e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }

}

/* TODO :
    1. MAKE THE SAVED PHOTOS BUTTON TO WORK - DONE
    2. ADD SAVE BUTTON IN EVERY POST AND BUILD A SERVLET THAT HANDLES THE FUNCTIONALITY - DONE
    3. ADD BUTTON FOR IMPORT AND EXPORT THE SAVED PHOTOS IN JSON AND XML
    4. RESOLVE FOR THE POSTS AND CATEGORY TO APPEAR THE ACTUAL NAMES NOT IDS
    5. LOOK INTO CSV/SVG FORMATING FOR STATISTICS
* */