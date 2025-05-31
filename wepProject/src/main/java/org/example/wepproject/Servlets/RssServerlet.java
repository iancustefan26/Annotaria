package org.example.wepproject.Servlets;

import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.SyndFeedOutput;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@WebServlet("/rss")
public class RssServerlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/rss+xml");
        response.setCharacterEncoding("UTF-8");

        try {
            SyndFeed feed = new SyndFeedImpl();
            feed.setFeedType("rss_2.0");
            feed.setTitle("Annatoria RSS Feed");
            feed.setLink("https://annatoria.com");
            feed.setDescription("RSS Feed Latest News");
            feed.setPublishedDate(new Date());

            List<SyndEntry> entries = new ArrayList<>();

            // Example item
            SyndEntry entry = new SyndEntryImpl();
            entry.setTitle("First Blog Post");
            entry.setLink("https://yourwebsite.com/blog/first-post");
            entry.setPublishedDate(new Date());

            SyndContent description = new SyndContentImpl();
            description.setType("text/plain");
            description.setValue("This is the summary of the first blog post.");
            entry.setDescription(description);

            entries.add(entry);
            feed.setEntries(entries);

            // Output to HTTP response
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed, new OutputStreamWriter(response.getOutputStream()));

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to generate RSS");
        }
    }
}
