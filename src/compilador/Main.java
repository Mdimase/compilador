package compilador;

import compilador.ast.base.Programa;
import compilador.lexico.Lexico;
import compilador.sintactico.Sintactico;
import compilador.visitor.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

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
            System.out.println(generadorCodigo.procesar(programa,"Programa.ll"));

        } catch(Exception e){
            System.out.println(e);
        }    
    }
}
