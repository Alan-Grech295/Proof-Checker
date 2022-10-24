package discretemaths.ui;
import java.awt.event.*;

public interface DepthChangeListener {
    public void onDepthChanged(int newDepth, Line line);
}
