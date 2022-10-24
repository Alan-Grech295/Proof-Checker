package discretemaths.ui.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import discretemaths.Main;
import discretemaths.ui.DepthChangeListener;
import discretemaths.ui.Line;
import discretemaths.ui.LineFocusListener;

public class FileSerializer {
    public static void Serialize(String pathStr, ArrayList<Line> lines) {
        String out = "";
        for (Line line : lines) {
            out += line.getLineNum() + "\t" + line.getText() + "\t" + line.getArgs() + "\t" + line.getReason() + "\n";
        }

        out = out.substring(0, out.length() - 1);

        Path path = Paths.get(pathStr);

        try {
            Files.writeString(path, out, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Invalid path (" + ex.getMessage() + ")", "Export",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static ArrayList<Line> Deserialize(String pathStr, ArrayList<Line> lines, LineFocusListener focusListener,
            DepthChangeListener depthListener) {
        try (BufferedReader buffer = new BufferedReader(new FileReader(pathStr))) {
            String line;

            while ((line = buffer.readLine()) != null) {
                String[] strings = line.split("\t");

                if (strings.length != 4) {
                    JOptionPane.showMessageDialog(null, "File is not in correct format", "Import",
                            JOptionPane.ERROR_MESSAGE);
                    return null;
                }

                try {
                    Line l = new Line(Main.getInstance().getPanel(), Integer.parseInt(strings[0]), 300,
                            Main.LINE_HEIGHT, lines.size() == 0 ? -1 : lines.get(lines.size() - 1).getDepth(),
                            focusListener, depthListener);

                    if (lines.size() == 0) {
                        l.forceReasons(new String[] { "Hyp" });
                    } else {
                        l.removeReasons(new String[] { "Hyp" });

                        if (!strings[3].equals("Sub-Hyp"))
                            l.removeReasons(new String[] { "Sub-Hyp" });
                    }

                    l.setText(strings[1]);
                    l.setArgs(strings[2]);
                    l.setReason(strings[3]);

                    l.updateDepth();

                    lines.add(l);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Import",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Invalid path (" + e.getMessage() + ")", "Import",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return lines;
    }
}
