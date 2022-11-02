package discretemaths;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

import static discretemaths.Proof.hyp;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import discretemaths.forms.False;
import discretemaths.forms.Form;
import discretemaths.ui.InvalidStatementException;
import discretemaths.ui.Line;
import discretemaths.ui.file.FileSerializer;

public class Main {
    private int nextLineNum = 1;
    private ArrayList<Line> lines;
    private JPanel panel;
    private JPanel linePanel;
    private JFrame frame;

    private GridLayout lyt;

    public final static int LINE_HEIGHT = 30;
    private static Main instance;
    private static boolean hasFocus = true;

    public Main() {
        instance = this;

        // Creates the Window
        frame = new JFrame();
        frame.setPreferredSize(new Dimension(500, 500));
        frame.pack();

        frame.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                // Checks if the window is focused
                hasFocus = e.getNewState() != WindowEvent.WINDOW_LOST_FOCUS;
            }
        });

        // Main panel
        panel = new JPanel();

        // Sets up the panel displaying the lines
        linePanel = new JPanel();
        JScrollPane scrlPane = new JScrollPane(linePanel);
        // Removes horizontal scroll bar
        scrlPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        lyt = new GridLayout(frame.getHeight() / LINE_HEIGHT, 0);
        linePanel.setLayout(lyt);

        panel.setLayout(new BorderLayout());

        lines = new ArrayList<Line>();
        // Creates the first line
        Line line1 = new Line(panel, 1, 300, LINE_HEIGHT, -1, (focus, l) -> {
            if (!focus) {
                checkLines();
            }
        }, (depth, l) -> {
        });

        // Sets the first line's reason to only be Hypothesis
        line1.forceReasons(new String[] { "Hyp" });

        // Adds line 1 to the panel
        addItem(line1);
        // Adds line 1 to the list of all lines
        lines.add(line1);

        nextLineNum++;

        // Adds the line panel to the main panel
        panel.add(scrlPane, BorderLayout.CENTER);

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        // File Menu
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        file.getAccessibleContext().setAccessibleDescription("File");
        menuBar.add(file);

        // Import Menu Item
        JMenuItem importItem = new JMenuItem("Import...", KeyEvent.VK_I);
        importItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));

                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    lines.clear();
                    FileSerializer.Deserialize(file.getAbsolutePath(), lines, (focus, l) -> {
                        if (!focus) {
                            checkLines();
                        }
                    }, (depth, l) -> {
                        // If the depth of this line is greater than the depth of the past line (i.e.
                        // starting a proof),
                        // the line's reasons are forced to be Sub-Hypothesis
                        if (lines.get(l.getLineNum() - 2).getDepth() < depth) {
                            l.forceReasons(new String[] { "Sub-Hyp" });
                        } else {
                            l.resetReasons();
                            l.removeReasons(new String[] { "Hyp", "Sub-Hyp" });
                        }
                    });

                    linePanel.removeAll();
                    linePanel.revalidate();
                    linePanel.repaint();

                    lyt.setRows(panel.getHeight() / LINE_HEIGHT);
                    linePanel.setPreferredSize(new Dimension(linePanel.getWidth(), panel.getHeight()));

                    nextLineNum = lines.size() + 1;

                    for (Line l : lines)
                        addItem(l);

                    checkLines();

                    frame.pack();
                }
            }
        });
        file.add(importItem);

        // Export Menu Item
        JMenuItem exportItem = new JMenuItem("Export...", KeyEvent.VK_I);
        exportItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));

                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    FileSerializer.Serialize(file.getAbsolutePath().replaceFirst("[.][^.]+$", "") + ".txt", lines);
                }
            }
        });
        file.add(exportItem);

        // Help Menu
        JMenu help = new JMenu("Help");
        help.setMnemonic(KeyEvent.VK_H);
        help.getAccessibleContext().setAccessibleDescription("Help");
        menuBar.add(help);

        // View Readme
        JMenuItem readmeItem = new JMenuItem("View README...", KeyEvent.VK_R);
        readmeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        desktop.browse(
                                new URL("https://github.com/Alan-Grech295/Proof-Checker/blob/main/README.md").toURI());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Could not view repository README on browser", "Help",
                                JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Could not view repository README on browser", "Help",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        help.add(readmeItem);

        panel.add(menuBar, BorderLayout.NORTH);

        // Completes the window and displays it
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Proof Checker");
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        instance = new Main();
    }

    public static Main getInstance() {
        return instance;
    }

    public JPanel getPanel() {
        return panel;
    }

    // Method called when a key is pressed on a line
    public void keyPressed(int lineNum, KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            // If 'enter' is pressed on the last line, a new line is created
            if (lineNum == lines.size()) {
                Line line = new Line(panel, nextLineNum, 300, LINE_HEIGHT, lines.get(lines.size() - 1).getDepth(),
                        (focus, l) -> {
                            if (!focus) {
                                checkLines();
                            }
                        }, (depth, l) -> {
                            // If the depth of this line is greater than the depth of the past line (i.e.
                            // starting a proof),
                            // the line's reasons are forced to be Sub-Hypothesis
                            if (lines.get(l.getLineNum() - 2).getDepth() < depth) {
                                l.forceReasons(new String[] { "Sub-Hyp" });
                            } else {
                                l.resetReasons();
                                l.removeReasons(new String[] { "Hyp", "Sub-Hyp" });
                            }
                        });
                line.removeReasons(new String[] { "Hyp", "Sub-Hyp" });

                // Setting the depth of the new line to match the previous line
                if (lines.size() > 1) {
                    line.setDepth(lines.get(lines.size() - 1).getDepth());
                }
                lines.add(line);
                nextLineNum++;

                // Add the line to the panel
                addItem(line);
                frame.pack();

                // Select the new line
                line.select();
            } else {
                // If 'enter' is pressed when this is not the last line, move down a line
                moveDown(lineNum);
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            if (lineNum > 1 && lineNum <= lines.size()) {
                removeItem(lines.get(lineNum - 1));

                lines.remove(lineNum - 1);

                boolean selectedLine = false;
                if (lineNum <= lines.size()) {
                    for (int i = lineNum - 1; i < lines.size(); i++) {
                        Line l = lines.get(i);
                        l.setLineNum(l.getLineNum() - 1);
                        l.decrementArgs(lineNum - 1);

                        if (!selectedLine) {
                            l.select();
                            selectedLine = true;
                        }
                    }
                }

                if (!selectedLine) {
                    lines.get(lines.size() - 1).select();
                    selectedLine = true;
                }

                nextLineNum = lines.size() + 1;

                checkLines();
            }
        }

        // 0 1 2 3 4 5
        // 1 2 3 5 6 7

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            moveUp(lineNum);
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            moveDown(lineNum);
        }
    }

    private void moveUp(int lineNum) {
        if (lineNum <= 1)
            return;

        // Select the line above (line num is the index of the line in lines list + 1)
        lines.get(lineNum - 2).select();
    }

    private void moveDown(int lineNum) {
        if (lineNum >= lines.size())
            return;

        // Select the line below (line num is the index of the line in lines list + 1)
        lines.get(lineNum).select();
    }

    private void addItem(Component c) {
        // If a line is added when the layout has no rows left, increment the rows
        if (lines.size() >= lyt.getRows()) {
            lyt.setRows(lines.size() + 1);
            linePanel.setPreferredSize(new Dimension(linePanel.getWidth(), (lines.size() + 1) * LINE_HEIGHT));
        }

        linePanel.add(c);
        frame.pack();
    }

    private void removeItem(Component c) {
        // If a line is added when the layout has no rows left, increment the rows
        lyt.setRows(Math.max(lines.size() - 1, frame.getHeight() / LINE_HEIGHT));
        linePanel.setPreferredSize(new Dimension(linePanel.getWidth(), (lines.size() - 1) * LINE_HEIGHT));

        linePanel.remove(c);
        linePanel.revalidate();
        linePanel.repaint();
        frame.pack();
    }

    private void checkLines() {
        // If the window does not have focus do not check the lines
        if (!hasFocus)
            return;

        // Find last line with text
        int lastLineNum = -1;
        for (int i = lines.size() - 1; i >= 0; i--) {
            if (!lines.get(i).getText().replaceAll("[ \n\t|]", "").isEmpty()) {
                lastLineNum = i;
                break;
            }
        }

        Proof p = null;

        int lastDepth = 0;
        for (int i = 0; i <= lastLineNum; i++) {
            // Get current line
            Line l = lines.get(i);
            // Convert the line text into a Form
            l.updateForm();
            if (l.errorParsingForm()) {
                break;
            }

            int depth = l.getDepth();

            // If the depth of this line is smaller than the depth of the previous line
            // (i.e. sub-hyp ended),
            // add the necessary ends to the proof
            if (lastDepth > depth) {
                try {
                    for (int j = 0; j < lastDepth - depth; j++)
                        p.end();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            lastDepth = depth;

            p = l.appendProof(p);
        }

        if (p == null)
            return;

        try {
            p.end().print();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (p.isWellFormed()) {
            int invalidLine = -1;
            // Loop over all lines to set their validity
            for (int i = 0; i < p.getLines().size(); i++) {
                // If there is an invalid line before this line this line is also invalid
                if (invalidLine > 0) {
                    lines.get(i).setValidity(false,
                            "Line " + Integer.toString(invalidLine) + " is invalid, cannot continue");
                    continue;
                }

                try {
                    if (p.getLine(i + 1).isFalse()) {
                        invalidLine = i + 1;
                        lines.get(i).setValidity(false, p.getReason(i + 1).toString());

                        // If the line entered and the line generated by the form does not match
                        // (i.e. the line entered does not match the rule), the line is invalid
                    } else if (!p.getLine(i + 1).equals(lines.get(i).getForm())) {
                        invalidLine = i + 1;
                        lines.get(i).setValidity(false,
                                "Line given does not match rule [" + p.getLine(i + 1).toString() + "]");
                    } else {
                        lines.get(i).setValidity(true, "");
                    }
                } catch (Exception e) {
                    lines.get(i).setValidity(false, e.getMessage());
                    invalidLine = i + 1;
                }
            }
        }
    }
}
