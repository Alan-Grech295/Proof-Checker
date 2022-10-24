package discretemaths.ui.parser;

public class VariableToken extends Token {
    private char name;
    public VariableToken(char name){
        super(Token.Type.VARIABLE);
        this.name = name;
    }

    public char getName(){
        return name;
    }

    @Override
    public String toString(){
        return "Variable: " + name;
    }
}
