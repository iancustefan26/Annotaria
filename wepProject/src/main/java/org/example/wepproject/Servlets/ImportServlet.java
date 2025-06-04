package org.example.wepproject.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.wepproject.DAOs.NamedTagDAO;
import org.example.wepproject.DAOs.PostDAO;
import org.example.wepproject.DAOs.UserDAO;
import org.example.wepproject.DTOs.ApiDTO;
import org.example.wepproject.Models.NamedTag;
import org.example.wepproject.Models.Post;

import java.io.IOException;
import java.io.InputStream;

import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.sql.rowset.serial.SerialBlob;

@WebServlet("/import")
@MultipartConfig(maxFileSize = 1024 * 1024 * 50) // 50MB limit
public class ImportServlet extends HttpServlet {
    private PostDAO postDAO;
    private ObjectMapper objectMapper;
    private UserDAO userDAO;
    private NamedTagDAO namedTagDAO;

    @Override
    public void init() throws ServletException {
        postDAO = new PostDAO();
        objectMapper = new ObjectMapper();
        userDAO = new UserDAO();
        namedTagDAO = new NamedTagDAO();
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
            String[] namedTagIds = req.getParameterValues("namedTagIds[]");
            String[] userTaggedIds = req.getParameterValues("userTaggedIds[]");
            String mediaType = req.getParameter("mediaType");
            for(int i = 0; i < namedTagIds.length / 2 ; i++) {
                NamedTag tag = namedTagDAO.findById(Long.valueOf(namedTagIds[i]));
                description += " #" + tag.getName() + " ";
            }
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
                String fileType = filePart.getContentType();
                if (!fileType.startsWith("image/") && !fileType.equals("video/mp4") && !fileType.equals("video/quicktime")) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Invalid file type. Only images and MP4/MOV videos are supported"));
                    return;
                }
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
                    .mediaType(mediaType)
                    .datePosted(new Timestamp(System.currentTimeMillis()))
                    .likesCount(0)
                    .categoryId(categoryId)
                    .commentsCount(0)
                    .build();
            post = postDAO.save(post); // Save post and get generated ID

            // Add named tag frames
            if (namedTagIds != null) {
                for (String tagIdStr : namedTagIds) {
                    try {
                        Long tagId = Long.parseLong(tagIdStr);
                        namedTagDAO.addNamedTagFrame(tagId, post.getId());
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid namedTagId: " + tagIdStr);
                    } catch (SQLException e) {
                        System.err.println("Failed to add named tag frame for tagId " + tagIdStr + ": " + e.getMessage());
                    }
                }
            }

            // Add user tag frames
            if (userTaggedIds != null) {
                for (String taggedIdStr : userTaggedIds) {
                    try {
                        Long taggedId = Long.parseLong(taggedIdStr);
                        namedTagDAO.addUserTagFrame(post.getId(), userId, taggedId);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid userTaggedId: " + taggedIdStr);
                    } catch (SQLException e) {
                        System.err.println("Failed to add user tag frame for userId " + taggedIdStr + ": " + e.getMessage());
                    }
                }
            }

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