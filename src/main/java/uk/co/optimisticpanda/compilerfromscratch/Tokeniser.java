package uk.co.optimisticpanda.compilerfromscratch;

import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Tokeniser {

    public enum TokenType {
        DEF("^\\b(def)\\b"),
        END("^\\b(end)\\b"),
        IDENTIFIER("^([a-zA-Z]+)"),
        INTEGER("^([0-9]+)"),
        OPAREN("^(\\()"),
        CPAREN("^(\\))"),
        COMMA("^(,)");

        private final Pattern regex;

        TokenType(String regex) {
            this.regex = Pattern.compile(regex);
        }
    }

    private final List<TokenType> types = List.of(TokenType.values());
    private String content;

    public Tokeniser(String content) {
        this.content = content;
    }

    public List<Token> tokenize() {

        var tokens = new ArrayList<Token>();

        while(!this.content.isEmpty()) {

            this.content = this.content.trim();
            tokens.add(tokenizeOneToken(content));
        }

        return tokens;
    }

    private Token tokenizeOneToken(String value) {

        for (var type : types) {

            var matcher = type.regex.matcher(value);

            if (matcher.find()) {

                this.content = value.substring(matcher.end(1));
                return ImmutableToken.of(type, matcher.group(1));
            }

        }
        throw new IllegalArgumentException("Couldn't match token on: " + value);
    }


    @Value.Immutable
    public static abstract class Token {
        @Value.Parameter
        public abstract TokenType type();
        @Value.Parameter
        public abstract String value();

        public boolean is(TokenType tokenType) {
            return this.type() == tokenType;
        }
    }
}
