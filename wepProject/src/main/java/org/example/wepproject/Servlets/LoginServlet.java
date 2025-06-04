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


import java.util.regex.Pattern;


import org.owasp.encoder.Encode;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;
    private ObjectMapper objectMapper;

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern SAFE_INPUT_PATTERN = Pattern.compile("^[a-zA-Z0-9@._-]+$");

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        String csrfToken = java.util.UUID.randomUUID().toString();
        session.setAttribute("csrfToken", csrfToken);
        req.setAttribute("csrfToken", Encode.forHtmlAttribute(csrfToken));
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            HttpSession session = req.getSession(false);
            if (session == null) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Invalid session"));
                return;
            }

            LoginDTO loginDTO = objectMapper.readValue(req.getReader(), LoginDTO.class);
            String username = loginDTO.getUsername();
            String password = loginDTO.getPassword();

            if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Username and password required"));
                return;
            }

            username = sanitizeInput(username.trim());
            if (!isValidUsername(username)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Invalid username format"));
                return;
            }

            if (isRateLimited(req.getRemoteAddr(), username)) {
                resp.setStatus(HttpServletResponse.SC_GATEWAY_TIMEOUT);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Too many login attempts"));
                return;
            }

            User user = userDAO.findByUsername(username);

            if (user != null && user.getPassword() != null) {
                try {
                    boolean passwordMatch = BCrypt.checkpw(password, user.getPassword());

                    if (passwordMatch) {
                        session.invalidate();
                        session = req.getSession(true);

                        session.setAttribute("userId", user.getId());
                        session.setAttribute("username", Encode.forHtmlAttribute(user.getUsername()));

                        String sessionCookie = String.format("JSESSIONID=%s; Path=%s; HttpOnly; Secure; SameSite=Lax",
                                session.getId(), req.getContextPath());
                        resp.addHeader("Set-Cookie", sessionCookie);

                        resp.setStatus(HttpServletResponse.SC_OK);
                        objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "Login successful"));
                    } else {
                        logFailedAttempt(req.getRemoteAddr(), username);
                        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Invalid username or password"));
                    }
                } catch (Exception e) {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Authentication error occurred"));
                }
            } else {
                logFailedAttempt(req.getRemoteAddr(), username);
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Invalid username or password"));
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "An unexpected error occurred"));
            } catch (IOException ioException) {
                // Log ioException if needed
            }
        }
    }

    private String sanitizeInput(String input) {
        if (input == null) return null;
        // Use OWASP Encoder for HTML context
        return Encode.forHtml(input.trim());
    }

    private String sanitizeOutput(String output) {
        if (output == null) return null;
        return Encode.forHtmlAttribute(output);
    }

    private boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    private boolean isRateLimited(String ip, String username) {
        return false;
    }

    private void logFailedAttempt(String ip, String username) {
        System.out.println("Failed login attempt from IP: " + ip + " for username: " + username);
    }
}