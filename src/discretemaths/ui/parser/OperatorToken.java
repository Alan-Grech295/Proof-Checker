package discretemaths.ui.parser;

public class OperatorToken extends Token {
    private OpNode.Type type;
    public OperatorToken(OpNode.Type type){
        super(Token.Type.OPERATOR);
        this.type = type;
    }

    public OpNode.Type getType(){
        return type;
    }

    @Override
    public String toString(){
        return "Operator: " + type.name();
    }
}
