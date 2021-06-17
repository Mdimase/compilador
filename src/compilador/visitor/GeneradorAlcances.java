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

    private Stack<Bloque> alcances = new Stack<Bloque>();
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

    @Override
    public Void visit(Bloque bloque) throws ExcepcionDeAlcance {
        if(bloque.getNombre().equals("DECLARACIONES")){
            super.visit(bloque);
        } else {
            alcance_actual = new Alcance(bloque.getNombre(), alcance_actual);
            bloque.setAlcance(alcance_actual);
            super.visit(bloque);
            alcance_actual = alcance_actual.getPadre(); //esta fue parte de la solucion
        }
        return null;
    }

    /*

    //seteo main como hijo de global
    private void mainConDeclaraciones (Bloque bloque){
        bloque.setAlcance(new Alcance("main",alcance_global));  //seteo su padre, que sera el alcance global
        this.alcance_actual = bloque.getAlcance();
    }

    //seteo main como hijo de global
    private void mainSinDeclaraciones (Bloque bloque){
        alcance_global = new Alcance("global");
        bloque.setAlcance(new Alcance("main",alcance_global));  //seteo su padre, que sera el alcance global
        this.alcance_actual = bloque.getAlcance();
        alcances.push(new Bloque(new ArrayList<Sentencia>(),"DECLARACIONES",false,alcance_global)); //error empty stack
    }

    @Override
    public Void visit(Bloque bloque) throws ExcepcionDeAlcance {
        if(bloque.getNombre().equals("DECLARACIONES")){
            alcance_actual = alcance_global;
            alcances.push(bloque);
            super.visit(bloque);
            return null;
        }
        if (alcance_global == null){    //no hay declaraciones de funciones ni variables globales
            this.mainSinDeclaraciones(bloque);
        } else{ // si hay declaraciones
            if (bloque.esProgramaPrincipal()){  // bloque main con declaraciones previas
                this.mainConDeclaraciones(bloque);
            }
            if(alcance_actual.getNombre().equals("BLOQUE_FUNCION") && !bloqueF){ // cuando llegue el bloque funcion va a entrar aca
                if(bloque.getNombre().equals("BLOQUE_ELSE")){
                    bloque.setAlcance(new Alcance(bloque.getNombre(),alcances.peek().getAlcance()));
                    alcance_actual = bloque.getAlcance();
                } else {
                    bloque.setAlcance(alcance_actual);
                    alcance_actual = bloque.getAlcance();
                    bloqueF=true;   // seteo flag para que el proximo bloque no entre aca, ya que seran if o while. si no hay if o while internos, lo soluciona el flag en el pop()
                }
            } else{
                bloque.setAlcance(new Alcance(bloque.getNombre(),alcances.peek().getAlcance()));
                alcance_actual = bloque.getAlcance();
                }
            }
        alcances.push(bloque);
        super.visit(bloque);    //visito a visit(Bloque) de Visitor, para recorrer las sentencias de este bloque
        if(!alcances.peek().getAlcance().getNombre().equals("global")){
            alcances.pop();
            bloqueF=false;  // despues de sacar el bloque funcion, vuelvo a setear el flag por si viene otra funcion
            this.alcance_actual = alcances.peek().getAlcance();
        }
        return null;
    } */

    // cuando llegue a visit(decaracionVariable) aca si esta, por ende, usa este y no el de la superclase
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
        declaracionFuncion.setAlcance(alcance_actual); //esta fue parte de la solucion , asi no renegamos con los parametros
        if(!declaracionFuncion.getParametros().isEmpty()){
            for (Parametro parametro:declaracionFuncion.getParametros()){
                this.visit(parametro);
            }
        }
        this.visit(declaracionFuncion.getBloque());
        alcance_actual = alcance_actual.getPadre(); //esta fue parte de la solucion
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