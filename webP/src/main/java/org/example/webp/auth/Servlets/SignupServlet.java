package org.example.webp.auth.Servlets;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.webp.Helpers.JpaUtil;
import org.example.webp.auth.Models.User;
import org.example.webp.auth.Repositories.UserRepository;

import java.io.IOException;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {

    private UserRepository userRepository;

    @Override
    public void init() throws ServletException {
        EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        userRepository = new UserRepository(em);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        User user = User.builder()
                .username(request.getParameter("username"))
                .email(request.getParameter("email"))
                .password(request.getParameter("password"))
                .build();
        try{
            // check if a username exists in the database
            boolean checkIfUserExists = userRepository.existsByUsername(user.getUsername());
            if(checkIfUserExists){
                response.sendRedirect("signup.jsp?error=exists");
                return;
            }
            // than save
            userRepository.save(user);
            response.sendRedirect("signup.jsp?error=success");
        }catch (Exception e){
            e.printStackTrace();
            response.sendRedirect("signup.jsp?error=error");
        }
    }
    @Override
    public void destroy() {
        if (userRepository != null && userRepository.getEm() != null) {
            userRepository.getEm().close();
        }
        JpaUtil.shutdown();
    }
}

