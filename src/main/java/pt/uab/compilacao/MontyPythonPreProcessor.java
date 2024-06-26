package pt.uab.compilacao;

import java.io.*;
import java.util.Stack;

/**
 * O pré-processador lê um ficheiro caracter a carater e compara a quantidade
 * de espaços de cada linha.
 *
 * Sempre que aumenta em relação à linha anterior, adiciona o caracter {
 * Quando diminui, adiciona o caracyter }
 */
public class MontyPythonPreProcessor {

    public static StringBuffer preprocess(String inputFile) throws IOException {
        StringBuffer outputBuffer = new StringBuffer();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            Stack<Integer> indentStack = new Stack<>();
            indentStack.push(0); // Nível inicial de indentação

            while ((line = reader.readLine()) != null) {
                int currentIndent = countLeadingSpaces(line);

                // Remove os espaços no inicio e fim da linha corrente
                String trimmedLine = line.trim();

                if (!trimmedLine.isEmpty()) {
                    // Compara a indentação corrente com a anterior
                    int previousIndent = indentStack.peek();

                    if (currentIndent > previousIndent) {
                        outputBuffer.append("{\n");
                        indentStack.push(currentIndent);
                    } else {
                        while (currentIndent < indentStack.peek()) {
                            outputBuffer.append("}\n");
                            indentStack.pop();
                        }
                    }

                    outputBuffer.append(line).append("\n");
                }
            }

            // Fecha algum ainda existente bloco não encerrado
            while (indentStack.size() > 1) {
                outputBuffer.append("}\n");
                indentStack.pop();
            }
        }

        return outputBuffer;
    }

    /**
     * Conta o numero de caracteres brancos no inicio da linha
     *
     * @param line String que contem uma linha
     * @return Numero de espaços no inicio da linha
     */
    private static int countLeadingSpaces(String line) {
        int count = 0;
        for (char c : line.toCharArray()) {
            if (c == ' ') {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    /**
     * Main para testes autónomos
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java MontyPythonPreProcessor <inputFile>");
            return;
        }

        String inputFile = args[0];

        try {
            StringBuffer output = preprocess(inputFile);
            System.out.println(output.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

