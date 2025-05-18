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

        try{
            LoginDTO loginDTO = objectMapper.readValue(req.getReader(), LoginDTO.class);
            String username = loginDTO.getUsername();
            String password = loginDTO.getPassword();

            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Username and password requiered"));
                return;
            }

            User user = userDAO.findByUsername(username);


            if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                // create session
                HttpSession session = req.getSession(true);
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                // create session cookie
                Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());
                sessionCookie.setHttpOnly(true);
                sessionCookie.setSecure(req.isSecure());
                sessionCookie.setPath(req.getContextPath());
                resp.addCookie(sessionCookie);

                objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "Login successful"));
                System.out.println("Redirecting to feed.jsp");

            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Invalid username or password"));
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", e.getMessage()));
        }
    }

}
