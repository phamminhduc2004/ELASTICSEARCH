/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PhamMinhDuc;

/**
 * Tìm kiếm song song trong 3000 file log
 * Từ khóa: "login by 99"
 * Ghi kết quả ra file ketqua.txt
 */

/**
 * Chương trình tìm kiếm song song trong các file log
 * Từ khóa: "login by 99"
 * Ghi kết quả ra file ketqua.txt
 */

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.*;

public class TimKiemLogSongSong {

    // --- Thư mục chứa log ---
    private static final String LOG_DIR = "C:\\Users\\ADMIN\\Desktop\\PhamMinhDuc\\PhamMinhDuc\\log";
    private static final String OUTPUT_FILE = LOG_DIR + "\\ketqua.txt";
    private static final String KEYWORD = "login by 99";
    private static final int THREAD_COUNT = 8;
    private static final int MAX_PARALLEL_FILES = 10;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        Semaphore semaphore = new Semaphore(MAX_PARALLEL_FILES);
        AtomicInteger totalMatches = new AtomicInteger(0);

        // Xóa file kết quả cũ nếu có
        try {
            Files.deleteIfExists(Paths.get(OUTPUT_FILE));
        } catch (IOException e) {
            System.err.println("Khong the xoa file cu: " + e.getMessage());
        }

        try (Stream<Path> paths = Files.list(Paths.get(LOG_DIR))) {
            paths
                .filter(p -> p.getFileName().toString().startsWith("log_") && p.toString().endsWith(".txt"))
                .forEach(path -> executor.submit(() -> {
                    try {
                        semaphore.acquire();

                        // Tìm kiếm trong file
                        StringBuilder sb = searchInFile(path, totalMatches);
                        if (sb.length() > 0) {
                            // synchronized để tránh lỗi ghi đồng thời
                            synchronized (TimKiemLogSongSong.class) {
                                try (BufferedWriter writer = Files.newBufferedWriter(
                                        Paths.get(OUTPUT_FILE),
                                        StandardOpenOption.CREATE,
                                        StandardOpenOption.APPEND)) {
                                    writer.write(sb.toString());
                                }
                            }
                        }

                    } catch (InterruptedException | IOException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("Loi khi xu ly file " + path.getFileName() + ": " + e.getMessage());
                    } finally {
                        semaphore.release();
                    }
                }));
        } catch (IOException e) {
            System.err.println("Loi khi duyet thu muc log: " + e.getMessage());
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        long end = System.currentTimeMillis();
        System.out.println("Hoan thanh trong " + (end - start) / 1000 + " giay.");
        System.out.println("Ket qua duoc luu tai: " + OUTPUT_FILE);
        System.out.println("Tong so dong chua tu khoa: " + totalMatches.get());
    }

    // Hàm tìm kiếm trong từng file log
    private static StringBuilder searchInFile(Path path, AtomicInteger totalMatches) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.toLowerCase().contains(KEYWORD.toLowerCase())) {
                    sb.append(path.getFileName())
                      .append(" - Dong ")
                      .append(lineNumber)
                      .append(": ")
                      .append(line)
                      .append(System.lineSeparator());
                    totalMatches.incrementAndGet();
                }
            }
        }
        return sb;
    }
}