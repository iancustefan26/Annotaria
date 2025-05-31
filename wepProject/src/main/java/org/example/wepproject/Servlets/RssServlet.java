package org.example.wepproject.Servlets;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.wepproject.Helpers.Exporters.RssExporter;

import java.io.*;


@WebServlet("/rss")
public class RssServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        File rssFile = new File(RssExporter.exportPath);

        if (!rssFile.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "RSS feed not available.");
            return;
        }

        response.setContentType("application/rss+xml");
        response.setCharacterEncoding("UTF-8");

        try (BufferedReader reader = new BufferedReader(new FileReader(rssFile));
             PrintWriter writer = response.getWriter()) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.sendError(500, "Failed to serve RSS feed.");
        }
    }
}
