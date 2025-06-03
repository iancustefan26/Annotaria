package org.example.wepproject.Servlets;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.wepproject.DAOs.UserDAO;
import org.example.wepproject.DTOs.ApiDTO;
import org.example.wepproject.Models.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
TODO:
    - validare cod HTML CSS etc. -> DONE
    - comentare si curatare cod
    - specificatie OpenAPI
    - ReadME
    - licenta open-source -> DONE
    - fisa cerintelor in format ScholarlyHTML disponisibil pe site ul nostru
    - filmulet demonstrativ
 */

@WebServlet("/users")
public class UsersServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        if (userId == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "User not logged in"));
            return;
        }

        resp.setContentType("application/json");

        String searchTerm = req.getParameter("term");
        if (searchTerm == null) {
            searchTerm = "";
        }
        searchTerm = searchTerm.toLowerCase().trim();

        Map<Long, String> userMap = new HashMap<>();
        List<User> users = userDAO.findAll();

        for (User user : users) {
            if (!user.getId().equals(userId)) {
                if (searchTerm.isEmpty() ||
                        user.getUsername().toLowerCase().contains(searchTerm)) {
                    userMap.put(user.getId(), user.getUsername());
                }
            }
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("userMap", userMap);
        objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "Users retrieved successfully", responseData));
    }
}
