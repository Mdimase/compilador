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

    private boolean bloqueF = false;
    private Stack<Bloque> alcances = new Stack<Bloque>();
    private Alcance alcance_actual; //alcance actual de un bloque determinado
    private Alcance alcance_global; //alcance al que todos pueden acceder

    public GeneradorAlcances(Alcance alcance_global) {
        this.alcance_global = alcance_global;
    }

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
    }

    // dispara toda la generacion de alcances del AST
    public void procesar(Programa programa) throws ExcepcionDeAlcance{
        this.visit(programa);   // como aca no hay visit(programa) usa de la superclase
    }

    @Override
    public Void visit(Bloque bloque) throws ExcepcionDeAlcance {
        if(bloque.getNombre().equals("DECLARACIONES")){
            alcance_actual = alcance_global;
            alcances.push(bloque);
            System.out.println("bloque: " + bloque.getAlcance().getNombre());
            System.out.println("actual: " + alcance_actual.getNombre());
            System.out.println("padre: " + bloque.getAlcance().getPadre());
            System.out.println("\n");
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
                    System.out.println("bloque: " + bloque.getAlcance().getNombre());
                    System.out.println("actual: " + alcance_actual.getNombre());
                    System.out.println("padre: " + bloque.getAlcance().getPadre().getNombre());
                    System.out.println("\n");
                } else {
                    bloque.setAlcance(alcance_actual);
                    alcance_actual = bloque.getAlcance();
                    bloqueF=true;   // seteo flag para que el proximo bloque no entre aca, ya que seran if o while. si no hay if o while internos, lo soluciona el flag en el pop()
                    System.out.println("bloque: " + bloque.getAlcance().getNombre());
                    System.out.println("actual: " + alcance_actual.getNombre());
                    System.out.println("padre: " + bloque.getAlcance().getPadre().getNombre());
                    System.out.println("\n");
                }
            } else{
                bloque.setAlcance(new Alcance(bloque.getNombre(),alcances.peek().getAlcance()));
                alcance_actual = bloque.getAlcance();
                    System.out.println("bloque: " + bloque.getAlcance().getNombre());
                    System.out.println("actual: " + alcance_actual.getNombre());
                    System.out.println("padre: " + bloque.getAlcance().getPadre().getNombre());
                    System.out.println("\n");
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
    }

    // cuando llegue a visit(decaracionVariable) aca si esta, por ende, usa este y no el de la superclase
    @Override
    public Void visit(DeclaracionVariable dv) throws ExcepcionDeAlcance{
        if(alcance_actual == alcance_global){
            return null;
        }
        Variable var = new Variable(dv);    // var : declaracionVariable
        Object result = this.agregarSimbolo(var.getDeclaracion().getId().getNombre(), dv);
        if(result!=null){   //repetido
            throw new ExcepcionDeAlcance(String.format("El nombre de la variable %1$s de tipo %2$s fue utilizado previamente\"]\n",
                    dv.getId().getNombre(), dv.getTipo() ));
        }
        super.visit(dv);
        return null;
    }

    public boolean estaDeclarado(Identificador identificador){
        boolean esta=true;
        Object elemento = alcance_actual.resolver(identificador.getNombre());
        if(elemento == null){
            esta=false;
        }
        return esta;
    }

    @Override
    public Void visit(Identificador identificador) throws ExcepcionDeAlcance {
        if(!estaDeclarado(identificador)){
            throw new ExcepcionDeAlcance(String.format("%1$s NO esta declarado previamente\"]\n",identificador.getNombre()));
        }
        return null;
    }

    @Override
    public Void visit(DeclaracionFuncion declaracionFuncion) throws ExcepcionDeAlcance{
        alcance_actual = new Alcance("BLOQUE_FUNCION",alcance_global);  //esto para que meta los parametros en un diccionario perteneciente al bloque funcion como pedia el enunciado que los parametros tengan la misma validez que una variable local al bloque
        if(!declaracionFuncion.getParametros().isEmpty()){
            for (Parametro parametro:declaracionFuncion.getParametros()){
                Object resultP = this.agregarSimbolo(parametro.getIdentificador().getNombre(), parametro);
                if(resultP!=null){   //repetido
                    throw new ExcepcionDeAlcance(String.format("El nombre del parametro %1$s de tipo %2$s fue utilizado previamente\"]\n",
                            parametro.getIdentificador().getNombre(), parametro.getTipo()));
                }
            }
        }
        super.visit(declaracionFuncion);
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
    protected Void procesarWhenIs(WhenIs whenIs, Void expresion, Void bloque) {
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
    protected Void procesarFor(For aFor, Void identificador, Void bloque, Void from, Void to, Void by) {
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