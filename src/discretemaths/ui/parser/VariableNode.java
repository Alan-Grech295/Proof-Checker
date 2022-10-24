package discretemaths.ui.parser;

import static discretemaths.forms.Form.$;

import discretemaths.forms.Form;

public class VariableNode extends Node {
    private char name;
    public VariableNode(char name){
        super(Node.Type.VALUE);
        this.name = name;
    }

    public char getName(){
        return name;
    }

    @Override
    public Form getForm(){
        return $(Character.toString(name));
    }
}
