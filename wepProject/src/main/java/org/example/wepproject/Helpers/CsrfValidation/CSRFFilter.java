package org.example.wepproject.Helpers.CsrfValidation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.wepproject.DTOs.ApiDTO;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@WebFilter(filterName = "CSRFFilter", urlPatterns = {
        "/login", "/signup"
})
public class CSRFFilter implements Filter {

    private ObjectMapper objectMapper;

    private static final Set<String> PROTECTED_METHODS = new HashSet<>(
            Arrays.asList("POST", "PUT", "DELETE", "PATCH")
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        objectMapper = new ObjectMapper();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String method = httpRequest.getMethod();
        String requestURI = httpRequest.getRequestURI();

        if (!PROTECTED_METHODS.contains(method)) {
            chain.doFilter(request, response);
            return;
        }

        System.out.println("CSRF Filter: Validating " + method + " " + requestURI);

        String contentType = httpRequest.getContentType();

        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(httpRequest);

            if (!validateCSRFToken(cachedRequest, httpResponse)) {
                return;
            }

            chain.doFilter(cachedRequest, response);
        } else {
            if (!validateCSRFToken(httpRequest, httpResponse)) {
                return;
            }
            chain.doFilter(request, response);
        }
    }

    private boolean validateCSRFToken(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("csrfToken") == null) {
            System.out.println("CSRF Filter: No session or CSRF token in session");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            objectMapper.writeValue(response.getWriter(),
                    new ApiDTO("error", "CSRF token missing in session"));
            return false;
        }

        String sessionToken = (String) session.getAttribute("csrfToken");
        String requestToken = extractCSRFToken(request);

        System.out.println("CSRF Filter: Session token: " + sessionToken);
        System.out.println("CSRF Filter: Request token: " + requestToken);

        // Validate token
        if (requestToken == null || !sessionToken.equals(requestToken)) {
            System.out.println("CSRF Filter: Invalid CSRF token");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            objectMapper.writeValue(response.getWriter(),
                    new ApiDTO("error", "Invalid CSRF token"));
            return false;
        }

        System.out.println("CSRF Filter: CSRF token validation successful");
        return true;
    }

    private String extractCSRFToken(HttpServletRequest request) throws IOException {
        String contentType = request.getContentType();

        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            return extractTokenFromJSON(request);
        }

        return request.getParameter("csrfToken");
    }

    private String extractTokenFromJSON(HttpServletRequest request) throws IOException {
        try {
            String jsonBody;
            // check if chached
            if (request instanceof CachedBodyHttpServletRequest) {
                jsonBody = ((CachedBodyHttpServletRequest) request).getCachedBody();
            } else {
                // internal server error
                throw new IllegalStateException("Expected CachedBodyHttpServletRequest for JSON content");
            }

            if (jsonBody == null || jsonBody.trim().isEmpty()) {
                return null;
            }


            // extract csrf from json
            JsonNode jsonNode = objectMapper.readTree(jsonBody);
            JsonNode csrfNode = jsonNode.get("csrfToken");

            return csrfNode != null ? csrfNode.asText() : null;

        } catch (Exception e) {
            System.err.println("Error extracting CSRF token from JSON: " + e.getMessage());
            return null;
        }
    }

}

