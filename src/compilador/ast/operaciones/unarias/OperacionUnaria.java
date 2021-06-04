/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.ast.operaciones.unarias;

import compilador.ast.base.Expresion;
import compilador.ast.base.Tipo;

public abstract class  OperacionUnaria extends Expresion{
    private Expresion expresion;

    public OperacionUnaria(Expresion expresion) {
        super(Tipo.UNKNOWN);
        this.expresion = expresion;
    }
    
    public OperacionUnaria(String nombre, Expresion expresion) {
        super(Tipo.UNKNOWN, nombre);
        this.expresion = expresion;
    }

    public OperacionUnaria(Expresion expresion, Tipo tipo) {
        super(tipo);
        this.expresion = expresion;
    }
    
    public OperacionUnaria(String nombre, Expresion expresion, Tipo tipo) {
        super(tipo, nombre);
        this.expresion = expresion;
    }

    public Expresion getExpresion() {
        return expresion;
    }

    public void setExpresion(Expresion expresion) {
        this.expresion = expresion;
    }

}
