/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.ast.instrucciones;

import compilador.ast.base.Nodo;

/**
 *
 * @author ITT
 */
public abstract class Sentencia extends Nodo{

    public Sentencia() {
    }
    
    public Sentencia(String nombre) {
        super(nombre);
    }

}
