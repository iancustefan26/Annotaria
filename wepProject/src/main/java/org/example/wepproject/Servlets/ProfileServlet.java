package org.example.wepproject.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.wepproject.DAOs.PostDAO;
import org.example.wepproject.Exceptions.PostNotFoundException;
import org.example.wepproject.Models.Post;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private PostDAO postDAO;

    @Override
    public void init() {
        postDAO = new PostDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (req.getSession().getAttribute("userId") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }
        try {
            Long userId = (Long) req.getSession().getAttribute("userId");
            List<Post> posts = postDAO.findByUserId(userId);
            long postCount =  posts.size();

            req.setAttribute("posts", posts);
            req.setAttribute("postCount", postCount);
            req.getRequestDispatcher("/profile.jsp").forward(req, resp);
        } catch (PostNotFoundException e) {
            // no posts found
        }catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            req.setAttribute("error", "Failed to load profile: " + e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }

}
