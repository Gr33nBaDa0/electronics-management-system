package main;

import gui.DangNhap_GUI;
import java.security.Security;
import javax.swing.SwingUtilities;

public class Main {
    static {
        try {
            String disabledAlgorithms = Security.getProperty("jdk.tls.disabledAlgorithms");
            if (disabledAlgorithms != null) {
                disabledAlgorithms = disabledAlgorithms
                        .replace("TLSv1.1,", "")
                        .replace("TLSv1,", "")
                        .replace(", TLSv1.1", "")
                        .replace(", TLSv1", "")
                        .replace("TLSv1.1", "")
                        .replace("TLSv1", "");
                Security.setProperty("jdk.tls.disabledAlgorithms", disabledAlgorithms);
            }
        } catch (Exception ignored) {
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DangNhap_GUI().setVisible(true);
        });
    }
}
