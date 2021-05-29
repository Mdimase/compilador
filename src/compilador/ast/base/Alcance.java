/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.ast.base;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ITT
 */
public class Alcance extends HashMap{

    // diccionario
    // ej clave:valor -> Variable : declaracionVariable
    private String nombre;  //nombre del alcance. si es de una funcion pepe() -> alcance pepe()
    private Alcance padre;  // alcance padre que lo engloba. ej si esta es el alcance de un for, su padre puede ser programa u otro for si estan anidados

    public Alcance(String nombre, Alcance padre) {
        this.nombre = nombre;
        this.padre = padre;
    }

    public Alcance(String nombre) {
        this.nombre = nombre;
        this.padre = null;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Alcance getPadre() {
        return padre;
    }

    public void setPadre(Alcance padre) {
        this.padre = padre;
    }

    //revisar
    // posible uso para cuando tengamos que buscar un nombre en un alcance, busca primero en actual sino en su padre hasta llegar a null
    //retorna objeto encontrado o null si no lo encuentra
    public Object resolver(String name){
        Alcance alcance_actual = this;
        Object elemento = null;
        while(alcance_actual != null){
            elemento = alcance_actual.get(name);
            if(elemento != null){
                return elemento;
            }
            alcance_actual = alcance_actual.getPadre();
        }
        return elemento;
    }
    
}
