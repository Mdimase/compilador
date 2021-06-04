/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.ast.instrucciones;

import compilador.ast.base.*;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

public class DeclaracionVariable extends Declaracion{
    
    private Identificador id;   //identificador que estoy creando (seria con nombre solo aca, xq el tipo lo agrego abajo)
    private Tipo tipo;      //tipo de dato, util para hacer el chequeo despues
    private Expresion expresion;
    
    public DeclaracionVariable(Identificador id, Tipo tipo){
        this.id = id;
        this.tipo = tipo;
        this.setDeafult(tipo);
    }

    public DeclaracionVariable(Identificador id, Tipo tipo,Expresion expresion){
        this.id = id;
        this.tipo = tipo;
        this.expresion=expresion;
    }

    public Identificador getId() {
        return id;
    }

    public void setId(Identificador id) {
        this.id = id;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Expresion getExpresion() {
        return expresion;
    }

    public void setExpresion(Expresion expresion) {
        this.expresion = expresion;
    }

    private void setDeafult(Tipo tipo){
        if(tipo == Tipo.BOOL){
            this.setExpresion(new Constante("false",Tipo.BOOL));
        }
        if (tipo == Tipo.FLOAT){
            this.setExpresion(new Constante("0.0",Tipo.FLOAT));
        }
        if (tipo == Tipo.INTEGER){
            this.setExpresion(new Constante("0",Tipo.INTEGER));
        }
    }

    @Override
    public <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance{
        return v.visit(this);   //invoca el visit(declaracionVariable) de visitor o el visit(declaracionVariable) de cualquier subclase de Visitor, va a depender de <T>
    }


    @Override
    public DeclaracionVariable accept_transfomer(Transformer t) throws ExcepcionDeTipos {
        return t.transform(this);
    }

}
