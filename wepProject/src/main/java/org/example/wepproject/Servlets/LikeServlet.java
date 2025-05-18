package org.example.wepproject.Servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.wepproject.DAOs.LikeDAO;
import org.example.wepproject.DTOs.ApiDTO;
import org.example.wepproject.DTOs.LikeDTO;
import org.example.wepproject.Models.Like;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/like")
public class LikeServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private LikeDAO likeDAO;

    @Override
    public void init() throws ServletException {
        likeDAO = new LikeDAO();
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
            if (postId == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Post ID is required"));
                return;
            }

            Like existingLike = likeDAO.findByUserIdAndPostId(userId, postId);
            long likeCount;

            if (existingLike != null) {
                // delete the like if exists
                likeDAO.deleteById(existingLike.getId());
                likeCount = likeDAO.findByPostId(postId).size();
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "Like removed", LikeDTO.builder()
                        .id(null)
                        .postId(postId)
                        .userId(userId)
                        .userHasLiked(false)
                        .likeCount(likeCount)
                        .build()));
            } else {
                // add like
                Like like = Like.builder()
                        .userId(userId)
                        .postId(postId)
                        .build();
                likeDAO.save(like);
                likeCount = likeDAO.findByPostId(postId).size();
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "Like added", LikeDTO.builder()
                        .id(like.getId())
                        .postId(postId)
                        .userId(userId)
                        .userHasLiked(true)
                        .likeCount(likeCount)
                        .build()));
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Invalid post ID format"));
        } catch (Exception e) {
            System.out.println("Error processing like: " + e.getMessage());
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Failed to process like: " + e.getMessage()));
        }
    }
}
