package compilador.visitor;

import compilador.ast.base.*;
import compilador.ast.instrucciones.*;
import compilador.ast.operaciones.binarias.And;
import compilador.ast.operaciones.binarias.OperacionBinaria;
import compilador.ast.operaciones.binarias.Or;
import compilador.ast.operaciones.unarias.*;

import java.util.*;

public class GeneradorCodigo extends Visitor<String>{

    private String nombreArchivo;
    private int id=0;
    private Alcance alcance_global;
    private Alcance alcance_actual;
    private String condicion;   // necesario para hacer el br en el visit(Continue)
    private Stack<ArrayList<String>> etiquetasSalto = new Stack<>();
    private StringBuilder resultado = new StringBuilder();
    private StringBuilder inicializaciones = new StringBuilder();
    private StringBuilder globalVar = new StringBuilder();
    private StringBuilder str = new StringBuilder();

    public GeneradorCodigo(Alcance alcance_global) {
        this.alcance_global = alcance_actual = alcance_global;
    }

    // la lista tendra en .get(0)=tipoir y en .get(1)=valor
    // no use tuplas por problemas con el IDE
    private final Map<Tipo, ArrayList<String>> LLVM_IR_TYPE_INFO = new HashMap<>() {{
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

    public String getIRGlobalName(){
        return String.format("@.%1$s", this.getGlobalID());
    }

    public int getGlobalID(){
        id+=1;
        return id;
    }

    public String getStrName(){
        return String.format("@str.%1$s",String.valueOf(getID()));
    }

    public String newTempId(){
        return String.format("%%t$%1$s", String.valueOf(getID()));
    }

    public String newTempLabel(){
        return String.format("%%label$%1$s", String.valueOf(getID()));
    }

    //funcion que recibe un %label y devuelve un label:
    public String reFormatLabel(String label){
        label = label.replace("%","");  //elimino el %
        return label.concat(":");  //agrego el :
    }

    public ArrayList<String> initElementoPila (String expV, String expF){
        ArrayList<String> elementPila = new ArrayList<>();
        elementPila.add(expV);
        elementPila.add(expF);
        return  elementPila;
        // [expV,expF] -> [0] = expV ; [1] -> expF
    }

    public String procesar(Programa programa,String nombreArchivo) throws ExcepcionDeAlcance {
        this.nombreArchivo = nombreArchivo;
        globalVar.append(String.format("source_filename = \"%1$s\"\n", nombreArchivo));

        //CAMBIAR LOS TARGET
        globalVar.append("target datalayout = \"e-m:w-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128\"\n");
        globalVar.append("target triple = \"x86_64-pc-windows-msvc19.29.30038\"\n\n");

        globalVar.append("declare i32 @puts(i8*)\n");
        globalVar.append("declare i32 @printf(i8*, ...)\n");
        globalVar.append("declare i32 @scanf(i8* %0, ...)\n\n");
        //globalVar.append("@.true = private constant[4 x i8] c\".T.\\00\"\n");
        //globalVar.append("@.false = private constant[4 x i8] c\".F.\\00\"\n");
        //globalVar.append("@.truen = private constant[5 x i8] c\".T.\\00\"\n");
        //globalVar.append("@.falsen = private constant[5 x i8] c\".F.\\0A\\00\"\n");
        globalVar.append("@.bool = private constant [3 x i8] c\"%d\\00\"\n");
        globalVar.append("@.booln = private constant [4 x i8] c\"%d\\0A\\00\"\n");
        globalVar.append("@.integer = private constant [3 x i8] c\"%d\\00\"\n");
        globalVar.append("@.float = private constant [3 x i8] c\"%f\\00\"\n");
        globalVar.append("@.integern = private constant [4 x i8] c\"%d\\0A\\00\"\n");
        globalVar.append("@.floatn = private constant [4 x i8] c\"%f\\0A\\00\"\n");
        globalVar.append("@.int_read_format = unnamed_addr constant [3 x i8] c\"%d\\00\"\n");
        globalVar.append("@.double_read_format = unnamed_addr constant [4 x i8] c\"%lf\\00\"\n\n");

        if(programa.getDeclaraciones() == null){
           resultado.append(this.visit(programa.getCuerpo()));   //dispara el visit(bloqueMain)
        } else{
           resultado.append(this.visit(programa));
        }
        return globalVar.toString() + str + resultado;
    }

    @Override
    public String visit(Programa programa) throws ExcepcionDeAlcance {
        return super.visit(programa);
    }

    @Override
    public String visit(Bloque bloque) throws ExcepcionDeAlcance {
        alcance_actual = bloque.getAlcance();
        if(bloque.getNombre().equals("MAIN")){
            resultado.append("\n");
            resultado.append("define i32 @main(i32, i8**) {\n");
            resultado.append(inicializaciones); //inicializacion de todas las variables globales
            resultado.append(super.visit(bloque));
            resultado.append("ret i32 0\n");
            resultado.append("}");
        } else{
            resultado.append(super.visit(bloque));
        }
        return "";
    }

    @Override
    public String visit(InvocacionFuncion invocacionFuncion) throws ExcepcionDeAlcance {
        Object res = alcance_actual.resolver(invocacionFuncion.getIdentificador().getNombre());
        DeclaracionFuncion df = (DeclaracionFuncion) res;
        invocacionFuncion.setIrRef(this.newTempId());   //rg resultado de la invocacion
        String tipoLlvm = this.LLVM_IR_TYPE_INFO.get(df.getTipoRetorno()).get(0);   //tipo retorno
        StringBuilder p = this.conseguirParametrosInvocacion(invocacionFuncion);   //string con los parametros de la invocacion
        if(df.getParametros().size() > invocacionFuncion.getParams().size()){
            for(int i=invocacionFuncion.getParams().size();i<df.getParametros().size();i++){
                Parametro param = df.getParametros().get(i);
                String tipoPLlvm = this.LLVM_IR_TYPE_INFO.get(param.getTipo()).get(0);   //tipo retorno
                param.getValorDefecto().accept(this);
                p.append(String.format(", %1$s %2$s",tipoPLlvm,param.getValorDefecto().getIrRef()));
            }
        }
        resultado.append(String.format("  %1$s = call %2$s %3$s(%4$s)\n",invocacionFuncion.getIrRef(),tipoLlvm,df.getIdentificador().getIrRef(),p));
        return "";
    }

    public StringBuilder conseguirParametrosInvocacion(InvocacionFuncion invocacionFuncion) throws ExcepcionDeAlcance {
        StringBuilder aux = new StringBuilder();
        StringBuilder params = new StringBuilder();
        for(Expresion p:invocacionFuncion.getParams()){
            resultado.append(p.accept(this)); //codigo del parametros
            String tipoPLlvm = this.LLVM_IR_TYPE_INFO.get(p.getTipo()).get(0);   //tipo retorno
            params.append(String.format("%1$s %2$s, ",tipoPLlvm,p.getIrRef()));
        }
        return params.deleteCharAt(params.lastIndexOf(","));    //le borro la coma que me genera al final
    }

    @Override
    public String visit(DeclaracionFuncion declaracionFuncion) throws ExcepcionDeAlcance {
        resultado.append("\n");
        String tipoLlvm = this.LLVM_IR_TYPE_INFO.get(declaracionFuncion.getTipoRetorno()).get(0);
        declaracionFuncion.getIdentificador().setIrRef(this.getIRGlobalName());
        String params = this.conseguirParametros(declaracionFuncion.getParametros());
        resultado.append(String.format("define %1$s %2$s (%3$s) {\n",   //firma de la funcion
                tipoLlvm,declaracionFuncion.getIdentificador().getIrRef() , params));
        this.inicializarParametros(declaracionFuncion.getParametros()); //inicializacion de los parametros
        resultado.append(declaracionFuncion.getBloque().accept(this));
        resultado.append("}\n");
        alcance_actual = declaracionFuncion.getAlcance().getPadre();
        return "";
    }

    // se encarga de inicializar los parametros
    public void inicializarParametros(List<Parametro> parametros){
        for(Parametro p: parametros){
            String tipoLlvm = this.LLVM_IR_TYPE_INFO.get(p.getTipo()).get(0);
            String pName = p.getIrRef();    //guardo el que tenia en la firma
            p.setIrRef(this.newTempId());   //nuevo nombre para el alloca
            resultado.append(String.format("  %1$s = alloca %2$s ; alloca = %1$s\n", p.getIrRef(), tipoLlvm));
            resultado.append(String.format("  store %1$s %2$s, %1$s* %3$s ; %3$s = %2$s\n", tipoLlvm, pName, p.getIrRef()));
        }
    }

    // devuelve el string con los parametros listo para insertar en la firma de la funcion
    // podria haber usado un visit(Parametro), pero me resulto mas simple hacer esta funcion
    public String conseguirParametros(List<Parametro> list){
        StringBuilder auxp = new StringBuilder();
        for (Parametro p: list){
            p.setIrRef(this.newTempId());
            String tipoPLlvm = this.LLVM_IR_TYPE_INFO.get(p.getTipo()).get(0);
            auxp.append(String.format("%1$s %2$s,", tipoPLlvm, p.getIrRef()));
        }
        return auxp.deleteCharAt(auxp.lastIndexOf(",")).toString();    //le borro la coma que me genera al final
    }

    @Override
    public String visit(Return r) throws ExcepcionDeAlcance {
        resultado.append(r.getExpresion().accept(this));
        String tipoLlvm = this.LLVM_IR_TYPE_INFO.get(r.getExpresion().getTipo()).get(0);
        resultado.append(String.format("  ret %1$s %2$s\n",tipoLlvm,r.getExpresion().getIrRef()));
        return "";
    }

    @Override
    public String visit(DeclaracionVariable declaracionVariable) throws ExcepcionDeAlcance {
        String tipoLlvm = this.LLVM_IR_TYPE_INFO.get(declaracionVariable.getTipo()).get(0);
        if(alcance_actual == alcance_global){ //variables globales
            declaracionVariable.setIrName(this.getIRGlobalName());
            String valor_ir = LLVM_IR_TYPE_INFO.get(declaracionVariable.getTipo()).get(1);
            globalVar.append(String.format("%1$s = global %2$s %3$s\n", declaracionVariable.getIrName(), tipoLlvm, valor_ir));
            inicializaciones.append(declaracionVariable.getExpresion().accept(this));
            inicializaciones.append(String.format("  store %1$s %2$s, %1$s* %3$s ; %3$s = %2$s\n",
                    tipoLlvm, declaracionVariable.getExpresion().getIrRef(), declaracionVariable.getIrName()));
        } else {    //variables locales
            declaracionVariable.setIrName(this.newTempId());
            resultado.append(String.format("  %1$s = alloca %2$s ; alloca = %1$s\n", declaracionVariable.getIrName(), tipoLlvm));
            resultado.append(declaracionVariable.getExpresion().accept(this));
            resultado.append(String.format("  store %1$s %2$s, %1$s* %3$s ; %3$s = %2$s\n"
                    , tipoLlvm, declaracionVariable.getExpresion().getIrRef(), declaracionVariable.getIrName()));
        }
        return "";
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
        identificador.setIrRef(this.newTempId());
        if(res instanceof DeclaracionVariable dv){
            String tipoLlvm = this.LLVM_IR_TYPE_INFO.get(dv.getTipo()).get(0);
            String inicializacion = String.format("  %1$s = load %2$s, %2$s* %3$s ; %1$s = %4$s\n",
                    identificador.getIrRef(), tipoLlvm, dv.getIrName(), identificador.getNombre());
            if(alcance_actual == alcance_global){ //variable global
                inicializaciones.append(inicializacion);
            } else {    // variable local
                resultado.append(inicializacion);
            }
        }
        if( res instanceof Parametro parametro){
            String tipoLlvm = this.LLVM_IR_TYPE_INFO.get(parametro.getTipo()).get(0);
            resultado.append(String.format("  %1$s = load %2$s, %2$s* %3$s ; %1$s = %4$s\n",
                    identificador.getIrRef(), tipoLlvm, parametro.getIrRef(), identificador.getNombre()));
        }
        return "";
    }

    // genera el codigo de toda operacion binaria
    // en el caso de And y Or genera la instruccion sin cortocircuito para casos donde no son condiciones de estructuras de control
    public void generarCodigoOperacionBinaria(OperacionBinaria s) throws ExcepcionDeAlcance {
        StringBuilder aux = new StringBuilder();
        aux.append(super.visit(s));
        s.setIrRef(this.newTempId());
        String tipoLlvm = this.LLVM_IR_TYPE_INFO.get(s.getIzquierda().getTipo()).get(0);    //estaba sin izquierda
        aux.append(String.format("  %1$s = %2$s %3$s %4$s, %5$s\n", s.getIrRef(),
                s.get_llvm_op_code(), tipoLlvm, s.getIzquierda().getIrRef(), s.getDerecha().getIrRef()));
        if(alcance_actual == alcance_global){
            inicializaciones.append(aux);
        } else {
            resultado.append(aux);
        }
    }

    public String visit(Or or) throws ExcepcionDeAlcance {
        if (this.etiquetasSalto.size() > 0){    // condicion de un if o while
            String etiquetaDerecha = this.newTempLabel();
            ArrayList<String> elementoPila = this.initElementoPila(etiquetasSalto.peek().get(0),etiquetaDerecha);
            etiquetasSalto.push(elementoPila);  //true,derecha
            resultado.append(or.getIzquierda().accept(this));
            elementoPila = etiquetasSalto.pop();    // ya evaluada la izquierda del OR
            String etiquetaTrue = elementoPila.get(0);
            String etiquetaFalse = elementoPila.get(1);
            resultado.append(String.format("  br i1 %1$s, label %2$s, label %3$s\n\n",or.getIzquierda().getIrRef(),etiquetaTrue,etiquetaFalse)); //salto segun resultado Or.iz
            resultado.append(String.format("  %1$s\n",this.reFormatLabel(etiquetaDerecha)));  // etDer:
            resultado.append(or.getDerecha().accept(this));
            or.setIrRef(or.getDerecha().getIrRef());
        } else {    //asignaciones o inicializaciones de variable por ej
            this.generarCodigoOperacionBinaria(or);
        }
        return "";
    }

    public String visit(And and) throws ExcepcionDeAlcance {
        if (this.etiquetasSalto.size() > 0){    // condicion de un if o while
            String etiquetaDerecha = this.newTempLabel();
            ArrayList<String> elementoPila = this.initElementoPila(etiquetaDerecha,etiquetasSalto.peek().get(1));
            etiquetasSalto.push(elementoPila);  // derecha,false
            resultado.append(and.getIzquierda().accept(this));
            elementoPila = etiquetasSalto.pop();    // ya evaluada la izquierda del And
            String etiquetaTrue = elementoPila.get(0);
            String etiquetaFalse = elementoPila.get(1);
            resultado.append(String.format("  br i1 %1$s, label %2$s, label %3$s\n\n",and.getIzquierda().getIrRef(),etiquetaTrue,etiquetaFalse)); //salto segun resultado Or.iz
            resultado.append(String.format("  %1$s\n",this.reFormatLabel(etiquetaDerecha)));  // etDer:
            resultado.append(and.getDerecha().accept(this));
            and.setIrRef(and.getDerecha().getIrRef());  //guardo el nombre del %RV de la exp derecho en el and
        } else {    //asignaciones o inicializaciones de variable por ej
            this.generarCodigoOperacionBinaria(and);
        }
        return "";
    }


    @Override
    public String visit(OperacionBinaria ob) throws ExcepcionDeAlcance {
        if(ob.getClass() == Or.class){
            this.visit( (Or) ob);
            return "";
        }
        if(ob.getClass() == And.class){
            this.visit( (And) ob);
            return "";
        }
        this.generarCodigoOperacionBinaria(ob); //todos los que no sean Or o And
        return "";
    }


    //esto quedo asi para no tener que modificar el visitor abstractco ni todos los que lo implementaban
    // ya que quitar esto implicaria crear los 4 visit correspondientes en Visitor y en ASTgraf como minimo
    @Override
    public String visit(OperacionUnaria ou) throws ExcepcionDeAlcance {
        if(ou.getClass() == EnteroAFlotante.class){
            this.visit( (EnteroAFlotante) ou);
            return "";
        }
        if(ou.getClass() == FlotanteAEntero.class){
            this.visit( (FlotanteAEntero) ou);
            return "";
        }
        if(ou.getClass() == MenosUnario.class){
            this.visit( (MenosUnario) ou);
            return "";
        }
        if(ou.getClass() == Not.class){
            this.visit((Not) ou);
            return "";
        }
        return "";
    }

    public String visit(EnteroAFlotante eaf) throws ExcepcionDeAlcance {
        StringBuilder aux = new StringBuilder();
        aux.append(super.visit(eaf));
        eaf.setIrRef(this.newTempId());
        aux.append(String.format("  %1$s = sitofp i32 %2$s to float\n", eaf.getIrRef(), eaf.getExpresion().getIrRef()));
        if(alcance_actual == alcance_global){
            inicializaciones.append(aux);
        } else {
            resultado.append(aux);
        }
        return "";
    }


    public String visit(FlotanteAEntero fae) throws ExcepcionDeAlcance {
        StringBuilder aux = new StringBuilder();
        aux.append(super.visit(fae));
        fae.setIrRef(this.newTempId());
        aux.append(String.format("  %1$s = fptosi float %2$s to i32\n", fae.getIrRef(), fae.getExpresion().getIrRef()));
        if(alcance_actual == alcance_global){
            inicializaciones.append(aux);
        } else {
            resultado.append(aux);
        }
        return "";
    }

    public String visit (MenosUnario menosUnario) throws ExcepcionDeAlcance {
        StringBuilder aux = new StringBuilder();
        aux.append(super.visit(menosUnario));
        menosUnario.setIrRef(this.newTempId());
        if (menosUnario.getExpresion().getTipo() == Tipo.FLOAT) {  // %rv=fneg float,%rv
            aux.append(String.format("  %1$s = fneg float %2$s\n", menosUnario.getIrRef(), menosUnario.getExpresion().getIrRef()));
        } else{ //integer  %rv=sub i32 0,%rv
            aux.append(String.format("  %1$s = sub i32 0, %2$s\n", menosUnario.getIrRef(), menosUnario.getExpresion().getIrRef()));
        }
        if (alcance_actual == alcance_global) {
            inicializaciones.append(aux);
        } else {
            resultado.append(aux);
        }
        return "";
    }

    public String visit(Not not) throws ExcepcionDeAlcance {
        if (this.etiquetasSalto.size() > 0) {    // condicion de un if o while
            ArrayList<String> elementoPila = etiquetasSalto.pop();
            String etiquetaTrue = elementoPila.get(0);
            String etiquetaFalse = elementoPila.get(1);
            elementoPila = this.initElementoPila(etiquetaFalse,etiquetaTrue); // ya realizado el inverso
            etiquetasSalto.push(elementoPila);
            resultado.append(not.getExpresion().accept(this));
            not.setIrRef(not.getExpresion().getIrRef());    //guardo el %rv de la expresion en el NOT
        } else {
            this.generarCodigoNot(not);
        }
        return "";
    }

    //instruccion xor que implementa el not
    public void generarCodigoNot(Not not) throws ExcepcionDeAlcance {
        StringBuilder aux = new StringBuilder();
        aux.append(super.visit(not));
        not.setIrRef(this.newTempId());
        aux.append(String.format("  %1$s = xor i1 %2$s, 1\n", not.getIrRef(), not.getExpresion().getIrRef()));
        if (alcance_actual == alcance_global) {
            inicializaciones.append(aux);
        } else {
            resultado.append(aux);
        }
    }

    public void generarCodigoIfElse(If iF) throws ExcepcionDeAlcance {
        String etiquetaThen = this.newTempLabel();
        String etiquetaElse = this.newTempLabel();
        String etiquetaFin = this.newTempLabel();
        ArrayList<String> elementoPila = this.initElementoPila(etiquetaThen,etiquetaElse);
        etiquetasSalto.push(elementoPila);
        resultado.append(iF.getCondicion().accept(this));
        elementoPila = etiquetasSalto.pop();    //elemento de pila con la condicion evaluada
        String etiquetaTrue = elementoPila.get(0);
        String etiquetaFalse = elementoPila.get(1);
        resultado.append(String.format("  br i1 %1$s, label %2$s, label %3$s\n\n", iF.getCondicion().getIrRef(),etiquetaTrue,etiquetaFalse));
        resultado.append(String.format("  %1$s\n",this.reFormatLabel(etiquetaThen)));   // etThen:
        resultado.append(iF.getBloqueThen().accept(this));
        resultado.append(String.format("  br label %1$s\n\n",etiquetaFin));   // jump etFin
        resultado.append(String.format("  %1$s\n",this.reFormatLabel(etiquetaElse)));   //etElse:
        resultado.append(iF.getBloqueElse().accept(this));
        resultado.append(String.format("  br label %1$s\n\n",etiquetaFin));   // jump etFin
        resultado.append(String.format("  %1$s\n",this.reFormatLabel(etiquetaFin)));   // etFin:
    }

    public void generarCodigoIf(If iF) throws ExcepcionDeAlcance {
        String etiquetaThen = this.newTempLabel();
        String etiquetaFin = this.newTempLabel();
        ArrayList<String> elementoPila = this.initElementoPila(etiquetaThen,etiquetaFin);   //si es falso salta al fin
        etiquetasSalto.push(elementoPila);
        resultado.append(iF.getCondicion().accept(this));
        elementoPila = etiquetasSalto.pop();    //elemento de pila con la condicion evaluada
        String etiquetaTrue = elementoPila.get(0);
        String etiquetaFalse = elementoPila.get(1);
        resultado.append(String.format("  br i1 %1$s, label %2$s, label %3$s\n\n", iF.getCondicion().getIrRef(),etiquetaTrue,etiquetaFalse));
        resultado.append(String.format("  %1$s\n",this.reFormatLabel(etiquetaThen)));   // etThen:
        resultado.append(iF.getBloqueThen().accept(this));
        resultado.append(String.format("  br label %1$s\n\n",etiquetaFin));   // jump etFin
        resultado.append(String.format("  %1$s\n",this.reFormatLabel(etiquetaFin)));   // etFin:
    }

    @Override
    public String visit(If iF) throws ExcepcionDeAlcance {
        if(iF.getBloqueElse() != null){ //if+else
            this.generarCodigoIfElse(iF);
        } else { // if solo
            this.generarCodigoIf(iF);
        }
        return "";
    }

    @Override
    public String visit(While w) throws ExcepcionDeAlcance {
        String etiquetaCondicion = condicion = this.newTempLabel();
        String etiquetaVerdadero = this.newTempLabel();
        String etiquetaFalso = this.newTempLabel();
        ArrayList<String> elementoPila = this.initElementoPila(etiquetaVerdadero,etiquetaFalso);
        etiquetasSalto.push(elementoPila);
        resultado.append(String.format("  br label %1$s\n\n",etiquetaCondicion));
        resultado.append(String.format("  %1$s\n",this.reFormatLabel(etiquetaCondicion)));
        resultado.append(w.getCondicion().accept(this));    //codigo de la condicion
        String etiquetaTrue = etiquetasSalto.peek().get(0);
        String etiquetaFalse = etiquetasSalto.peek().get(1);
        resultado.append(String.format("  br i1 %1$s, label %2$s, label %3$s\n\n", w.getCondicion().getIrRef(),etiquetaTrue,etiquetaFalse));
        resultado.append(String.format("  %1$s\n",this.reFormatLabel(etiquetaTrue)));   // etTrue:
        resultado.append(w.getBloque().accept(this));   //codigo del bloque
        resultado.append(String.format("  br label %1$s\n\n",etiquetaCondicion));   // jump etCondicion
        resultado.append(String.format("  %1$s\n",this.reFormatLabel(etiquetaFalse)));   //etFalse:
        etiquetasSalto.pop();
        return "";
    }

    @Override
    public String visit(Continue c){
        resultado.append(String.format("  br label %1$s",condicion));
        return "";
    }

    @Override
    public String visit(Break b){
        resultado.append(String.format("  br label %1$s",etiquetasSalto.peek().get(1)));
        return "";
    }

    @Override
    public String visit(Asignacion asignacion) throws ExcepcionDeAlcance {
        Object res = this.alcance_actual.resolver(asignacion.getIdentificador().getNombre());
        if(res instanceof DeclaracionVariable dv){
            String tipo_llvm = this.LLVM_IR_TYPE_INFO.get(dv.getTipo()).get(0);
            resultado.append(asignacion.getExpresion().accept(this));   //genero exp
            resultado.append(String.format("  store %1$s %2$s, %1$s* %3$s ; %3$s = %2$s\n",
                    tipo_llvm, asignacion.getExpresion().getIrRef(), dv.getIrName()));
        }
        if(res instanceof Parametro p){
            String tipo_llvm = this.LLVM_IR_TYPE_INFO.get(p.getTipo()).get(0);
            resultado.append(asignacion.getExpresion().accept(this));   //genero exp
            resultado.append(String.format("  store %1$s %2$s, %1$s* %3$s ; %3$s = %2$s\n",
                    tipo_llvm, asignacion.getExpresion().getIrRef(), p.getIrRef()));
        }
        return resultado.toString();
    }

    @Override
    public String visit(Write w) throws ExcepcionDeAlcance {
        String variable_print;
        if(w.getEsString()){
            this.generarCodigoWriteString(w);
        } else {
            this.generarCodigoWriteExp(w);
        }
        return "";
    }

    public void generarCodigoWriteString(Write w){
        // constante global a imprimir
        int longitud = w.getMensaje().getCadena().length() + 1; // por el \00
        w.setIrRefStr(this.getStrName());
        String fin = "\\00\"\n";
        if(w.getEsLn()){    //agrego el salto de linea
            longitud +=1;   //por el \0A
            fin = "\\0A\\00\"\n";
        }
        str.append(String.format("%1$s = private constant [%2$s x i8] c\"%3$s%4$s",
                w.getIrRefStr(), String.valueOf(longitud),w.getMensaje().getCadena(),fin));
        //call
        /*
        resultado.append(String.format("  %1$s = call i32 @puts(i8* getelementptr ([%2$s x i8], [%2$s x i8] * %3$s, i32 0, i32 0))\n",
                this.newTempId(),String.valueOf(longitud),w.getIrRefStr())); */
        resultado.append(String.format("  %1$s = call i32 (i8*, ...) @printf(i8* getelementptr([%2$s x i8], [%2$s x i8]* %3$s, i32 0, i32 0))\n",
                this.newTempId(),String.valueOf(longitud), w.getIrRefStr()));
    }


    public void generarCodigoWriteExp(Write w) throws ExcepcionDeAlcance {
        String variable_print;
        resultado.append(w.getExpresion().accept(this));
        String tipo_llvm = this.LLVM_IR_TYPE_INFO.get(w.getExpresion().getTipo()).get(0);
        String ref_to_print = w.getExpresion().getIrRef();
        if (w.getExpresion().getTipo() == Tipo.FLOAT){
            String temp_ref_to_print = this.newTempId();
            resultado.append(String.format("  %1$s = fpext float %2$s to double\n", temp_ref_to_print, ref_to_print));
            ref_to_print = temp_ref_to_print;
            tipo_llvm = "double";
            if(w.getEsLn()){    //modificacion para el salto de linea
                variable_print = "@.floatn";
            } else {
                variable_print = "@.float";
            }
        } else {    //integer y boolean (se imprimiran como 0 y 1)
            if(w.getEsLn()){    //modificacion para el salto de linea
                variable_print = "@.integern";
            } else {
                variable_print = "@.integer";
            }
            if(w.getExpresion().getTipo() == Tipo.BOOL){
                // 0 = false
                // 1 = true
                ref_to_print = this.newTempId();
                tipo_llvm = "i32";
                resultado.append(String.format("  %s = zext i1 %s to i32\n", ref_to_print, w.getExpresion().getIrRef())); //casteo i1 a i32 para poder imprimir
            }
        }
        if(w.getEsLn()) {  //writeln culpa del [x x i8]
            resultado.append(String.format("  %1$s = call i32 (i8*, ...) @printf(i8* getelementptr([4 x i8], [4 x i8]* %2$s, i32 0, i32 0), %3$s %4$s)\n",
                    this.newTempId(), variable_print, tipo_llvm, ref_to_print));
        } else {
            resultado.append(String.format("  %1$s = call i32 (i8*, ...) @printf(i8* getelementptr([3 x i8], [3 x i8]* %2$s, i32 0, i32 0), %3$s %4$s)\n",
                    this.newTempId(), variable_print, tipo_llvm, ref_to_print));
        }
    }

    @Override
    public String visit(Read read){
        if(alcance_actual == alcance_global){   // read guardado en una variable global
            inicializaciones.append(this.generarCodigoRead(read));
        } else {
            resultado.append(this.generarCodigoRead(read));
        }
        return "";
    }

    public String generarCodigoRead(Read read){
        StringBuilder aux = new StringBuilder();
        String temp = this.newTempId();
        String tipoLlvm = this.LLVM_IR_TYPE_INFO.get(read.getTipo()).get(0);
        aux.append(String.format("  %1$s = alloca %2$s ; alloca = %1$s\n", temp, tipoLlvm));
        if (read.getTipo() != Tipo.FLOAT){
            aux.append(String.format("  %1$s = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.int_read_format, i64 0, i64 0), %2$s* %3$s)\n",
                    this.newTempId(),tipoLlvm,temp));
        } else {    // read_float()
            aux.append(this.generarCodigoReadFloat(read,temp));
        }
        read.setIrRef(this.newTempId());
        aux.append(String.format("  %1$s = load %2$s, %2$s* %3$s ; %1$s = %4$s\n", read.getIrRef(), tipoLlvm, temp, temp)); //este load es xq al asignar el read a una variable necesito un valor, y no un puntero
        return aux.toString();
    }

    public String generarCodigoReadFloat(Read read,String temp){
        StringBuilder rf = new StringBuilder();
        String irName = this.newTempId();   // alloca double
        rf.append(String.format("  %1$s = alloca double ; alloca = %1$s\n", irName));
        rf.append(String.format("  %1$s = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.double_read_format, i64 0, i64 0), double* %2$s)\n",
                this.newTempId(),irName));
        String tempDouble = this.newTempId();
        rf.append(String.format("  %1$s = load double, double* %2$s\n",tempDouble,irName));
        String tempFloat = this.newTempId();
        rf.append(String.format("  %1$s = fptrunc double %2$s to float\n",tempFloat,tempDouble));
        rf.append(String.format("  store float %1$s, float* %2$s\n",tempFloat,temp));
        return rf.toString();
    }

    @Override
    protected String procesarWhenIs(WhenIs whenIs,String simboloCpm, String expresion, String bloque) {
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
