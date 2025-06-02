package org.example.wepproject.Servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import org.example.wepproject.DAOs.UserDAO;
import org.example.wepproject.DTOs.ApiDTO;
import org.example.wepproject.DTOs.LoginDTO;
import org.example.wepproject.Models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // Read and parse the JSON request
            LoginDTO loginDTO = objectMapper.readValue(req.getReader(), LoginDTO.class);
            String username = loginDTO.getUsername();
            String password = loginDTO.getPassword();

            System.out.println("Login attempt for username: " + username); // Debug log

            // Validate input
            if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
                System.out.println("Invalid input - empty username or password"); // Debug log
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Username and password required"));
                return;
            }

            // Find user by username
            User user = null;
            user = userDAO.findByUsername(username.trim());
            System.out.println("User found: " + (user != null ? "Yes" : "No")); // Debug log

            // Check credentials
            if (user != null && user.getPassword() != null) {
                try {
                    boolean passwordMatch = BCrypt.checkpw(password, user.getPassword());
                    System.out.println("Password match: " + passwordMatch); // Debug log

                    if (passwordMatch) {
                        // Create session
                        HttpSession session = req.getSession(true);
                        session.setAttribute("userId", user.getId());
                        session.setAttribute("username", user.getUsername());

                        // Create session cookie
                        Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());
                        sessionCookie.setHttpOnly(true);
                        sessionCookie.setSecure(req.isSecure());
                        sessionCookie.setPath(req.getContextPath());
                        resp.addCookie(sessionCookie);

                        System.out.println("Login successful for user: " + username); // Debug log
                        resp.setStatus(HttpServletResponse.SC_OK);
                        objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "Login successful"));
                    } else {
                        System.out.println("Invalid password for user: " + username); // Debug log
                        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Invalid username or password"));
                    }
                } catch (Exception e) {
                    System.err.println("Error during password verification: " + e.getMessage());
                    e.printStackTrace();
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Authentication error occurred"));
                }
            } else {
                System.out.println("User not found or password is null for username: " + username); // Debug log
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Invalid username or password"));
            }

        } catch (com.fasterxml.jackson.core.JsonParseException e) {
            System.err.println("JSON parsing error: " + e.getMessage());
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Invalid JSON format"));
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Server error occurred"));
        } catch (Exception e) {
            System.err.println("Unexpected error in login: " + e.getMessage());
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "An unexpected error occurred"));
            } catch (IOException ioException) {
                System.err.println("Failed to write error response: " + ioException.getMessage());
            }
        }
    }
}