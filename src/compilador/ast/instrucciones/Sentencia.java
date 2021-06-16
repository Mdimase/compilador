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
        List<Sentencia> sentencias = new ArrayList<>();
        if(this.getClass() == Bloque.class){
            sentencias = ((Bloque) this).getSentencias();
        } else {
            sentencias.add(this);
        }
        return new Bloque(sentencias,"BLOQUE");
    }
}
