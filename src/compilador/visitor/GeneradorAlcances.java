/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.visitor;

import compilador.ast.base.*;
import compilador.ast.instrucciones.*;
import compilador.ast.operaciones.binarias.OperacionBinaria;
import java.util.List;

/**
 *
 * @author ITT
 */
// no retorna nada
public class GeneradorAlcances extends Visitor<Void>{
    
    private Alcance alcance_actual;
    private Alcance alcance_global; //alcance al que todos pueden acceder

    // dispara toda la generacion de alcances del AST
    public void procesar(Programa programa) throws ExcepcionDeAlcance{
        programa.getCuerpo().setAlcance(new Alcance("global")); // el alcance del cuerpo del programa va a ser global
        alcance_global = alcance_actual = programa.getCuerpo().getAlcance();    // el actual y global es el mismo xq es el unico bloque en este ejemplo
        this.visit(programa);   // como aca no hay visit(programa) usa de la superclase
    }
    
    private Object agregarSimbolo(String nombre, Object s){
        // agregar un nodo, en este ejemplo declaraciones de variables, a nuestro alcance actual, si no estaba
        return this.alcance_actual.putIfAbsent(nombre, s);  //retorna lo que habia previamente, si no habia nada tira null
    }

    // cuando llegue a visit(decaracionVariable) aca si esta, por ende, usa este y no el de la superclase
    @Override
    public Void visit(DeclaracionVariable dv) throws ExcepcionDeAlcance{
        Variable var = new Variable(dv);
        Object result = this.agregarSimbolo(var.getDeclaracion().getId().getNombre(), dv);
        if(result!=null){
            throw new ExcepcionDeAlcance(
                    String.format("El nombre de la variable %1$s de tipo %2$s fue utilizado previamente\"]\n", 
                            dv.getId().getNombre(), dv.getTipo() ));
        }
        return null;
    }

    //revisar esto si todos retornan null esta bien?

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
    protected Void procesarWhile(While aWhile, Void expresion, List<Void> sentencias) {
        return null;
    }

    @Override
    protected Void procesarFor(For aFor, Void identificador, List<Void> sentencias, Void from, Void to, Void by) {
        return null;
    }

    //sobreescribir el visit(declaracionFuncion)

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
    protected Void procesarIf(If anIf, Void expresion, List<Void> sentencias) {
        return null;
    }

    @Override
    protected Void procesarIf(If anIf, Void expresion, List<Void> sentenciasIf, List<Void> sentenciasElse) {
        return null;
    }


}
