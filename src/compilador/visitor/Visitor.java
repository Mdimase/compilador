/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.visitor;

import compilador.ast.base.*;
import compilador.ast.instrucciones.*;
import compilador.ast.operaciones.binarias.OperacionBinaria;
import compilador.ast.operaciones.unarias.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Visitor<T> {

    private int iden=0;
    private boolean enFuncion;  //flags para controlar el return
    private boolean enBucle;    //flag para controlar el continue y el break

    protected boolean isEnFuncion() {
        return enFuncion;
    }

    protected void setEnFuncion(boolean enFuncion) {
        this.enFuncion = enFuncion;
    }

    protected boolean isEnBucle() {
        return enBucle;
    }

    protected void setEnBucle(boolean enBucle) {
        this.enBucle = enBucle;
    }

    protected int getID(){
        iden+=1;
        return iden;
    }

    public T visit(OperacionBinaria ob) throws ExcepcionDeAlcance{
        T ti = ob.getIzquierda().accept(this); //operando 1
        T td = ob.getDerecha().accept(this);    //operando 2
        return this.procesarOperacionBinaria(ob, ti, td);
    }

    public T visit(Constante c) {
        return procesarNodo(c);
    }

    public T visit(Asignacion a) throws ExcepcionDeAlcance{
        T identificador = a.getIdentificador().accept(this);
        T expresion = a.getExpresion().accept(this);
        return this.procesarAsignacion(a, identificador, expresion);
    }

    public T visit(Write w) throws ExcepcionDeAlcance{
        if(w.getExpresion() !=null ) {
            return w.getExpresion().accept(this);
        } else {
            return w.getMensaje().accept(this);
        }
    }

    public T visit(Mensaje mensaje){
        return procesarNodo(mensaje);
    }

    public T visit(Read read){
        return procesarNodo(read);
    }

    public T visit(Identificador i) throws ExcepcionDeAlcance {
        return procesarNodo(i); //identificador no tiene ningun nodo como atributo, tonce lo imprimo xq es un nodo hoja
    }

    public T visit(DeclaracionVariable dv) throws ExcepcionDeAlcance{
        T identificador = dv.getId().accept(this);
        if (dv.getExpresion() == null){
            return dv.getId().accept(this);
        } else{
            T expresion = dv.getExpresion().accept(this);
            return procesarDeclaracionVariable(dv,identificador,expresion);
        }
    }

    public T visit(Programa p) throws ExcepcionDeAlcance{
        if(p.getDeclaraciones() == null){
            return p.getCuerpo().accept(this);  //invoca el accept del bloque main
        } else {
            T declaraciones = p.getDeclaraciones().accept(this);
            T sentencias = p.getCuerpo().accept(this);
            return procesarPrograma(p,declaraciones,sentencias);
        }

    };

    public T visit(Bloque b) throws ExcepcionDeAlcance{
        List<T> result = new ArrayList<>();
        for (Sentencia sentencia : b.getSentencias()){  //para cada sentencia dentro del bloque
            result.add(sentencia.accept(this)); //invoco al accept de sentencia, es decir, de alguna de las clases que heredan de ella(xq sentencia es abstract)
        }
        return procesarBloque(b, result);
    }

    public T visit(OperacionUnaria ou) throws ExcepcionDeAlcance{
        return ou.getExpresion().accept(this);
    }

    public T visit(InvocacionFuncion invocacionFuncion) throws ExcepcionDeAlcance {
        T id = invocacionFuncion.getIdentificador().accept(this);
        if(invocacionFuncion.getParams().isEmpty()){
            return procesarInvocacionFuncion(invocacionFuncion,id);
        } else{
            List<T> parametros = new ArrayList<>();
            for (Expresion parametro : invocacionFuncion.getParams()){
                parametros.add(parametro.accept(this));
            }
            return procesarInvocacionFuncion(invocacionFuncion,id,parametros);
        }
    }

    public T visit(Return aReturn) throws ExcepcionDeAlcance {
        return aReturn.getExpresion().accept(this);
    }

    public T visit(Continue aContinue) throws ExcepcionDeAlcance {
        return procesarNodo(aContinue);
    }

    public T visit(Break aBreak) throws ExcepcionDeAlcance {
        return procesarNodo(aBreak);
    }

    public T visit(If anIf) throws ExcepcionDeAlcance {
        T exp = anIf.getCondicion().accept(this);   //accepto la expresion
        T bloqueThen = anIf.getBloqueThen().accept(this);
        if(anIf.getBloqueElse() != null) {  //si tiene bloque else
            T bloqueElse = anIf.getBloqueElse().accept(this);
            return procesarIf(anIf,exp,bloqueThen,bloqueElse);  //proceso con else
        } else {
            return procesarIf(anIf, exp, bloqueThen); //proceso sin else
        }
    }

    public T visit(While aWhile) throws ExcepcionDeAlcance {
        boolean aux  = this.isEnBucle();
        setEnBucle(true);
        T exp = aWhile.getCondicion().accept(this);
        T bloque = aWhile.getBloque().accept(this);
        setEnBucle(aux);
        return procesarWhile(aWhile, exp, bloque);
    }

    public T visit(DeclaracionFuncion declaracionFuncion) throws ExcepcionDeAlcance {
        setEnFuncion(true);
        T id = declaracionFuncion.getIdentificador().accept(this);
        if(declaracionFuncion.getParametros().isEmpty()){
            T bloque = declaracionFuncion.getBloque().accept(this);
            setEnFuncion(false);
            return procesarDeclaracionFuncion(declaracionFuncion,id,bloque);
        } else{
            ArrayList<T> parametros = new ArrayList<>();
            for (Parametro parametro : declaracionFuncion.getParametros()){
                parametros.add(parametro.accept(this));
            }
            T bloque = declaracionFuncion.getBloque().accept(this);
            setEnFuncion(false);
            return procesarDeclaracionFuncion(declaracionFuncion,id,parametros,bloque);
        }
    }

    public T visit(Parametro parametro) throws ExcepcionDeAlcance {
        T id = parametro.getIdentificador().accept(this);
        if(parametro.getValorDefecto() != null){
            T valor_defecto = parametro.getValorDefecto().accept(this);
            return procesarParametro(parametro,id,valor_defecto);
        }
        return procesarParametro(parametro,id);
    }

    public T visit(SimboloCmp simboloCmp){
        return procesarNodo(simboloCmp);
    }

    public T visit(WhenIs whenIs) throws ExcepcionDeAlcance {
        T simboloCmp = whenIs.getSimboloCmp().accept(this);
        T exp = whenIs.getExpresion().accept(this);
        T bloque = whenIs.getBloque().accept(this);
        return procesarWhenIs(whenIs,simboloCmp,exp,bloque);
    }

    public T visit(When when) throws ExcepcionDeAlcance {
        T exp = when.getExpresionBase().accept(this);
        ArrayList<T> list = new ArrayList<>();
        for(WhenIs whenIs: when.getWhenIs()){
            list.add(whenIs.accept(this));
        }
        if(when.getBloqueElse() != null){
            T bloque = when.getBloqueElse().accept(this);
            return procesarWhen(when,exp,list,bloque);
        } else {
            return procesarWhen(when,exp,list);
        }
    }

    protected abstract T procesarWhenIs (WhenIs whenIs,T simboloCpm, T expresion, T bloque);

    protected abstract T procesarWhen(When when,T expresion,List<T> whenIs,T bloque);

    protected abstract T procesarWhen(When when,T expresion,List<T> whenIs);

    protected abstract T procesarParametro(Parametro parametro, T identificador, T valor_defecto);

    protected abstract T procesarParametro(Parametro parametro, T identificador);

    protected abstract T procesarPrograma (Programa programa,T declaraciones,T sentencias);

    protected abstract T procesarDeclaracionFuncion(DeclaracionFuncion declaracionFuncion,T identificador, T bloque);

    protected abstract T procesarDeclaracionFuncion(DeclaracionFuncion declaracionFuncion,T identificador,List<T> parametros, T bloque);

    protected abstract T procesarDeclaracionVariable(DeclaracionVariable declaracionVariable,T identificador,T expresion);

    protected abstract T procesarInvocacionFuncion(InvocacionFuncion invocacionFuncion, T identificador, List<T> parametros);

    protected abstract T procesarInvocacionFuncion(InvocacionFuncion invocacionFuncion, T identificador);

    protected abstract T procesarWhile(While aWhile, T expresion, T bloqueWhile);

    protected abstract T procesarBloque(Bloque bloque, List<T> sentencias);

    protected abstract T procesarOperacionBinaria(OperacionBinaria ob, T ei, T ed);

    protected abstract T procesarNodo(Nodo n);

    protected abstract T procesarAsignacion(Asignacion a, T identificador, T expresion);

    protected abstract T procesarIf(If anIf,T expresion, T bloqueThen);

    protected abstract T procesarIf(If anIf, T expresion, T bloqueThen, T bloqueElse);

}