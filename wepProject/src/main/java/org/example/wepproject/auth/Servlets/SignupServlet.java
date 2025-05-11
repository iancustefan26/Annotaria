package org.example.wepproject.auth.Servlets;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.wepproject.auth.DAOs.UserDAO;
import org.example.wepproject.auth.Models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/signup.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String email = req.getParameter("email");

        if (username == null || password == null || email == null ||
                username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/signup.jsp?error=exists");
            return;
        }

        try {
            // check username
            if (userDAO.findByUsername(username) != null) {
                resp.sendRedirect(req.getContextPath() + "/signup.jsp?error=exists");
                return;
            }
            // check email
            if (userDAO.findByEmail(email) != null) {
                resp.sendRedirect(req.getContextPath() + "/signup.jsp?error=exists");
                return;
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            User user = User.builder()
                    .username(username)
                    .password(hashedPassword)
                    .email(email)
                    .build();
            userDAO.save(user);

            resp.sendRedirect(req.getContextPath() + "/login.jsp?signup=success");
        } catch (Exception e) {
            String errorMessage = "Registration failed";
            if (e.getCause() instanceof SQLException) {
                SQLException sqlEx = (SQLException) e.getCause();
                if (sqlEx.getErrorCode() == 1) {
                    errorMessage = "Username or email already exists";
                }
            }
            resp.sendRedirect(req.getContextPath() + "/signup.jsp?error=exists");
        }
    }
}

