package discretemaths.ui;

import java.util.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import javax.swing.event.DocumentEvent;

import discretemaths.Main;
import discretemaths.Proof;
import discretemaths.forms.Form;
import discretemaths.rules.InvalidRuleException;
import discretemaths.ui.parser.FormParser;
import discretemaths.ui.rules.*;

import javax.swing.text.AbstractDocument;

public class Line extends JPanel {
    private enum Reason {
        HYP("Hyp", Hyp.class), SUB_HYP("Sub-Hyp", SubHyp.class),
        ANDE1("^E1", AndE1.class), ANDE2("^E2", AndE2.class), ANDI("^I", AndI.class),
        BIIMPE1("<=>E1", BiimpliesE1.class), BIIMPE2("<=>E2", BiimpliesE2.class), BIIMPI("<=>I", BiimpliesI.class),
        COPY("Copy", Copy.class),
        IMPE("=>E", ImpliesE.class), IMPI("=>I", ImpliesI.class),
        ORE("vE", OrE.class), ORI1("vI1", OrI1.class), ORI2("vI2", OrI2.class);

        private String name;
        private Class<? extends Rule> rule;

        private static HashMap<String, Reason> reasonMap = null;

        private <R extends Rule> Reason(String name, Class<R> rule) {
            this.name = name;
            this.rule = rule;
        }

        public String getName() {
            return name;
        }

        public Class<? extends Rule> getRule() {
            return rule;
        }

        public static Reason getReason(String name) {
            if (reasonMap == null)
                initReasonMap();

            return reasonMap.get(name);
        }

        private static void initReasonMap() {
            reasonMap = new HashMap<>();
            for (Reason r : Reason.values()) {
                reasonMap.put(r.getName(), r);
            }
        }
    }

    private static String[] reasons = null;

    private JTextField line;
    private JComboBox<String> reasonBox;
    private JTextField argsField;
    private int lineNum;

    private boolean hasFocus = false;

    private boolean hasLineFocus = false;
    private boolean hasParamsFocus = false;
    private boolean hasReasonsFocus = false;

    Form form = null;

    String lineErrorMsg = null;
    String argsErrorMsg = null;

    private int depth = 0;

    private LineFocusListener lineFocusListener;
    private DepthChangeListener depthListener;

    public Line(JPanel panel, int lineNum, int width, int height, int targetDepth, LineFocusListener lineFocusListener,
            DepthChangeListener depthListener) {
        if (reasons == null)
            initReasons();

        this.lineFocusListener = lineFocusListener;
        this.depthListener = depthListener;
        this.lineNum = lineNum;
        JLabel lineNumLabel = new JLabel(Integer.toString(lineNum) + ". ");
        lineNumLabel
                .setPreferredSize(new Dimension(lineNumLabel.getFont().getSize() * 2, Main.getInstance().LINE_HEIGHT));

        line = new JTextField();
        line.setPreferredSize(new Dimension(300, Main.getInstance().LINE_HEIGHT));

        argsField = new JTextField();
        argsField.setPreferredSize(new Dimension(50, Main.getInstance().LINE_HEIGHT));

        reasonBox = new JComboBox<>(reasons);

        SpringLayout layout = new SpringLayout();
        setLayout(layout);
        add(lineNumLabel);
        add(line);
        add(argsField);
        add(reasonBox);
        layout.putConstraint(SpringLayout.WEST, lineNumLabel,
                5,
                SpringLayout.WEST, panel);

        layout.putConstraint(SpringLayout.WEST, line,
                5,
                SpringLayout.EAST, lineNumLabel);

        layout.putConstraint(SpringLayout.WEST, argsField,
                5,
                SpringLayout.EAST, line);

        layout.putConstraint(SpringLayout.WEST, reasonBox,
                5,
                SpringLayout.EAST, argsField);

        Line thisLine = this;
        line.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                Main.getInstance().keyPressed(lineNum, e);
            }
        });

        line.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                hasLineFocus = true;
                updateFocus();
            }

            @Override
            public void focusLost(FocusEvent e) {
                hasLineFocus = false;
                updateFocus();
                updateForm();
            }
        });

        argsField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                hasParamsFocus = true;
                updateFocus();
            }

            @Override
            public void focusLost(FocusEvent e) {
                hasParamsFocus = false;
                updateFocus();
            }
        });

        reasonBox.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                hasReasonsFocus = true;
                updateFocus();
            }

            @Override
            public void focusLost(FocusEvent e) {
                hasReasonsFocus = false;
                updateFocus();
            }
        });

        ((AbstractDocument) line.getDocument()).setDocumentFilter(new DepthDocumentFilter(targetDepth));

        line.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateFieldState(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateFieldState(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateFieldState(e);
            }

            protected void updateFieldState(DocumentEvent e) {
                updateDepth();
            }
        });
    }

    public void select() {
        line.requestFocusInWindow();
        hasFocus = true;
    }

    public int getLineNum() {
        return lineNum;
    }

    public String getText() {
        return line.getText();
    }

    public String getArgs() {
        return argsField.getText();
    }

    public String getReason() {
        return reasonBox.getSelectedItem().toString();
    }

    public void setText(String text) {
        line.setText(text);
    }

    public void setArgs(String args) {
        argsField.setText(args);
    }

    public void setReason(String reason) {
        reasonBox.setSelectedItem(reason);
    }

    private void initReasons() {
        reasons = new String[Reason.values().length];
        for (int i = 0; i < reasons.length; i++) {
            reasons[i] = Reason.values()[i].getName();
        }
    }

    private void updateFocus() {
        boolean pastFocus = hasFocus;
        hasFocus = hasLineFocus | hasParamsFocus | hasReasonsFocus;

        if (pastFocus != hasFocus)
            lineFocusListener.changedFocus(hasFocus, this);
    }

    private Form parseForm() throws InvalidStatementException {
        return FormParser.getForm(line.getText());
    }

    public Form getForm() {
        return form;
    }

    public void updateDepth() {
        String text = line.getText().replaceAll("[ \t\n]", "");
        int pastDepth = depth;
        depth = 0;
        for (char c : text.toCharArray()) {
            if (c == '|')
                depth++;
            else
                break;
        }

        if (depth != pastDepth)
            depthListener.onDepthChanged(depth, this);
    }

    public void updateForm() {
        lineErrorMsg = null;
        argsErrorMsg = null;

        if (line.getText().replaceAll("[ \t\n|]", "").isEmpty()) {
            form = null;
            lineErrorMsg = "Line empty";
        }

        try {
            form = parseForm();
            lineErrorMsg = null;
        } catch (InvalidStatementException ex) {
            lineErrorMsg = ex.getMessage();
            form = null;
        }

        updateErrorBackground();
    }

    public boolean errorParsingForm() {
        return lineErrorMsg != null || argsErrorMsg != null;
    }

    public String getLineError() {
        return lineErrorMsg;
    }

    public String getArgsError() {
        return argsErrorMsg;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        String text = line.getText().replaceAll("[|]", "");
        String depthText = "";
        for (int i = 0; i < depth; i++)
            depthText += "| ";

        line.setText(depthText + text);
        this.depth = depth;
    }

    public void forceReasons(String[] newReasons) {
        reasonBox.setModel(new DefaultComboBoxModel<>(newReasons));
        reasonBox.setSelectedIndex(0);
    }

    public void resetReasons() {
        reasonBox.setModel(new DefaultComboBoxModel<>(reasons));
        reasonBox.setSelectedIndex(0);
    }

    public void removeReasons(String[] removed) {
        for (String str : removed) {
            reasonBox.removeItem(str);
        }
        reasonBox.setSelectedIndex(0);
    }

    public void setValidity(boolean valid, String tooltipText) {
        line.setToolTipText(null);
        argsField.setToolTipText(null);

        if (errorParsingForm()) {
            updateErrorBackground();
            return;
        }

        line.setBackground(Color.WHITE);
        argsField.setBackground(Color.WHITE);

        if (valid) {
            line.setBackground(new Color(106, 255, 77));
        } else {
            line.setBackground(new Color(255, 80, 74));
            line.setToolTipText("[Line " + Integer.toString(lineNum) + "]: " + tooltipText);
        }
    }

    public void updateErrorBackground() {
        if (errorParsingForm()) {
            if (lineErrorMsg != null) {
                line.setBackground(new Color(179, 0, 0));
                line.setToolTipText("[Line " + Integer.toString(lineNum) + "]: " + lineErrorMsg);
            }

            if (argsErrorMsg != null) {
                argsField.setBackground(new Color(179, 0, 0));
                argsField.setToolTipText("[Line " + Integer.toString(lineNum) + "]: " + argsErrorMsg);
            }
        }
    }

    public void setLineError(String error) {
        lineErrorMsg = error;
    }

    public void setArgsError(String error) {
        argsErrorMsg = error;
    }

    public Proof appendProof(Proof p) {
        lineErrorMsg = null;
        argsErrorMsg = null;

        try {
            return Reason.getReason(reasonBox.getSelectedItem().toString()).getRule().getDeclaredConstructor()
                    .newInstance().addRule(form, p, argsField.getText().replaceAll("[ \t\n]|", ""));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (InvalidRuleException e) {
            lineErrorMsg = e.getMessage();
            e.printStackTrace();
        } catch (InvalidArgsException e) {
            argsErrorMsg = e.getMessage();
            e.printStackTrace();
        }

        updateErrorBackground();

        return p;
    }

    private class DepthDocumentFilter extends DocumentFilter {
        private int targetDepth;

        public DepthDocumentFilter(int targetDepth) {
            this.targetDepth = targetDepth;
        }

        @Override
        public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
            if (str == null) {
                return;
            }

            System.out.println("Original: " + fb.getDocument().getText(0, fb.getDocument().getLength()) +
                    "Insert: " + str +
                    "Final: " + insert(fb.getDocument().getText(0, fb.getDocument().getLength()), offs, str));

            if (!str.contains("|")) {
                super.insertString(fb, offs, str, a);
                return;
            }

            int newDepth = calcDepth(insert(fb.getDocument().getText(0, fb.getDocument().getLength()), offs, str));

            if (newDepth <= targetDepth + 1)
                super.insertString(fb, offs, str, a);
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            int newDepth = calcDepth(remove(fb.getDocument().getText(0, fb.getDocument().getLength()), offset, length));
            if (newDepth >= targetDepth - 1)
                fb.remove(offset, length);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String str, AttributeSet attrs)
                throws BadLocationException {
            if (str == null) {
                return;
            }

            if (!str.contains("|")) {
                fb.replace(offset, length, str, attrs);
                return;
            }

            int newDepth = calcDepth(
                    replace(fb.getDocument().getText(0, fb.getDocument().getLength()), offset, length, str));

            if (newDepth <= targetDepth + 1 && newDepth >= targetDepth - 1)
                fb.replace(offset, length, str, attrs);
        }

        private int calcDepth(String text) {
            String cleanText = text.replaceAll("[ \t\n]", "");
            int depth = 0;
            for (char c : cleanText.toCharArray()) {
                if (c == '|')
                    depth++;
                else
                    break;
            }

            return depth;
        }

        private String insert(String original, int offset, String str) {
            String newString = original.substring(0, offset);
            newString += str;
            newString += original.substring(offset, original.length());
            return newString;
        }

        private String remove(String str, int offset, int length) {
            String newString = str.substring(0, offset);
            newString += str.substring(offset + length, str.length());
            return newString;
        }

        private String replace(String original, int offset, int length, String str) {
            String newString = original.substring(0, offset);
            newString += str;
            newString += original.substring(offset + length, original.length());
            return newString;
        }
    }
}