package discretemaths.ui.parser;

import java.rmi.ConnectIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import discretemaths.forms.Form;
import discretemaths.ui.InvalidStatementException;
import discretemaths.ui.parser.OpNode.Type;

public class FormParser {
    private static HashMap<String, OpNode.Type> operators = new HashMap<String, OpNode.Type>() {
        {
            put("¬", Type.NOT);
            put("^", Type.AND);
            put("v", Type.OR);
            put("=>", Type.IMPLIES);
            put("<=>", Type.BI_IMPLIES);
        }
    };

    public static Form getForm(String statement) throws InvalidStatementException {
        statement = statement.replaceAll("[ \t\n|]", "");

        ArrayList<Token> tokens = lex(statement);

        Node opTree = parse(tokens);

        if (opTree == null) {
            throw new InvalidStatementException("Invalid statement");
        }

        return opTree.getForm();
    }

    private static Node parse(ArrayList<Token> tokens) throws InvalidStatementException {
        ArrayList<Token> notTokens = parseTokensNot(tokens);

        ArrayList<Token> l2RToks = parseTokensL2R(notTokens);

        return parseTokensR2L(l2RToks);
    }

    private static ArrayList<Token> parseTokensNot(ArrayList<Token> tokens) throws InvalidStatementException {
        ArrayList<Token> finalTokens = new ArrayList<>();
        Node currentNode = null;

        int bracketLevel = 0;
        ArrayList<Token> bracketTokens = new ArrayList<>();

        boolean foundNot = false;
        for (Token tok : tokens) {
            if (!foundNot) {
                if (tok.type == Token.Type.OPERATOR && ((OperatorToken) tok).getType() == Type.NOT) {
                    foundNot = true;
                    currentNode = new OpNode(Type.NOT);
                    continue;
                }
                finalTokens.add(tok);
                continue;
            }

            if (tok.type == Token.Type.CLOSE_BRACKET) {
                bracketLevel--;
                if (bracketLevel == 0) {
                    Node nextNode = parse(bracketTokens);
                    ((OpNode) currentNode).setLeft(nextNode);
                    finalTokens.add(new NodeToken(currentNode));
                    currentNode = null;
                    foundNot = false;
                    bracketTokens.clear();
                }
                continue;
            }

            if (bracketLevel > 0) {
                bracketTokens.add(tok);
                continue;
            }

            if (tok.type == Token.Type.OPEN_BRACKET) {
                bracketLevel++;
                continue;
            }

            if (tok.type == Token.Type.VARIABLE) {
                ((OpNode) currentNode).setLeft(new VariableNode(((VariableToken) tok).getName()));
                finalTokens.add(new NodeToken(currentNode));
                currentNode = null;
                foundNot = false;
                continue;
            }

            throw new InvalidStatementException("Invalid statement after not (¬)!");
        }

        if (foundNot)
            throw new InvalidStatementException("Cannot end statement with a not (¬)!");

        return finalTokens;
    }

    private static ArrayList<Token> parseTokensL2R(ArrayList<Token> tokens) throws InvalidStatementException {
        ArrayList<Token> finalTokens = new ArrayList<>();
        Node currentNode = null;

        int bracketLevel = 0;
        ArrayList<Token> bracketTokens = new ArrayList<>();

        for (Token tok : tokens) {
            if (tok.type == Token.Type.CLOSE_BRACKET) {
                bracketLevel--;
                if (bracketLevel == 0) {
                    Node nextNode = parse(bracketTokens);
                    if (currentNode == null)
                        currentNode = nextNode;
                    else
                        ((OpNode) currentNode).setRight(nextNode);
                    bracketTokens.clear();
                    continue;
                }
            }

            if (tok.type == Token.Type.OPEN_BRACKET) {
                bracketLevel++;

                if (bracketLevel == 1)
                    continue;
            }

            if (bracketLevel > 0) {
                bracketTokens.add(tok);
                continue;
            }

            if (tok.type == Token.Type.OPERATOR) {
                OperatorToken opTok = (OperatorToken) tok;
                if (opTok.getType() == Type.IMPLIES || opTok.getType() == Type.BI_IMPLIES) {
                    if (currentNode != null) {
                        finalTokens.add(new NodeToken(currentNode));
                        currentNode = null;
                    }
                    finalTokens.add(tok);
                    continue;
                }

                if (currentNode == null) {
                    if (opTok.getType() == OpNode.Type.NOT)
                        currentNode = new OpNode(Type.NOT);
                    else
                        throw new InvalidStatementException("Cannot start statement with operator!");
                } else {
                    OpNode newNode = new OpNode(opTok.getType());
                    newNode.setLeft(currentNode);
                    currentNode = newNode;
                }
                continue;
            }

            if (tok.type == Token.Type.VARIABLE) {
                if (currentNode == null) {
                    currentNode = new VariableNode(((VariableToken) tok).getName());
                } else {
                    if (currentNode.type != Node.Type.OPERATOR) {
                        throw new InvalidStatementException("Variables must be separated by operators");
                    }
                    ((OpNode) currentNode).setRight(new VariableNode(((VariableToken) tok).getName()));
                }
                continue;
            }

            if (tok.type == Token.Type.NODE) {
                NodeToken nodeTok = (NodeToken) tok;
                if (currentNode == null) {
                    currentNode = nodeTok.getNode();
                } else {
                    ((OpNode) currentNode).setRight(nodeTok.getNode());
                }
            }
        }
        // P=>Q^RvP<=>Q^¬R
        finalTokens.add(new NodeToken(currentNode));

        return finalTokens;
    }

    private static Node parseTokensR2L(ArrayList<Token> tokens) throws InvalidStatementException {
        Node currentNode = null;

        for (int i = tokens.size() - 1; i >= 0; i--) {
            Token tok = tokens.get(i);

            if (tok.type == Token.Type.NODE) {
                NodeToken nodeTok = (NodeToken) tok;
                if (currentNode == null) {
                    currentNode = nodeTok.getNode();
                } else {
                    ((OpNode) currentNode).setLeft(nodeTok.getNode());
                }

                continue;
            }

            if (tok.type == Token.Type.OPERATOR) {
                OperatorToken opTok = (OperatorToken) tok;
                if (currentNode == null) {
                    throw new InvalidStatementException("Cannot end statement with operator!");
                } else {
                    OpNode nextNode = new OpNode(opTok.getType());
                    nextNode.setRight(currentNode);
                    currentNode = nextNode;
                }
                continue;
            }

            throw new InvalidStatementException("Invalid statement!");
        }

        return currentNode;
    }

    private static ArrayList<Token> lex(String statement) throws InvalidStatementException {
        ArrayList<Token> tokens = new ArrayList<Token>();
        for (int i = 0; i < statement.length(); i++) {
            if (statement.charAt(i) == '(') {
                tokens.add(new Token(Token.Type.OPEN_BRACKET));
                continue;
            }
            if (statement.charAt(i) == ')') {
                tokens.add(new Token(Token.Type.CLOSE_BRACKET));
                continue;
            }

            boolean foundOp = false;
            for (Entry<String, OpNode.Type> entry : operators.entrySet()) {
                if (statement.regionMatches(i, entry.getKey(), 0, entry.getKey().length())) {
                    tokens.add(new OperatorToken(entry.getValue()));
                    i += entry.getKey().length() - 1;
                    foundOp = true;
                    break;
                }
            }
            if (foundOp)
                continue;

            if (Character.isLetter(statement.charAt(i))) {
                tokens.add(new VariableToken(statement.charAt(i)));
                continue;
            }

            throw new InvalidStatementException("Invalid input in " + statement);
        }

        return tokens;
    }
}
