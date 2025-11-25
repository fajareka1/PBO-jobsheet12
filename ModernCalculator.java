import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ModernCalculator extends JFrame implements ActionListener {

    private JTextField input;
    private double num1 = 0, num2 = 0, result = 0;
    private String operator = "";
    private boolean operatorPressed = false;

    public ModernCalculator() {

        setTitle("Calculator");
        setSize(360, 520);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 242, 245));

        input = new JTextField();
        input.setFont(new Font("Segoe UI", Font.BOLD, 30));
        input.setHorizontalAlignment(SwingConstants.RIGHT);
        input.setEditable(false);
        input.setBackground(Color.WHITE);
        input.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(input, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 4, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(240, 242, 245));

        String[] buttons = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "C", "CE", "←", "%"
        };

        for (String text : buttons) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 22));
            btn.setFocusPainted(false);

            if (text.matches("[+\\-*/%=]")) {
                btn.setBackground(new Color(255, 153, 51));
                btn.setForeground(Color.WHITE);
            } else if (text.equals("C") || text.equals("CE") || text.equals("←")) {
                btn.setBackground(new Color(255, 71, 87));
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.BLACK);
            }

            btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 2));
            btn.addActionListener(this);
            panel.add(btn);
        }

        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        // Tekan angka
        if (cmd.matches("[0-9]") || cmd.equals(".")) {

            if (operatorPressed) {
                input.setText(input.getText() + " "); // tambah spasi
                operatorPressed = false;
            }

            input.setText(input.getText() + cmd);
        }

        // Tekan operator
        else if (cmd.matches("[+\\-*/%]")) {
            if (!input.getText().isEmpty()) {
                num1 = Double.parseDouble(input.getText().split(" ")[0]); // ambil angka pertama
            }

            operator = cmd;

            // Tampilkan operator di layar
            input.setText(num1 + " " + operator);
            operatorPressed = true;
        }

        else if (cmd.equals("CE")) {
            input.setText("");
        }

        else if (cmd.equals("C")) {
            input.setText("");
            num1 = num2 = result = 0;
            operator = "";
            operatorPressed = false;
        }

        else if (cmd.equals("←")) {
            String curr = input.getText();
            if (!curr.isEmpty()) {
                input.setText(curr.substring(0, curr.length() - 1));
            }
        }

        else if (cmd.equals("=")) {

            if (!input.getText().contains(" ")) return;

            String[] parts = input.getText().split(" ");

            if (parts.length < 3) return;

            num1 = Double.parseDouble(parts[0]);
            num2 = Double.parseDouble(parts[2]);

            switch (operator) {
                case "+": result = num1 + num2; break;
                case "-": result = num1 - num2; break;
                case "*": result = num1 * num2; break;
                case "/":
                    if (num2 == 0) {
                        JOptionPane.showMessageDialog(this, "Tidak bisa membagi dengan nol!");
                        return;
                    }
                    result = num1 / num2;
                    break;
                case "%": result = num1 % num2; break;
            }

            input.setText(String.valueOf(result));
            operator = "";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ModernCalculator().setVisible(true));
    }
}
