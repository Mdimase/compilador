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

public class ValidadorTipos extends Transformer{

    private Alcance alcance_actual; //bloque actual, si no esta aca, busco en el padre hasta llegar a null
    private Tipo tipoRetorno;   //tipo de retorno de una funcion
    private boolean masFunciones = true;    //flag que indica si es posible que vengan mas declaraciones de funciones
    private boolean hayInvocaciones = false;
    private boolean hayReturn = false;

    //inicio de ejecucion del validador de tipos
    public Programa procesar(Programa programa) throws ExcepcionDeTipos{
        return programa.accept_transfomer(this);
    }

    @Override
    public Bloque transform(Bloque bloque) throws ExcepcionDeTipos {
        this.alcance_actual = bloque.getAlcance();
        if(alcance_actual.getNombre().equals("main")){
            masFunciones=false; //a partir de aca no habra mas declaraciones de funciones
        }
        super.transform(bloque);
        return bloque;
    }

    @Override
    public If transform(If aIf) throws ExcepcionDeTipos {
        super.transform(aIf);
        if(aIf.getCondicion().getTipo() != Tipo.BOOL){
            throw new ExcepcionDeTipos(String.format("El resultado de una condicion debe ser BOOL, y es %1$s", aIf.getCondicion().getTipo()));
        }
        return aIf;
    }

    @Override
    public While transform(While aWhile) throws ExcepcionDeTipos {
        super.transform(aWhile);
        if(aWhile.getCondicion().getTipo() != Tipo.BOOL){
            throw new ExcepcionDeTipos(String.format("El resultado de una condicion debe ser BOOL, y es %1$s", aWhile.getCondicion().getTipo()));
        }
        return aWhile;
    }

    @Override
    public When transform(When aWhen) throws ExcepcionDeTipos {
        super.transform(aWhen);
        for(WhenIs whenIs:aWhen.getWhenIs()){
            if(whenIs.getSimboloCmp().getComparador() != Comparador.DISTINTO && whenIs.getSimboloCmp().getComparador() != Comparador.IGUALIGUAL){
                if(whenIs.getExpresion().getTipo() == Tipo.BOOL || aWhen.getExpresionBase().getTipo() == Tipo.BOOL){
                    throw new ExcepcionDeTipos("No se puede comparar <,>,<=,>= con operandos booleanos");
                }
            }
            whenIs.setExpresion(convertir_a_tipo(whenIs.getExpresion(),aWhen.getExpresionBase().getTipo()));
        }
        return aWhen;
    }

    private static Tipo tipo_comun(Tipo tipo_1, Tipo tipo_2) throws ExcepcionDeTipos{
        if (tipo_1 == tipo_2){
            return tipo_1;
        }
        if(tipo_1 == Tipo.INTEGER && tipo_2 == Tipo.FLOAT){
            return tipo_2;
        }
        if(tipo_1 == Tipo.FLOAT && tipo_2 == Tipo.INTEGER){
            return tipo_1;
        }
        throw new ExcepcionDeTipos(
                String.format("No existe un tipo común entre %1$s y %2$s\n", tipo_1, tipo_2 ));
    }

    // recibe la expresion y el tipo al cual convertirla
    private static Expresion convertir_a_tipo(Expresion expresion, Tipo tipo_destino) throws ExcepcionDeTipos{
        Tipo tipo_origen = expresion.getTipo();
        if(tipo_origen == tipo_destino){
            return expresion;
        }
        if(tipo_origen == Tipo.INTEGER && tipo_destino == Tipo.FLOAT){
            return new EnteroAFlotante(expresion);
        }
        if(tipo_origen == Tipo.FLOAT && tipo_destino == Tipo.INTEGER){
            return new FlotanteAEntero(expresion);
        }
        throw new ExcepcionDeTipos(
                String.format("No existe un tipo común entre %1$s y %2$s\n", tipo_origen, tipo_destino ));
    }

    @Override
    public Asignacion transform(Asignacion a) throws ExcepcionDeTipos{
        Asignacion asignacion = super.transform(a);
        asignacion.setExpresion(convertir_a_tipo(asignacion.getExpresion(), asignacion.getIdentificador().getTipo()));
        return asignacion;
    }

    @Override
    public Read transform(Read read){
        return read;
    }

    private void transformarOperacionUnaria(OperacionUnaria ou) throws ExcepcionDeTipos{
        if(ou.getTipo() == Tipo.UNKNOWN){
            ou.setTipo(ou.getExpresion().getTipo());
        }else{
            ou.setExpresion(convertir_a_tipo(ou.getExpresion(), ou.getTipo()));
        }
    }

    private void transformarOperacionBinaria(OperacionBinaria ob) throws ExcepcionDeTipos{
        Tipo tipo_en_comun = tipo_comun(ob.getIzquierda().getTipo(), ob.getDerecha().getTipo());
        ob.setIzquierda(convertir_a_tipo(ob.getIzquierda(),tipo_en_comun));
        ob.setDerecha(convertir_a_tipo(ob.getDerecha(), tipo_en_comun));
        ob.setTipo(tipo_en_comun);
    }

    @Override
    public Expresion transform(Division d) throws ExcepcionDeTipos {
        Division nueva_op = (Division) super.transform(d);
        if (d.getIzquierda().getTipo() != Tipo.BOOL && d.getDerecha().getTipo() != Tipo.BOOL){
            transformarOperacionBinaria(nueva_op);
            return nueva_op;
        } else {
            throw new ExcepcionDeTipos("No se puede Dividir operandos logicos");
        }
    }

    @Override
    public Expresion transform(Multiplicacion m) throws ExcepcionDeTipos {
        Multiplicacion nueva_op = (Multiplicacion) super.transform(m);
        if (m.getIzquierda().getTipo() != Tipo.BOOL && m.getDerecha().getTipo() != Tipo.BOOL){
            transformarOperacionBinaria(nueva_op);
            return nueva_op;
        } else {
            throw new ExcepcionDeTipos("No se puede Multiplicar operandos logicos");
        }
    }

    @Override
    public Expresion transform(Resta r) throws ExcepcionDeTipos {
        Resta nueva_op = (Resta) super.transform(r);
        if (r.getIzquierda().getTipo() != Tipo.BOOL && r.getDerecha().getTipo() != Tipo.BOOL){
            transformarOperacionBinaria(nueva_op);
            return nueva_op;
        } else {
            throw new ExcepcionDeTipos("No se puede Restar operandos logicos");
        }
    }

    @Override
    public Expresion transform(Suma s) throws ExcepcionDeTipos {
        Suma nueva_op = (Suma) super.transform(s);
        if (s.getIzquierda().getTipo() != Tipo.BOOL && s.getDerecha().getTipo() != Tipo.BOOL){
            transformarOperacionBinaria(nueva_op);
            return nueva_op;
        } else {
            throw new ExcepcionDeTipos("No se puede Sumar operandos logicos");
        }
    }

    @Override
    public Expresion transform(And and) throws ExcepcionDeTipos {
        And nueva_op = (And) super.transform(and);
        if(and.getIzquierda().getTipo() == Tipo.BOOL && and.getDerecha().getTipo() == Tipo.BOOL){
            transformarOperacionBinaria(nueva_op);
            return nueva_op;
        } else{
            throw new ExcepcionDeTipos("No se puede operar un AND con operadores aritmeticos");
        }
    }

    @Override
    public Expresion transform(Or or) throws ExcepcionDeTipos {
        Or nueva_op = (Or) super.transform(or);
        if(or.getIzquierda().getTipo() == Tipo.BOOL && or.getDerecha().getTipo() == Tipo.BOOL){
            transformarOperacionBinaria(nueva_op);
            return nueva_op;
        } else{
            throw new ExcepcionDeTipos("No se puede operar un OR con operadores aritmeticos");
        }
    }

    @Override
    public Expresion transform(Menor menor) throws ExcepcionDeTipos {
        Menor nueva_op = (Menor) super.transform(menor);
        if(menor.getIzquierda().getTipo() != Tipo.BOOL && menor.getDerecha().getTipo() != Tipo.BOOL){
            transformarOperacionBinaria(nueva_op);
            nueva_op.setTipo(Tipo.BOOL);
            return nueva_op;
        } else{
            throw new ExcepcionDeTipos("No se puede operar un < con operandos logicos");
        }
    }

    @Override
    public Expresion transform(Mayor mayor) throws ExcepcionDeTipos {
        Mayor nueva_op = (Mayor) super.transform(mayor);
        if(mayor.getIzquierda().getTipo() != Tipo.BOOL && mayor.getDerecha().getTipo() != Tipo.BOOL){
            transformarOperacionBinaria(nueva_op);
            nueva_op.setTipo(Tipo.BOOL);
            return nueva_op;
        } else{
            throw new ExcepcionDeTipos("No se puede operar un > con operandos logicos");
        }
    }

    @Override
    public Expresion transform(MayorIgual mayorIgual) throws ExcepcionDeTipos {
        MayorIgual nueva_op = (MayorIgual) super.transform(mayorIgual);
        if(mayorIgual.getIzquierda().getTipo() != Tipo.BOOL && mayorIgual.getDerecha().getTipo() != Tipo.BOOL){
            transformarOperacionBinaria(nueva_op);
            nueva_op.setTipo(Tipo.BOOL);
            return nueva_op;
        } else{
            throw new ExcepcionDeTipos("No se puede operar un >= con operandos logicos");
        }
    }

    @Override
    public Expresion transform(MenorIgual menorIgual) throws ExcepcionDeTipos {
        MenorIgual nueva_op = (MenorIgual) super.transform(menorIgual);
        if(menorIgual.getIzquierda().getTipo() != Tipo.BOOL && menorIgual.getDerecha().getTipo() != Tipo.BOOL){
            transformarOperacionBinaria(nueva_op);
            nueva_op.setTipo(Tipo.BOOL);
            return nueva_op;
        } else{
            throw new ExcepcionDeTipos("No se puede operar un <= con operandos logicos");
        }
    }

    @Override
    public Expresion transform(IgualIgual igualIgual) throws ExcepcionDeTipos {
        IgualIgual nueva_op = (IgualIgual) super.transform(igualIgual);
        transformarOperacionBinaria(nueva_op);
        nueva_op.setTipo(Tipo.BOOL);
        return nueva_op;
    }

    @Override
    public Expresion transform(Distinto distinto) throws ExcepcionDeTipos {
        Distinto nueva_op = (Distinto) super.transform(distinto);
        transformarOperacionBinaria(nueva_op);
        nueva_op.setTipo(Tipo.BOOL);
        return nueva_op;
    }

    @Override
    public Expresion transform(Not not) throws ExcepcionDeTipos {
        Not nueva_op = (Not) super.transform(not);
        if(not.getExpresion().getTipo() == Tipo.BOOL){
            transformarOperacionUnaria(nueva_op);
            return nueva_op;
        } else {
            throw new ExcepcionDeTipos(
                    String.format("No existe un tipo común entre boolean y %1$s\n",not.getExpresion().getTipo() ));
        }
    }

    @Override
    public MenosUnario transform(MenosUnario menosUnario) throws ExcepcionDeTipos {
        MenosUnario nueva_op = super.transform(menosUnario);
        if(menosUnario.getExpresion().getTipo() != Tipo.BOOL){
            transformarOperacionUnaria(nueva_op);
            return nueva_op;
        } else {
            throw new ExcepcionDeTipos(
                    String.format("No se puede realizar una operacion -%1$s\n",menosUnario.getExpresion().getTipo() ));
        }
    }

    @Override
    public Expresion transform(FlotanteAEntero fae) throws ExcepcionDeTipos {
        return super.transform(fae);
    }

    @Override
    public Expresion transform(EnteroAFlotante eaf) throws ExcepcionDeTipos {
        return super.transform(eaf);
    }

    @Override
    public DeclaracionVariable transform(DeclaracionVariable declaracionVariable) throws ExcepcionDeTipos {
        super.transform(declaracionVariable);
        if(declaracionVariable.getId().getTipo() != declaracionVariable.getExpresion().getTipo()){
            if(declaracionVariable.getId().getTipo() != Tipo.BOOL && declaracionVariable.getExpresion().getTipo() != Tipo.BOOL){ //convierto los numericos
                declaracionVariable.setExpresion(convertir_a_tipo(declaracionVariable.getExpresion(),declaracionVariable.getId().getTipo()));
            } else{
                throw new ExcepcionDeTipos(String.format
                        ("el tipo declarado %1$s no es compatible con el de la expresion %2$s",declaracionVariable.getId().getTipo(),declaracionVariable.getExpresion().getTipo()));
            }
        }
        return declaracionVariable;
    }

    @Override
    public DeclaracionFuncion transform(DeclaracionFuncion declaracionFuncion) throws ExcepcionDeTipos {
        super.transform(declaracionFuncion);
        if(!hayReturn){ // agrego return implicito
            Return r = new Return(new Constante("unknown",Tipo.UNKNOWN));
            if (tipoRetorno == Tipo.BOOL){
                r = new Return(new Constante("false",Tipo.BOOL));
            }
            if (tipoRetorno == Tipo.INTEGER){
                r = new Return(new Constante("0",Tipo.INTEGER));
            }
            if (tipoRetorno == Tipo.FLOAT){
                r = new Return(new Constante("0.0",Tipo.FLOAT));
            }
            declaracionFuncion.getBloque().getSentencias().add(r);
        }
        hayReturn = false;  //dejo en falso para la proxima funcion
        return declaracionFuncion;
    }

    @Override
    public InvocacionFuncion transform(InvocacionFuncion invocacionFuncion) throws ExcepcionDeTipos {
        hayInvocaciones=true;
        super.transform(invocacionFuncion);
        invocacionFuncion.setTipo(invocacionFuncion.getIdentificador().getTipo());
        DeclaracionFuncion declaracionFuncion = (DeclaracionFuncion) alcance_actual.resolver(invocacionFuncion.getIdentificador().getNombre());
        int cont =0;
        for (Parametro parametro:declaracionFuncion.getParametros()){
            if(parametro.getValorDefecto() != null){
                cont++;
            }
        }
        if(invocacionFuncion.getParams().size() < declaracionFuncion.getParametros().size() - cont){
            throw new ExcepcionDeTipos("la cantidad de parametros invocados es inferior a los declarados ");
        }
        if(invocacionFuncion.getParams().size() > declaracionFuncion.getParametros().size()){
            throw new ExcepcionDeTipos("la cantidad de parametros invocados es superior a los declarados ");
        }
        for(int i=0;i<invocacionFuncion.getParams().size();i++){
            if(declaracionFuncion.getParametros().get(i).getTipo() != invocacionFuncion.getParams().get(i).getTipo() ){
                if(invocacionFuncion.getParams().get(i).getTipo() != Tipo.BOOL && invocacionFuncion.getParams().get(i).getTipo() != Tipo.BOOL){
                    Tipo destino = declaracionFuncion.getParametros().get(i).getTipo();
                    invocacionFuncion.getParams().set(i,convertir_a_tipo(invocacionFuncion.getParams().get(i),destino));
                } else {
                    throw new ExcepcionDeTipos(("Tipo de Parametro incorrecto"));
                }
            }
        }
        return invocacionFuncion;
    }

    @Override
    public Return transform(Return aReturn) throws ExcepcionDeTipos {
        super.transform(aReturn);
        hayReturn=true; //hay return -> que no se agregue uno implicito
        if(aReturn.getExpresion().getTipo() != tipoRetorno){
            aReturn.setExpresion(convertir_a_tipo(aReturn.getExpresion(),tipoRetorno));
        }
        return aReturn;
    }

    @Override
    public Identificador transform(Identificador identificador) throws ExcepcionDeTipos{
        Object elemento = alcance_actual.resolver(identificador.getNombre());   //verifica si el nombre esta declarado
        Tipo tipo = Tipo.UNKNOWN;
        if(elemento instanceof DeclaracionVariable){
            tipo = ((DeclaracionVariable) elemento).getTipo();
        }
        if(elemento instanceof DeclaracionFuncion){
                if(masFunciones && !hayInvocaciones){
                    tipo = ((DeclaracionFuncion) elemento).getTipoRetorno();
                    tipoRetorno = tipo;
                    alcance_actual = ((DeclaracionFuncion) elemento).getBloque().getAlcance();
                } else{ //invocaciones objetivo
                    tipo = ((DeclaracionFuncion) elemento).getTipoRetorno();
                    //tipoRetorno = tipo;
                    hayInvocaciones=false;
                }
        }
        if(elemento instanceof Parametro){
            tipo = ((Parametro) elemento).getTipo();
        }
        if (tipo != Tipo.UNKNOWN){  //se encontro y modifico el tipo, ahora es de lo encontrado
            identificador.setTipo(tipo);
            return identificador;
        }
        throw new ExcepcionDeTipos(String.format("No se declaró el nombre %1$s\n", identificador.getNombre()));
    }

}