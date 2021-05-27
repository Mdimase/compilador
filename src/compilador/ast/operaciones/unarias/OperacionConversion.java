/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.ast.operaciones.unarias;

import compilador.ast.base.Expresion;
import compilador.ast.base.Tipo;

/**
 *
 * @author ITT
 */
public abstract class OperacionConversion extends OperacionUnaria{
    
    public OperacionConversion(Expresion expresion) {
        super(expresion);
    }
    
    public OperacionConversion(String nombre, Expresion expresion) {
        super(nombre, expresion);
    }

    public OperacionConversion(Expresion expresion, Tipo tipo) {
        super(expresion, tipo);
    }
    
    public OperacionConversion(String nombre, Expresion expresion, Tipo tipo) {
        super(nombre, expresion, tipo);
    }
    
}
