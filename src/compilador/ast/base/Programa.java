    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.ast.base;

import compilador.ast.instrucciones.Declaracion;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

    /**
 *
 * @author ITT
 */
// raiz del AST
public class Programa extends Nodo{
    private Bloque declaraciones; //declaraciones de funciones y variables
    private Bloque cuerpo;  //bloque_main
    
    public Programa(Bloque cuerpo) {
        this.cuerpo = cuerpo;
    }

        public Programa(Bloque declaraciones, Bloque cuerpo) {
            this.declaraciones = declaraciones;
            this.cuerpo = cuerpo;
        }

        public Bloque getDeclaraciones() {
            return declaraciones;
        }

        public void setDeclaraciones(Bloque declaraciones) {
            this.declaraciones = declaraciones;
        }

        public Bloque getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(Bloque cuerpo) {
        this.cuerpo = cuerpo;
    }
    
    @Override
    public <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance{
        return v.visit(this);
    }

    /*
    @Override
    public Programa accept_transfomer(Transformer t) throws ExcepcionDeTipos{
        return t.transform(this);
    }
    */
}
