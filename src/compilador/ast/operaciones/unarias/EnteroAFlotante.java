/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.ast.operaciones.unarias;

import compilador.ast.base.ExcepcionDeAlcance;
import compilador.ast.base.ExcepcionDeTipos;
import compilador.ast.base.Expresion;
import compilador.ast.base.Tipo;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

/**
 *
 * @author ITT
 */
public class EnteroAFlotante extends OperacionConversion{
    
    
    public EnteroAFlotante(Expresion expresion) {
        super("entero a flotante", expresion, Tipo.FLOAT);
    }
    
    @Override
    public <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance {
        return v.visit(this);
    }

    /*
    @Override
    public EnteroAFlotante accept_transfomer(Transformer t) throws ExcepcionDeTipos{
        return t.transform(this);
    }

     */
}
