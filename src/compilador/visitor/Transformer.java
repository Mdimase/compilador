/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.visitor;

import compilador.ast.base.*;
import compilador.ast.instrucciones.*;
import compilador.ast.operaciones.binarias.*;
import compilador.ast.operaciones.unarias.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//  la secuencia de llamados sera igual a la del Visitor abstracto
//  aca se va a poder modificar la estructura del AST, por eso en cada metodo se retorna el Nodo transformado
//  por ende retorna un AST modificado a diferencia del Visitor comun que retorna nada o archivos externos como dot o llvm

public abstract class Transformer {

    // retorna un Programa tranformado
    public Programa transform(Programa p) throws ExcepcionDeTipos{
        if(p.getDeclaraciones() == null){
            p.setCuerpo(p.getCuerpo().accept_transfomer(this));
        } else {
            p.setDeclaraciones(p.getDeclaraciones().accept_transfomer(this));
            p.setCuerpo(p.getCuerpo().accept_transfomer(this));
        }
        return p;
    }

    public Bloque transform(Bloque b) throws ExcepcionDeTipos {
        ArrayList<Sentencia> result = new ArrayList<>();
        for (Sentencia sentencia : b.getSentencias()){
            result.add(sentencia.accept_transfomer(this));
        }
        b.setSentencias(result);
        return b;
    }

    public Constante transform(Constante c) {
        return c;
    }

    public Identificador transform(Identificador i) throws ExcepcionDeTipos{
        return i;
    }

    public Asignacion transform(Asignacion a) throws ExcepcionDeTipos{
        Identificador id = a.getIdentificador().accept_transfomer(this);
        Expresion e = a.getExpresion().accept_transfomer(this);
        a.setIdentificador(id);
        a.setExpresion(e);
        return a;
    }

    public DeclaracionVariable transform(DeclaracionVariable dv) throws ExcepcionDeTipos {
        Identificador id = dv.getId().accept_transfomer(this);
        Expresion e = dv.getExpresion().accept_transfomer(this);
        dv.setId(id);
        dv.setExpresion(e);
        return dv;
    }

    private OperacionBinaria transformar_operacion_binaria(OperacionBinaria operacion) throws ExcepcionDeTipos{
        operacion.setIzquierda(operacion.getIzquierda().accept_transfomer(this));
        operacion.setDerecha(operacion.getDerecha().accept_transfomer(this));
        return operacion;
    }

    //retornan Expresion x el constant folding
    public Expresion transform(Division d) throws ExcepcionDeTipos {
        return transformar_operacion_binaria(d);
    }

    //retornan Expresion x el constant folding
    public Expresion transform(Multiplicacion m) throws ExcepcionDeTipos {
        return transformar_operacion_binaria(m);
    }

    //retornan Expresion x el constant folding
    public Expresion transform(Resta r) throws ExcepcionDeTipos {
        return transformar_operacion_binaria(r);
    }

    //retornan Expresion x el constant folding
    public Expresion transform(Suma s) throws ExcepcionDeTipos {
        return transformar_operacion_binaria(s);
    }

    //retornan Expresion x el constant folding
    public Expresion transform(Mayor mayor) throws ExcepcionDeTipos {
        return transformar_operacion_binaria(mayor);
    }

    //retornan Expresion x el constant folding
    public Expresion transform(Menor menor) throws ExcepcionDeTipos {
        return transformar_operacion_binaria(menor);
    }

    //retornan Expresion x el constant folding
    public Expresion transform(MayorIgual mayorIgual)  throws ExcepcionDeTipos {
        return transformar_operacion_binaria(mayorIgual);
    }

    //retornan Expresion x el constant folding
    public Expresion transform(MenorIgual menorIgual)  throws ExcepcionDeTipos {
        return transformar_operacion_binaria(menorIgual);
    }

    //retornan Expresion x el constant folding
    public Expresion transform(IgualIgual igualIgual)  throws ExcepcionDeTipos{
        return transformar_operacion_binaria(igualIgual);
    }

    //retornan Expresion x el constant folding
    public Expresion transform(Distinto distinto)  throws ExcepcionDeTipos {
        return transformar_operacion_binaria(distinto);
    }

    //retornan Expresion x el constant folding
    public Expresion transform(Or or) throws ExcepcionDeTipos {
        return transformar_operacion_binaria(or);
    }

    //retornan Expresion x el constant folding
    public Expresion transform(And and) throws ExcepcionDeTipos {
        return transformar_operacion_binaria(and);
    }

    private OperacionUnaria transformar_operacion_unaria(OperacionUnaria operacion) throws ExcepcionDeTipos{
        operacion.setExpresion(operacion.getExpresion().accept_transfomer(this));
        return operacion;
    }

    public MenosUnario transform(MenosUnario menosUnario) throws ExcepcionDeTipos{
        return (MenosUnario) transformar_operacion_unaria(menosUnario);
    }

    //retornan Expresion x el constant folding
    public Expresion transform(Not not) throws ExcepcionDeTipos {
        return transformar_operacion_unaria(not);
    }

    //retornan Expresion x el constant folding
    public Expresion transform(FlotanteAEntero fae) throws ExcepcionDeTipos {
        fae.setExpresion(fae.getExpresion().accept_transfomer(this));
        return fae;
    }

    //retornan Expresion x el constant folding
    public Expresion transform(EnteroAFlotante eaf) throws ExcepcionDeTipos {
        eaf.setExpresion(eaf.getExpresion().accept_transfomer(this));
        return eaf;
    }

    public InvocacionFuncion transform(InvocacionFuncion invocacionFuncion) throws ExcepcionDeTipos {
        Identificador id = invocacionFuncion.getIdentificador().accept_transfomer(this);
        ArrayList<Expresion> result = new ArrayList<>();
        for (Expresion expresion : invocacionFuncion.getParams()){
            result.add(expresion.accept_transfomer(this));
        }
        invocacionFuncion.setIdentificador(id);
        invocacionFuncion.setParams(result);
        return invocacionFuncion;
    }

    public Break transform(Break aBreak){
        return aBreak;
    }

    public Continue transform(Continue aContinue){
        return aContinue;
    }

    public Return transform(Return aReturn) throws ExcepcionDeTipos {
        Expresion e = aReturn.getExpresion().accept_transfomer(this);
        aReturn.setExpresion(e);
        return aReturn;
    }

    public Parametro transform(Parametro parametro) throws ExcepcionDeTipos {
        Identificador id = parametro.getIdentificador().accept_transfomer(this);
        if(parametro.getValorDefecto() != null ){
            Constante valor_defecto = parametro.getValorDefecto().accept_transfomer(this);
            parametro.setValorDefecto(valor_defecto);
        }
        parametro.setIdentificador(id);
        return parametro;
    }

    public DeclaracionFuncion transform(DeclaracionFuncion declaracionFuncion) throws ExcepcionDeTipos {
        Identificador id = declaracionFuncion.getIdentificador().accept_transfomer(this);
        if(!declaracionFuncion.getParametros().isEmpty()){
            ArrayList<Parametro> parametros = new ArrayList<>();
            for (Parametro parametro : declaracionFuncion.getParametros()){
                parametros.add(parametro.accept_transfomer(this));
            }
            declaracionFuncion.setParametros(parametros);
        }
        Bloque bloque = declaracionFuncion.getBloque().accept_transfomer(this);
        declaracionFuncion.setIdentificador(id);
        declaracionFuncion.setBloque(bloque);
        return declaracionFuncion;
    }

    public If transform(If anIf) throws ExcepcionDeTipos {
        Expresion e = anIf.getCondicion().accept_transfomer(this);
        Bloque bloqueThen = anIf.getBloqueThen().accept_transfomer(this);
        if(anIf.getBloqueElse() != null){
            Bloque bloqueElse = anIf.getBloqueElse().accept_transfomer(this);
            anIf.setBloqueElse(bloqueElse);
        }
        anIf.setCondicion(e);
        anIf.setBloqueThen(bloqueThen);
        return anIf;
    }

    public While transform(While aWhile) throws ExcepcionDeTipos {
        Expresion e = aWhile.getCondicion().accept_transfomer(this);
        Bloque bloque = aWhile.getBloque().accept_transfomer(this);
        aWhile.setCondicion(e);
        aWhile.setBloque(bloque);
        return aWhile;
    }

    public Write transform(Write write) throws ExcepcionDeTipos {
        if(write.getExpresion() != null){
            Expresion e = write.getExpresion().accept_transfomer(this);
            write.setExpresion(e);
        }
        return write;
    }

    public Mensaje transform(Mensaje mensaje){
        return mensaje;
    }

    public Read transform(Read read){
        return read;
    }

    public WhenIs transform(WhenIs whenIs) throws ExcepcionDeTipos {
        Expresion e = whenIs.getExpresion().accept_transfomer(this);
        Bloque bloque = whenIs.getBloque().accept_transfomer(this);
        whenIs.setExpresion(e);
        whenIs.setBloque(bloque);
        return whenIs;
    }

    // retorna sentencia xq al reescribirlo en un if, dicho metodo retorna un Bloque
    // y para tener un tipo compatible entre ambos visit, aca retorno Sentencia
    public Sentencia transform(When when) throws ExcepcionDeTipos {
        Expresion e = when.getExpresionBase().accept_transfomer(this);
        ArrayList<WhenIs> list = new ArrayList<>();
        for (WhenIs whenIs : when.getWhenIs()){
            list.add(whenIs.accept_transfomer(this));
        }
        if(when.getBloqueElse() != null){
            Bloque bloque = when.getBloqueElse().accept_transfomer(this);
            when.setBloqueElse(bloque);
        }
        when.setExpresionBase(e);
        when.setWhenIs(list);
        return when;
    }

    public SimboloCmp transform(SimboloCmp simboloCmp){
        return simboloCmp;
    }
}