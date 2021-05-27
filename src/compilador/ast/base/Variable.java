/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.ast.base;

import compilador.ast.instrucciones.DeclaracionVariable;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

/**
 *
 * @author ITT
 */
public class Variable extends Expresion{
    
    private DeclaracionVariable declaracion;
    
    public Variable(DeclaracionVariable declaracion){
        this.declaracion = declaracion;
    }

    public DeclaracionVariable getDeclaracion() {
        return declaracion;
    }

    public void setDeclaracion(DeclaracionVariable declaracion) {
        this.declaracion = declaracion;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this); //invoca el visit(variable) de visitor o el visit(variable) de cualquier subclase de Visitor, va a depender de <T>
    }

    /*
    @Override
    public Variable accept_transfomer(Transformer t) {
        return t.transform(this);
    }
    */
}
