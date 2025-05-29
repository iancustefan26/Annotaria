package org.example.wepproject.Servlets;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.wepproject.DAOs.PostDAO;
import org.example.wepproject.DAOs.UserDAO;
import org.example.wepproject.DTOs.ApiDTO;
import org.example.wepproject.DTOs.PostDTO;
import org.example.wepproject.Exceptions.PostNotFoundException;
import org.example.wepproject.Models.Post;
import org.example.wepproject.Models.User;

import java.io.IOException;
import java.sql.SQLException;

import static org.example.wepproject.DTOs.PostDTO.PostToPostDTO;

@WebServlet("/post")
public class PostServlet extends HttpServlet {
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
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Check if user is logged in
        if (req.getSession().getAttribute("userId") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "User not logged in"));
            return;
        }
        try{

            Long LoggedInUserId = Long.parseLong(req.getSession().getAttribute("userId").toString());
            String idParam = req.getParameter("id");
            if(idParam == null || idParam.trim().isEmpty()){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Post id required"));
                return;
            }
            Long postId = Long.parseLong(idParam);
            Post post = postDAO.findById(postId);
            Long authorId = post.getAuthorId();
            if(post.getAuthorId() != LoggedInUserId ){
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "User doest not have permission to delete another user post"));
                return;
            }
            System.out.println(authorId + " " + LoggedInUserId);
            postDAO.deleteByIdWithQuerry(postId);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("success", "Post deleted"));
        }catch(PostNotFoundException e){
            System.out.println("catch 1");
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Post not found"));
            return;
        }catch(NumberFormatException e){
            System.out.println("catch 2");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", "Invalid post id"));
        }catch (Exception e){
            System.out.println("catch 3");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ApiDTO("error", e.getMessage()));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Set response type to JSON
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Long userId = Long.parseLong(req.getSession().getAttribute("userId").toString());
        if (userId == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }

        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            Long postId = Long.parseLong(idParam);
            Post post = postDAO.findById(postId);
            Long postAuthorId = post.getAuthorId();

            User author = userDAO.findById(postAuthorId);

            PostDTO postDTO = PostToPostDTO(post,userId, author.getUsername());
            boolean isOwnProfile = postAuthorId.equals(userId) ? true : false;
            req.setAttribute("post", postDTO);
            req.setAttribute("isOwnProfile", isOwnProfile);
            req.getRequestDispatcher("/post.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            req.setAttribute("error", "Number format exception");
            req.getRequestDispatcher("/post.jsp").forward(req, resp);
        } catch (PostNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            req.setAttribute("error", "Invalid post id");
            req.getRequestDispatcher("/post.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            req.setAttribute("error", "Internal server error");
            req.getRequestDispatcher("/post.jsp").forward(req, resp);
        }
    }
}
