/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.visitor;

import compilador.ast.base.*;
import compilador.ast.instrucciones.*;
import compilador.ast.operaciones.binarias.OperacionBinaria;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// no retorna nada
// clase que se encargara de recorrer el AST y setear los valores de alcance correspondientes
public class GeneradorAlcances extends Visitor<Void> {

    private Stack<Bloque> alcances = new Stack<Bloque>();
    private Alcance alcance_actual; //alcance actual de un bloque determinado
    private Alcance alcance_global; //alcance al que todos pueden acceder

    // seteo el alcance global, que puede ser declaraciones o main(en caso de que no haya declaraciones)
    private void setGlobal(Bloque bloque){
        bloque.setAlcance(new Alcance("global"));
        this.alcance_global = this.alcance_actual = bloque.getAlcance();
    }

    //seteo main como hijo de global
    private void mainConDeclaraciones (Bloque bloque){
        bloque.setAlcance(new Alcance("main",alcance_global));  //seteo su padre, que sera el alcance global
        this.alcance_actual = bloque.getAlcance();
    }

    // dispara toda la generacion de alcances del AST
    public void procesar(Programa programa) throws ExcepcionDeAlcance{
        this.visit(programa);   // como aca no hay visit(programa) usa de la superclase
    }

    /* aca el primer bloque que venga puede ser de 2 formas posibles:
        1) venga un bloque declaraciones, el cual sera mi alcance global
        2) no venga bloque declaraciones, sino,un bloque main,ahora, sera alcance global xq no hay declaraciones encima de el */
    public Void visit(Bloque bloque) throws ExcepcionDeAlcance {
        if (alcance_global == null){    //aca entra solo el primer bloque (declaraciones o main)
            if (!bloque.esProgramaPrincipal()){ //es declaraciones
                this.setGlobal(bloque);
            } else {  // bloque main sin declaraciones
                this.setGlobal(bloque);
                alcances.push(bloque);
                this.mainConDeclaraciones(bloque);
            }
        } else{ //global ya establecido
            if (bloque.esProgramaPrincipal()){  // bloque main con declaraciones previas
                this.mainConDeclaraciones(bloque);
            } else {
                bloque.setAlcance(new Alcance(bloque.getNombre(),alcances.peek().getAlcance()));
                this.alcance_actual = bloque.getAlcance();
                /*
                System.out.println("bloque: " + bloque.getAlcance().getNombre());
                System.out.println("actual: " + alcance_actual.getNombre());
                System.out.println("padre: " + bloque.getAlcance().getPadre().getNombre());
                System.out.println("\n"); */
            }
        }
        alcances.push(bloque);
        super.visit(bloque);    //visito a visit(Bloque) de Visitor, para recorrer las sentencias de este bloque
        if(!alcances.peek().getAlcance().getNombre().equals("global")){
            alcances.pop();
            this.alcance_actual = alcances.peek().getAlcance();
        }
        return null;
    }

    // cuando llegue a visit(decaracionVariable) aca si esta, por ende, usa este y no el de la superclase
    @Override
    public Void visit(DeclaracionVariable dv) throws ExcepcionDeAlcance{
        /*
        // EVITAR VARIABLE A IS FLOAT = 1 + PEPE();
        if(alcance_actual == alcance_global){
            Expresion exp = dv.getExpresion();
            while (exp.getClass() != InvocacionFuncion.class){
                exp =
            }
        }*/
        Variable var = new Variable(dv);    // var : declaracionVariable
        Object result = this.agregarSimbolo(var.getDeclaracion().getId().getNombre(), dv);
        if(result!=null){   //repetido
            throw new ExcepcionDeAlcance(
                    String.format("El nombre de la variable %1$s de tipo %2$s fue utilizado previamente\"]\n", 
                            dv.getId().getNombre(), dv.getTipo() ));
        }
        return null;
    }


    @Override
    public Void visit(DeclaracionFuncion declaracionFuncion) throws ExcepcionDeAlcance{
        Funcion funcion = new Funcion(declaracionFuncion);    // var : declaracionVariable
        Object result = this.agregarSimbolo(funcion.getDeclaracionFuncion().getIdentificador().getNombre(), declaracionFuncion);
        if(result!=null){   //repetido
            throw new ExcepcionDeAlcance(
                    String.format("El nombre de la funcion %1$s de tipo retorno %2$s fue utilizado previamente\"]\n",
                            declaracionFuncion.getIdentificador().getNombre(), declaracionFuncion.getTipoRetorno() ));
        }
        super.visit(declaracionFuncion);    //visito el bloque de la funcion
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

}
