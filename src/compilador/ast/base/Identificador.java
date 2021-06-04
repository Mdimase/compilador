/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.ast.base;

import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

public class Identificador extends Expresion{

    //nombre es de nodo y tipo es de expresion
    
    public Identificador(String nombre){
        super(Tipo.UNKNOWN, nombre);
    }
    
    public Identificador(String nombre, Tipo tipo){
        super(tipo, nombre);
    }
    
    @Override
    public String getEtiqueta() {
        return String.format(String.format("Ident %s %s", this.getNombre(), this.getTipo()));
    }
    
    @Override
    public <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance {
        return v.visit(this);   //invoca el visit(identificador) en visitor o el visit(identificador) de cualquier subclase de Visitor, va a depender de <T>
    }

    @Override
    public Identificador accept_transfomer(Transformer t) throws ExcepcionDeTipos{
        return t.transform(this);
    }

}
