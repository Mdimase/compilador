package compilador.visitor;

import compilador.ast.base.*;
import compilador.ast.instrucciones.DeclaracionVariable;
import compilador.ast.instrucciones.If;
import compilador.ast.instrucciones.Sentencia;
import compilador.ast.operaciones.binarias.*;
import compilador.ast.operaciones.unarias.EnteroAFlotante;
import compilador.ast.operaciones.unarias.FlotanteAEntero;
import compilador.ast.operaciones.unarias.Not;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Rewriter extends Transformer{
    private int n = 1;

    public Programa procesar(Programa programa) throws ExcepcionDeTipos {
        return programa.accept_transfomer(this);
    }

    //obtengo la condicion, segun la comparacion que tenga el whenIs
    // instancia la condicion correspondiente y le setea su tipo
    public Comparacion resolverCondicion(Expresion expresionBase, WhenIs whenIs){
        Comparacion condicion = null;
        if(whenIs.getSimboloCmp().getComparador().equals(Comparador.MAYOR)){
            condicion = new Mayor(expresionBase,whenIs.getExpresion());
        }
        if (whenIs.getSimboloCmp().getComparador().equals(Comparador.MAYORIGUAL)){
            condicion = new MenorIgual(expresionBase, whenIs.getExpresion());
        }
        if (whenIs.getSimboloCmp().getComparador().equals(Comparador.MENOR)){
            condicion = new Menor(expresionBase, whenIs.getExpresion());
        }
        if (whenIs.getSimboloCmp().getComparador().equals(Comparador.MENORIGUAL)){
            condicion = new MenorIgual(expresionBase, whenIs.getExpresion());
        }
        if (whenIs.getSimboloCmp().getComparador().equals(Comparador.IGUALIGUAL)){
            condicion = new IgualIgual(expresionBase, whenIs.getExpresion());
        }
        if (whenIs.getSimboloCmp().getComparador().equals(Comparador.DISTINTO)){
            condicion = new Distinto(expresionBase, whenIs.getExpresion());
        }
        assert condicion != null;
        condicion.setTipo(Tipo.BOOL);
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
        If current_if = null;
        If global_if = null;    //if principal
        List<Sentencia> sentencias = new ArrayList<>(); //sentencias del bloque a retornar
        // sigo recorriendo el arbol por si hay when anidados. NOTA: deberia haber sido la primera linea de este metodo
        when = (When) super.transform(when);    //tuve que hacer que el transform(when) retorne una sentencia, por eso ahora casteo

        Identificador identificador = new Identificador(crearNombreUnico(),when.getExpresionBase().getTipo());
        DeclaracionVariable dv = new DeclaracionVariable(identificador,when.getExpresionBase().getTipo(),when.getExpresionBase());  //expresion que se comparara
        sentencias.add(dv); //agrego la declaracion de variable inicial del bloque
        Bloque bloqueTransformado = new Bloque(sentencias,"When -> If",new Alcance("When->If"));

        //engancho el padre y acomodo el alcance
        Alcance alcancePadre = when.getWhenIs().get(0).getBloque().getAlcance().getPadre();
        bloqueTransformado.getAlcance().setPadre(alcancePadre);

        for(WhenIs wi: when.getWhenIs()){   //recorro los whenIs
            Comparacion cmp = this.resolverCondicion(when.getExpresionBase(), wi); //obtengo la condicion, segun la comparacion que tenga el whenIs
            if(current_if == null){ //primera comparacion -> If principal
                wi.getBloque().getAlcance().setPadre(bloqueTransformado.getAlcance());  //acomodo el alcance
                current_if = new If(cmp, wi.getBloque());   //creo el if principal, con bloque del primer whenIs
                current_if.getBloqueThen().setNombre("BLOQUE_THEN");
                global_if = current_if;
            } else {    //a partir de la segunda comparacion -> empiezan las cadenas de else/if
                If newIf = new If(cmp,wi.getBloque()); //creo el nuevo if
                newIf.getBloqueThen().setNombre("BLOQUE_THEN");
                List<Sentencia> ls = new ArrayList<>();
                ls.add(newIf);  //agrego el nuevo if a una lista
                Bloque bloqueElse = new Bloque(ls,"BLOQUE_ELSE");   //meto la lista con el nuevo if en un bloqueElse
                bloqueElse.setAlcance(wi.getBloque().getAlcance()); //acomodo el alcance
                bloqueElse.getAlcance().setPadre(bloqueTransformado.getAlcance());  //seteo padre
                current_if.setBloqueElse(bloqueElse);  //else if. seteo un bloqueElse al if actual
                current_if=newIf;   //actualizo el current if
            }
        }
        // para el else del final(opcional)
        if(current_if != null && when.getBloqueElse() != null){
            current_if.setBloqueElse(when.getBloqueElse()); //seteo el bloqueElse al if mas interno
            current_if.getBloqueElse().setNombre("BLOQUE_ELSE");
            current_if.getBloqueElse().getAlcance().setPadre(bloqueTransformado.getAlcance());  //acomodo alcance
        }
        bloqueTransformado.getSentencias().add(global_if);  //agrego al bloque (tenia solo la dv) todos los if anidados
        return bloqueTransformado;
    }

    //CONSTANT FOLDING con conversiones implicitas entre int y float
    // EN TODOS LOS CASOS SI LOS OPERANDO/S NO SON CONSTANTES NO HAGO NADA, RETORNO EL OBJETO COMO ESTABA

    // NOTA: SE PODRIA HABER IMPLEMENTADO DE OTRA FORMA, USANDO UN MISMO METODO DESDE ACA Y QUE LA LOGICA RECAIGA EN CADA OPERACION
    //       PEERO NO SE SI ENTRA EN CONFLICTO CON LO QUE HABLAMOS DE QUE LOS NODOS NO TENGAN LOGICA DE ALGUNA OPERACION PUNTUAL

    /*
    el resto de operaciones serian iguales a esta
    cada nodo operacion debera implementrar su propio resolverConstantes();
    en la clase operacion binaria y unaria estara este metodo como abstract

    @Override
    public Expresion transform(Resta r) throws ExcepcionDeTipos {
        r = (Resta) super.transform(r);
        return r.resolverConstantes();
    }*/

    /*
    // implementacion de resolverConstantes() para la resta. cada operacion tendra el suyo
    @Override
    public Expresion resolverConstantes(){
        if(this.getIzquierda.resolverConstantes() instanceof Constante && this.getDerecha().resolverConstantes() instanceof Constante){
            Constante constanteIzquierda = (Constante) this.getIzquierda.resolverConstantes();  //obtengo izquierda
            Constante constanteDerecha = (Constante) this.getDerecha.resolverConstantes();  //obtengo derecha
            float ci = Float.parseFloat((String) constanteIzquierda.getValor());    //parseo a flaot
            float cd = Float.parseFloat((String) constanteDerecha.getValor());  //parseo a float
            float result = ci-cd;   //resto
            if(constanteIzquierda.getTipo() == Tipo.INTEGER && constanteDerecha.getTipo() == Tipo.INTEGER){ //son integer
                int intResult = (int) result;   //convierto a int
                return new Constante(Integer.toString(intResult),Tipo.INTEGER); //retorno la constante resutlado de tipo int
            }
            if(constanteIzquierda.getTipo() == Tipo.FLOAT && constanteDerecha.getTipo() == Tipo.FLOAT){ //son float
                return new Constante(Float.toString(result),Tipo.FLOAT);    //retorno la constante resultado de tipo float
            }
        }
        return this; //cuando no hay constantes
    } */

    public Constante evaluarAritmeticosBinarios(OperacionBinaria operacionBinaria) {
        String result = "";
        Tipo tipo;
        Constante constanteIz = (Constante) operacionBinaria.getIzquierda();
        Constante constanteDer = (Constante) operacionBinaria.getDerecha();
        // separo los integer de los float unicamente por el ParseInt o ParseFloat
        if (constanteIz.getTipo().equals(Tipo.INTEGER) && constanteDer.getTipo().equals(Tipo.INTEGER)) {
            Integer valorIz = Integer.parseInt((String) constanteIz.getValor());
            Integer valorDer = Integer.parseInt((String) constanteDer.getValor());
            tipo=Tipo.INTEGER;
            if(operacionBinaria.getClass() == Suma.class){
                result = String.valueOf(valorIz + valorDer);
            }
            if(operacionBinaria.getClass() == Resta.class){
                result = String.valueOf(valorIz - valorDer);
            }
            if(operacionBinaria.getClass() == Multiplicacion.class){
                result = String.valueOf(valorIz * valorDer);
            }
            if(operacionBinaria.getClass() == Division.class){
                result = String.valueOf(valorIz / valorDer);
            }
        } else {    //son float
            Float valorIz = Float.parseFloat((String) constanteIz.getValor());
            Float valorDer = Float.parseFloat((String) constanteDer.getValor());
            tipo = Tipo.FLOAT;
            if(operacionBinaria.getClass() == Suma.class){
                result = String.valueOf(valorIz + valorDer);
            }
            if(operacionBinaria.getClass() == Resta.class){
                result = String.valueOf(valorIz - valorDer);
            }
            if(operacionBinaria.getClass() == Multiplicacion.class){
                result = String.valueOf(valorIz * valorDer);
            }
            if(operacionBinaria.getClass() == Division.class){
                result = String.valueOf(valorIz / valorDer);
            }
        }
        return new Constante(result,tipo);
    }

    @Override
    public Expresion transform(EnteroAFlotante eaf) throws ExcepcionDeTipos {
        super.transform(eaf);
        if(eaf.getExpresion().getClass() == Constante.class){
            Constante constante = (Constante) eaf.getExpresion();
            Float valorF = Float.parseFloat((String) constante.getValor()); //convierto a float
            return new Constante(String.valueOf(valorF) ,Tipo.FLOAT);   //devuelvo la constante con el neuvo valor float
        } else {
            return eaf;
        }
    }

    @Override
    public Expresion transform(FlotanteAEntero fae) throws ExcepcionDeTipos {
        super.transform(fae);   //expresion asociada resolver
        if(fae.getExpresion().getClass() == Constante.class){
            Constante constante = (Constante) fae.getExpresion();
            //Integer vi = Integer.parseInt((String) constante.getValor()); // si se podia, lo habre escrito mal
            float valorF = Float.parseFloat((String) constante.getValor()); //no se puede hacer un parseInt directo
            Integer valorI = (int) valorF;  //convierto a int
            return new Constante(String.valueOf(valorI) ,Tipo.INTEGER); // devuelvo la constante con el nuevo valor int
        } else {
            return fae;
        }
    }

    @Override
    public Expresion transform(Resta resta) throws ExcepcionDeTipos {
        super.transform(resta); //operandos resuelvanse
        if(resta.getIzquierda().getClass() == Constante.class && resta.getDerecha().getClass() == Constante.class){
            return this.evaluarAritmeticosBinarios(resta);
        } else { //no constantes
            return resta;
        }
    }

    @Override
    public Expresion transform(Suma suma) throws ExcepcionDeTipos {
        super.transform(suma);
        if(suma.getIzquierda().getClass() == Constante.class && suma.getDerecha().getClass() == Constante.class){
            return this.evaluarAritmeticosBinarios(suma);
        } else { //no constantes
            return suma;
        }
    }

    @Override
    public Expresion transform(Multiplicacion multiplicacion) throws ExcepcionDeTipos {
        super.transform(multiplicacion);
        if(multiplicacion.getIzquierda().getClass() == Constante.class && multiplicacion.getDerecha().getClass() == Constante.class){
            return this.evaluarAritmeticosBinarios(multiplicacion);
        } else { //no constantes
            return multiplicacion;
        }
    }

    @Override
    public Expresion transform(Division division) throws ExcepcionDeTipos {
        super.transform(division);
        if(division.getIzquierda().getClass() == Constante.class && division.getDerecha().getClass() == Constante.class){
            return this.evaluarAritmeticosBinarios(division);
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
                return new Constante("false",Tipo.BOOL);    //retorno el inverso
            } else{
                return new Constante("true",Tipo.BOOL); // retorno el inverso
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

    @Override
    public Expresion transform(Menor menor) throws ExcepcionDeTipos {
        super.transform(menor);
        if(menor.getIzquierda().getClass() == Constante.class && menor.getDerecha().getClass() == Constante.class){
            Constante constanteIz = (Constante) menor.getIzquierda();
            Constante constanteDer = (Constante) menor.getDerecha();
            if(constanteIz.getTipo().equals(Tipo.INTEGER) && constanteDer.getTipo().equals(Tipo.INTEGER)){
                int valorIz = Integer.parseInt((String) constanteIz.getValor());
                int valorDer = Integer.parseInt((String) constanteDer.getValor());
                if(valorIz < valorDer){ //menor
                    return new Constante("true",Tipo.BOOL);
                }
                else {  //mayor o igual
                    return new Constante("false",Tipo.BOOL);
                }
            } else {    //son float
                float valorIz = Float.parseFloat((String) constanteIz.getValor());
                float valorDer = Float.parseFloat((String) constanteDer.getValor());
                if(valorIz < valorDer){ //menor
                    return new Constante("true",Tipo.BOOL);
                }
                else {  //mayor o igual
                    return new Constante("false",Tipo.BOOL);
                }
            }
        } else { //no constantes
            return menor;
        }
    }

    @Override
    public Expresion transform(Mayor mayor) throws ExcepcionDeTipos {
        super.transform(mayor);
        if(mayor.getIzquierda().getClass() == Constante.class && mayor.getDerecha().getClass() == Constante.class){
            Constante constanteIz = (Constante) mayor.getIzquierda();
            Constante constanteDer = (Constante) mayor.getDerecha();
            if(constanteIz.getTipo().equals(Tipo.INTEGER) && constanteDer.getTipo().equals(Tipo.INTEGER)){
                int valorIz = Integer.parseInt((String) constanteIz.getValor());
                int valorDer = Integer.parseInt((String) constanteDer.getValor());
                if(valorIz > valorDer){ //menor
                    return new Constante("true",Tipo.BOOL);
                }
                else {  //mayor o igual
                    return new Constante("false",Tipo.BOOL);
                }
            } else {    //son float
                float valorIz = Float.parseFloat((String) constanteIz.getValor());
                float valorDer = Float.parseFloat((String) constanteDer.getValor());
                if(valorIz > valorDer){ //menor
                    return new Constante("true",Tipo.BOOL);
                }
                else {  //mayor o igual
                    return new Constante("false",Tipo.BOOL);
                }
            }
        } else { //no constantes
            return mayor;
        }
    }

    @Override
    public Expresion transform(MenorIgual menorIgual) throws ExcepcionDeTipos {
        super.transform(menorIgual);
        if(menorIgual.getIzquierda().getClass() == Constante.class && menorIgual.getDerecha().getClass() == Constante.class){
            Constante constanteIz = (Constante) menorIgual.getIzquierda();
            Constante constanteDer = (Constante) menorIgual.getDerecha();
            if(constanteIz.getTipo().equals(Tipo.INTEGER) && constanteDer.getTipo().equals(Tipo.INTEGER)){
                int valorIz = Integer.parseInt((String) constanteIz.getValor());
                int valorDer = Integer.parseInt((String) constanteDer.getValor());
                if(valorIz <= valorDer){ //menor
                    return new Constante("true",Tipo.BOOL);
                }
                else {  //mayor o igual
                    return new Constante("false",Tipo.BOOL);
                }
            } else {    //son float
                float valorIz = Float.parseFloat((String) constanteIz.getValor());
                float valorDer = Float.parseFloat((String) constanteDer.getValor());
                if(valorIz <= valorDer){ //menor
                    return new Constante("true",Tipo.BOOL);
                }
                else {  //mayor o igual
                    return new Constante("false",Tipo.BOOL);
                }
            }
        } else { //no constantes
            return menorIgual;
        }
    }

    @Override
    public Expresion transform(MayorIgual mayorIgual) throws ExcepcionDeTipos {
        super.transform(mayorIgual);
        if(mayorIgual.getIzquierda().getClass() == Constante.class && mayorIgual.getDerecha().getClass() == Constante.class){
            Constante constanteIz = (Constante) mayorIgual.getIzquierda();
            Constante constanteDer = (Constante) mayorIgual.getDerecha();
            if(constanteIz.getTipo().equals(Tipo.INTEGER) && constanteDer.getTipo().equals(Tipo.INTEGER)){
                int valorIz = Integer.parseInt((String) constanteIz.getValor());
                int valorDer = Integer.parseInt((String) constanteDer.getValor());
                if(valorIz >= valorDer){ //menor
                    return new Constante("true",Tipo.BOOL);
                }
                else {  //mayor o igual
                    return new Constante("false",Tipo.BOOL);
                }
            } else {    //son float
                float valorIz = Float.parseFloat((String) constanteIz.getValor());
                float valorDer = Float.parseFloat((String) constanteDer.getValor());
                if(valorIz >= valorDer){ //menor
                    return new Constante("true",Tipo.BOOL);
                }
                else {  //mayor o igual
                    return new Constante("false",Tipo.BOOL);
                }
            }
        } else { //no constantes
            return mayorIgual;
        }
    }

    @Override
    public Expresion transform(IgualIgual igualIgual) throws ExcepcionDeTipos {
        super.transform(igualIgual);
        if(igualIgual.getIzquierda().getClass() == Constante.class && igualIgual.getDerecha().getClass() == Constante.class){
            Constante constanteIz = (Constante) igualIgual.getIzquierda();
            Constante constanteDer = (Constante) igualIgual.getDerecha();
            if(constanteIz.getValor().equals(constanteDer.getValor())){
                return new Constante("true",Tipo.BOOL);
            }
            if (!constanteIz.getValor().equals(constanteDer.getValor())) {
                return new Constante("false",Tipo.BOOL);
            }
            if(constanteIz.getTipo().equals(Tipo.INTEGER) && constanteDer.getTipo().equals(Tipo.INTEGER)){
                int valorIz = Integer.parseInt((String) constanteIz.getValor());
                int valorDer = Integer.parseInt((String) constanteDer.getValor());
                if(valorIz == valorDer){ //menor
                    return new Constante("true",Tipo.BOOL);
                }
                else {  //mayor o igual
                    return new Constante("false",Tipo.BOOL);
                }
            } else {    //son float
                float valorIz = Float.parseFloat((String) constanteIz.getValor());
                float valorDer = Float.parseFloat((String) constanteDer.getValor());
                if(valorIz == valorDer){ //menor
                    return new Constante("true",Tipo.BOOL);
                }
                else {  //mayor o igual
                    return new Constante("false",Tipo.BOOL);
                }
            }
        } else { //no constantes
            return igualIgual;
        }
    }

    @Override
    public Expresion transform(Distinto distinto) throws ExcepcionDeTipos {
        super.transform(distinto);
        if(distinto.getIzquierda().getClass() == Constante.class && distinto.getDerecha().getClass() == Constante.class){
            Constante constanteIz = (Constante) distinto.getIzquierda();
            Constante constanteDer = (Constante) distinto.getDerecha();
            if(constanteIz.getValor().equals(constanteDer.getValor())){
                return new Constante("false",Tipo.BOOL);
            }
            if (!constanteIz.getValor().equals(constanteDer.getValor())) {
                return new Constante("true",Tipo.BOOL);
            }
            if(constanteIz.getTipo().equals(Tipo.INTEGER) && constanteDer.getTipo().equals(Tipo.INTEGER)){
                int valorIz = Integer.parseInt((String) constanteIz.getValor());
                int valorDer = Integer.parseInt((String) constanteDer.getValor());
                if(valorIz != valorDer){ //menor
                    return new Constante("true",Tipo.BOOL);
                }
                else {  //mayor o igual
                    return new Constante("false",Tipo.BOOL);
                }
            } else {    //son float
                float valorIz = Float.parseFloat((String) constanteIz.getValor());
                float valorDer = Float.parseFloat((String) constanteDer.getValor());
                if(valorIz != valorDer){ //menor
                    return new Constante("true",Tipo.BOOL);
                }
                else {  //mayor o igual
                    return new Constante("false",Tipo.BOOL);
                }
            }
        } else { //no constantes
            return distinto;
        }
    }
}