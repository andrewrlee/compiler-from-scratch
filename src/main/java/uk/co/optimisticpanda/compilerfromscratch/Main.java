package uk.co.optimisticpanda.compilerfromscratch;

import java.io.File;
import java.nio.file.Files;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Main {

    public static void main(String[] args) throws Exception {

        var file = new File(Main.class.getResource("/example.txt").getFile());

        var content = new String(Files.readAllBytes(file.toPath()), UTF_8);

        var tokens = new Tokeniser(content).tokenize();

        var root = new Parser(tokens).parse();

        var generatedCode = new CodeGenerator().generate(root);

        System.out.println("function add(x, y) { return x + y; }");
        System.out.println("function multiply(x, y) { return x * y; }");
        System.out.println("//#### GENERATED ####");
        System.out.println(generatedCode);
        System.out.println("//###################");
        System.out.println("console.log(\"'addedTwiceAndSquared(2, 3) == 64' is: \", addedTwiceAndSquared(2, 3) == 64);");
    }
}
