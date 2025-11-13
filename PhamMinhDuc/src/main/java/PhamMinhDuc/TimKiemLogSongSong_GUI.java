/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PhamMinhDuc;

/**
 *
 * @author ADMIN
 */
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class TimKiemLogSongSong_GUI extends JFrame {

    private JTextField txtKeyword, txtDirectory;
    private JTextArea txtResult;
    private JLabel lblSummary;
    private JButton btnFind;
    private JFileChooser directoryChooser;

    private static final int THREAD_COUNT = 8;
    private static final int MAX_PARALLEL_FILES = 10;

    public TimKiemLogSongSong_GUI() {
        setTitle("Find in Files");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Giao diện chính
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        JPanel keywordPanel = new JPanel(new BorderLayout(5, 5));
        keywordPanel.add(new JLabel("Find what:"), BorderLayout.WEST);
        txtKeyword = new JTextField();
        keywordPanel.add(txtKeyword, BorderLayout.CENTER);

        JPanel dirPanel = new JPanel(new BorderLayout(5, 5));
        dirPanel.add(new JLabel("Directory:"), BorderLayout.WEST);
        txtDirectory = new JTextField();
        JButton btnBrowse = new JButton("Browse...");
        btnBrowse.addActionListener(e -> chooseDirectory());
        dirPanel.add(txtDirectory, BorderLayout.CENTER);
        dirPanel.add(btnBrowse, BorderLayout.EAST);

        btnFind = new JButton("Find All");
        btnFind.addActionListener(this::startSearch);

        inputPanel.add(keywordPanel);
        inputPanel.add(dirPanel);
        inputPanel.add(btnFind);

        // Kết quả
        txtResult = new JTextArea();
        txtResult.setEditable(false);
        txtResult.setFont(new Font("Consolas", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(txtResult);

        lblSummary = new JLabel("Kết quả sẽ hiển thị tại đây...");

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(lblSummary, BorderLayout.SOUTH);

        add(panel);
    }

    private void chooseDirectory() {
        if (directoryChooser == null) {
            directoryChooser = new JFileChooser();
            directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        if (directoryChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtDirectory.setText(directoryChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void startSearch(ActionEvent e) {
        String keyword = txtKeyword.getText().trim();
        String directory = txtDirectory.getText().trim();

        if (keyword.isEmpty() || directory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa và chọn thư mục!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        txtResult.setText("");
        lblSummary.setText("Đang tìm kiếm...");
        btnFind.setEnabled(false);

        new Thread(() -> runSearch(keyword, directory)).start();
    }

    private void runSearch(String keyword, String directory) {
        long start = System.currentTimeMillis();
        AtomicInteger totalMatches = new AtomicInteger(0);
        AtomicInteger filesWithKey = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        Semaphore semaphore = new Semaphore(MAX_PARALLEL_FILES);

        Path outputFile = Paths.get(directory, "ketqua.txt");

        // Xóa file kết quả cũ
        try {
            Files.deleteIfExists(outputFile);
        } catch (IOException ignored) {}

        try (Stream<Path> paths = Files.list(Paths.get(directory))) {
            paths.filter(p -> p.toString().endsWith(".txt") && !p.getFileName().toString().equals("ketqua.txt"))
                 .forEach(path -> {
                executor.submit(() -> {
                    try {
                        semaphore.acquire();
                        StringBuilder sb = new StringBuilder();
                        boolean foundInFile = false;
                        int lineNumber = 0;

                        try (BufferedReader reader = Files.newBufferedReader(path)) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                lineNumber++;
                                if (line.toLowerCase().contains(keyword.toLowerCase())) {
                                    sb.append(path.getFileName())
                                            .append(" - Dòng ")
                                            .append(lineNumber)
                                            .append(": ")
                                            .append(line)
                                            .append(System.lineSeparator());
                                    totalMatches.incrementAndGet();
                                    foundInFile = true;
                                }
                            }
                        }

                        if (sb.length() > 0) {
                            synchronized (txtResult) {
                                txtResult.append(sb.toString());
                            }
                            // Ghi ra file ketqua.txt
                            synchronized (outputFile) {
                                try (BufferedWriter writer = Files.newBufferedWriter(outputFile,
                                        StandardOpenOption.CREATE,
                                        StandardOpenOption.APPEND)) {
                                    writer.write(sb.toString());
                                }
                            }
                        }

                        if (foundInFile) {
                            filesWithKey.incrementAndGet();
                        }

                    } catch (Exception ex) {
                        synchronized (txtResult) {
                            txtResult.append("Lỗi khi đọc file: " + path.getFileName() + "\n");
                        }
                    } finally {
                        semaphore.release();
                    }
                });
            });
        } catch (IOException ex) {
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Lỗi khi duyệt thư mục!", "Lỗi", JOptionPane.ERROR_MESSAGE)
            );
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException ignored) {}

        long end = System.currentTimeMillis();
        long duration = (end - start);

        SwingUtilities.invokeLater(() -> {
            lblSummary.setText(String.format(
                    "Đã tìm thấy %d dòng trong %d file. Thời gian: %.2f giây. (Kết quả lưu tại: %s)",
                    totalMatches.get(), filesWithKey.get(), duration / 1000.0, outputFile.toString()
            ));
            btnFind.setEnabled(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TimKiemLogSongSong_GUI().setVisible(true));
    }
}
