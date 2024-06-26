package pt.uab.compilacao;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.ArrayList;
import java.util.List;

public class TACGenerator extends MontyPythonParserBaseVisitor<String> {
/*     private int tempCount = 0;
    private int labelCount = 0;
    private List<String> code = new ArrayList<>();

    private String newTemp() {
        return "t" + (++tempCount);
    }

    private String newLabel() {
        return "L" + (++labelCount);
    }

    @Override
    public String visitAssignment(MontyPythonParser.AssignmentContext ctx) {
        String varName = ctx.ID().getText();
        String exprCode = visit(ctx.expr());
        code.add(varName + " = " + exprCode);
        return varName;
    }

    @Override
    public String visitAddExpr(MontyPythonParser.AddExprContext ctx) {
        String left = visit(ctx.expr(0));
        String right = visit(ctx.expr(1));
        String temp = newTemp();
        code.add(temp + " = " + left + " + " + right);
        return temp;
    }

    @Override
    public String visitMulExpr(MontyPythonParser.MulExprContext ctx) {
        String left = visit(ctx.expr(0));
        String right = visit(ctx.expr(1));
        String temp = newTemp();
        code.add(temp + " = " + left + " * " + right);
        return temp;
    }

    @Override
    public String visitIntExpr(MontyPythonParser.IntExprContext ctx) {
        return ctx.INT().getText();
    }

    // Implement other visit methods as needed

    public String getCode() {
        return String.join("\n", code);
    }

    public static void main(String[] args) throws Exception {
        String input = "x = 3 + 4 * 5";
        CharStream inputStream = CharStreams.fromString(input);
        MontyPythonLexer lexer = new MontyPythonLexer(inputStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        MontyPythonParser parser = new MontyPythonParser(tokenStream);
        ParseTree tree = parser.program(); // Assuming 'program' is the start rule

        TACGenerator tacGenerator = new TACGenerator();
        tacGenerator.visit(tree);
        System.out.println(tacGenerator.getCode());
    } */
}
