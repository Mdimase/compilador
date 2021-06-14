package compilador.visitor;

import compilador.ast.base.*;
import compilador.ast.instrucciones.*;
import compilador.ast.operaciones.binarias.OperacionBinaria;
import compilador.ast.operaciones.unarias.*;

import java.util.*;

public class GeneradorCodigo extends Visitor<String>{

    private Alcance alcance_global;
    private Alcance alcance_actual;
    private StringBuilder resultado;
    private StringBuilder inicializaciones;

    public GeneradorCodigo(Alcance alcance_global,StringBuilder string) {
        this.alcance_global = alcance_actual = alcance_global;
        resultado=string;
    }

    public void setInicializaciones(StringBuilder inicializaciones) {
        this.inicializaciones = inicializaciones;
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


    public String getIRGlobalName(Identificador identificador){
        return String.format("@%1$s", identificador.getNombre());
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
            resultado.append("\n");
            resultado.append("define i32 @main(i32, i8**) {\n");
            if(!inicializaciones.isEmpty()){
                resultado.append(inicializaciones); //inicializacion de todas las variables globales
            }
            resultado.append(super.visit(bloque));
            resultado.append("ret i32 0\n");
            resultado.append("}");
        } else{
            alcance_actual = bloque.getAlcance();
            resultado.append(super.visit(bloque));
        }
        return "";
    }

    @Override
    public String visit(DeclaracionFuncion declaracionFuncion) throws ExcepcionDeAlcance {
        resultado.append("\n");
        alcance_actual = declaracionFuncion.getBloque().getAlcance();
        String tipoLlvm = this.LLVM_IR_TYPE_INFO.get(declaracionFuncion.getTipoRetorno()).get(0);
        StringBuilder params = new StringBuilder();
        if(!declaracionFuncion.getParametros().isEmpty()){
           params = this.conseguirParametros(declaracionFuncion.getParametros());
        }
        resultado.append(String.format("define %1$s %2$s (%3$s) {\n",
                tipoLlvm, this.getIRGlobalName(declaracionFuncion.getIdentificador()), params));
        //CUERPO DE LA FUNCION
        resultado.append("}\n");
        return "";
    }

    public StringBuilder conseguirParametros(List<Parametro> list){
        StringBuilder auxp = new StringBuilder();
        ArrayList<String> pa = new ArrayList<>();
        for (Parametro p: list){
            String tipoPLlvm = this.LLVM_IR_TYPE_INFO.get(p.getTipo()).get(0);
            pa.add(String.format("%1$s %2$s", tipoPLlvm, this.newTempId()));
        }
        auxp.append(pa);
        return new StringBuilder(auxp.substring(1,auxp.length()-1));
    }

    @Override
    public String visit(Parametro parametro){
        return "";
    }

    @Override
    public String visit(DeclaracionVariable declaracionVariable) throws ExcepcionDeAlcance {
        if(alcance_actual == alcance_global){ //ya declaradas
            return "";
        }
        declaracionVariable.setIrName(this.newTempId());
        String tipoLlvm = this.LLVM_IR_TYPE_INFO.get(declaracionVariable.getTipo()).get(0);
        resultado.append(String.format("  %1$s = alloca %2$s ; alloca = %1$s\n",
                declaracionVariable.getIrName(), tipoLlvm));
        resultado.append(declaracionVariable.getExpresion().accept(this));
        resultado.append(String.format("  store %1$s %2$s, %1$s* %3$s ; %3$s = %2$s\n",
                tipoLlvm, declaracionVariable.getExpresion().getIrRef(), declaracionVariable.getIrName()));
        return resultado.toString();
    }

    @Override
    public String visit(Constante c) {
        if(c.getValor().equals("false")){
            c.setIrRef("0");
            return "";
        }
        if(c.getValor().equals("true")){
            c.setIrRef("1");
            return "";
        }
        if (c.getTipo() == Tipo.FLOAT){
            double a = Float.parseFloat((String) c.getValor());
            c.setIrRef(Double.toString(a));
        }else
            c.setIrRef((String) c.getValor());
        return "";
    }

    @Override
    public String visit(Identificador identificador){
        Object res = alcance_actual.resolver(identificador.getNombre());
        String tempId = this.newTempId();
        if(res instanceof DeclaracionVariable dv){
            String tipoLlvm = this.LLVM_IR_TYPE_INFO.get(dv.getTipo()).get(0);
            String llvmRef = dv.getIrName();
            resultado.append(String.format("  %1$s = load %2$s, %2$s* %3$s ; %1$s = %4$s\n",
                    tempId, tipoLlvm, llvmRef, identificador.getNombre()));
            identificador.setIrRef(tempId);
        }
        if(res instanceof Parametro){
            System.out.println(alcance_actual.getNombre());
            System.out.println(res);
        }
        return "";
    }

    public void generarCodigoOperacionBinaria(OperacionBinaria s) throws ExcepcionDeAlcance {
        resultado.append(super.visit(s));
        s.setIrRef(this.newTempId());
        String tipoLlvm = this.LLVM_IR_TYPE_INFO.get(s.getIzquierda().getTipo()).get(0);
        resultado.append(String.format("  %1$s = %2$s %3$s %4$s, %5$s\n", s.getIrRef(),
                s.get_llvm_op_code(), tipoLlvm, s.getIzquierda().getIrRef(), s.getDerecha().getIrRef()));
    }

    @Override
    public String visit(OperacionBinaria ob) throws ExcepcionDeAlcance {
        this.generarCodigoOperacionBinaria(ob);
        return "";
    }

    public void generarCodigoConversion(OperacionConversion o) throws ExcepcionDeAlcance {
        resultado.append(super.visit(o));
        o.setIrRef(this.newTempId());
        if(o.getClass() == EnteroAFlotante.class){
            resultado.append(String.format("  %1$s = sitofp i32 %2$s to float\n", o.getIrRef(), o.getExpresion().getIrRef()));
        } else{
            resultado.append(String.format("  %1$s = sitofp float %2$s to i32\n", o.getIrRef(), o.getExpresion().getIrRef()));
        }
    }

    @Override
    public String visit(OperacionUnaria ou) throws ExcepcionDeAlcance {
        if(ou.getClass() == EnteroAFlotante.class || ou.getClass() == FlotanteAEntero.class){
            this.generarCodigoConversion((OperacionConversion) ou);
            return "";
        }
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
            resultado.append(String.format("  %1$s = xor i1 %2$s, 1\n", ou.getIrRef(), ou.getExpresion().getIrRef()));
        }
        return "";
    }

    @Override
    public String visit(Asignacion asignacion) throws ExcepcionDeAlcance {
        resultado.append(asignacion.getExpresion().accept(this));
        DeclaracionVariable dv = (DeclaracionVariable) this.alcance_actual.resolver(asignacion.getIdentificador().getNombre());
        String tipo_llvm = this.LLVM_IR_TYPE_INFO.get(dv.getTipo()).get(0);
        resultado.append(String.format("  store %1$s %2$s, %1$s* %3$s ; %3$s = %2$s\n",
                tipo_llvm, asignacion.getExpresion().getIrRef(), dv.getIrName()));
        return "";
    }


    @Override
    public String visit(Write w) throws ExcepcionDeAlcance {
        if(w.getEsString()){
            String tipo_llvm="string";
            String variable_print = "@.str";
            //TERMINAR ESTO
            return "";
        }
        resultado.append(w.getExpresion().accept(this));
        String tipo_llvm = this.LLVM_IR_TYPE_INFO.get(w.getExpresion().getTipo()).get(0);
        String variable_print = "@.integer";
        String ref_to_print = w.getExpresion().getIrRef();
        if (w.getExpresion().getTipo() == Tipo.FLOAT){
            String temp_ref_to_print = this.newTempId();
            resultado.append(String.format("%1$s = fpext float %2$s to double\n", temp_ref_to_print, ref_to_print));
            ref_to_print = temp_ref_to_print;
            tipo_llvm = "double";
            variable_print = "@.float";
        }
        resultado.append(String.format("%1$s = call i32 (i8*, ...) @printf(i8* getelementptr([4 x i8], [4 x i8]* %2$s, i32 0, i32 0), %3$s %4$s)\n",
                this.newTempId(), variable_print, tipo_llvm, ref_to_print));
        //REVISAR LO DE QUE AMBOS WRITELN HAGAN UN SALTO DE LINEA
        //REVISAR LOS WRITE(STRING)
        return resultado.toString();
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
