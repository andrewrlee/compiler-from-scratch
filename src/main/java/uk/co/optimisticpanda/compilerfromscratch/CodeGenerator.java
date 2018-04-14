package uk.co.optimisticpanda.compilerfromscratch;

import uk.co.optimisticpanda.compilerfromscratch.Parser.CallNode;
import uk.co.optimisticpanda.compilerfromscratch.Parser.DefNode;
import uk.co.optimisticpanda.compilerfromscratch.Parser.Node;
import uk.co.optimisticpanda.compilerfromscratch.Parser.VarRefNode;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static uk.co.optimisticpanda.compilerfromscratch.Parser.*;

public class CodeGenerator {

    public String generate(Node node) {

        switch (node.type()) {

            case ROOT:
                RootNode root = (RootNode) node;
                return root.defs().stream().map(this::generate).collect(joining("\n"));

            case DEF:
                DefNode def = (DefNode) node;
                return format("function %s(%s) { return %s; }",
                        def.name(),
                        def.names().stream().collect(joining(", ")),
                        generate(def.body()));

            case CALL:
                CallNode call = (CallNode) node;
                return format("%s(%s)",
                        call.name(),
                        call.argExprs().stream().map(this::generate).collect(joining(", ")));

            case VAR:
                VarRefNode var = (VarRefNode) node;
                return var.name();

            case INT:
                IntegerNode integerNode = (IntegerNode) node;
                return String.valueOf(integerNode.i());

            default:
                throw new IllegalStateException("unexpected type: " + node.type());

        }
    }
}
