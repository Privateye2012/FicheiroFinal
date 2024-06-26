package pt.uab.compilacao;

// JDK
import java.util.*;
import java.nio.file.*;
import java.io.IOException;
import javax.swing.*;

// ANTLR runtime classes
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.gui.TreeViewer;

/**
 * Programa principal, que permite a execução do parser de monty-python
 * O compilador de Monty Python executa as seguintes fases:
 *   - Pre-Processamento : Para tratar a questão da indentação serem blocos
 *   - Analise sintatica e semantica : Converte a sequancia de caracteres do pre processador numa arvore sintatica
 *   - Geracao de TAC : Converte a arvore sintatica numa sequencia de instruções TAC
 *   - Optimização : Converte as instruções TAC numa sequência optimizada
 *
 * TODO : Analisar os argumetos de forma mais estruturada
 * TODO : Permitir executar cada passo em separado
 * TODO : Permitir que a apresentação da árvores sintática seja opcional
 */
public class Main {


    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Usage: java Main <file>");
            System.exit(1);
        }

        String fileName = args[0];

        try {
            // Programa simples - TODO : Para remover
            // String inputProgram = "int a = 5\nfloat b = 10.0\n";
            // String inputProgram = readFileToString(fileName);
            StringBuffer preprocessedCode = MontyPythonPreProcessor.preprocess(fileName);
            // TODO : Remover debug
            System.out.println("Preprocessed code:");
            System.out.println(preprocessedCode.toString());
            String inputProgram = preprocessedCode.toString();

            // Cria uma stream de carateres a partir da string resultado do pre processamento
            CharStream input = CharStreams.fromString(inputProgram);

            // Cria um lexer que será alimentado com a stream de caracteres
            MontyPythonLexer lexer = new MontyPythonLexer(input);

            // Cria uma stream (lista) de tokens obtidos da análise lexicográfica
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Cria um parser que é alimentado pelo buffer de tokens
            MontyPythonParser parser = new MontyPythonParser(tokens);

            // Inicia o parsing com inicio na regra "program"
            ParseTree tree = parser.program();

            // Mostra a árvore sintática no GUI
            // TODO : Deve poder ser opcional
            displayParseTree(tree, parser);

            // Criar um objecto visitor que gera uma sequência de TAC(s) - C+odigo de três endereços
            MontyPythonTACVisitor visitor = new MontyPythonTACVisitor();

            // Visita a árvore sintática e gera o código de três endereços
            visitor.visit(tree);

            // Obtem a lista que contem o código de três endereços
            List<String> tac = visitor.getTAC();

            // Mostra o código de três endereços
            System.out.println("Three-address code:");
            for (String code : tac) {
                System.out.println(code);
            }

            // Optimiza o código de três endereços
            Optimizer optimizer = new Optimizer();
            List<String> optimizedTac = optimizer.optimize(tac);

            // Imprime o código de três endereços optimizado
            System.out.println("\nOptimized TAC:");
            for (String code : optimizedTac) {
                System.out.println(code);
            }

            // Gera o código intermédio do LLVM a partir do código TAC optimizado
            MontyPythonTACtoLLVM converterToLLVM = new MontyPythonTACtoLLVM(fileName);
            converterToLLVM.convert(optimizedTac);
            converterToLLVM.saveToFile();

            // Imprime o código em LLVM
            System.out.println("\n\n ------ Código LLVM --------");
            System.out.println(converterToLLVM.getLLVMIR());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Lê o conteúdo do ficheiro a ser analisado e deixa-o numa string
     *
     * @param fileName String que contem o nome do  ficheiro
     * @return String com todo o conteúdo do ficheiro
     * @throws IOException
     */
    private static String readFileToString(String fileName) throws IOException {
        System.out.println("Reading file: " + Paths.get(fileName));
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    /**
     * Mostra a parse tree numa JFrame
     * @param tree A árvore a ser mostrada
     * @param parser O parser usado para gerar a árvore
     */
    private static void displayParseTree(ParseTree tree, Parser parser) {
        // Cria uma JFrame para mostrar o TreeViewer
        JFrame frame = new JFrame("Parse Tree");
        JPanel panel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(panel);

        // Cria o TreeViewer
        TreeViewer treeViewer = new TreeViewer(Arrays.asList(parser.getRuleNames()), tree);
        treeViewer.setScale(1.5); // Scale a little

        // Adiciona o TreeViewer ao painel
        panel.add(treeViewer);

        // Configura a frame que vai ser mostrada no ecra
        frame.add(scrollPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

}
