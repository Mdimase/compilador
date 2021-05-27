/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.ast.instrucciones;

import compilador.ast.base.ExcepcionDeAlcance;
import compilador.ast.base.ExcepcionDeTipos;
import compilador.ast.base.Expresion;
import compilador.ast.base.Identificador;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

/**
 *
 * @author ITT
 */
public class Asignacion extends Sentencia{
    
    private Identificador identificador;    //nombre y tipo de la variable a la que le quiero asignar una expresion
    private Expresion expresion;    //expresion a asignar
    
    public Asignacion(Identificador id, Expresion e){
        this.identificador = id;
        this.expresion = e;
    }

    public void setIdentificador(Identificador identificador) {
        this.identificador = identificador;
    }

    public void setExpresion(Expresion expresion) {
        this.expresion = expresion;
    }
    
    public Identificador getIdentificador(){
        return identificador;
    }
    
    public Expresion getExpresion(){
        return expresion;
    }
    
    protected String getNombreOperacion() {
        return "=";
    }
    
    @Override
    public String getEtiqueta() {
        return String.format("%s", this.getNombreOperacion());
    }
    
    @Override
    public <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance{
        return v.visit(this);   //invoca el visit(asignacion) de visitor o el visit(asignacion) de cualquier subclase de Visitor, va a depender de <T>
    }

    /*
    @Override
    public Asignacion accept_transfomer(Transformer t) throws ExcepcionDeTipos{
        return t.transform(this);
    }
    */
}
