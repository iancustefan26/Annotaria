package org.example.wepproject.Servlets;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.wepproject.DAOs.UserDAO;
import org.example.wepproject.DTOs.ApiDTO;
import org.example.wepproject.DTOs.SignupDTO;
import org.example.wepproject.Exceptions.UserNotFoundException;
import org.example.wepproject.Models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;


import org.owasp.encoder.Encode;

import java.util.regex.Pattern;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
    private UserDAO userDAO;
    private ObjectMapper objectMapper;

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    @Override
    public void init() {
        userDAO = new UserDAO();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        String csrfToken = java.util.UUID.randomUUID().toString();
        session.setAttribute("csrfToken", csrfToken);
        req.setAttribute("csrfToken", Encode.forHtmlAttribute(csrfToken));
        req.getRequestDispatcher("/signup.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            SignupDTO signupDTO = objectMapper.readValue(req.getReader(), SignupDTO.class);
            String username = signupDTO.getUsername();
            String email = signupDTO.getEmail();
            String password = signupDTO.getPassword();

            if (username == null || password == null || email == null ||
                    username.trim().isEmpty() || password.trim().isEmpty() || email.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Username, password, and email are required"));
                return;
            }

            username = sanitizeInput(username.trim());
            email = sanitizeInput(email.trim());

            if (!isValidUsername(username)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Invalid username format"));
                return;
            }

            if (!isValidEmail(email)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Invalid email format"));
                return;
            }

            try {
                userDAO.findByUsername(username);
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Username already exists"));
                return;
            } catch (UserNotFoundException e) {
                // Username is available
            } catch (RuntimeException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Database error: " + e.getMessage()));
                return;
            }

            try {
                userDAO.findByEmail(email);
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Email already exists"));
                return;
            } catch (UserNotFoundException e) {
                // Email is available
            } catch (RuntimeException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Database error: " + e.getMessage()));
                return;
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            User user = User.builder()
                    .username(username)
                    .password(hashedPassword)
                    .email(email)
                    .build();
            userDAO.save(user);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "User created"));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", Encode.forHtml(e.getMessage())));
        }
    }

    private String sanitizeInput(String input) {
        if (input == null) return null;
        return Encode.forHtml(input.trim());
    }

    private boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}

