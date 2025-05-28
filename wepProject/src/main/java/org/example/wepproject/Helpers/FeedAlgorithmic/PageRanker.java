package org.example.wepproject.Helpers.FeedAlgorithmic;

import java.util.ArrayList;
import java.util.List;


/*
* PageRank Formula: PR[i] = (1 - d) / n + d * sum( PR[j] / links(j) );
*  ---- d = damping factor
*  ---- j -> has link to i
* */

public class PageRanker {
    private static double d = 0.85; // damping factor
    private static int iterations = 50; // max iterations of PageRank algorithm
    private List<List<Integer>> graph;
    private List<Integer> postIds;
    private double[] PR;
    private int n;


    public PageRanker(float[][] matrix) {
        if (matrix == null)
            throw new IllegalArgumentException("Matrix passed to PageRanker constructor cannot be null");
        graph = new ArrayList<>();
        n = matrix.length - 1;

        // build adjacency list graph
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }
        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                if (matrix[i][j] == 1.0) {
                    graph.get(i).add(j);
                    graph.get(j).add(i);
                }
            }
        }

        // Retrieve post ids from the last row
        postIds = new ArrayList<>(n);
        for (int i = 0; i < n; i++){
            postIds.add((int) matrix[n][i]);
        }

        // Extract influence scores from the main diagonal
        PR = new double[n];
        double sumInfluence = 0.0;
        for (int i = 0; i < n; i++) {
            PR[i] = matrix[i][i];
            sumInfluence += PR[i];
        }

        // Initialize scores to the normalized influence (personalization)
        for (int i = 0; i < n; i++) {
            PR[i] = PR[i] / sumInfluence;
        }

    }

    private void iterate(int n, double[] PR) {
        // PageRank iterations
        for (int iter = 0; iter < iterations; iter++) {
            for (int i = 0; i < n; i++) {
                double incomingSum = 0.0;
                for (int j : graph.get(i)) {
                    incomingSum += PR[j] / graph.get(j).size();
                }
                PR[i] = (1 - d) / n + d * incomingSum;
            }
        }
    }

    public int[] runAndGetRankedPostIds() {

        iterate(n, PR);

        // Pair up post IDs with their final scores, then sort by score descending
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            order.add(i);
        }
        order.sort((a, b) -> Double.compare(PR[b], PR[a]));

        // Build the ranked array of post IDs
        int[] rankedPosts = new int[n];
        for (int i = 0; i < n; i++) {
            rankedPosts[i] = postIds.get(order.get(i));
        }

        return rankedPosts;
    }
}
