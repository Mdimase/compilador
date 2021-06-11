package compilador.visitor;

import compilador.ast.base.*;
import compilador.ast.instrucciones.DeclaracionVariable;
import compilador.ast.instrucciones.If;
import compilador.ast.instrucciones.Sentencia;
import compilador.ast.operaciones.binarias.*;
import compilador.ast.operaciones.unarias.EnteroAFlotante;
import compilador.ast.operaciones.unarias.FlotanteAEntero;
import compilador.ast.operaciones.unarias.Not;

import java.util.ArrayList;
import java.util.List;

public class Rewriter extends Transformer{

    private int n = 1;

    public Programa procesar(Programa programa) throws ExcepcionDeTipos {
        return programa.accept_transfomer(this);
    }

    // instancia la condicion correspondiente y le setea su tipo
    public Comparacion resolverCondicion(Expresion expresionBase, WhenIs whenIs){
        Comparacion condicion = null;
        if(whenIs.getComparador().equals(Comparador.MAYOR)){
            condicion = new Mayor(expresionBase,whenIs.getExpresion());
        }
        if (whenIs.getComparador().equals(Comparador.MAYORIGUAL)){
            condicion = new MenorIgual(expresionBase, whenIs.getExpresion());
        }
        if (whenIs.getComparador().equals(Comparador.MENOR)){
            condicion = new Menor(expresionBase, whenIs.getExpresion());
        }
        if (whenIs.getComparador().equals(Comparador.MENORIGUAL)){
            condicion = new MenorIgual(expresionBase, whenIs.getExpresion());
        }
        if (whenIs.getComparador().equals(Comparador.IGUALIGUAL)){
            condicion = new IgualIgual(expresionBase, whenIs.getExpresion());
        }
        if (whenIs.getComparador().equals(Comparador.DISTINTO)){
            condicion = new Distinto(expresionBase, whenIs.getExpresion());
        }
        assert condicion != null;
        condicion.setTipo(expresionBase.getTipo());
        return condicion;
    }

    //nombre unico y que no puede ser utilizado como entrada en este lenguaje
    public String crearNombreUnico(){
        String nombre = "$"+n;
        n++;
        return nombre;
    }

    @Override
    public Sentencia transform(When when) throws ExcepcionDeTipos {
        when = (When) super.transform(when);    //tuve que hacer que el transform(when) retorne una sentencia, por eso ahora casteo
        List<Sentencia> sentencias = new ArrayList<>();
        Identificador identificador = new Identificador(crearNombreUnico(),when.getExpresionBase().getTipo());
        DeclaracionVariable dv = new DeclaracionVariable(identificador,when.getExpresionBase().getTipo(),when.getExpresionBase());  //expresion que se comparara
        sentencias.add(dv);//sentencias del bloque a retornar
        If current_if = null;
        If global_if = null;
        for(WhenIs wi: when.getWhenIs()){
            if(current_if == null){ //primera comparacion
                current_if = new If(resolverCondicion(when.getExpresionBase(), wi),new Bloque(wi.getBloque().getSentencias(),"THEN",false));
                global_if = current_if;
            } else {    //a partir de la segunda comparacion
                If newIf = new If(resolverCondicion(when.getExpresionBase(),wi),new Bloque(wi.getBloque().getSentencias(),"THEN",false));
                List<Sentencia> ls = new ArrayList<>();
                ls.add(newIf);
                current_if.setBloqueElse(new Bloque(ls,"ELSE",false));  //else if
                current_if=newIf;
            }
        }
        // para el else del final que era opcional
        if(current_if != null && when.getBloqueElse() != null){
            current_if.setBloqueElse(new Bloque(when.getBloqueElse().getSentencias(),"ELSE",false));
        }
        sentencias.add(global_if);
        //retorno un bloque que contiene la declaracion de variable temp + los if anidados
        return new Bloque(sentencias,"When -> If",false);
    }

    //CONSTANT FOLDING con conversiones implicitas entre int y float

    @Override
    public Expresion transform(EnteroAFlotante eaf) throws ExcepcionDeTipos {
        super.transform(eaf);
        if(eaf.getExpresion().getClass() == Constante.class){
            Constante constante = (Constante) eaf.getExpresion();
            Float valorF = Float.parseFloat((String) constante.getValor());
            return new Constante(String.valueOf(valorF) ,Tipo.FLOAT);
        } else {
            return eaf;
        }
    }

    @Override
    public Expresion transform(FlotanteAEntero fae) throws ExcepcionDeTipos {
        super.transform(fae);
        if(fae.getExpresion().getClass() == Constante.class){
            Constante constante = (Constante) fae.getExpresion();
            float valorF = Float.parseFloat((String) constante.getValor()); //no se puede hacer un parseInt directo
            Integer valorI = (int) valorF;
            return new Constante(String.valueOf(valorI) ,Tipo.INTEGER);
        } else {
            return fae;
        }
    }

    @Override
    public Expresion transform(Resta resta) throws ExcepcionDeTipos {
        super.transform(resta);
        if(resta.getIzquierda().getClass() == Constante.class && resta.getDerecha().getClass() == Constante.class){
            Constante constanteIz = (Constante) resta.getIzquierda();
            Constante constanteDer = (Constante) resta.getDerecha();
            if(constanteIz.getTipo().equals(Tipo.INTEGER) && constanteDer.getTipo().equals(Tipo.INTEGER)){
                Integer valorIz = Integer.parseInt((String) constanteIz.getValor());
                Integer valorDer = Integer.parseInt((String) constanteDer.getValor());
                String result = String.valueOf(valorIz-valorDer);
                return new Constante(result,Tipo.INTEGER);
            } else {    //son float
                Float valorIz = Float.parseFloat((String) constanteIz.getValor());
                Float valorDer = Float.parseFloat((String) constanteDer.getValor());
                String result = String.valueOf(valorIz-valorDer);
                return new Constante(result,Tipo.FLOAT);
            }
        } else { //no constantes
            return resta;
        }
    }

    @Override
    public Expresion transform(Suma suma) throws ExcepcionDeTipos {
        super.transform(suma);
        if(suma.getIzquierda().getClass() == Constante.class && suma.getDerecha().getClass() == Constante.class){
            Constante constanteIz = (Constante) suma.getIzquierda();
            Constante constanteDer = (Constante) suma.getDerecha();
            if(constanteIz.getTipo().equals(Tipo.INTEGER) && constanteDer.getTipo().equals(Tipo.INTEGER)){
                Integer valorIz = Integer.parseInt((String) constanteIz.getValor());
                Integer valorDer = Integer.parseInt((String) constanteDer.getValor());
                String result = String.valueOf(valorIz+valorDer);
                return new Constante(result,Tipo.INTEGER);
            } else {    //son float
                Float valorIz = Float.parseFloat((String) constanteIz.getValor());
                Float valorDer = Float.parseFloat((String) constanteDer.getValor());
                String result = String.valueOf(valorIz+valorDer);
                return new Constante(result,Tipo.FLOAT);
            }
        } else { //no constantes
            return suma;
        }
    }

    @Override
    public Expresion transform(Multiplicacion multiplicacion) throws ExcepcionDeTipos {
        super.transform(multiplicacion);
        if(multiplicacion.getIzquierda().getClass() == Constante.class && multiplicacion.getDerecha().getClass() == Constante.class){
            Constante constanteIz = (Constante) multiplicacion.getIzquierda();
            Constante constanteDer = (Constante) multiplicacion.getDerecha();
            if(constanteIz.getTipo().equals(Tipo.INTEGER) && constanteDer.getTipo().equals(Tipo.INTEGER)){
                Integer valorIz = Integer.parseInt((String) constanteIz.getValor());
                Integer valorDer = Integer.parseInt((String) constanteDer.getValor());
                String result = String.valueOf(valorIz*valorDer);
                return new Constante(result,Tipo.INTEGER);
            } else {    //son float
                Float valorIz = Float.parseFloat((String) constanteIz.getValor());
                Float valorDer = Float.parseFloat((String) constanteDer.getValor());
                String result = String.valueOf(valorIz*valorDer);
                return new Constante(result,Tipo.FLOAT);
            }
        } else { //no constantes
            return multiplicacion;
        }
    }

    @Override
    public Expresion transform(Division division) throws ExcepcionDeTipos {
        super.transform(division);
        if(division.getIzquierda().getClass() == Constante.class && division.getDerecha().getClass() == Constante.class){
            Constante constanteIz = (Constante) division.getIzquierda();
            Constante constanteDer = (Constante) division.getDerecha();
            if(constanteIz.getTipo().equals(Tipo.INTEGER) && constanteDer.getTipo().equals(Tipo.INTEGER)){
                Integer valorIz = Integer.parseInt((String) constanteIz.getValor());
                Integer valorDer = Integer.parseInt((String) constanteDer.getValor());
                String result = String.valueOf(valorIz/valorDer);
                return new Constante(result,Tipo.INTEGER);
            } else {    //son float
                Float valorIz = Float.parseFloat((String) constanteIz.getValor());
                Float valorDer = Float.parseFloat((String) constanteDer.getValor());
                String result = String.valueOf(valorIz/valorDer);
                return new Constante(result,Tipo.FLOAT);
            }
        } else { //no constantes
            return division;
        }
    }

    @Override
    public Expresion transform(Not not) throws ExcepcionDeTipos {
        super.transform(not);
        if(not.getExpresion().getClass() == Constante.class){
            Constante constante = (Constante) not.getExpresion();
            if (constante.getValor().equals("true")){
                return new Constante("false",Tipo.BOOL);
            } else{
                return new Constante("true",Tipo.BOOL);
            }
        } else{
            return not;
        }
    }

    @Override
    public Expresion transform(And and) throws ExcepcionDeTipos {
        super.transform(and);
        if(and.getIzquierda().getClass() == Constante.class && and.getDerecha().getClass() == Constante.class){
            Constante constanteIz = (Constante) and.getIzquierda();
            Constante constanteDer = (Constante) and.getDerecha();
            if(constanteIz.getValor().equals("true") && constanteDer.getValor().equals("true")){
                return new Constante("true",Tipo.BOOL);
            }
            else{
                return new Constante("false",Tipo.BOOL);
            }
        } else { //no constantes
            return and;
        }
    }

    @Override
    public Expresion transform(Or or) throws ExcepcionDeTipos {
        super.transform(or);
        if(or.getIzquierda().getClass() == Constante.class && or.getDerecha().getClass() == Constante.class){
            Constante constanteIz = (Constante) or.getIzquierda();
            Constante constanteDer = (Constante) or.getDerecha();
            if(constanteIz.getValor().equals("false") && constanteDer.getValor().equals("false")){
                return new Constante("false",Tipo.BOOL);
            }
            else{
                return new Constante("true",Tipo.BOOL);
            }
        } else { //no constantes
            return or;
        }
    }

}
