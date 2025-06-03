package org.example.wepproject.Helpers.Exporters;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import org.example.wepproject.Models.RssRecord;

public class RssExporter {
    public static Integer latestPosts = 20;
    public static Integer latestComments = 20;
    public static String exportDir = "export";
    public static String exportPath = "news.rss";

    public static void exportRss(List<RssRecord> records) {
        System.out.println("Exporting RSS to: " + exportDir + "/" + exportPath);
        
        File directory = new File(RssExporter.exportDir);
        try {
            if (!directory.exists()) {
                directory.mkdirs();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(exportDir + "/" + exportPath))) {
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            writer.println("<rss version=\"2.0\">");
            writer.println("  <channel>");
            writer.println("    <title>Latest News Feed</title>");
            writer.println("    <link>https://annatoria.com</link>");
            writer.println("    <description>Latest posts and comments</description>");
            writer.println("    <language>en-us</language>");
            writer.println("    <lastBuildDate>" + new java.util.Date() + "</lastBuildDate>");

            for (RssRecord record : records) {
                writer.println("    <item>");
                writer.println("      <title>" + escapeXml(record.getTitle()) + "</title>");
                writer.println("      <link>" + escapeXml(record.getLink()) + "</link>");
                writer.println("      <description>" + escapeXml(record.getDescription()) + "</description>");
                writer.println("      <pubDate>" + record.getPubDate() + "</pubDate>");
                writer.println("      <guid>" + record.getGuid() + "</guid>");
                writer.println("    </item>");
            }

            writer.println("  </channel>");
            writer.println("</rss>");

            System.out.println("RSS exported successfully.");
        } catch (Exception e) {
            System.err.println("Failed to export RSS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Minimal XML escaping for safety
    private static String escapeXml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
