package pt.uab.compilacao;

import java.nio.file.Paths;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Conversor de TAC (Three Address Code) para a representação
 * intermédia do LLVM (https://llvm.org/docs/Reference.html#llvm-ir)
 * 
 * A vantagem do LLVM IR sobre a geração directa de Assembler é a
 * existência de ferramentas diversas que permitem gerar o assembler
 * especifico de uma arquitectura (x86, ARM ou RISC V)
 */
public class MontyPythonTACtoLLVM {
    private StringBuilder llvmIR;
    private int tempCounter;
    private String filename;

    /**
     * Operações do Trhree Address Code, reconhecidas
     */
    private static final Map<String, String> tacOperations = new HashMap<>();
    static {
        tacOperations.put("+", "add");
        tacOperations.put("-", "sub");
        tacOperations.put("*", "mul");
        tacOperations.put("/", "sdiv");
        tacOperations.put("==", "icmp eq");
        tacOperations.put("!=", "icmp ne");
        tacOperations.put("<", "icmp slt");
        tacOperations.put("<=", "icmp sle");
        tacOperations.put(">", "icmp sgt");
        tacOperations.put(">=", "icmp sge");
        tacOperations.put("&&", "and");
        tacOperations.put("||", "or");
    }

    public MontyPythonTACtoLLVM(String _filename) {
        this.llvmIR = new StringBuilder();
        this.filename = _filename;
        this.tempCounter = 0;
    }

    /**
     * Converte uma lista de instruçlões de Three address code em
     * LLVM IR
     */
    public void convert(List<String> tacInstructions) {
        llvmIR.append("; ModuleID = '" + convertToLLVMExtension(this.filename) + "'\n");
        // Declara a existência de printf no systema que retorna um inteiro de 32 bits
        llvmIR.append("declare i32 @printf(i8*, ...)\n");
        llvmIR.append("@print.str = constant [3 x i8] c\"%d\\00\"\n\n");

        // Inicio da declaração da função main() que retorna um int de 32 bits
        llvmIR.append("define i32 @main() {\n");

        for (String tac : tacInstructions) {
            llvmIR.append(convertInstruction(tac)).append("\n");
            tempCounter++;
        }

        llvmIR.append("  ret i32 0\n");
        llvmIR.append("}\n");
    }

    /**
     * Obtem o código em LLVR IR (que fica numa string)
     */
    public String getLLVMIR() {
        return llvmIR.toString();
    }

    /**
     * Converte uma instrução em Three Address Code para
     * a representação intermédia LLVM
     * 
     * A conversão é efectuada, partinda cada instrução por espaços
     * e começando por perceber o que contém a primeira parte. 
     */
    private String convertInstruction(String tac) {
        String[] parts = tac.split(" ");
        String result = "";
        
        switch (parts[1]) {
            case "=":
                result = String.format("  %s = alloca i32, align 4\n", parts[0]);
                result += String.format("  store i32 %s, i32* %s, align 4", parts[2], parts[0]);
                break;
            case "+":
            case "-":
            case "*":
            case "/":
                result = String.format("  %%%d = %s i32 %s, %s", tempCounter, tacOperations.get(parts[1]), parts[2], parts[4]);
                break;
            case "==":
            case "!=":
            case "<":
            case "<=":
            case ">":
            case ">=":
                result = String.format("  %%%d = %s i32 %s, %s", tempCounter, tacOperations.get(parts[1]), parts[2], parts[4]);
                break;
            case "&&":
            case "||":
                result = String.format("  %%%d = %s i1 %s, %s", tempCounter, tacOperations.get(parts[1]), parts[2], parts[4]);
                break;
            case "goto":
                result = String.format("  br label %%%s", parts[2]);
                break;
            case "if":
                result = String.format("  br i1 %s, label %%%s, label %%%s", parts[2], parts[4], parts[6]);
                break;
            case "label":
                result = String.format("%s:", parts[2]);
                break;
            case "call":
                result = convertCall(parts);
                break;
            case "return":
                result = String.format("  ret i32 %s", parts[2]);
                break;
            case "print":
                result = String.format("  %%call%d = call i32 (i8*, ...) @printf(i8* getelementptr ([3 x i8], [3 x i8]* @print.str, i32 0, i32 0), i32 %s)", tempCounter, parts[2]);
                break;
            default:
                throw new IllegalArgumentException("Unsupported TAC operation: " + parts[1]);
        }
        
        return result;
    }

   /**
    * Converte uma instrução TAC que executa uma função
    */
    private String convertCall(String[] parts) {
        StringBuilder call = new StringBuilder();
        if (parts.length > 3) {  // function call with parameters
            call.append(String.format("  %%call%d = call i32 @%s(", tempCounter, parts[2]));
            for (int i = 3; i < parts.length; i++) {
                call.append("i32 ").append(parts[i]);
                if (i < parts.length - 1) {
                    call.append(", ");
                }
            }
            call.append(")");
        } else {  // function call without parameters
            call.append(String.format("  call void @%s()", parts[2]));
        }
        return call.toString();
    }

    /**
     * Converte a extensão de um nome de ficheiro
     * Provavelmente neste contexto teria .mp (Monty Python)
     * e converte para .ll (LLVM IR)
     * 
     * @param inputProgram String com nome de um ficheiro
     */
    public String convertToLLVMExtension(String inputProgram) {
        if (inputProgram == null || inputProgram.isEmpty()) {
            return "default_llvm.ll";
        }

        // Extrai o nome do arquivo do caminho
        String fileName = Paths.get(inputProgram).getFileName().toString();
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            // Se não houver ponto, adiciona .ll no final
            return fileName + ".ll";
        } else {
            // Se houver ponto, substitui a extensão existente por .ll
            return fileName.substring(0, lastDotIndex) + ".ll";
        }
    }

    public void saveToFile() {
        this.saveToFile(this.filename);
    }

    /**
     * Salva o código LLVM gerado num ficheiro
     * 
     * @param filename Nome onde o código LLVM será escrito
     */
    public void saveToFile(String filename) {
        try (FileWriter fileWriter = new FileWriter(convertToLLVMExtension(filename))) {
            fileWriter.write(getLLVMIR());
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

/* Main para eectuar testes autónomos, completamente independentes
   sem a integração com as outras fases
    public static void main(String[] args) {
        List<String> tacInstructions = List.of(
                "a = 10",
                "b = 20",
                "c = a + b",
                "d = a < b",
                "if d goto L1 else L2",
                "label L1",
                "print c",
                "goto L3",
                "label L2",
                "print b",
                "label L3",
                "return c"
        );

        MontyPythonTACtoLLVM converter = new MontyPythonTACtoLLVM("Test.mp");
        converter.convert(tacInstructions);
        System.out.println(converter.getLLVMIR());

        // Save the generated LLVM IR to a file
        converter.saveToFile("output.ll");
    }
    */
}
