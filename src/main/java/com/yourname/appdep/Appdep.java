package com.yourname.appdep;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import com.google.gson.*;

public class Appdep {
    private static final String DATA_FILE = "appdep_sites.json";
    private static JPanel buttonPanel;
    private static java.util.List<SiteEntry> sites = new ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Appdep::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        loadSites();

        JFrame frame = new JFrame("Appdep - Quick Launch Websites");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(630, 400);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(new BorderLayout(10, 5));
        frame.getContentPane().setBackground(Color.WHITE);

        // 輸入區
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        JTextField nameField = new JTextField(10);
        JTextField urlField  = new JTextField(20);
        JButton addButton   = styleButton(new JButton("Add"), 60, 30);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("URL:"));
        inputPanel.add(urlField);
        inputPanel.add(addButton);
        frame.add(inputPanel, BorderLayout.NORTH);

        // 按鈕區：WrapLayout，hgap/vgap = 10px，按鈕大小固定
        buttonPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.getViewport().setBackground(Color.WHITE);
        frame.add(scrollPane, BorderLayout.CENTER);

        // 渲染既有站點
        for (SiteEntry site : sites) {
            buttonPanel.add(createSitePanel(site));
        }

        // 新增邏輯
        addButton.addActionListener((ActionEvent e) -> {
            String name = nameField.getText().trim();
            String url  = urlField.getText().trim();
            if (name.isEmpty() || url.isEmpty()) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please enter both name and URL.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            SiteEntry entry = new SiteEntry(name, url);
            sites.add(entry);
            buttonPanel.add(createSitePanel(entry));
            buttonPanel.revalidate();
            buttonPanel.repaint();
            saveSites();
            nameField.setText("");
            urlField.setText("");
        });

        frame.setVisible(true);
    }

    private static JPanel createSitePanel(SiteEntry site) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        panel.setBackground(Color.WHITE);

        // 打開按鈕：140×40
        JButton openBtn = styleButton(new JButton(site.name), 140, 40);
        openBtn.setFont(openBtn.getFont().deriveFont(12f));
        openBtn.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI(site.url));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    panel,
                    "Cannot open URL: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
        panel.add(openBtn);

        // 刪除按鈕：40×40
        JButton delBtn = styleButton(new JButton("X"), 40, 40);
        delBtn.setFont(delBtn.getFont().deriveFont(Font.BOLD, 18f));
        delBtn.addActionListener(e -> {
            sites.remove(site);
            buttonPanel.remove(panel);
            saveSites();
            buttonPanel.revalidate();
            buttonPanel.repaint();
        });
        panel.add(delBtn);

        return panel;
    }

    private static JButton styleButton(JButton btn, int w, int h) {
        btn.setPreferredSize(new Dimension(w, h));
        btn.setBackground(new Color(200, 200, 200));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        return btn;
    }

    private static void loadSites() {
        try {
            Path path = Paths.get(DATA_FILE);
            if (Files.exists(path)) {
                String json = Files.readString(path);
                SiteEntry[] entries = new Gson().fromJson(json, SiteEntry[].class);
                sites.addAll(Arrays.asList(entries));
            }
        } catch (IOException e) {
            System.err.println("Failed to load sites: " + e.getMessage());
        }
    }

    private static void saveSites() {
        try (Writer writer = new FileWriter(DATA_FILE)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(sites, writer);
        } catch (IOException e) {
            System.err.println("Failed to save sites: " + e.getMessage());
        }
    }

    static class SiteEntry {
        String name, url;
        SiteEntry(String name, String url) {
            this.name = name;
            this.url  = url;
        }
    }
}
