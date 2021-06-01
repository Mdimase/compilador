package compilador;

import compilador.ast.base.Programa;
import compilador.lexico.Lexico;
import compilador.sintactico.Sintactico;
import compilador.visitor.ASTGraphviz;
import compilador.visitor.GeneradorAlcanceGlobal;
import compilador.visitor.GeneradorAlcances;
import compilador.visitor.ValidadorTipos;

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
            GeneradorAlcanceGlobal gb = new GeneradorAlcanceGlobal();
            gb.procesar(programa);
            GeneradorAlcances ga = new GeneradorAlcances(gb.getAlcance_global());
            ga.procesar(programa);
            System.out.println("Alcances procesados");
            ValidadorTipos vt = new ValidadorTipos();
            vt.procesar(programa);
            pw = new PrintWriter(new FileWriter("arbol_tp.dot"));
            pw.println(graficador.visit(vt.procesar(programa)));
            pw.close();
            cmd = "dot -Tpng arbol_tp.dot -o arbol_tp.png";
            Runtime.getRuntime().exec(cmd);
            System.out.println("Tipos validados");
        } catch(Exception e){
            System.out.println(e);
        }    
    }
}
