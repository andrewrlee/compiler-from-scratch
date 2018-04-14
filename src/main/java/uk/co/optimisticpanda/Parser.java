package uk.co.optimisticpanda;

import org.immutables.value.Value;
import uk.co.optimisticpanda.Tokeniser.Token;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static uk.co.optimisticpanda.Parser.Node.NodeType.CALL;
import static uk.co.optimisticpanda.Parser.Node.NodeType.INT;
import static uk.co.optimisticpanda.Parser.Node.NodeType.VAR;
import static uk.co.optimisticpanda.Tokeniser.TokenType;
import static uk.co.optimisticpanda.Tokeniser.TokenType.*;

public class Parser {

    private Queue<Token> tokens;

    public Parser(List<Token> tokens) {
        this.tokens = new LinkedBlockingQueue<>(tokens);
    }

    public RootNode parse() {

        List<DefNode> nodes = new ArrayList<>();

        while (peek(DEF)){

            nodes.add(parseDef());

        }

        return ImmutableRootNode.of(nodes);
    }


    private DefNode parseDef() {

        consume(DEF);

        var name = consume(IDENTIFIER);
        var argNames = parseArgNames();
        var body = parseExpression();

        consume(END);

        return ImmutableDefNode.of(name, argNames, body);
    }

    private Node parseExpression() {

        if (peek(INTEGER)) {
            return parseInteger();
        }

        if (peek(IDENTIFIER) && peek(TokenType.OPAREN, 1)) {
            return parseCall();
        }

        return parseVarRef();

    }

    private Node parseCall() {
        return ImmutableCallNode.of(
                consume(IDENTIFIER),
                parseArgExprs());
    }

    private VarRefNode parseVarRef() {
        return ImmutableVarRefNode.of(consume(IDENTIFIER));
    }

    private IntegerNode parseInteger() {
        return ImmutableIntegerNode.of(Integer.valueOf(consume(INTEGER)));
    }

    private List<Node> parseArgExprs() {

        consume(OPAREN);

        var argExprs = new ArrayList<Node>();

        if (!peek(CPAREN)) {

            do {

                argExprs.add(parseExpression());

            } while (skipComma());
        }

        consume(CPAREN);

        return argExprs;
    }

    private List<String> parseArgNames() {

        consume(OPAREN);

        var argNames = new ArrayList<String>();

        if (peek(IDENTIFIER)) {

            do {

                argNames.add(consume(IDENTIFIER));

            } while (skipComma());
        }

        consume(CPAREN);

        return argNames;
    }

    private boolean skipComma() {
        return peek(COMMA) && consume(COMMA) != null;
    }

    private boolean peek(TokenType type, int i) {
        Iterator<Token> tokens = this.tokens.iterator();
        int count = i;
        while(--count >= 0 && tokens.hasNext()) {
            tokens.next();
        }
        return tokens.hasNext() && tokens.next().is(type);
    }

    private boolean peek(TokenType type) {
        return peek(type, 0);
    }

    private String consume(TokenType type) {
        var token = tokens.poll();
        if (token == null || !token.is(type)) {
            throw new RuntimeException("Token not correct type, expected: " + type + ", was: " + token);
        }
        return token.value();
    }

    @Value.Immutable
    static abstract class RootNode extends Node {
        @Override
        NodeType type() {
            return NodeType.ROOT;
        }
        @Value.Parameter
        abstract List<DefNode> defs();
    }


    @Value.Immutable
    static abstract class DefNode extends Node {
        @Override
        NodeType type() {
            return NodeType.DEF;
        }
        @Value.Parameter
        abstract String name();
        @Value.Parameter
        abstract List<String> names();
        @Value.Parameter
        abstract Node body();
    }

    @Value.Immutable
    static abstract class CallNode extends Node {
        @Override
        NodeType type() {
            return CALL;
        }
        @Value.Parameter
        abstract String name();
        @Value.Parameter
        abstract List<Node> argExprs();
    }

    @Value.Immutable
    static abstract class IntegerNode extends Node {
        @Override
        NodeType type() {
            return INT;
        }
        @Value.Parameter
        abstract int i();
    }

    @Value.Immutable
    static abstract class VarRefNode extends Node {
        @Override
        NodeType type() {
            return VAR;
        }
        @Value.Parameter
        abstract String name();
    }

    static abstract class Node {
        enum NodeType {
            ROOT, VAR, INT, CALL, DEF
        }
        abstract NodeType type();
    }
}
