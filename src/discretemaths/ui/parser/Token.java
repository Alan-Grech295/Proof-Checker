package discretemaths.ui.parser;

public class Token {
    public enum Type{OPEN_BRACKET, CLOSE_BRACKET, VARIABLE, OPERATOR, NODE}
    protected Type type;

    public Token(Type type){
        this.type = type;
    }

    @Override
    public String toString(){
        return type.name();
    }
}
