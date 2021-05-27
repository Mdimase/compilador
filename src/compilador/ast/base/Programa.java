    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.ast.base;

import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

/**
 *
 * @author ITT
 */
// raiz del AST
public class Programa extends Nodo{

    // bloque main
    private Bloque cuerpo;
    
    public Programa(Bloque cuerpo) {
        this.cuerpo = cuerpo;
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
