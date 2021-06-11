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

import java.util.Collections;
import java.util.List;

public class ValidadorTipos extends Transformer{

    private Alcance alcance_actual; //bloque actual, si no esta aca, busco en el padre hasta llegar a null
    private Tipo tipoRetorno;   //tipo de retorno de una funcion
    private boolean masFunciones = true;    //flag que indica si es posible que vengan mas declaraciones de funciones
    private boolean hayInvocaciones = false;

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
        for (WhenIs whenIs: aWhen.getWhenIs()){
            if(whenIs.getComparador() == Comparador.MENOR){
                Menor menor = new Menor(aWhen.getExpresionBase(),whenIs.getExpresion());
                this.transform(menor);
            }
            if(whenIs.getComparador() == Comparador.MAYOR){
                Mayor mayor = new Mayor(aWhen.getExpresionBase(),whenIs.getExpresion());
                this.transform(mayor);
            }
            if(whenIs.getComparador() == Comparador.MENORIGUAL){
                MenorIgual menorIgual = new MenorIgual(aWhen.getExpresionBase(),whenIs.getExpresion());
                this.transform(menorIgual);
            }
            if(whenIs.getComparador() == Comparador.MAYORIGUAL){
                MayorIgual mayorIgual = new MayorIgual(aWhen.getExpresionBase(),whenIs.getExpresion());
                this.transform(mayorIgual);
            }
            if(whenIs.getComparador() == Comparador.IGUALIGUAL){
                IgualIgual igualIgual = new IgualIgual(aWhen.getExpresionBase(),whenIs.getExpresion());
                this.transform(igualIgual);
            }
            if(whenIs.getComparador() == Comparador.DISTINTO){
                Distinto distinto = new Distinto(aWhen.getExpresionBase(),whenIs.getExpresion());
                this.transform(distinto);
            }
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

    private OperacionUnaria transformarOperacionUnaria(OperacionUnaria ou) throws ExcepcionDeTipos{
        if(ou.getTipo() == Tipo.UNKNOWN){
            ou.setTipo(ou.getExpresion().getTipo());
        }else{
            ou.setExpresion(convertir_a_tipo(ou.getExpresion(), ou.getTipo()));
        }
        return ou;
    }

    private OperacionBinaria transformarOperacionBinaria(OperacionBinaria ob) throws ExcepcionDeTipos{
        Tipo tipo_en_comun = tipo_comun(ob.getIzquierda().getTipo(), ob.getDerecha().getTipo());
        ob.setIzquierda(convertir_a_tipo(ob.getIzquierda(),tipo_en_comun));
        ob.setDerecha(convertir_a_tipo(ob.getDerecha(), tipo_en_comun));
        ob.setTipo(tipo_en_comun);
        return ob;
    }

    @Override
    public Expresion transform(Division d) throws ExcepcionDeTipos {
        Division nueva_op = (Division) super.transform(d);
        if (d.getIzquierda().getTipo() != Tipo.BOOL && d.getDerecha().getTipo() != Tipo.BOOL){
            nueva_op = (Division) transformarOperacionBinaria(nueva_op);
            return nueva_op;
        } else {
            throw new ExcepcionDeTipos("No se puede Dividir operandos logicos");
        }
    }

    @Override
    public Expresion transform(Multiplicacion m) throws ExcepcionDeTipos {
        Multiplicacion nueva_op = (Multiplicacion) super.transform(m);
        if (m.getIzquierda().getTipo() != Tipo.BOOL && m.getDerecha().getTipo() != Tipo.BOOL){
            nueva_op = (Multiplicacion) transformarOperacionBinaria(nueva_op);
            return nueva_op;
        } else {
            throw new ExcepcionDeTipos("No se puede Multiplicar operandos logicos");
        }
    }

    @Override
    public Expresion transform(Resta r) throws ExcepcionDeTipos {
        Resta nueva_op = (Resta) super.transform(r);
        if (r.getIzquierda().getTipo() != Tipo.BOOL && r.getDerecha().getTipo() != Tipo.BOOL){
            nueva_op = (Resta) transformarOperacionBinaria(nueva_op);
            return nueva_op;
        } else {
            throw new ExcepcionDeTipos("No se puede Restar operandos logicos");
        }
    }

    @Override
    public Expresion transform(Suma s) throws ExcepcionDeTipos {
        Suma nueva_op = (Suma) super.transform(s);
        if (s.getIzquierda().getTipo() != Tipo.BOOL && s.getDerecha().getTipo() != Tipo.BOOL){
            nueva_op = (Suma) transformarOperacionBinaria(nueva_op);
            return nueva_op;
        } else {
            throw new ExcepcionDeTipos("No se puede Sumar operandos logicos");
        }
    }

    @Override
    public Expresion transform(And and) throws ExcepcionDeTipos {
        And nueva_op = (And) super.transform(and);
        if(and.getIzquierda().getTipo() == Tipo.BOOL && and.getDerecha().getTipo() == Tipo.BOOL){
            nueva_op = (And) transformarOperacionBinaria(nueva_op);
            return nueva_op;
        } else{
            throw new ExcepcionDeTipos("No se puede operar un AND con operadores aritmeticos");
        }
    }

    @Override
    public Expresion transform(Or or) throws ExcepcionDeTipos {
        Or nueva_op = (Or) super.transform(or);
        if(or.getIzquierda().getTipo() == Tipo.BOOL && or.getDerecha().getTipo() == Tipo.BOOL){
            nueva_op = (Or) transformarOperacionBinaria(nueva_op);
            return nueva_op;
        } else{
            throw new ExcepcionDeTipos("No se puede operar un OR con operadores aritmeticos");
        }
    }

    @Override
    public Menor transform(Menor menor) throws ExcepcionDeTipos {
        Menor nueva_op = super.transform(menor);
        if(menor.getIzquierda().getTipo() != Tipo.BOOL && menor.getDerecha().getTipo() != Tipo.BOOL){
            nueva_op = (Menor) transformarOperacionBinaria(nueva_op);
            nueva_op.setTipo(Tipo.BOOL);
            return nueva_op;
        } else{
            throw new ExcepcionDeTipos("No se puede operar un < con operandos logicos");
        }
    }

    @Override
    public Mayor transform(Mayor mayor) throws ExcepcionDeTipos {
        Mayor nueva_op = super.transform(mayor);
        if(mayor.getIzquierda().getTipo() != Tipo.BOOL && mayor.getDerecha().getTipo() != Tipo.BOOL){
            nueva_op = (Mayor) transformarOperacionBinaria(nueva_op);
            nueva_op.setTipo(Tipo.BOOL);
            return nueva_op;
        } else{
            throw new ExcepcionDeTipos("No se puede operar un > con operandos logicos");
        }
    }

    @Override
    public MayorIgual transform(MayorIgual mayorIgual) throws ExcepcionDeTipos {
        MayorIgual nueva_op = super.transform(mayorIgual);
        if(mayorIgual.getIzquierda().getTipo() != Tipo.BOOL && mayorIgual.getDerecha().getTipo() != Tipo.BOOL){
            nueva_op = (MayorIgual) transformarOperacionBinaria(nueva_op);
            nueva_op.setTipo(Tipo.BOOL);
            return nueva_op;
        } else{
            throw new ExcepcionDeTipos("No se puede operar un >= con operandos logicos");
        }
    }

    @Override
    public MenorIgual transform(MenorIgual menorIgual) throws ExcepcionDeTipos {
        MenorIgual nueva_op = super.transform(menorIgual);
        if(menorIgual.getIzquierda().getTipo() != Tipo.BOOL && menorIgual.getDerecha().getTipo() != Tipo.BOOL){
            nueva_op = (MenorIgual) transformarOperacionBinaria(nueva_op);
            nueva_op.setTipo(Tipo.BOOL);
            return nueva_op;
        } else{
            throw new ExcepcionDeTipos("No se puede operar un <= con operandos logicos");
        }
    }

    @Override
    public IgualIgual transform(IgualIgual igualIgual) throws ExcepcionDeTipos {
        IgualIgual nueva_op = super.transform(igualIgual);
        if(igualIgual.getIzquierda().getTipo() != Tipo.FLOAT && igualIgual.getDerecha().getTipo() != Tipo.FLOAT){
            if(igualIgual.getIzquierda().getTipo() == igualIgual.getDerecha().getTipo()){
                nueva_op = (IgualIgual) transformarOperacionBinaria(nueva_op);
                nueva_op.setTipo(Tipo.BOOL);
                return nueva_op;
            } else{
                throw new ExcepcionDeTipos(String.format("No se puede realizar un == con %1$s y %2$s\n",igualIgual.getIzquierda().getTipo(),igualIgual.getDerecha().getTipo())); }
        } else{
            throw new ExcepcionDeTipos(String.format("No se puede realizar un == con %1$s y %2$s\n",igualIgual.getIzquierda().getTipo(),igualIgual.getDerecha().getTipo()));
        }
    }

    @Override
    public Distinto transform(Distinto distinto) throws ExcepcionDeTipos {
        Distinto nueva_op = super.transform(distinto);
        if(distinto.getIzquierda().getTipo() != Tipo.FLOAT && distinto.getDerecha().getTipo() != Tipo.FLOAT){
            if(distinto.getIzquierda().getTipo() == distinto.getDerecha().getTipo()){
                nueva_op = (Distinto) transformarOperacionBinaria(nueva_op);
                nueva_op.setTipo(Tipo.BOOL);
                return nueva_op;
            } else{
                throw new ExcepcionDeTipos(String.format("No se puede realizar un != con %1$s y %2$s\n",distinto.getIzquierda().getTipo(),distinto.getDerecha().getTipo())); }
        } else{
            throw new ExcepcionDeTipos(String.format("No se puede realizar un != con %1$s y %2$s\n",distinto.getIzquierda().getTipo(),distinto.getDerecha().getTipo()));
        }
    }

    @Override
    public Expresion transform(Not not) throws ExcepcionDeTipos {
        Not nueva_op = (Not) super.transform(not);
        if(not.getExpresion().getTipo() == Tipo.BOOL){
            nueva_op = (Not) transformarOperacionUnaria(nueva_op);
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
            nueva_op = (MenosUnario) transformarOperacionUnaria(nueva_op);
            return nueva_op;
        } else {
            throw new ExcepcionDeTipos(
                    String.format("No se puede realizar una operacion -%1$s\n",menosUnario.getExpresion().getTipo() ));
        }
    }

    @Override
    public Expresion transform(FlotanteAEntero fae) throws ExcepcionDeTipos {
        FlotanteAEntero nueva_op = (FlotanteAEntero) super.transform(fae);
        //nueva_op = (FlotanteAEntero) transformarOperacionUnaria(nueva_op);
        return nueva_op;
    }

    @Override
    public Expresion transform(EnteroAFlotante eaf) throws ExcepcionDeTipos {
        EnteroAFlotante nueva_op = (EnteroAFlotante) super.transform(eaf);
        //nueva_op = (EnteroAFlotante) transformarOperacionUnaria(nueva_op);
        return nueva_op;
    }

    @Override
    public DeclaracionVariable transform(DeclaracionVariable declaracionVariable) throws ExcepcionDeTipos {
        super.transform(declaracionVariable);
        if(declaracionVariable.getExpresion().getClass() == FlotanteAEntero.class || declaracionVariable.getExpresion().getClass() == EnteroAFlotante.class){
            return declaracionVariable;
        }
        if(declaracionVariable.getId().getTipo() != declaracionVariable.getExpresion().getTipo()){
            if(declaracionVariable.getId().getTipo() != Tipo.BOOL && declaracionVariable.getExpresion().getTipo() != Tipo.BOOL){ //convierto los numericos
                Tipo destino = declaracionVariable.getId().getTipo();
                declaracionVariable.setExpresion(convertir_a_tipo(declaracionVariable.getExpresion(),destino));
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
        boolean hayReturn = false;
        for (Sentencia sentencia:declaracionFuncion.getBloque().getSentencias()){
            if(sentencia.getClass() == Return.class){
                hayReturn =true;
            }
        }
        if(!hayReturn){
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
        if(aReturn.getExpresion().getTipo() != tipoRetorno){
            if(aReturn.getExpresion().getTipo() == Tipo.FLOAT || aReturn.getExpresion().getTipo() == Tipo.INTEGER){
                aReturn.setExpresion(convertir_a_tipo(aReturn.getExpresion(),tipoRetorno));
            } else {
                throw new ExcepcionDeTipos(
                        String.format("Tipo de retorno %1$s incompatible %2$s\n",aReturn.getExpresion().getTipo(), tipoRetorno));
            }
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