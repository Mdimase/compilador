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

    public Variable transform(Variable v) {
        return v;
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

    public Division transform(Division d) throws ExcepcionDeTipos {
        return (Division) transformar_operacion_binaria(d);
    }

    public Multiplicacion transform(Multiplicacion m) throws ExcepcionDeTipos {
        return (Multiplicacion) transformar_operacion_binaria(m);
    }

    public Resta transform(Resta r) throws ExcepcionDeTipos {
        return (Resta) transformar_operacion_binaria(r);
    }

    public Suma transform(Suma s) throws ExcepcionDeTipos {
        return (Suma) transformar_operacion_binaria(s);
    }

    public Mayor transform(Mayor mayor) throws ExcepcionDeTipos {
        return (Mayor) transformar_operacion_binaria(mayor);
    }

    public Menor transform(Menor menor) throws ExcepcionDeTipos {
        return (Menor) transformar_operacion_binaria(menor);
    }

    public MayorIgual transform(MayorIgual mayorIgual)  throws ExcepcionDeTipos {
        return (MayorIgual) transformar_operacion_binaria(mayorIgual);
    }

    public MenorIgual transform(MenorIgual menorIgual)  throws ExcepcionDeTipos {
        return (MenorIgual) transformar_operacion_binaria(menorIgual);
    }

    public IgualIgual transform(IgualIgual igualIgual)  throws ExcepcionDeTipos{
        return (IgualIgual) transformar_operacion_binaria(igualIgual);
    }

    public Distinto transform(Distinto distinto)  throws ExcepcionDeTipos {
        return (Distinto) transformar_operacion_binaria(distinto);
    }

    public Or transform(Or or) throws ExcepcionDeTipos {
        return (Or) transformar_operacion_binaria(or);
    }

    public And transform(And and) throws ExcepcionDeTipos {
        return (And) transformar_operacion_binaria(and);
    }

    private OperacionUnaria transformar_operacion_unaria(OperacionUnaria operacion) throws ExcepcionDeTipos{
        operacion.setExpresion(operacion.getExpresion().accept_transfomer(this));
        return operacion;
    }

    public MenosUnario transform(MenosUnario menosUnario) throws ExcepcionDeTipos{
        return (MenosUnario) transformar_operacion_unaria(menosUnario);
    }

    public Not transform(Not not) throws ExcepcionDeTipos {
        return (Not) transformar_operacion_unaria(not);
    }

    public FlotanteAEntero transform(FlotanteAEntero fae) throws ExcepcionDeTipos {
        fae.setExpresion(fae.getExpresion().accept_transfomer(this));
        return fae;
    }

    public EnteroAFlotante transform(EnteroAFlotante eaf) throws ExcepcionDeTipos {
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

    public Funcion transform(Funcion funcion) throws ExcepcionDeTipos {
        return funcion;
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

    public For transform(For aFor) throws ExcepcionDeTipos {
        Identificador id = aFor.getIdentificador().accept_transfomer(this);
        Bloque bloque = aFor.getBloque().accept_transfomer(this);
        Constante from = aFor.getFrom().accept_transfomer(this);
        Constante to = aFor.getTo().accept_transfomer(this);
        Constante by = aFor.getBy().accept_transfomer(this);
        aFor.setIdentificador(id);
        aFor.setBloque(bloque);
        aFor.setFrom(from);
        aFor.setTo(to);
        aFor.setBy(by);
        return aFor;
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

    public Read transform(Read read){
        return read;
    }
}
