package org.example.wepproject.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.wepproject.DAOs.PostDAO;
import org.example.wepproject.DAOs.UserDAO;
import org.example.wepproject.DTOs.ApiDTO;
import org.example.wepproject.Models.Post;

import java.io.IOException;
import java.io.InputStream;
import oracle.sql.BLOB;

import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.sql.rowset.serial.SerialBlob;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import jakarta.servlet.http.HttpSession;
import javax.sql.rowset.serial.SerialBlob;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/import")
@MultipartConfig(maxFileSize = 1024 * 1024 * 50) // 50MB limit
public class ImportServlet extends HttpServlet {
    private PostDAO postDAO;
    private ObjectMapper objectMapper;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        postDAO = new PostDAO();
        objectMapper = new ObjectMapper();
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            Long userId = (Long) req.getSession().getAttribute("userId");
            if (userId == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "User not logged in"));
                return;
            }

            Part filePart = req.getPart("contentFile");
            String description = req.getParameter("description");
            String categoryIdStr = req.getParameter("categoryId");
            Long categoryId;

            try {
                categoryId = Long.parseLong(categoryIdStr);
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Invalid category selected"));
                return;
            }

            Blob mediaBlob = null;
            if (filePart != null && filePart.getSize() > 0) {
                try (InputStream input = filePart.getInputStream()) {
                    byte[] bytes = input.readAllBytes();
                    if (bytes.length > 0) {
                        mediaBlob = new SerialBlob(bytes);
                    } else {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Uploaded file is empty"));
                        return;
                    }
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "No file uploaded"));
                return;
            }

            if (description == null || description.trim().isEmpty()) {
                description = "No description provided";
            } else if (description.length() > 2000) {
                description = description.substring(0, 2000);
            }

            Post post = Post.builder()
                    .authorId(userId)
                    .description(description)
                    .mediaBlob(mediaBlob)
                    .datePosted(new Timestamp(System.currentTimeMillis()))
                    .likesCount(0)
                    .categoryId(categoryId)
                    .commentsCount(0)
                    .build();
            postDAO.save(post);

            objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "Post uploaded successfully"));
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Database error: " + e.getMessage()));
        } catch (IOException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "File processing error: " + e.getMessage()));
        } catch (RuntimeException e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            String errorMessage = cause != null ? cause.getMessage() : e.getMessage();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Failed to save post: " + errorMessage));
        }
    }
}