/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.ast.instrucciones;

import compilador.ast.base.Bloque;
import compilador.ast.base.Nodo;

import java.util.ArrayList;
import java.util.List;

public abstract class Sentencia extends Nodo{

    public Sentencia() {
    }
    
    public Sentencia(String nombre) {
        super(nombre);
    }

    public Bloque toBloque() {
        if(this.getClass() == Bloque.class){
            return (Bloque) this;
        } else {
            List<Sentencia> sentencias = new ArrayList<>();
            sentencias.add(this);
            return new Bloque(sentencias,"BLOQUE");
        }
    }
}
