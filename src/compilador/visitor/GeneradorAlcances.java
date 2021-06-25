/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.visitor;

import compilador.ast.base.*;
import compilador.ast.instrucciones.*;
import compilador.ast.operaciones.binarias.OperacionBinaria;

import javax.print.attribute.standard.Fidelity;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// no retorna nada
// clase que se encargara de recorrer el AST y setear los valores de alcance correspondientes
public class GeneradorAlcances extends Visitor<Void> {

    private Alcance alcance_actual; //alcance actual de un bloque determinado
    private Alcance alcance_global; //alcance al que todos pueden acceder

    public GeneradorAlcances(Alcance alcance_global) {
        this.alcance_global = alcance_global;
    }

    public Alcance getAlcance_global() {
        return alcance_global;
    }

    // dispara toda la generacion de alcances del AST
    public void procesar(Programa programa) throws ExcepcionDeAlcance{
        alcance_actual = alcance_global;
        this.visit(programa);   // como aca no hay visit(programa) usa de la superclase
    }

    //mejor ahora, no?
    @Override
    public Void visit(Bloque bloque) throws ExcepcionDeAlcance {
        if(bloque.getNombre().equals("DECLARACIONES")){
            super.visit(bloque);
        } else {
            alcance_actual = new Alcance(bloque.getNombre(), alcance_actual);
            bloque.setAlcance(alcance_actual);
            super.visit(bloque);
            alcance_actual = alcance_actual.getPadre();
        }
        return null;
    }

    @Override
    public Void visit(DeclaracionVariable dv) throws ExcepcionDeAlcance{
        if(alcance_actual != alcance_global){
            Object result = this.agregarSimbolo(dv.getId().getNombre(), dv);
            if(result!=null){   //repetido
                throw new ExcepcionDeAlcance(String.format("El nombre de la variable %1$s de tipo %2$s fue utilizado previamente\"]\n",
                        dv.getId().getNombre(), dv.getTipo() ));
            }
            super.visit(dv);
        }
        return null;
    }

    @Override
    public Void visit(Identificador identificador) throws ExcepcionDeAlcance {
        if(this.alcance_actual.resolver(identificador.getNombre()) == null){
            throw new ExcepcionDeAlcance(String.format("%1$s NO esta declarado previamente\"]\n",identificador.getNombre()));
        }
        return null;
    }

    @Override
    public Void visit(DeclaracionFuncion declaracionFuncion) throws ExcepcionDeAlcance{
        alcance_actual = new Alcance("BLOQUE_FUNCION",alcance_actual);
        declaracionFuncion.setAlcance(alcance_actual); //asi no renegamos con los parametros
        if(!declaracionFuncion.getParametros().isEmpty()){
            for (Parametro parametro:declaracionFuncion.getParametros()){
                this.visit(parametro);
            }
        }
        this.visit(declaracionFuncion.getBloque());
        alcance_actual = alcance_actual.getPadre();
        return null;
    }

    @Override
    public Void visit(Parametro parametro) throws ExcepcionDeAlcance {
        Object resultP = this.agregarSimbolo(parametro.getIdentificador().getNombre(), parametro);
        if(resultP!=null){   //repetido
            throw new ExcepcionDeAlcance(String.format("El nombre del parametro %1$s de tipo %2$s fue utilizado previamente\"]\n",
                    parametro.getIdentificador().getNombre(), parametro.getTipo()));
        }
        return null;
    }

    // agregarSimbolo(nombre variable, declaracion)
    private Object agregarSimbolo(String nombre, Object s) throws ExcepcionDeAlcance {
        if(alcance_actual.resolver(nombre) != null){    //retorna el repetido, si no esta -> null
            throw new ExcepcionDeAlcance(String.format("El nombre de %2$s %1$s fue utilizado previamente\"]\n",nombre,s.getClass().getSimpleName()));
        }
        return this.alcance_actual.putIfAbsent(nombre, s);  //retorna lo que habia previamente, si no habia nada tira null
    }

    @Override
    protected Void procesarPrograma(Programa programa, Void declaraciones, Void sentencias) {
        return null;
    }

    @Override
    protected Void procesarDeclaracionFuncion(DeclaracionFuncion declaracionFuncion, Void identificador, Void bloque) {
        return null;
    }

    @Override
    protected Void procesarWhenIs(WhenIs whenIs,Void simboloCpm, Void expresion, Void bloque) {
        return null;
    }

    @Override
    protected Void procesarWhen(When when, Void expresion, List<Void> whenIs, Void bloque) {
        return null;
    }

    @Override
    protected Void procesarWhen(When when, Void expresion, List<Void> whenIs) {
        return null;
    }

    @Override
    protected Void procesarDeclaracionFuncion(DeclaracionFuncion declaracionFuncion, Void identificador, List<Void> parametros, Void bloque) {
        return null;
    }

    @Override
    protected Void procesarDeclaracionVariable(DeclaracionVariable declaracionVariable, Void identificador, Void expresion) {
        return null;
    }

    @Override
    protected Void procesarInvocacionFuncion(InvocacionFuncion invocacionFuncion, Void identificador, List<Void> parametros) {
        return null;
    }

    @Override
    protected Void procesarInvocacionFuncion(InvocacionFuncion invocacionFuncion, Void identificador) {
        return null;
    }

    @Override
    protected Void procesarWhile(While aWhile, Void expresion, Void bloqueWhile) {
        return null;
    }

    @Override
    protected Void procesarBloque(Bloque bloque, List<Void> sentencias) {
        return null;
    }

    @Override
    protected Void procesarOperacionBinaria(OperacionBinaria ob, Void ei, Void ed) {
        return null;
    }

    @Override
    protected Void procesarNodo(Nodo n) {
        return null;
    }

    @Override
    protected Void procesarAsignacion(Asignacion a, Void identificador, Void expresion) {
        return null;
    }

    @Override
    protected Void procesarIf(If anIf, Void expresion, Void bloqueThen) {
        return null;
    }

    @Override
    protected Void procesarIf(If anIf, Void expresion, Void bloqueThen, Void bloqueElse) {
        return null;
    }

    @Override
    protected Void procesarParametro(Parametro parametro, Void identificador, Void valor_defecto) {
        return null;
    }

    @Override
    protected Void procesarParametro(Parametro parametro, Void identificador) {
        return null;
    }

}