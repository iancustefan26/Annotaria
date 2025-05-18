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

@WebServlet("/post")
public class PostServlet extends HttpServlet {
    private PostDAO postDAO;

    @Override
    public void init() throws ServletException {
        postDAO = new PostDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession().getAttribute("userId") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }
        try {
            Long postId = Long.parseLong(req.getParameter("id"));
            Post post = postDAO.findById(postId);
            req.setAttribute("post", post);
            req.getRequestDispatcher("/post.jsp").forward(req, resp);
        } catch (PostNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            req.setAttribute("error", "Failed to load post: " + e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }
}
