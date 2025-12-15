import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*; 
import java.util.List;

public class ModernScientificCalculator extends JFrame implements ActionListener, KeyListener {

    private JTextField display;
    private JLabel previewLabel;
    private JTextArea historyArea;
    private double ans = 0;
    private java.util.List<String> history = new java.util.ArrayList<>();
    private boolean startNewNumber = false;
    private String pendingFunction = null;

    public ModernScientificCalculator() {
        setTitle("Scientific Calculator Pro");
        setSize(500, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(30, 30, 35));

        // ================= DISPLAY PANEL =================
        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BorderLayout(5, 5));
        displayPanel.setBackground(new Color(30, 30, 35));
        displayPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        // Preview/Expression Label
        previewLabel = new JLabel(" ");
        previewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        previewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        previewLabel.setForeground(new Color(150, 150, 160));
        displayPanel.add(previewLabel, BorderLayout.NORTH);

        // Main Display
        display = new JTextField("0");
        display.setFont(new Font("Segoe UI", Font.BOLD, 36));
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setEditable(false);
        display.setBackground(new Color(40, 40, 45));
        display.setForeground(Color.WHITE);
        display.setCaretColor(Color.WHITE);
        display.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 70), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        displayPanel.add(display, BorderLayout.CENTER);
        add(displayPanel, BorderLayout.NORTH);

        // ================= HISTORY PANEL =================
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(new Color(35, 35, 40));
        historyPanel.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, new Color(60, 60, 70)));
        
        JLabel historyTitle = new JLabel(" History ");
        historyTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        historyTitle.setForeground(new Color(150, 150, 160));
        historyTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        historyPanel.add(historyTitle, BorderLayout.NORTH);

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        historyArea.setBackground(new Color(35, 35, 40));
        historyArea.setForeground(new Color(200, 200, 210));
        historyArea.setMargin(new Insets(5, 10, 5, 10));

        JScrollPane scroll = new JScrollPane(historyArea);
        scroll.setPreferredSize(new Dimension(180, 0));
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setBackground(new Color(35, 35, 40));
        historyPanel.add(scroll, BorderLayout.CENTER);
        
        add(historyPanel, BorderLayout.EAST);

        // ================= BUTTON PANEL =================
        JPanel btnPanel = new JPanel(new GridLayout(7, 4, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        btnPanel.setBackground(new Color(30, 30, 35));

        String[] buttons = {
                "sin", "cos", "tan", "ln",
                "√", "^", "(", ")",
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "ANS", "+",
                "C", "CE", "←", "="
        };

        for (String t : buttons) {
            JButton btn = createStyledButton(t);
            btn.addActionListener(this);
            btnPanel.add(btn);
        }

        add(btnPanel, BorderLayout.CENTER);

        // Keyboard Support
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
        display.addKeyListener(this);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(true);
        
        // Color Scheme
        if (text.matches("sin|cos|tan|ln|√|\\^")) {
            // Scientific functions - Blue gradient
            btn.setBackground(new Color(52, 152, 219));
            btn.setForeground(Color.WHITE);
        } else if (text.matches("[+\\-*/]")) {
            // Operators - Orange
            btn.setBackground(new Color(230, 126, 34));
            btn.setForeground(Color.WHITE);
        } else if (text.equals("=")) {
            // Equals - Green
            btn.setBackground(new Color(46, 204, 113));
            btn.setForeground(Color.WHITE);
        } else if (text.equals("C") || text.equals("CE") || text.equals("←")) {
            // Clear buttons - Red
            btn.setBackground(new Color(231, 76, 60));
            btn.setForeground(Color.WHITE);
        } else if (text.equals("ANS")) {
            // ANS button - Purple
            btn.setBackground(new Color(155, 89, 182));
            btn.setForeground(Color.WHITE);
        } else if (text.equals("(") || text.equals(")")) {
            // Parentheses - Dark gray
            btn.setBackground(new Color(70, 70, 80));
            btn.setForeground(Color.WHITE);
        } else {
            // Numbers and decimal - Light gray
            btn.setBackground(new Color(60, 60, 70));
            btn.setForeground(Color.WHITE);
        }

        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 90), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            Color originalColor = btn.getBackground();
            
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(brighten(originalColor));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(originalColor);
            }
        });

        return btn;
    }

    private Color brighten(Color color) {
        int r = Math.min(255, color.getRed() + 30);
        int g = Math.min(255, color.getGreen() + 30);
        int b = Math.min(255, color.getBlue() + 30);
        return new Color(r, g, b);
    }

    private void handleCommand(String cmd) {
        String currentText = display.getText();
        
        // Clear "0" when starting new input
        if (currentText.equals("0") && cmd.matches("[0-9]")) {
            display.setText("");
            currentText = "";
        }

        // Number or decimal
        if (cmd.matches("[0-9]") || cmd.equals(".")) {
            if (startNewNumber) {
                display.setText("");
                currentText = "";
                startNewNumber = false;
                previewLabel.setText(" ");
                pendingFunction = null;
            }
            
            // Prevent multiple decimals in current number
            if (cmd.equals(".")) {
                String[] parts = currentText.split("[+\\-*/%^() ]");
                if (parts.length > 0) {
                    String lastPart = parts[parts.length - 1];
                    if (lastPart.contains(".")) {
                        requestFocusInWindow();
                        return;
                    }
                }
            }
            
            display.setText(currentText + cmd);
            requestFocusInWindow();
            return;
        }

        // ANS button
        if (cmd.equals("ANS")) {
            if (startNewNumber) {
                display.setText(Double.toString(ans));
                startNewNumber = false;
            } else {
                display.setText(currentText + ans);
            }
            requestFocusInWindow();
            return;
        }

        // Scientific functions - improved logic
        if (cmd.matches("sin|cos|tan|ln|√")) {
            // Set pending function and wait for input
            pendingFunction = cmd;
            previewLabel.setText(cmd + "(");
            
            if (startNewNumber || currentText.equals("0")) {
                display.setText("");
                startNewNumber = false;
            }
            
            requestFocusInWindow();
            return;
        }

        // BACKSPACE
        if (cmd.equals("←")) {
            if (!currentText.isEmpty() && !currentText.equals("0")) {
                String newText = currentText.substring(0, currentText.length() - 1);
                display.setText(newText.isEmpty() ? "0" : newText);
                startNewNumber = false;
            }
            requestFocusInWindow();
            return;
        }

        // CLEAR
        if (cmd.equals("CE")) {
            display.setText("0");
            previewLabel.setText(" ");
            pendingFunction = null;
            startNewNumber = false;
            requestFocusInWindow();
            return;
        }
        if (cmd.equals("C")) {
            display.setText("0");
            previewLabel.setText(" ");
            history.clear();
            historyArea.setText("");
            ans = 0;
            pendingFunction = null;
            startNewNumber = false;
            requestFocusInWindow();
            return;
        }

        // Operators
        if (cmd.matches("[+\\-*/%\\^]")) {
            // If just finished a calculation, use the result
            if (startNewNumber) {
                currentText = display.getText();
                startNewNumber = false;
            }
            
            if (currentText.isEmpty() || currentText.equals("0")) {
                display.setText(Double.toString(ans));
                currentText = Double.toString(ans);
            }
            
            if (!currentText.isEmpty() && !currentText.endsWith(" ")) {
                display.setText(currentText + " " + cmd + " ");
                startNewNumber = false;
                previewLabel.setText(" ");
                pendingFunction = null;
            }
            requestFocusInWindow();
            return;
        }

        // Parentheses
        if (cmd.equals("(") || cmd.equals(")")) {
            if (startNewNumber && cmd.equals("(")) {
                display.setText("");
                startNewNumber = false;
            }
            display.setText(currentText + cmd);
            requestFocusInWindow();
            return;
        }

        // EQUALS
        if (cmd.equals("=")) {
            try {
                // Check if there's a pending scientific function
                if (pendingFunction != null) {
                    String exp = currentText.trim();
                    if (exp.isEmpty() || exp.equals("0")) {
                        exp = "0";
                    }
                    
                    double val = Double.parseDouble(exp);
                    double result = applyScientificFunction(pendingFunction, val);
                    
                    String functionExpr = pendingFunction + "(" + val + ")";
                    addHistory(functionExpr + " = " + result);
                    
                    ans = result;
                    display.setText(formatResult(result));
                    previewLabel.setText(functionExpr);
                    pendingFunction = null;
                    startNewNumber = true;
                } else {
                    String exp = currentText.trim();
                    if (exp.isEmpty() || exp.equals("0")) {
                        requestFocusInWindow();
                        return;
                    }
                    
                    double result = evaluate(exp);
                    
                    addHistory(exp + " = " + result);
                    ans = result;
                    previewLabel.setText(exp);
                    display.setText(formatResult(result));
                    startNewNumber = true;
                }
            } catch (NumberFormatException ex) {
                display.setText("Error: Invalid Number");
                previewLabel.setText(" ");
            } catch (ArithmeticException ex) {
                display.setText("Error: Math Error");
                previewLabel.setText(ex.getMessage());
            } catch (Exception ex) {
                display.setText("Error: Invalid Expression");
                previewLabel.setText(" ");
            }
            requestFocusInWindow();
        }
    }

    private double applyScientificFunction(String func, double val) {
        return switch (func) {
            case "sin" -> Math.sin(Math.toRadians(val));
            case "cos" -> Math.cos(Math.toRadians(val));
            case "tan" -> Math.tan(Math.toRadians(val));
            case "ln" -> {
                if (val <= 0) throw new ArithmeticException("ln of non-positive number");
                yield Math.log(val);
            }
            case "√" -> {
                if (val < 0) throw new ArithmeticException("√ of negative number");
                yield Math.sqrt(val);
            }
            default -> 0;
        };
    }

    private String formatResult(double result) {
        // Format to remove unnecessary decimals
        if (result == (long) result) {
            return String.format("%d", (long) result);
        } else {
            String formatted = String.format("%.10f", result);
            formatted = formatted.replaceAll("0*$", "").replaceAll("\\.$", "");
            return formatted;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        handleCommand(cmd);
    }

    private double evaluate(String exp) {
        List<String> tokens = tokenize(exp);
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Empty expression");
        }
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
                if (c != ' ')
                    result.add(String.valueOf(c));
            }
        }
        if (!num.isEmpty())
            result.add(num.toString());
        return result;
    }

    private List<String> toRPN(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        Map<String, Integer> prec = Map.of(
                "+", 1, "-", 1,
                "*", 2, "/", 2, "%", 2,
                "^", 3);

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
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            }
        }

        while (!stack.isEmpty())
            output.add(stack.pop());
        return output;
    }

    private double evalRPN(List<String> rpn) {
        Stack<Double> stack = new Stack<>();

        for (String t : rpn) {
            if (t.matches("[0-9.]+")) {
                stack.push(Double.parseDouble(t));
            } else {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Invalid expression");
                }
                double b = stack.pop();
                double a = stack.pop();
                double r = switch (t) {
                    case "+" -> a + b;
                    case "-" -> a - b;
                    case "*" -> a * b;
                    case "/" -> {
                        if (b == 0) throw new ArithmeticException("Division by zero");
                        yield a / b;
                    }
                    case "%" -> a % b;
                    case "^" -> Math.pow(a, b);
                    default -> 0;
                };
                stack.push(r);
            }
        }
        
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid expression");
        }
        return stack.pop();
    }

    private void addHistory(String entry) {
        history.add(entry);
        historyArea.append(entry + "\n");
        historyArea.setCaretPosition(historyArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ModernScientificCalculator calc = new ModernScientificCalculator();
            calc.setVisible(true);
        });
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();

        if (Character.isDigit(c) || c == '.') {
            handleCommand(String.valueOf(c));
        } else if ("+-*/%^()".indexOf(c) >= 0) {
            handleCommand(String.valueOf(c));
        } else if (c == '=') {
            handleCommand("=");
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_ENTER) {
            handleCommand("=");
        } else if (code == KeyEvent.VK_BACK_SPACE) {
            handleCommand("←");
        } else if (code == KeyEvent.VK_DELETE) {
            handleCommand("CE");
        } else if (code == KeyEvent.VK_ESCAPE) {
            handleCommand("C");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}