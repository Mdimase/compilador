/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.visitor;

import compilador.ast.base.*;
import compilador.ast.instrucciones.*;
import compilador.ast.operaciones.binarias.OperacionBinaria;
import compilador.ast.operaciones.unarias.OperacionUnaria;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author ITT
 * @param <T>
 */
public abstract class Visitor<T> {
    
    private int iden=0;
    
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
            return procesarNodo(w);
        }
    }
    
    public T visit(Identificador i) {
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

    //hasta aca bien

    public T visit(InvocacionFuncion invocacionFuncion) throws ExcepcionDeAlcance {
        T id = invocacionFuncion.getIdentificador().accept(this);
        if(invocacionFuncion.getParams().isEmpty()){
            return procesarInvocacionFuncion(invocacionFuncion,id);
        } else{
            List<T> parametros = new ArrayList<>();
            for (Expresion parametro : invocacionFuncion.getParams()){
                parametros.add(parametro.accept(this)); //acepto cada una de las sentencias que estan en el bloque if
            }
            return procesarInvocacionFuncion(invocacionFuncion,id,parametros);
        }
    }

    public T visit(Return aReturn) throws ExcepcionDeAlcance {
        return aReturn.getExpresion().accept(this);
    }

    public T visit(Continue aContinue){
        return procesarNodo(aContinue);
    }

    public T visit(Break aBreak){
        return procesarNodo(aBreak);
    }

    public T visit(If anIf) throws ExcepcionDeAlcance {
        T exp = anIf.getCondicion().accept(this);   //accepto la expresion
        List<T> sentenciasThen = new ArrayList<>();
        for (Sentencia sentencia : anIf.getBloqueThen().getSentencias()){
            sentenciasThen.add(sentencia.accept(this)); //acepto cada una de las sentencias que estan en el bloque if
        }
        if(anIf.getBloqueElse() != null) {  //si tiene bloque else
            List<T> sentenciasElse = new ArrayList<>();
            for (Sentencia sentencia : anIf.getBloqueElse().getSentencias()) {
                sentenciasThen.add(sentencia.accept(this)); //acepto cada una de las sentencias que estan en el bloque else
            }
            return procesarIf(anIf,exp,sentenciasThen,sentenciasElse);  //proceso con else
        } else {
            return procesarIf(anIf, exp, sentenciasThen); //proceso sin else
        }
    }

    // QUEDO SIN USO, POR LA TRANSFORMACION DE UN FOR A WHILE EN EL PARSING
    public T visit(For aFor) throws ExcepcionDeAlcance {
        T id = aFor.getIdentificador().accept(this);
        T from = aFor.getFrom().accept(this);
        T to = aFor.getTo().accept(this);
        T by = aFor.getBy().accept(this);
        List<T> bloque = new ArrayList<>();
        for (Sentencia sentencia : aFor.getBloque().getSentencias()){  //para cada sentencia dentro del bloque
            bloque.add(sentencia.accept(this)); //invoco al accept de sentencia, es decir, de alguna de las clases que heredan de ella(xq sentencia es abstract)
        }
        return procesarFor(aFor,id,bloque,from,to,by);
        }


    public T visit(While aWhile) throws ExcepcionDeAlcance {
        T exp = aWhile.getCondicion().accept(this);
        /*
        if (aWhile.getBloque().getSentencias().get(0).getClass() == Bloque.class) {
            aWhile.getBloque().getSentencias().get(0).setNombre("BLOQUE_WHILE");    //asigno nombre al bloque while
            List<T> bloque = new ArrayList<>();
            for (Sentencia sentencia : aWhile.getBloque().getSentencias()) {  //para cada sentencia dentro del bloque
                bloque.add(sentencia.accept(this)); //invoco al accept de sentencia, es decir, de alguna de las clases que heredan de ella(xq sentencia es abstract)
            }
            return procesarWhile(aWhile, exp, bloque);
        } else {    // si tiene una sola linea adentro el while, primero imprimo BLOQUE_WHILE
            T bloqueWhile = aWhile.getBloque().accept(this);
            return procesarWhile(aWhile,exp,bloqueWhile);
        }
         */
        List<T> bloque = new ArrayList<>();
        for (Sentencia sentencia : aWhile.getBloque().getSentencias()) {  //para cada sentencia dentro del bloque
            bloque.add(sentencia.accept(this)); //invoco al accept de sentencia, es decir, de alguna de las clases que heredan de ella(xq sentencia es abstract)
        }
        return procesarWhile(aWhile, exp, bloque);
    }

    public T visit(DeclaracionFuncion declaracionFuncion) throws ExcepcionDeAlcance {
        T id = declaracionFuncion.getIdentificador().accept(this);
        List<T> bloque = new ArrayList<>();
        for (Sentencia sentencia : declaracionFuncion.getBloque().getSentencias()){  //para cada sentencia dentro del bloque
            bloque.add(sentencia.accept(this)); //invoco al accept de sentencia, es decir, de alguna de las clases que heredan de ella(xq sentencia es abstract)
        }
        if(declaracionFuncion.getParametros().isEmpty()){
            return procesarDeclaracionFuncion(declaracionFuncion,id,bloque);
        } else{
            List<T> parametros = new ArrayList<>();
            List<Parametro> list= declaracionFuncion.getParametros();
            Collections.reverse(list);
            for (Parametro parametro : list){
                if(parametro.getValorDefecto() != null){
                    parametros.add(parametro.getIdentificador().accept(this));
                    parametros.add(parametro.getValorDefecto().accept(this));
                }else{
                    parametros.add(parametro.getIdentificador().accept(this));
                }
            }
            return procesarDeclaracionFuncion(declaracionFuncion,id,parametros,bloque);
        }
    }

    protected abstract T procesarPrograma (Programa programa,T declaraciones,T sentencias);

    protected abstract T procesarDeclaracionFuncion(DeclaracionFuncion declaracionFuncion,T identificador, List<T> sentencias);

    protected abstract T procesarDeclaracionFuncion(DeclaracionFuncion declaracionFuncion,T identificador,List<T> parametros, List<T> sentencias);

    protected abstract T procesarDeclaracionVariable(DeclaracionVariable declaracionVariable,T identificador,T expresion);

    protected abstract T procesarInvocacionFuncion(InvocacionFuncion invocacionFuncion, T identificador, List<T> parametros);

    protected abstract T procesarInvocacionFuncion(InvocacionFuncion invocacionFuncion, T identificador);

    protected abstract T procesarWhile(While aWhile, T expresion, List<T> sentencias);

    protected abstract T procesarWhile(While aWhile, T expresion, T bloqueWhile);

    protected abstract T procesarFor(For aFor, T identificador,List<T> sentencias , T from, T to,T by);

    protected abstract T procesarBloque(Bloque bloque, List<T> sentencias);
    
    protected abstract T procesarOperacionBinaria(OperacionBinaria ob, T ei, T ed);
    
    protected abstract T procesarNodo(Nodo n);

    protected abstract T procesarAsignacion(Asignacion a, T identificador, T expresion);

    protected abstract T procesarIf(If anIf,T expresion, List<T> sentencias);

    protected abstract T procesarIf(If anIf, T expresion, List<T> sentenciasIf, List<T> sentenciasElse);

    public <T> T visit(Variable v) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
