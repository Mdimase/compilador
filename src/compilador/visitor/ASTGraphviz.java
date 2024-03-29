/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.visitor;

import compilador.ast.base.*;
import compilador.ast.instrucciones.*;
import compilador.ast.operaciones.binarias.OperacionBinaria;
import compilador.ast.operaciones.unarias.OperacionUnaria;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

// Visitor<String> me obliga a que todos mis metodos ,@override del Visitor, retornen un String
public class ASTGraphviz extends Visitor<String>{

    private final Deque<Integer> parents;   //pila de IDs de los nodos
    /* cuando un nodo se grafique ,antes de que llame al graficado de sus hijos, va a apilar su ID
     * para que los hijos miren esa pila y sepan a quien engancharse dentro del grafico (lenguaje DOT) */
    private int current_id = 0; // id del nodo actual

    public ASTGraphviz() {
        this.parents = new ArrayDeque<>();
    }

    @Override
    //graficar programa
    public String visit(Programa p) throws ExcepcionDeAlcance{
        StringBuilder resultado = new StringBuilder();  // aca guardo el DOT completo
        resultado.append("graph G {");  //abrimos el grafico, sintaxis DOT
        current_id = this.getID();
        resultado.append(this.procesarNodo(p)); //grafico nodo programa
        parents.push(current_id);   //apilo el id del nodo programa, para que sus hijos se enganchen a este nodo
        resultado.append(super.visit(p));   //disparo los visit de los atributos de prorgama, es decir, bloque. invoca el visit(programa) de Visitor
        parents.pop(); //desapilo
        resultado.append("}");  //cierro el grafico
        return resultado.toString();
    }

    @Override
    //graficar bloque
    public String visit(Bloque b) throws ExcepcionDeAlcance{
        StringBuilder resultado = new StringBuilder();
        current_id = this.getID();
        resultado.append(this.procesarNodo(b));
        parents.push(current_id);
        resultado.append(super.visit(b)); //disparo los visit de los atributos nodo, es decir, cada una de las sentencia que componen el bloque
        parents.pop();
        return resultado.toString();
    }

    @Override
    public String visit(Asignacion a) throws ExcepcionDeAlcance{
        StringBuilder resultado = new StringBuilder();
        current_id = this.getID();
        resultado.append(this.procesarNodo(a));
        parents.push(current_id);
        resultado.append(super.visit(a));
        parents.pop();
        return resultado.toString();
    }

    @Override
    public String visit(OperacionBinaria ob) throws ExcepcionDeAlcance{
        StringBuilder resultado = new StringBuilder();
        current_id = this.getID();
        resultado.append(this.procesarNodo(ob));
        parents.push(current_id);
        resultado.append(super.visit(ob));
        parents.pop();
        return resultado.toString();
    }

    //graficar operacion unaria
    @Override
    public String visit(OperacionUnaria operacionUnaria) throws ExcepcionDeAlcance{
        StringBuilder resultado = new StringBuilder();
        current_id = this.getID();
        resultado.append(this.procesarNodo(operacionUnaria));
        parents.push(current_id);
        resultado.append(super.visit(operacionUnaria));
        parents.pop();
        return resultado.toString();
    }

    @Override
    public String visit(DeclaracionVariable dv) throws ExcepcionDeAlcance{
        StringBuilder resultado = new StringBuilder();
        current_id = this.getID();
        resultado.append(this.procesarNodo(dv));
        parents.push(current_id);
        resultado.append(super.visit(dv)); //invoco los visit de sus nodos atributos
        parents.pop();
        return resultado.toString();
    }

    @Override
    public String visit(DeclaracionFuncion declaracionFuncion) throws ExcepcionDeAlcance{
        StringBuilder resultado = new StringBuilder();
        current_id = this.getID();
        resultado.append(this.procesarNodo(declaracionFuncion));
        parents.push(current_id);
        resultado.append(super.visit(declaracionFuncion)); //invoco los visit de sus nodos atributos
        parents.pop();
        return resultado.toString();
    }

    @Override
    public String visit(Write w) throws ExcepcionDeAlcance{
        StringBuilder resultado = new StringBuilder();
        current_id = this.getID();
        resultado.append(this.procesarNodo(w));
        parents.push(current_id);
        resultado.append(super.visit(w));
        parents.pop();
        return resultado.toString();
    }

    @Override
    public String visit(Mensaje m){
        StringBuilder resultado = new StringBuilder();
        current_id = this.getID();
        resultado.append(this.procesarNodo(m));
        parents.push(current_id);
        parents.pop();
        return resultado.toString();
    }

    @Override
    public String visit(Read read){
        StringBuilder resultado = new StringBuilder();
        current_id = this.getID();
        resultado.append(this.procesarNodo(read));
        parents.push(current_id);
        parents.pop();
        return resultado.toString();
    }

    //graficar nodo while
    @Override
    public String visit(While aWhile) throws ExcepcionDeAlcance{
        StringBuilder resultado = new StringBuilder();
        current_id = this.getID();
        resultado.append(this.procesarNodo(aWhile));
        parents.push(current_id);
        resultado.append(super.visit(aWhile)); //invoco los visit de sus nodos atributos
        parents.pop();
        return resultado.toString();
    }

    @Override
    public String visit(If i) throws ExcepcionDeAlcance{
        StringBuilder resultado = new StringBuilder();
        current_id = this.getID();
        resultado.append(this.procesarNodo(i));
        parents.push(current_id);
        resultado.append(super.visit(i)); //invoco los visit de sus nodos atributos
        parents.pop();
        return resultado.toString();
    }

    //graficar nodo inv_funcion
    @Override
    public String visit(InvocacionFuncion invocacionFuncion) throws ExcepcionDeAlcance{
        StringBuilder resultado = new StringBuilder();
        current_id = this.getID();
        resultado.append(this.procesarNodo(invocacionFuncion));
        parents.push(current_id);
        resultado.append(super.visit(invocacionFuncion)); //invoco los visit de sus nodos atributos
        parents.pop();
        return resultado.toString();
    }

    @Override
    //graficar nodo constante, es Hoja
    //no ejecuta un super.visit xq no tiene atributos nodo. es una hoja
    public String visit(Constante c) {
        StringBuilder resultado = new StringBuilder();
        current_id = this.getID();
        resultado.append(this.procesarNodo(c));
        parents.push(current_id);
        parents.pop();
        return resultado.toString();
    }

    @Override
    public String visit(Identificador i) {
        StringBuilder resultado = new StringBuilder();
        current_id = this.getID();
        resultado.append(this.procesarNodo(i));
        parents.push(current_id);
        parents.pop();
        return resultado.toString();
    }

    @Override
    public String visit(Parametro parametro) throws ExcepcionDeAlcance {
        StringBuilder resultado = new StringBuilder();
        current_id = this.getID();
        resultado.append(this.procesarNodo(parametro));
        parents.push(current_id);
        resultado.append(super.visit(parametro));
        parents.pop();
        return resultado.toString();
    }

    @Override
    public String visit(Continue c) throws ExcepcionDeAlcance {
        StringBuilder resultado = new StringBuilder();
        current_id = this.getID();
        resultado.append(this.procesarNodo(c));
        parents.push(current_id);
        parents.pop();
        return resultado.toString();
    }

    @Override
    public String visit(Break b) throws ExcepcionDeAlcance {
        StringBuilder resultado = new StringBuilder();
        current_id = this.getID();
        resultado.append(this.procesarNodo(b));
        parents.push(current_id);
        parents.pop();
        return resultado.toString();
    }

    //graficar nodo return
    @Override
    public String visit(Return r) throws ExcepcionDeAlcance{
        StringBuilder resultado = new StringBuilder();
        current_id = this.getID();
        resultado.append(this.procesarNodo(r));
        parents.push(current_id);
        resultado.append(super.visit(r));
        parents.pop();
        return resultado.toString();
    }

    @Override
    public String visit(When when) throws ExcepcionDeAlcance{
        StringBuilder resultado = new StringBuilder();
        current_id = this.getID();
        resultado.append(this.procesarNodo(when));
        parents.push(current_id);
        resultado.append(super.visit(when));
        parents.pop();
        return resultado.toString();
    }


    @Override
    public String visit(WhenIs wi) throws ExcepcionDeAlcance{
        StringBuilder resultado = new StringBuilder();
        current_id = this.getID();
        resultado.append(this.procesarNodo(wi));
        parents.push(current_id);
        resultado.append(super.visit(wi));
        parents.pop();
        return resultado.toString();
    }

    @Override
    public String visit(SimboloCmp simboloCmp){
        StringBuilder resultado = new StringBuilder();
        current_id = this.getID();
        resultado.append(this.procesarNodo(simboloCmp));
        parents.push(current_id);
        parents.pop();
        return resultado.toString();
    }

    @Override
    protected String procesarWhenIs(WhenIs whenIs, String simboloCmp, String expresion, String bloque) {
        return simboloCmp+expresion+bloque;
    }

    @Override
    protected String procesarWhen(When when, String expresion, List<String> whenIs, String bloque) {
        StringBuilder resultado = new StringBuilder();
        resultado.append(expresion);
        whenIs.forEach(resultado::append);
        resultado.append(bloque);
        return resultado.toString();
    }

    @Override
    protected String procesarWhen(When when, String expresion, List<String> whenIs) {
        StringBuilder resultado = new StringBuilder();
        resultado.append(expresion);
        whenIs.forEach(resultado::append);
        return resultado.toString();
    }

    @Override
    protected String procesarParametro(Parametro parametro, String identificador, String valor_defecto) {
        return identificador+valor_defecto;
    }

    @Override
    protected String procesarParametro(Parametro parametro, String identificador) {
        return identificador;
    }

    @Override
    protected String procesarPrograma(Programa programa, String declaraciones, String sentencias) {
        return declaraciones+sentencias;
    }

    @Override
    protected String procesarDeclaracionVariable(DeclaracionVariable declaracionVariable, String identificador, String expresion) {
        return identificador+expresion;
    }

    @Override
    protected String procesarDeclaracionFuncion(DeclaracionFuncion declaracionFuncion, String identificador, String bloque) {
        return identificador + bloque;
    }

    @Override
    protected String procesarDeclaracionFuncion(DeclaracionFuncion declaracionFuncion, String identificador, List<String> parametros, String bloque) {
        StringBuilder resultado = new StringBuilder();
        resultado.append(identificador);
        parametros.forEach(resultado::append);
        resultado.append(bloque);
        return resultado.toString();
    }

    @Override
    protected String procesarInvocacionFuncion(InvocacionFuncion invocacionFuncion, String identificador, List<String> parametros) {
        StringBuilder resultado = new StringBuilder();
        resultado.append(identificador);
        parametros.forEach(resultado::append);
        return resultado.toString();
    }

    @Override
    protected String procesarInvocacionFuncion(InvocacionFuncion invocacionFuncion, String identificador) {
        return identificador;
    }

    @Override
    protected String procesarWhile(While aWhile, String expresion, String bloqueWhile) {
        return expresion + bloqueWhile;
    }

    @Override
    protected String procesarBloque(Bloque b, List<String> sentencias) {
        StringBuilder resultado = new StringBuilder();
        sentencias.forEach(resultado::append);
        return resultado.toString();
    }

    @Override
    protected String procesarOperacionBinaria(OperacionBinaria ob, String ei, String ed) {
        return ei+ed;
    }

    @Override
    protected String procesarAsignacion(Asignacion a, String identificador, String expresion) {
        return identificador+expresion;
    }

    @Override
    protected String procesarIf(If anIf, String expresion, String bloqueThen) {
        return expresion + bloqueThen;
    }

    @Override
    protected String procesarIf(If anIf, String expresion, String bloqueThen, String bloqueElse) {
        return expresion + bloqueThen + bloqueElse;
    }

    @Override
    protected String procesarNodo(Nodo n) {
        Integer idPadre = parents.peekFirst();  //reviso tope de pila
        if(idPadre == null){ //pila vacia = nodo raiz
            return String.format("%1$s [label=\"%2$s\"]\n", current_id, n.getEtiqueta()); // declaramos el nodo, y su eqiqueta para imprimir
        }
        return String.format("%1$s [label=\"%2$s\"]\n%3$s -- %1$s\n", current_id, n.getEtiqueta(), idPadre); //declaramos el nodo, su etiqueta para imprimir y lo enganchamos a su nodo padre
    }

}