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

// no retorna nada
// clase que se encargara de recorrer el AST y setear los valores de alcance correspondientes
public class GeneradorAlcances extends Visitor<Void> {
    
    private Alcance alcance_actual; //alcance actual de un bloque determinado
    private Alcance alcance_global; //alcance al que todos pueden acceder
    boolean anidamientoFlag = false;

    // dispara toda la generacion de alcances del AST
    public void procesar(Programa programa) throws ExcepcionDeAlcance{
        /*

            programa.getDeclaraciones().setAlcance(new Alcance("global"));  //bloque declaraciones tiene alcance global
            Alcance padre = programa.getDeclaraciones().getAlcance();
            programa.getCuerpo().setAlcance(new Alcance("Main",padre)); // seteo alcance de bloque main, que tendra como padre a bloque declaraciones
            alcance_global = alcance_actual = programa.getCuerpo().getAlcance();    //REVISAR
            this.visit(programa);   // como aca no hay visit(programa) usa de la superclase

         */
        this.visit(programa);   // como aca no hay visit(programa) usa de la superclase
    }
    
    private Object agregarSimbolo(String nombre, Object s){
        // agregar un nodo, en este ejemplo declaraciones de variables, al alcance actual, si no estaba
        return this.alcance_actual.putIfAbsent(nombre, s);  //retorna lo que habia previamente, si no habia nada tira null
    }

    /* aca el primer bloque que venga puede ser de 2 formas posibles:
        1) venga un bloque declaraciones, el cual sera mi alcance global
        2) no venga bloque declaraciones, sino,un bloque main,ahora, sera alcance global xq no hay declaraciones encima de el
         */
    public Void visit(Bloque bloque) throws ExcepcionDeAlcance {
        if (alcance_global == null){    //aca entra solo el primer bloque
            this.declaracionesGlobal(bloque);
            if (bloque.esProgramaPrincipal()){  // bloque main sin declaraciones
                this.mainSinDeclaraciones(bloque);
                System.out.println("bloque: " + bloque.getAlcance().getNombre());
                System.out.println("actual: " + alcance_actual.getNombre());
                System.out.println("padre: " + bloque.getAlcance().getPadre());
            }
        } else{ //global ya establecido
            if (bloque.esProgramaPrincipal()){  // bloque main con declaraciones previas
                this.mainConDeclaraciones(bloque);
            }

            if(alcance_actual.getNombre().equals("global")){
                this.anidar(bloque); //anido
                anidamientoFlag = this.haveAnidamiento(bloque);  //dejo un flag que indica si tiene bloques anidados
                System.out.println(anidamientoFlag);
                System.out.println("anido xq estaba en global");
                System.out.println("bloque: " + bloque.getAlcance().getNombre());
                System.out.println("padre: " + bloque.getAlcance().getPadre().getNombre());
                System.out.println("actual: " + alcance_actual.getNombre());

            } else{
                if(anidamientoFlag){    //debo anidarme
                    this.anidar(bloque); //anido
                    anidamientoFlag = this.haveAnidamiento(bloque);
                    System.out.println("anido");
                } else{     //no debo anidarme
                    this.desapilar(bloque);
                    anidamientoFlag=this.haveAnidamiento(bloque);
                    System.out.println("no anido");
                }

                System.out.println("bloque: " + bloque.getAlcance().getNombre());
                System.out.println("padre: " + bloque.getAlcance().getPadre().getNombre());
                System.out.println("actual: " + alcance_actual.getNombre());
            }

        }

            //}
        //}
        super.visit(bloque);    //visito a visit(Bloque) de Visitor, para recorrer las sentencias de este bloque
        return null;
    }

    // seteo bloque declaraciones como global
    public void declaracionesGlobal(Bloque bloque){
        bloque.setAlcance(new Alcance("global"));
        this.alcance_global = this.alcance_actual = bloque.getAlcance();
    }

    //seteo main como global
    public void mainSinDeclaraciones(Bloque bloque){
        bloque.setAlcance(new Alcance("global"));
        this.alcance_global = this.alcance_actual = bloque.getAlcance();

    }

    //seteo main como hijo de global
    public void mainConDeclaraciones (Bloque bloque){
        bloque.setAlcance(new Alcance("main",alcance_global));  //seteo su padre, que sera el alcance global
        this.alcance_actual = bloque.getAlcance();
    }

    // bloques anidados
    public void anidar (Bloque bloque){
        bloque.setAlcance(new Alcance(bloque.getNombre(),alcance_actual));   //seteo el alcance con su nombre y padre
        this.alcance_actual = bloque.getAlcance();  //actualizo alcance actual
        this.alcance_actual.setPadre(bloque.getAlcance().getPadre());   //actualizo el padre
    }

    public void desapilar (Bloque bloque){
        bloque.setAlcance(new Alcance(bloque.getNombre(),alcance_global));   //seteo el alcance con su nombre y padre
        this.alcance_actual = bloque.getAlcance();  //actualizo alcance actual
        this.alcance_actual.setPadre(bloque.getAlcance().getPadre());   //actualizo el padre
    }

    public boolean haveAnidamiento(Bloque bloque){
        boolean anidamientoFlag = false;
        for (Sentencia sentencia : bloque.getSentencias()) {
            if (sentencia.getClass() == While.class || sentencia.getClass() == If.class || sentencia.getClass() == Bloque.class) {
                anidamientoFlag = true;
            }
        }
        return anidamientoFlag;
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
    
     //sobreescribir el visit(declaracionFuncion)

     // sobreescribir el visit(bloque)

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
