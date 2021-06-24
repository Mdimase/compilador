package compilador;

import compilador.ast.base.Programa;
import compilador.lexico.Lexico;
import compilador.sintactico.Sintactico;
import compilador.visitor.*;

import java.io.*;

public class Main {

    public static void main(String[] args) throws Exception {
        FileReader entrada = new FileReader("./entrada.txt");
        Lexico lexico = new Lexico(entrada);
        Sintactico sintactico= new Sintactico(lexico);
        Programa programa = (Programa) sintactico.parse().value;    //raiz del AST
        try{
            PrintWriter pw = new PrintWriter(new FileWriter("arbol.dot"));
            ASTGraphviz graficador = new ASTGraphviz(); //instancio el concrete visitor para graficar
            pw.println(graficador.visit(programa)); //empiezo a visitar desde el raiz con mi graficador
            pw.close();
            String cmd = "dot -Tpng arbol.dot -o arbol.png";        //comando consola
            Runtime.getRuntime().exec(cmd); //genero archivos dot y png

            //alcance global
            GeneradorAlcanceGlobal gb = new GeneradorAlcanceGlobal();
            gb.procesar(programa);
            System.out.println("Alcance global procesado");

            //alcances locales
            GeneradorAlcances ga = new GeneradorAlcances(gb.getAlcance_global());
            ga.procesar(programa);
            System.out.println("Alcances procesados");

            //control de sentencias return,break y continue que esten en lugares correctos
            Control control = new Control();
            control.procesar(programa);

            //validador de tipos
            ValidadorTipos vt = new ValidadorTipos();
            vt.procesar(programa);
            System.out.println("Tipos validados");

            // reescritura de When + constantFolding
            Rewriter rw = new Rewriter();
            rw.procesar(programa);
            System.out.println("Codigo transformado");

            //graficar nuevamente
            pw = new PrintWriter(new FileWriter("arbol_tp.dot"));
            pw.println(graficador.visit(programa));
            pw.close();
            cmd = "dot -Tpng arbol_tp.dot -o arbol_tp.png";
            Runtime.getRuntime().exec(cmd);

            //generar codigo IR para el LLVM
            GeneradorCodigo generadorCodigo = new GeneradorCodigo(ga.getAlcance_global());
            pw = new PrintWriter(new FileWriter("programa.ll"));
            pw.println(generadorCodigo.procesar(programa, "programa.ll"));
            pw.close();
            System.out.println("Código generado");

            Process process = Runtime.getRuntime().exec("clang -c -o programa.o programa.ll");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            Process process2 = Runtime.getRuntime().exec("clang -o programa.exe programa.o scanf.o");
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(process2.getInputStream()));
            String line2;
            while ((line2 = reader2.readLine()) != null) {
                System.out.println(line2);
            }
            System.out.println("Ejecutable generado");

            //System.out.println(generadorCodigo.procesar(programa,"Programa.ll"));   //muestra por consola
        } catch(Exception e){
            System.out.println(e);
        }    
    }
}
