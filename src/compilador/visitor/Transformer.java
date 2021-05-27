/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.visitor;

import compilador.ast.base.*;
import compilador.ast.instrucciones.Asignacion;
import compilador.ast.instrucciones.DeclaracionVariable;
import compilador.ast.instrucciones.Sentencia;
import compilador.ast.operaciones.binarias.*;
import compilador.ast.operaciones.unarias.EnteroAFlotante;
import compilador.ast.operaciones.unarias.FlotanteAEntero;
import compilador.ast.operaciones.unarias.MenosUnario;
import compilador.ast.operaciones.unarias.Not;

import java.util.ArrayList;

/**
 *
 * @author ITT
 */
public abstract class Transformer {
/*
    public Programa transform(Programa p) throws ExcepcionDeTipos{
        p.setCuerpo(p.getCuerpo().accept_transfomer(this));
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

    public DeclaracionVariable transform(DeclaracionVariable dv) {
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
    
    public FlotanteAEntero transform(FlotanteAEntero fae) throws ExcepcionDeTipos {
        fae.setExpresion(fae.getExpresion().accept_transfomer(this));
        return fae;
    }

    public EnteroAFlotante transform(EnteroAFlotante eaf) throws ExcepcionDeTipos {
        eaf.setExpresion(eaf.getExpresion().accept_transfomer(this));
        return eaf;
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

    public MenosUnario transform(MenosUnario menosUnario) throws ExcepcionDeTipos{
        menosUnario.setExpresion(menosUnario.getExpresion().accept_transfomer(this));
        return menosUnario;
    }

    public Not transform(Not not) throws ExcepcionDeTipos {
        not.setExpresion(not.getExpresion().accept_transfomer(this));
        return not;
    }

    public InvocacionFuncion transform(InvocacionFuncion invocacionFuncion) throws ExcepcionDeTipos {
        return invocacionFuncion;
    }

 */
}
