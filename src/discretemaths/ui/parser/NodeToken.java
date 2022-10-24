package discretemaths.ui.parser;

public class NodeToken extends Token {
    private Node node;

    public NodeToken(Node node){
        super(Type.NODE);
        this.node = node;
    }

    public Node getNode(){
        return node;
    }
}
