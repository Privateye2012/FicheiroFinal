package pt.uab.compilacao;

import org.antlr.v4.runtime.tree.ParseTree;
import pt.uab.compilacao.MontyPythonParser;
import java.util.*;

/**
 * Visitor da gramatica do Monty Python que gera as instruções em
 * TAC - Código de três Endereços
 *
 * Para cada definição de uma regra no BNF do parser, deve ser criado
 * um método cujo nome segue a nomenclatura visit<NomeDaRegra>.
 * Por exemplo para a regra "program" existente em MontyPythonParser.g4
 * deve ser criado o método visitProgram()
 *
 * O ponto de entrada da visita é executado ao executar o método visit()
 * com a AST como parâmetro. Este método existe na classe gerada a partir da
 * gramática e a qual o nosso visitor concreto extende.
 */
public class MontyPythonTACVisitor extends MontyPythonParserBaseVisitor<String> {
    private int tempCount = 0;
    private int labelCount = 0;
    private List<String> tac = new ArrayList<>();

    private String newTemp() {
        return "t" + (tempCount++);
    }

    private String newLabel() {
        return "L" + (labelCount++);
    }

    /**
     * Obtem a lista de instruções TAC, geradas
     * @return Lista de instruções TAC
     */
    public List<String> getTAC() {
        return tac;
    }

    @Override
    public String visitProgram(MontyPythonParser.ProgramContext ctx) {
        for (ParseTree child : ctx.children) {
            visit(child);
        }
        return null;
    }

    @Override
    public String visitStatement(MontyPythonParser.StatementContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public String visitDecl_var(MontyPythonParser.Decl_varContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public String visitDecl_list(MontyPythonParser.Decl_listContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public String visitFloat(MontyPythonParser.FloatContext ctx) {
        for (int i = 0; i < ctx.ID().size(); i++) {
            String id = ctx.ID(i).getText();
            String value = (ctx.FLOAT(i) != null) ? ctx.FLOAT(i).getText() : "0.0";
            tac.add(id + " = " + value);
        }
        return null;
    }

    @Override
    public String visitInt(MontyPythonParser.IntContext ctx) {
        for (int i = 0; i < ctx.ID().size(); i++) {
            String id = ctx.ID(i).getText();
            String value = (ctx.INT(i) != null) ? ctx.INT(i).getText() : "0";
            tac.add(id + " = " + value);
        }
        return null;
    }

    @Override
    public String visitFloat_list(MontyPythonParser.Float_listContext ctx) {
        String id = ctx.ID().getText();
        tac.add(id + " = []");
        if (ctx.EQUAL() != null) {
            for (int i = 0; i < ctx.FLOAT().size(); i++) {
                String value = ctx.FLOAT(i).getText();
                tac.add("add(" + id + ", " + value + ")");
            }
            for (int i = 0; i < ctx.INT().size(); i++) {
                String value = ctx.INT(i).getText();
                tac.add("add(" + id + ", " + value + ")");
            }
        }
        return null;
    }

    @Override
    public String visitInt_list(MontyPythonParser.Int_listContext ctx) {
        String id = ctx.ID().getText();
        tac.add(id + " = []");
        if (ctx.EQUAL() != null) {
            for (int i = 0; i < ctx.INT().size(); i++) {
                String value = ctx.INT(i).getText();
                tac.add("add(" + id + ", " + value + ")");
            }
        }
        return null;
    }

    @Override
    public String visitAtrib_var(MontyPythonParser.Atrib_varContext ctx) {
        String id = ctx.ID().getText();
        String value = visit(ctx.oper()); // Correctly handle the oper context
        tac.add(id + " = " + value);
        return null;
    }

    @Override
    public String visitAtrib_list(MontyPythonParser.Atrib_listContext ctx) {
        if (ctx.list_index() != null) {
            String index = visit(ctx.list_index());
            String value = visit(ctx.oper(0)); // Correct handling of the oper context
            tac.add(index + " = " + value);
        } else {
            String id = ctx.ID().getText();
            List<String> values = new ArrayList<>();
            for (MontyPythonParser.OperContext operCtx : ctx.oper()) {
                values.add(visit(operCtx));
            }
            tac.add(id + " = [" + String.join(", ", values) + "]");
        }
        return null;
    }

    @Override
    public String visitList_index(MontyPythonParser.List_indexContext ctx) {
        String id = ctx.ID().getText();
        String index = ctx.INT().getText();
        return id + "[" + index + "]";
    }

    @Override
    public String visitOper(MontyPythonParser.OperContext ctx) {
        if (ctx.oper().size() == 2) {
            String left = visit(ctx.oper(0));
            String right = visit(ctx.oper(1));
            String temp = newTemp();
            String op = ctx.getChild(1).getText();
            tac.add(temp + " = " + left + " " + op + " " + right);
            return temp;
        } else if (ctx.oper().size() == 1) {
            String value = visit(ctx.oper(0));
            if (ctx.NOT() != null) {
                String temp = newTemp();
                tac.add(temp + " = !" + value);
                return temp;
            } else {
                return value;
            }
        } else if (ctx.ID() != null) {
            return ctx.ID().getText();
        } else if (ctx.INT() != null) {
            return ctx.INT().getText();
        } else if (ctx.FLOAT() != null) {
            return ctx.FLOAT().getText();
        }
        return null;
    }

    @Override
    public String visitSize_function(MontyPythonParser.Size_functionContext ctx) {
        String id = ctx.ID().getText();
        String temp = newTemp();
        tac.add(temp + " = size(" + id + ")");
        return temp;
    }

    @Override
    public String visitAdd_function(MontyPythonParser.Add_functionContext ctx) {
        String id = ctx.ID().getText();
        String value = ctx.INT().getText();
        tac.add("add(" + id + ", " + value + ")");
        return null;
    }

    @Override
    public String visitRemove_function(MontyPythonParser.Remove_functionContext ctx) {
        String id = ctx.ID().getText();
        String value = ctx.INT().getText();
        tac.add("remove(" + id + ", " + value + ")");
        return null;
    }

    @Override
    public String visitFunction(MontyPythonParser.FunctionContext ctx) {
        String functionName = ctx.ID(0).getText(); // Correctly handle function name
        tac.add("def " + functionName + ":");
        visitChildren(ctx);
        return null;
    }

    @Override
    public String visitType(MontyPythonParser.TypeContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public String visitInput(MontyPythonParser.InputContext ctx) {
        String id = ctx.ID().getText();
        String prompt = ctx.STRING().getText();
        tac.add(id + " = input(" + prompt + ")");
        return null;
    }

    @Override
    public String visitPrint(MontyPythonParser.PrintContext ctx) {
        StringBuilder printCommand = new StringBuilder("print(");
        if (ctx.STRING() != null) {
            printCommand.append(ctx.STRING().getText());
            for (MontyPythonParser.OperContext operCtx : ctx.oper()) {
                printCommand.append(", ").append(visit(operCtx));
            }
        }
        printCommand.append(")");
        tac.add(printCommand.toString());
        return null;
    }

    @Override
    public String visitCiclos(MontyPythonParser.CiclosContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public String visitFor(MontyPythonParser.ForContext ctx) {
        String id = ctx.ID().getText();
        String range = visit(ctx.oper(0)); // Correct handling of the oper context
        String startLabel = newLabel();
        String endLabel = newLabel();
        tac.add(startLabel + ":");
        tac.add("if " + id + " >= " + range + " goto " + endLabel);
        visitChildren(ctx);
        tac.add(id + " = " + id + " + 1");
        tac.add("goto " + startLabel);
        tac.add(endLabel + ":");
        return null;
    }

    @Override
    public String visitWhile(MontyPythonParser.WhileContext ctx) {
        String startLabel = newLabel();
        String endLabel = newLabel();
        tac.add(startLabel + ":");
        String condition = visit(ctx.oper()); // Correct handling of the oper context
        tac.add("if !" + condition + " goto " + endLabel);
        visitChildren(ctx);
        tac.add("goto " + startLabel);
        tac.add(endLabel + ":");
        return null;
    }

    @Override
    public String visitIf(MontyPythonParser.IfContext ctx) {
        String condition = visit(ctx.oper()); // Correct handling of the oper context
        String endLabel = newLabel();
        tac.add("if !" + condition + " goto " + endLabel);
        visitChildren(ctx);
        tac.add(endLabel + ":");
        return null;
    }

    @Override
    public String visitElif(MontyPythonParser.ElifContext ctx) {
        String condition = visit(ctx.oper()); // Correct handling of the oper context
        String endLabel = newLabel();
        tac.add("if !" + condition + " goto " + endLabel);
        visitChildren(ctx);
        tac.add(endLabel + ":");
        return null;
    }

    @Override
    public String visitElse(MontyPythonParser.ElseContext ctx) {
        visitChildren(ctx);
        return null;
    }
}

