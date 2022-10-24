package discretemaths.ui.parser;

import discretemaths.forms.Form;

public abstract class Node {
    public enum Type{OPERATOR, VALUE}
    protected Type type;

    public Node(Type type){
        this.type = type;
    }

    public abstract Form getForm();
}
