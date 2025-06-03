package org.example.wepproject.Servlets;


import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.wepproject.Helpers.Exporters.StatisticsExporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/statistics")
public class StatisticsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String formatParam = request.getParameter("format");
        if (formatParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing 'format' parameter.");
            return;
        }

        String fileName;
        String contentType;

        switch (formatParam.toLowerCase()) {
            case "csv":
                fileName = "statistics_export.csv";
                contentType = "text/csv";
                break;
            case "svg":
                fileName = "statistics_export.svg";
                contentType = "image/svg+xml";
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid format: " + formatParam);
                return;
        }

        File file = new File(StatisticsExporter.exportDir + "/" + fileName);
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found: " + fileName);
            return;
        }

        // Set headers to trigger download
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentLengthLong(file.length());

        // Stream the file to the response
        try (FileInputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}
