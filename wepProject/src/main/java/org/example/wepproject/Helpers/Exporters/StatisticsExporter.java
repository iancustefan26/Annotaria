package org.example.wepproject.Helpers.Exporters;

import org.example.wepproject.Models.StatisticRecord;
import org.example.wepproject.Models.StatisticsExportFormat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class StatisticsExporter {
    public static String exportDir = "export";

    public static void export(StatisticsExportFormat format, List<StatisticRecord> records) {
        String fileName = "/statistics_export." + format.name().toLowerCase();
        String filePath = exportDir + fileName;
        System.out.println("CWD: " + System.getProperty("user.dir"));
        File directory = new File(StatisticsExporter.exportDir);
        try {
            if (!directory.exists()) {
                directory.mkdirs();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
        try (FileWriter writer = new FileWriter(filePath)) {
            switch (format) {
                case CSV:
                    writer.write("Title,Owner Name,Score\n");
                    for (StatisticRecord record : records) {
                        writer.write(String.format("%s,%s,%d\n",
                                record.getTitle(), record.getOwnerName(), record.getScore()));
                    }
                    break;

                case SVG:
                    writer.write("<svg xmlns='http://www.w3.org/2000/svg' width='500' height='"
                            + (records.size() * 20 + 20) + "'>\n");

                    int y = 20;
                    for (StatisticRecord record : records) {
                        writer.write(String.format(
                                "  <text x='10' y='%d'>%s - %s - %d</text>\n",
                                y, record.getTitle(), record.getOwnerName(), record.getScore()));
                        y += 20;
                    }

                    writer.write("</svg>\n");
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported export format: " + format);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to export statistics to " + filePath, e);
        }
        System.out.println("Exported statistics to " + filePath);
    }
}
