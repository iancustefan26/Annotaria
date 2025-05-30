package org.example.wepproject.Servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.wepproject.DAOs.CommentDAO;
import org.example.wepproject.DAOs.UserDAO;
import org.example.wepproject.DTOs.ApiDTO;
import org.example.wepproject.DTOs.CommentDTO;
import org.example.wepproject.Models.Comment;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/comment")
public class CommentServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private CommentDAO commentDAO;

    @Override
    public void init() throws ServletException {
        commentDAO = new CommentDAO();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        Long userId = (Long) req.getSession().getAttribute("userId");
        if(userId == null){
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "User not logged in"));
        }
        try{
            Long commentId = Long.parseLong(req.getParameter("id"));
            if(commentId == null){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Comment id is required"));
            }
            Long userIdFromComment = commentDAO.findUserIdFromComment(commentId);
            if(userIdFromComment == null){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Comment not found"));
            }

            commentDAO.deleteById(commentId);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "Comment deleted"));
        }catch (NumberFormatException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Comment id is not a number"));
        }catch(Exception e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Internal server error"));
        }

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

            Map<String, String> requestBody = objectMapper.readValue(req.getReader(), HashMap.class);
            Long postId = Long.valueOf(requestBody.get("postId"));
            String content = requestBody.get("content");
            String username = (String) req.getSession().getAttribute("username");

            if (postId == null || content == null || content.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Post ID and comment content are required"));
                return;
            }

            Comment comment = Comment.builder()
                    .postId(postId)
                    .userId(userId)
                    .content(content)
                    .datePosted(new Timestamp(System.currentTimeMillis()))
                    .build();
            commentDAO.save(comment);
            long commentCount = commentDAO.findByPostId(postId).size();

            objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "Comment added", CommentDTO.builder()
                    .id(comment.getId())
                    .postId(postId)
                    .userId(userId)
                    .username(username)
                    .content(content)
                    .datePosted(comment.getDatePosted())
                    .commentCount(commentCount)
                    .build()));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Invalid post ID format"));
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Failed to process comment: " + e.getMessage()));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            Long userId = (Long) req.getSession().getAttribute("userId");
            if (userId == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "User not logged in"));
                return;
            }

            String postIdParam = req.getParameter("postId");
            if (postIdParam == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Post ID is required"));
                return;
            }

            Long postId;
            try {
                postId = Long.valueOf(postIdParam);
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Invalid post ID format"));
                return;
            }

            List<Comment> comments = commentDAO.findByPostId(postId);
            List<CommentDTO> commentDTOs = comments.stream().map(comment -> CommentDTO.builder()
                    .id(comment.getId())
                    .postId(comment.getPostId())
                    .userId(comment.getUserId())
                    .username(getUsernameFromSessionOrDB(req, comment.getUserId())) // Fallback to DB if needed
                    .content(comment.getContent())
                    .datePosted(comment.getDatePosted())
                    .commentCount(comments.size())
                    .isOwnComment(userId != null && comment.getUserId().equals(userId))
                    .build()
            ).collect(Collectors.toList());

            objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "Comments retrieved successfully", commentDTOs));
        } catch (Exception e) {
            System.out.println("Error retrieving comments: " + e.getMessage());
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Failed to retrieve comments: " + e.getMessage()));
        }
    }

    private String getUsernameFromSessionOrDB(HttpServletRequest req, Long userId) {
        String username = (String) req.getSession().getAttribute("username");
        if (username != null && req.getSession().getAttribute("userId").equals(userId)) {
            return username;
        }

        return new UserDAO().findById(userId).getUsername();

    }

}
