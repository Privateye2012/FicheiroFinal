package pt.uab.compilacao;


import java.util.*;

/**
 * Optimizador de Código de Três Endereços
 *
 * A partir de uma lista de instruções TAC, procede à sua optimização
 * gerando uma lista de instruções optimizadas.
 *
 * As optimizações efectuadas foram:
 *   - Dobragem de constantes
 *   - Propagação de constantes
 *   - Eliminação de código morto
 *
 */
public class Optimizer {
    public List<String> optimize(List<String> tac) {
        List<String> optimizedTac = new ArrayList<>();
        Map<String, String> constants = new HashMap<>();
        Set<String> usedVariables = new HashSet<>();

        // Primeiro passo: Propagação e dobragem de constantes
        for (String line : tac) {
            String[] parts = line.split(" = ");
            if (parts.length == 2) {
                String left = parts[0].trim();
                String right = parts[1].trim();

                // Dobragem de Constantes
                if (right.matches("\\d+ \\+ \\d+")) {
                    String[] operands = right.split(" \\+ ");
                    int result = Integer.parseInt(operands[0]) + Integer.parseInt(operands[1]);
                    right = String.valueOf(result);
                }

                // Propagação de constantes
                if (right.matches("\\d+")) {
                    constants.put(left, right);
                } else if (constants.containsKey(right)) {
                    right = constants.get(right);
                }

                optimizedTac.add(left + " = " + right);
            } else {
                optimizedTac.add(line);
            }
        }

        // Segundo passo: Eliminação de código morto
        List<String> finalTac = new ArrayList<>();
        for (String line : optimizedTac) {
            String[] parts = line.split(" = ");
            if (parts.length == 2) {
                String left = parts[0].trim();
                usedVariables.add(left);
                String right = parts[1].trim();

                for (String variable : right.split(" ")) {
                    if (!variable.matches("\\d+") && !variable.matches("[\\+\\-\\*/]")) {
                        usedVariables.add(variable);
                    }
                }
            } else {
                finalTac.add(line);
            }
        }

        for (String line : optimizedTac) {
            String[] parts = line.split(" = ");
            if (parts.length == 2) {
                String left = parts[0].trim();
                if (usedVariables.contains(left)) {
                    finalTac.add(line);
                }
            } else {
                finalTac.add(line);
            }
        }

        return finalTac;
    }
}
