package org.example.wepproject.Servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.wepproject.DAOs.NamedTagDAO;
import org.example.wepproject.DTOs.ApiDTO;
import org.example.wepproject.Models.NamedTag;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/namedTags")
public class NamedTagServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();
    private NamedTagDAO namedTagDAO;

    @Override
    public void init() throws ServletException {
        namedTagDAO = new NamedTagDAO();
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
        Map<Long, String> namedTagMap = new HashMap<>();
        List<NamedTag> namedTags = namedTagDAO.findAll();
        for (NamedTag namedTag : namedTags) {
            namedTagMap.put(namedTag.getId(), namedTag.getName());
        }
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("namedTagMap", namedTagMap);
        objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "Categories retrieved successfully", responseData));
    }
}
