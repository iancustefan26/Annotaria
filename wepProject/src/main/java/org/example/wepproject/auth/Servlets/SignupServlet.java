package org.example.wepproject.auth.Servlets;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.wepproject.auth.DAOs.UserDAO;
import org.example.wepproject.Helpers.ApiDTO;
import org.example.wepproject.auth.DTOs.SignupDTO;
import org.example.wepproject.auth.Exceptions.UserNotFoundException;
import org.example.wepproject.auth.Models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
    private UserDAO userDAO;
    private ObjectMapper objectMapper;
    @Override
    public void init() {
        userDAO = new UserDAO();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/signup.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try{
            SignupDTO signupDTO = objectMapper.readValue(req.getReader(), SignupDTO.class);
            String username = signupDTO.getUsername();
            String password = signupDTO.getPassword();
            String email = signupDTO.getEmail();
            if (username == null || password == null || email == null ||
                username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Username and password are required"));
                return;
            }
            // check username
            try{
                userDAO.findByUsername(username);
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Username already exists"));
                return;
            }catch (UserNotFoundException e){
            }catch (RuntimeException e){
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Database error: " + e.getMessage()));
                return;
            }

            // check email
            try{
                userDAO.findByEmail(email);
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Email already exists"));
                return;
            }catch(UserNotFoundException e){
            }catch (RuntimeException e){
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
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", e.getMessage()));
        }
    }
}

