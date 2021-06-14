package compilador.visitor;

import compilador.ast.base.*;
import compilador.ast.instrucciones.*;
import compilador.ast.operaciones.binarias.OperacionBinaria;

import java.util.*;

public class GeneradorCodigo extends Visitor<String>{

    private Alcance alcance_global;
    private Alcance alcance_actual;
    private StringBuilder resultado;

    public GeneradorCodigo(Alcance alcance_global,StringBuilder string) {
        this.alcance_global = alcance_actual = alcance_global;
        resultado=string;
    }

    // la lista tendra en .get(0)=tipoir y en .get(1)=valor
    private final Map<Tipo, ArrayList<String>> LLVM_IR_TYPE_INFO = new HashMap<Tipo, ArrayList<String>>() {{
        ArrayList<String> listBool = new ArrayList<>();
        ArrayList<String> listInteger = new ArrayList<>();
        ArrayList<String> listFloat = new ArrayList<>();
        listBool.add("i1");
        listBool.add("0");
        listInteger.add("i32");
        listInteger.add("0");
        listFloat.add("float");
        listFloat.add("0.0");
        put(Tipo.BOOL, listBool);
        put(Tipo.INTEGER, listInteger);
        put(Tipo.FLOAT, listFloat);
    }};


    public String getIRGlobalVariableName(DeclaracionVariable dv){
        return String.format("@%1$s", dv.getId().getNombre());
    }

    public String newTempId(){
        return String.format("%%t$%1$s", String.valueOf(getID()));
    }

    public String newTempLabel(){
        return String.format("label$%1$s", String.valueOf(getID()));
    }

    public String procesar(Programa programa) throws ExcepcionDeAlcance {
        if(programa.getDeclaraciones() == null){
            this.visit(programa.getCuerpo());
        } else{
            this.visit(programa);
        }
        return resultado.toString();
    }

    @Override
    public String visit(Programa programa) throws ExcepcionDeAlcance {
        resultado.append(super.visit(programa));
        return resultado.toString();
    }

    @Override
    public String visit(Bloque bloque) throws ExcepcionDeAlcance {
        if(bloque.getNombre().equals("MAIN")){
            alcance_actual = bloque.getAlcance();
            // crear funcion main y su call
            //resultado.append(super.visit(bloque));
        } else{
            alcance_actual = bloque.getAlcance();
            //resultado.append(super.visit(bloque));
        }
        return "";
    }




















    @Override
    protected String procesarWhenIs(WhenIs whenIs, String expresion, String bloque) {
        return "";
    }

    @Override
    protected String procesarWhen(When when, String expresion, List<String> whenIs, String bloque) {
        return "";
    }

    @Override
    protected String procesarWhen(When when, String expresion, List<String> whenIs) {
        return "";
    }

    @Override
    protected String procesarParametro(Parametro parametro, String identificador, String valor_defecto) {
        return "";
    }

    @Override
    protected String procesarParametro(Parametro parametro, String identificador) {
        return "";
    }

    @Override
    protected String procesarPrograma(Programa programa, String declaraciones, String sentencias) {
        return "";
    }

    @Override
    protected String procesarDeclaracionFuncion(DeclaracionFuncion declaracionFuncion, String identificador, String bloque) {
        return "";
    }

    @Override
    protected String procesarDeclaracionFuncion(DeclaracionFuncion declaracionFuncion, String identificador, List<String> parametros, String bloque) {
        return "";
    }

    @Override
    protected String procesarDeclaracionVariable(DeclaracionVariable declaracionVariable, String identificador, String expresion) {
        return "";
    }

    @Override
    protected String procesarInvocacionFuncion(InvocacionFuncion invocacionFuncion, String identificador, List<String> parametros) {
        return "";
    }

    @Override
    protected String procesarInvocacionFuncion(InvocacionFuncion invocacionFuncion, String identificador) {
        return "";
    }

    @Override
    protected String procesarWhile(While aWhile, String expresion, String bloqueWhile) {
        return "";
    }

    @Override
    protected String procesarFor(For aFor, String identificador, String bloque, String from, String to, String by) {
        return "";
    }

    @Override
    protected String procesarBloque(Bloque bloque, List<String> sentencias) {
        return "";
    }

    @Override
    protected String procesarOperacionBinaria(OperacionBinaria ob, String ei, String ed) {
        return "";
    }

    @Override
    protected String procesarNodo(Nodo n) {
        return "";
    }

    @Override
    protected String procesarAsignacion(Asignacion a, String identificador, String expresion) {
        return "";
    }

    @Override
    protected String procesarIf(If anIf, String expresion, String bloqueThen) {
        return "";
    }

    @Override
    protected String procesarIf(If anIf, String expresion, String bloqueThen, String bloqueElse) {
        return "";
    }
}
