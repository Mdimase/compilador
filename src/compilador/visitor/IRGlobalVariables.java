package compilador.visitor;

import compilador.ast.base.*;
import compilador.ast.instrucciones.*;
import compilador.ast.operaciones.binarias.*;
import compilador.ast.operaciones.unarias.MenosUnario;
import compilador.ast.operaciones.unarias.OperacionUnaria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IRGlobalVariables extends Visitor<String>{

    private String nombre_archivo;
    private Alcance alcance_global;
    private StringBuilder resultado = new StringBuilder();

    public IRGlobalVariables(Alcance alcance_global) {
        this.alcance_global = alcance_global;
    }

    public StringBuilder getResultado() {
        return resultado;
    }

    public Alcance getAlcance_global() {
        return alcance_global;
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

    public String procesar(Bloque declaraciones, String nombre_archivo) throws ExcepcionDeAlcance {
        this.nombre_archivo = nombre_archivo;
        resultado.append(String.format("source_filename = \"%1$s\"\n", nombre_archivo));

        //CAMBIAR LOS TARGET
        resultado.append("target datalayout = \"e-m:w-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128\"\n");
        resultado.append("target triple = \"x86_64-pc-windows-msvc19.16.27038\"\n");
        resultado.append("\n");

        resultado.append("declare i32 @puts(i8*)\n");
        resultado.append("declare i32 @printf(i8*, ...)\n");
        resultado.append("\n");
        resultado.append("@.true = private constant[4 x i8] c\".T.\\00\"\n");
        resultado.append("@.false = private constant[4 x i8] c\".F.\\00\"\n");
        resultado.append("\n");
        resultado.append("@.integer = private constant [4 x i8] c\"%d\\0A\\00\"\n");
        resultado.append("@.float = private constant [4 x i8] c\"%f\\0A\\00\"\n");
        if(declaraciones != null){  //con declaraciones
            for (Object obj:alcance_global.values()){   //declaro todas las variables globales con valores por defecto
                if(obj instanceof DeclaracionVariable dv){
                    String tipo_ir = LLVM_IR_TYPE_INFO.get(dv.getTipo()).get(0);    //tipo default
                    String valor_ir = LLVM_IR_TYPE_INFO.get(dv.getTipo()).get(1);
                    if(dv.getExpresion().getClass() == Constante.class){    //en caso de constante ya la declaro inicializada
                        Constante constante = (Constante) dv.getExpresion();
                        if(constante.getValor().equals("true")){
                            valor_ir = "1";
                        }
                        if(constante.getValor().equals("false")){
                            valor_ir = "0";
                        }
                        if(constante.getTipo() == Tipo.INTEGER){
                            valor_ir = (String) constante.getValor();
                        }
                        if(constante.getTipo() == Tipo.FLOAT){
                            double aux = Float.parseFloat((String) constante.getValor());
                            valor_ir = Double.toString(aux);
                        }
                    }
                    String nombre_ir = this.getIRGlobalVariableName(dv);
                    resultado.append(String.format("%1$s = global %2$s %3$s\n", nombre_ir, tipo_ir, valor_ir));
                    dv.setIrName(nombre_ir);
                    alcance_global.replace(dv.getId().getNombre(), dv); //remplazo la declaracion original por una nueva que tiene seteado el nombreIr de esta variable
                }
            }
            resultado.append("define i32 @main(i32, i8**) {\n");
            resultado.append(super.visit(declaraciones));
            return resultado.toString();
        } else{
            resultado.append("define i32 @main(i32, i8**) {\n");
            return resultado.toString();    //sin agregar las declaraciones
        }
    }

    @Override
    public String visit(DeclaracionFuncion declaracionFuncion){
        return "";  //ignoro las funciones
    }

    @Override
    public String visit(DeclaracionVariable declaracionVariable) throws ExcepcionDeAlcance {
        if(declaracionVariable.getExpresion().getClass() == Constante.class){ //ya declaradas en procesar()
            return "";
        }
        resultado.append(declaracionVariable.getExpresion().accept(this));
        DeclaracionVariable dv = (DeclaracionVariable) alcance_global.resolver(declaracionVariable.getId().getNombre());
        String tipoLlvm = this.LLVM_IR_TYPE_INFO.get(dv.getTipo()).get(0);
        resultado.append(String.format("  store %1$s %2$s, %1$s* %3$s ; %3$s = %2$s\n",
             tipoLlvm, dv.getExpresion().getIrRef(), dv.getIrName()));
        return resultado.toString();
    }

    @Override
    public String visit(Identificador identificador){
        Object res = alcance_global.resolver(identificador.getNombre());
        String tempId = this.newTempId();
        if(res instanceof DeclaracionVariable dv){
            String tipoLlvm = this.LLVM_IR_TYPE_INFO.get(dv.getTipo()).get(0);
            String llvmRef = dv.getIrName();
            resultado.append(String.format("  %1$s = load %2$s, %2$s* %3$s ; %1$s = %4$s\n",
                    tempId, tipoLlvm, llvmRef, identificador.getNombre()));
            identificador.setIrRef(tempId);
        }
        return "";
    }

    // HACER EL CORTOCIRCUITO BOOLEANO PARA EL AND Y EL OR
    public void generarCortocircuitoBooleano(OperacionBinaria s){
        if(s.getClass() == And.class){
            //cortocircuito And
            int a=0;
        } else{ //cortocircuito Or
            int b=0;
        }
    }

    public void generarCodigoOperacionBinaria(OperacionBinaria s) throws ExcepcionDeAlcance {
        if(s.getClass() == And.class || s.getClass() == Or.class){
            this.generarCortocircuitoBooleano(s);
        } else {
            resultado.append(super.visit(s));
            s.setIrRef(this.newTempId());
            String tipoLlvm = this.LLVM_IR_TYPE_INFO.get(s.getIzquierda().getTipo()).get(0);
            resultado.append(String.format("  %1$s = %2$s %3$s %4$s, %5$s\n", s.getIrRef(),
                    s.get_llvm_op_code(), tipoLlvm, s.getIzquierda().getIrRef(), s.getDerecha().getIrRef()));
        }
    }

    @Override
    public String visit(OperacionBinaria ob) throws ExcepcionDeAlcance {
        this.generarCodigoOperacionBinaria(ob);
        return "";
    }

    @Override
    public String visit(Constante c) {
        if (c.getTipo() == Tipo.FLOAT){
            double a = Float.parseFloat((String) c.getValor());
            c.setIrRef(Double.toString(a));
        }else
            c.setIrRef((String) c.getValor());
        return "";
    }

    @Override
    public String visit(OperacionUnaria ou) throws ExcepcionDeAlcance {
        if(ou.getClass() == MenosUnario.class){
            if(ou.getExpresion().getTipo() == Tipo.FLOAT){  // %rv=fneg float,%rv
                resultado.append(super.visit(ou));
                ou.setIrRef(this.newTempId());
                resultado.append(String.format("  %1$s = fneg float , %2$s\n", ou.getIrRef(), ou.getExpresion().getIrRef()));
            } else{ //integer  %rv=sub i32 0,%rv
                resultado.append(super.visit(ou));
                ou.setIrRef(this.newTempId());
                resultado.append(String.format("  %1$s = sub i32 0, %2$s\n", ou.getIrRef(), ou.getExpresion().getIrRef()));
            }
        }
        else{   //not   %rv=xor i1 %rv, true
            resultado.append(super.visit(ou));
            ou.setIrRef(this.newTempId());
            resultado.append(String.format("  %1$s = xor i1 %2$s, true\n", ou.getIrRef(), ou.getExpresion().getIrRef()));
        }
        return "";
    }




















    /*
    @Override
    public String visit(Read r){
        return "";
    }*/



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

/*
    en visit(decFuncion){
        de generador de codigo
        visit(idetificador)
        alcance_actual=decFuncion.getBloque().getAlcance()
        visit(parametros)
        visit(bloque)
        asi en el visit de identificador sea una declaracion o una invocacion nunca actualizo el alcance actual y no tengo que hacer lo de los 2 flags
    }

    */
}
