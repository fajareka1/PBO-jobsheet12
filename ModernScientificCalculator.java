// Modern Scientific Calculator (Final Version – No ScriptEngine, No Bugs)

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;               // <— menggunakan util.List, bukan awt.List
import java.util.List;

public class ModernScientificCalculator extends JFrame implements ActionListener {

    private JTextField display;
    private JTextArea historyArea;
    private double ans = 0;
    private java.util.List<String> history = new java.util.ArrayList<>();

    public ModernScientificCalculator() {
        setTitle("Modern Scientific Calculator");
        setSize(420, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(new Color(245, 247, 249));

        // ================= DISPLAY =================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        display = new JTextField();
        display.setFont(new Font("Segoe UI", Font.BOLD, 28));
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setEditable(false);
        display.setBackground(Color.WHITE);
        display.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 2));

        topPanel.add(display, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // ================= HISTORY =================
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(historyArea);
        scroll.setPreferredSize(new Dimension(150, 0));
        add(scroll, BorderLayout.EAST);

        // ================= BUTTON PANEL =================
        JPanel btnPanel = new JPanel(new GridLayout(7, 4, 8, 8));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] buttons = {
            "sin", "cos", "tan", "log",
            "√", "^", "(", ")",
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "%", "+",
            "C", "CE", "←", "="
        };

        for (String t : buttons) {
            JButton btn = new JButton(t);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
            btn.setFocusPainted(false);

            // COLORS
            if (t.matches("sin|cos|tan|log|√|\\^")) {
                btn.setBackground(new Color(80, 140, 255));
                btn.setForeground(Color.WHITE);
            } else if (t.matches("[+\\-*/%]") || t.equals("=")) {
                btn.setBackground(new Color(255, 153, 0));
                btn.setForeground(Color.WHITE);
            } else if (t.equals("C") || t.equals("CE") || t.equals("←")) {
                btn.setBackground(new Color(255, 71, 87));
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.BLACK);
            }

            btn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
            btn.addActionListener(this);
            btnPanel.add(btn);
        }

        add(btnPanel, BorderLayout.CENTER);
    }

    // ================= ACTIONS =================
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        // Number or decimal
        if (cmd.matches("[0-9]") || cmd.equals(".")) {
            display.setText(display.getText() + cmd);
            return;
        }

        // Scientific functions
        if (cmd.matches("sin|cos|tan|log|√")) {
            try {
                double val = Double.parseDouble(display.getText());
                double result = switch (cmd) {
                    case "sin" -> Math.sin(Math.toRadians(val));
                    case "cos" -> Math.cos(Math.toRadians(val));
                    case "tan" -> Math.tan(Math.toRadians(val));
                    case "log" -> Math.log10(val);
                    case "√" -> Math.sqrt(val);
                    default -> 0;
                };
                addHistory(cmd + "(" + val + ") = " + result);
                display.setText(Double.toString(result));
            } catch (Exception ex) {
                display.setText("Error");
            }
            return;
        }

        // BACKSPACE
        if (cmd.equals("←")) {
            String txt = display.getText();
            if (!txt.isEmpty()) display.setText(txt.substring(0, txt.length() - 1));
            return;
        }

        // CLEAR
        if (cmd.equals("CE")) { display.setText(""); return; }
        if (cmd.equals("C"))  { display.setText(""); history.clear(); historyArea.setText(""); return; }

        // Operators
        if (cmd.matches("[+\\-*/%\\^]")) {
            display.setText(display.getText() + " " + cmd + " ");
            return;
        }

        // Parentheses
        if (cmd.equals("(") || cmd.equals(")")) {
            display.setText(display.getText() + cmd);
            return;
        }

        // EQUALS
        if (cmd.equals("=")) {
            try {
                String exp = display.getText();
                double result = evaluate(exp);

                addHistory(exp + " = " + result);
                display.setText(Double.toString(result));
            } catch (Exception ex) {
                display.setText("Error");
            }
        }
    }

    // ================= EXPRESSION EVALUATOR =================
    private double evaluate(String exp) {
        List<String> tokens = tokenize(exp);
        List<String> rpn = toRPN(tokens);
        return evalRPN(rpn);
    }

    private List<String> tokenize(String exp) {
        List<String> result = new ArrayList<>();
        StringBuilder num = new StringBuilder();

        for (char c : exp.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                num.append(c);
            } else {
                if (!num.isEmpty()) {
                    result.add(num.toString());
                    num.setLength(0);
                }
                if (c != ' ') result.add(String.valueOf(c));
            }
        }
        if (!num.isEmpty()) result.add(num.toString());
        return result;
    }

    private List<String> toRPN(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        Map<String, Integer> prec = Map.of(
            "+", 1, "-", 1,
            "*", 2, "/", 2, "%", 2,
            "^", 3
        );

        for (String t : tokens) {
            if (t.matches("[0-9.]+")) {
                output.add(t);
            } else if (prec.containsKey(t)) {
                while (!stack.isEmpty() && prec.getOrDefault(stack.peek(), 0) >= prec.get(t)) {
                    output.add(stack.pop());
                }
                stack.push(t);
            } else if (t.equals("(")) {
                stack.push(t);
            } else if (t.equals(")")) {
                while (!stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                stack.pop();
            }
        }

        while (!stack.isEmpty()) output.add(stack.pop());
        return output;
    }

    private double evalRPN(List<String> rpn) {
        Stack<Double> stack = new Stack<>();

        for (String t : rpn) {
            if (t.matches("[0-9.]+")) {
                stack.push(Double.parseDouble(t));
            } else {
                double b = stack.pop();
                double a = stack.pop();
                double r = switch (t) {
                    case "+" -> a + b;
                    case "-" -> a - b;
                    case "*" -> a * b;
                    case "/" -> a / b;
                    case "%" -> a % b;
                    case "^" -> Math.pow(a, b);
                    default -> 0;
                };
                stack.push(r);
            }
        }
        return stack.pop();
    }

    // ================= HISTORY =================
    private void addHistory(String entry) {
        history.add(entry);
        historyArea.append(entry + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ModernScientificCalculator().setVisible(true));
    }
}
