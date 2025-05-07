package org.example.webp.auth.Servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.webp.Helpers.JpaUtil;
import org.example.webp.auth.Models.User;
import org.example.webp.auth.Repositories.UserRepository;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserRepository userRepository;

    @Override
    public void init() throws ServletException {
        EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        userRepository = new UserRepository(em);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            User user = userRepository.findByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
                HttpSession session = request.getSession(true);
                session.setAttribute("username", username);
                System.out.println(user.getUsername());
                response.sendRedirect("welcome.jsp");
            } else {
                response.sendRedirect("login.jsp?error=invalid");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=error");
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
