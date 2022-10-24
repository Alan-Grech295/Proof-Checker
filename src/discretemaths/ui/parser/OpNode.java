package discretemaths.ui.parser;

import static discretemaths.forms.Form.and;
import static discretemaths.forms.Form.implies;
import static discretemaths.forms.Form.not;
import static discretemaths.forms.Form.or;
import static discretemaths.forms.Form.biimplies;

import discretemaths.forms.False;
import discretemaths.forms.Form;

public class OpNode extends Node{
    private Node left = null;
    private Node right = null;
    public enum Type {NOT, AND, OR, IMPLIES, BI_IMPLIES}

    private Type type;
    public OpNode(Type type){
        super(Node.Type.OPERATOR);
        this.type = type;
    }

    public void setLeft(Node node){
        this.left = node;
    }

    public void setRight(Node node){
        this.right = node;
    }

    public Node getLeft(){
        return left;
    }

    public Node getRight(){
        return right;
    }

    public Type getType(){
        return type;
    }

    @Override
    public Form getForm(){
        Form ret = new False();
        switch(type){
            case AND:
            try{
                ret = and(left.getForm(), right.getForm());
            }catch(Exception e){}
                break;
            case BI_IMPLIES:
            try{
                ret = biimplies(left.getForm(), right.getForm());
            }catch(Exception e){}
                break;
            case IMPLIES:
            try{
                ret = implies(left.getForm(), right.getForm());
            }catch(Exception e){}
                break;
            case NOT:
            try{
                ret = not(left.getForm());
            }catch(Exception e){}
                break;
            case OR:
            try{
                ret = or(left.getForm(), right.getForm());
            }catch(Exception e){}
                break;
            default:
                break;
        }

        return ret;
    }
}
