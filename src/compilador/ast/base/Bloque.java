/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.ast.base;

import compilador.ast.instrucciones.Sentencia;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;
import java.util.List;

public class Bloque extends Sentencia{
    
    private Alcance alcance;    //alcance del bloque
    private List<Sentencia> sentencias;     //sentencias que estan dentro del bloque
    private boolean esProgramaPrincipal = false;    // me sirve para diferenciar si es el bloque main o de otra estructura como un if


    public Bloque(List<Sentencia> sentencias, String nombre) {
        super(nombre);
        this.sentencias = sentencias;
    }
    
    public Bloque(List<Sentencia> sentencias, String nombre, Alcance alcance) {
        super(nombre);
        this.alcance = alcance;
        this.sentencias = sentencias;
    }
    
    public Bloque(List<Sentencia> sentencias, String nombre, boolean esProgramaPrincipal) {
        super(nombre);
        this.esProgramaPrincipal = esProgramaPrincipal;
        this.sentencias = sentencias;
    }

    public Bloque(List<Sentencia> sentencias, boolean esProgramaPrincipal) {
        this.esProgramaPrincipal = esProgramaPrincipal;
        this.sentencias = sentencias;
    }
    
    public Bloque(List<Sentencia> sentencias, String nombre, boolean esProgramaPrincipal, Alcance alcance) {
        super(nombre);
        this.alcance = alcance;
        this.esProgramaPrincipal = esProgramaPrincipal;
        this.sentencias = sentencias;
    }

    public Alcance getAlcance() {
        return alcance;
    }

    public void setAlcance(Alcance alcance) {
        this.alcance = alcance;
    }

    public boolean esProgramaPrincipal() {
        return esProgramaPrincipal;
    }

    public void setEsProgramaPrincipal(boolean esProgramaPrincipal) {
        this.esProgramaPrincipal = esProgramaPrincipal;
    }

    public List<Sentencia> getSentencias() {
        return sentencias;
    }

    public void setSentencias(List<Sentencia> sentencias) {
        this.sentencias = sentencias;
    }

    @Override
    public Bloque toBloque(){
        return this;
    }

    @Override
    public <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance{
        return v.visit(this);   //invoca el visit(bloque) de visitor o el visit(bloque) de cualquier subclase de Visitor, va a depender de <T>
    }

    @Override
    public Bloque accept_transfomer(Transformer t) throws ExcepcionDeTipos{
        return t.transform(this);
    }
    
}
