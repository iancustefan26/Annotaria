package org.example.wepproject.auth.Servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.wepproject.auth.DAOs.UserDAO;
import org.example.wepproject.auth.Models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?error=invalid");
            return;
        }

        try {
            User user = userDAO.findByUsername(username);
            if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                HttpSession session = req.getSession();
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                resp.sendRedirect(req.getContextPath() + "/welcome.jsp");
            } else {
                resp.sendRedirect(req.getContextPath() + "/login.jsp?error=invalid");
            }
        } catch (Exception e) {
            // Log the exception in production
            resp.sendRedirect(req.getContextPath() + "/login.jsp?error=db");
        }
    }

}
