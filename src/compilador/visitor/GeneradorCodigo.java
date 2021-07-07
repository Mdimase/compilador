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
    private Stack<ArrayList<String>> etiquetasSalto = new Stack<>(); //pila de etiquetas para implementar el cortocircuito booleano
    private StringBuilder resultado = new StringBuilder();
    private StringBuilder inicializaciones = new StringBuilder();   //contiene la inicializacion de las variables globales
    private StringBuilder globalVar = new StringBuilder();
    private StringBuilder str = new StringBuilder();    //contiene las constantes string de los WRITE(String)

    public GeneradorCodigo(Alcance alcance_global) {
        this.alcance_global = alcance_actual = alcance_global;
    }

    // mapeo entre tipos del lenguaje  a tipos LLVM
    // la lista tendra en .get(0)=tipoir y en .get(1)=valor
    // [tipoIr,valor] siendo ese valor uno por defecto
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

    //simplemente para que las locales no compartan la misma numeracion que las globales
    public int getGlobalID(){
        id+=1;
        return id;
    }

    //generar nombres globales
    public String getIRGlobalName(){
        return String.format("@.%1$s", this.getGlobalID());
    }

    //generar nombres de constantes string para el write(String)
    public String getStrName(){
        return String.format("@str.%1$s",String.valueOf(getID()));
    }

    // generar nombres de registros virtuales
    public String newTempId(){
        return String.format("%%t$%1$s", String.valueOf(getID()));
    }

    // generar nombres de etiquetas de bloques LLVM
    public String newTempLabel(){
        return String.format("%%label$%1$s", String.valueOf(getID()));
    }

    //funcion que recibe un %label y devuelve un label:
    public String reFormatLabel(String label){
        label = label.replace("%","");  //elimino el %
        return label.concat(":");  //agrego el :
    }

    // crea un array con formato acorde a la pila de etiquetas
    public ArrayList<String> initElementoPila (String expV, String expF){
        ArrayList<String> elementPila = new ArrayList<>();
        elementPila.add(expV);
        elementPila.add(expF);
        return  elementPila;
        // [expV,expF] -> [0] = expV ; [1] -> expF
    }

    //setea los nombres globales de funciones y variables
    // lo primero que hago, antes de arrancar a recorrer el arbol
    public void setGlobalNames(){
        for(Object res: alcance_global.values()){
            if(res instanceof DeclaracionVariable){
                ((DeclaracionVariable) res).setIrName(this.getIRGlobalName());
            }
            if(res instanceof DeclaracionFuncion){
                ((DeclaracionFuncion) res).getIdentificador().setIrRef(this.getIRGlobalName());
            }
        }
    }

    //agrega a las inicializaciones de variables globales o debajo de ellas para otras instrucciones
    //de ser necesario
    public void insertInToStringBuilder(String str){
        if(alcance_actual == alcance_global){
            inicializaciones.append(str);
        } else {
            resultado.append(str);
        }
    }

    public String procesar(Programa programa,String nombreArchivo) throws ExcepcionDeAlcance {
        this.nombreArchivo = nombreArchivo;
        globalVar.append(String.format("source_filename = \"%1$s\"\n", nombreArchivo)); //nombre del archivo

        //CAMBIAR LOS TARGET de ser necesario
        globalVar.append("target datalayout = \"e-m:w-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128\"\n"); // tratamiento de numeros
        globalVar.append("target triple = \"x86_64-pc-windows-msvc19.29.30038\"\n\n"); // "arquitectura destino"

        //importo funciones externas
        globalVar.append("declare i32 @puts(i8*)\n");
        globalVar.append("declare i32 @printf(i8*, ...)\n");
        globalVar.append("declare i32 @scanf(i8* %0, ...)\n\n");

        //constantes usadas en operaciones I/O
        globalVar.append("@.bool = private constant [3 x i8] c\"%d\\00\"\n");
        globalVar.append("@.booln = private constant [4 x i8] c\"%d\\0A\\00\"\n");
        globalVar.append("@.integer = private constant [3 x i8] c\"%d\\00\"\n");
        globalVar.append("@.float = private constant [3 x i8] c\"%f\\00\"\n");
        globalVar.append("@.integern = private constant [4 x i8] c\"%d\\0A\\00\"\n");
        globalVar.append("@.floatn = private constant [4 x i8] c\"%f\\0A\\00\"\n");
        globalVar.append("@.inputFloat = private constant [18 x i8] c\"Ingrese un Float:\\00\"\n");
        globalVar.append("@.inputInteger = private constant [20 x i8] c\"Ingrese un Integer:\\00\"\n");
        globalVar.append("@.inputBool = private constant [26 x i8] c\"Ingrese un Bool(0=f/1=t):\\00\"\n");
        globalVar.append("@.bool_read_format = unnamed_addr constant [3 x i8] c\"%d\\00\"\n");
        globalVar.append("@.int_read_format = unnamed_addr constant [3 x i8] c\"%d\\00\"\n");
        globalVar.append("@.double_read_format = unnamed_addr constant [4 x i8] c\"%lf\\00\"\n\n");

        this.setGlobalNames();  //creo todos los irName de variables globales y nombres de funciones sin inicializar

        //arranco a recorrer el arbol
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
            resultado.append("define i32 @main(i32, i8**) {\n");    //main del LLVM
            resultado.append(inicializaciones); //inicializacion de todas las variables globales
            resultado.append(super.visit(bloque));  //genero codigo de las sentencias dentro del bloque main
            resultado.append("  ret i32 0\n");
            resultado.append("}");
        } else{
            resultado.append(super.visit(bloque));  //genero codigo del bloque
            alcance_actual = bloque.getAlcance().getPadre();
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
                String tipoPLlvm = this.LLVM_IR_TYPE_INFO.get(param.getTipo()).get(0);   // tipo del parametro
                param.getValorDefecto().accept(this); // genero codigo para los parametros con valores por defecto
                p.append(String.format(", %1$s %2$s",tipoPLlvm,param.getValorDefecto().getIrRef()));
            }
        }
        resultado.append(String.format("  %1$s = call %2$s %3$s(%4$s)\n",invocacionFuncion.getIrRef(),tipoLlvm,df.getIdentificador().getIrRef(),p));
        return "";
    }

    public StringBuilder conseguirParametrosInvocacion(InvocacionFuncion invocacionFuncion) throws ExcepcionDeAlcance {
        StringBuilder params = new StringBuilder();
        for(Expresion p:invocacionFuncion.getParams()){
            resultado.append(p.accept(this)); //codigo del parametro
            String tipoPLlvm = this.LLVM_IR_TYPE_INFO.get(p.getTipo()).get(0);   //tipo
            params.append(String.format("%1$s %2$s, ",tipoPLlvm,p.getIrRef()));
        }
        return params.deleteCharAt(params.lastIndexOf(","));    //le borro la coma que me genera al final
    }

    @Override
    public String visit(DeclaracionFuncion declaracionFuncion) throws ExcepcionDeAlcance {
        //alcance_actual = declaracionFuncion.getAlcance();
        resultado.append("\n");
        String tipoLlvm = this.LLVM_IR_TYPE_INFO.get(declaracionFuncion.getTipoRetorno()).get(0);   //tipo retorno
        String params = this.conseguirParametros(declaracionFuncion.getParametros());
        resultado.append(String.format("define %1$s %2$s (%3$s) {\n",   //firma de la funcion
                tipoLlvm,declaracionFuncion.getIdentificador().getIrRef() , params));
        this.inicializarParametros(declaracionFuncion.getParametros()); //inicializacion de los parametros
        resultado.append(declaracionFuncion.getBloque().accept(this));  //cuerpo de la funcion
        resultado.append("}\n");
        alcance_actual = declaracionFuncion.getAlcance().getPadre();
        return "";
    }

    // se encarga de inicializar los parametros dentro de una declaracion de funcion
    public void inicializarParametros(List<Parametro> parametros){
        for(Parametro p: parametros){
            String tipoLlvm = this.LLVM_IR_TYPE_INFO.get(p.getTipo()).get(0);   //tipo parametro
            String pName = p.getIrRef();    //guardo el que tenia en la firma de la funcion
            p.setIrRef(this.newTempId());   //nuevo nombre para el alloca
            resultado.append(String.format("  %1$s = alloca %2$s ; alloca = %1$s\n", p.getIrRef(), tipoLlvm));
            resultado.append(String.format("  store %1$s %2$s, %1$s* %3$s ; %3$s = %2$s\n", tipoLlvm, pName, p.getIrRef()));
        }
    }

    // devuelve string con los parametros, listo para insertar en la firma de la funcion
    // podria haber usado un visit(Parametro), pero me resulto mas simple hacer esta funcion
    public String conseguirParametros(List<Parametro> list){
        StringBuilder auxp = new StringBuilder();
        for (Parametro p: list){
            p.setIrRef(this.newTempId());
            String tipoPLlvm = this.LLVM_IR_TYPE_INFO.get(p.getTipo()).get(0); //tipo parametro
            auxp.append(String.format("%1$s %2$s,", tipoPLlvm, p.getIrRef()));
        }
        return auxp.deleteCharAt(auxp.lastIndexOf(",")).toString();    //le borro la coma que me genera al final
    }

    @Override
    public String visit(Return r) throws ExcepcionDeAlcance {
        resultado.append(r.getExpresion().accept(this)); //genero codigo de la expresion del return
        String tipoLlvm = this.LLVM_IR_TYPE_INFO.get(r.getExpresion().getTipo()).get(0);    //como esta chequeado que coincida con el tipo de retorno de la funcion, puedo usar el tipo de l expresion
        resultado.append(String.format("  ret %1$s %2$s\n",tipoLlvm,r.getExpresion().getIrRef()));  // ret tipo irRef expresion
        return "";
    }

    @Override
    public String visit(DeclaracionVariable declaracionVariable) throws ExcepcionDeAlcance {
        String tipoLlvm = this.LLVM_IR_TYPE_INFO.get(declaracionVariable.getTipo()).get(0);
        if(alcance_actual == alcance_global){ //variables globales
            String valor_ir = LLVM_IR_TYPE_INFO.get(declaracionVariable.getTipo()).get(1);  //valor por default
            globalVar.append(String.format("%1$s = global %2$s %3$s\n", declaracionVariable.getIrName(), tipoLlvm, valor_ir));
            declaracionVariable.getExpresion().accept(this);  //genero codigo de la expresion de inicializacion
            inicializaciones.append(String.format("  store %1$s %2$s, %1$s* %3$s ; %3$s = %2$s\n",
                    tipoLlvm, declaracionVariable.getExpresion().getIrRef(), declaracionVariable.getIrName())); //store del resultado de la expresion
        } else {    //variables locales
            declaracionVariable.setIrName(this.newTempId());
            resultado.append(String.format("  %1$s = alloca %2$s ; alloca = %1$s\n", declaracionVariable.getIrName(), tipoLlvm));
            resultado.append(declaracionVariable.getExpresion().accept(this));  //genero codigo de la expresion
            resultado.append(String.format("  store %1$s %2$s, %1$s* %3$s ; %3$s = %2$s\n"
                    , tipoLlvm, declaracionVariable.getExpresion().getIrRef(), declaracionVariable.getIrName())); //store del resultado de la expresion
        }
        return "";
    }

    @Override
    public String visit(Constante c) {
        if(c.getValor().equals("false")){   //convierto el string "false" -> 0
            c.setIrRef("0");
            return "";
        }
        if(c.getValor().equals("true")){    //convierto el string "true" -> 1
            c.setIrRef("1");
            return "";
        }
        if (c.getTipo() == Tipo.FLOAT){ //usa double por representacion del LLVM
            double a = Float.parseFloat((String) c.getValor());
            c.setIrRef(Double.toString(a));
        }else
            c.setIrRef((String) c.getValor());  //guardo el valor "int" directamente
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
            this.insertInToStringBuilder(inicializacion);
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
        aux.append(super.visit(s)); //visitar hijos
        s.setIrRef(this.newTempId());  // IrRef %temp
        String tipoLlvm = this.LLVM_IR_TYPE_INFO.get(s.getIzquierda().getTipo()).get(0);
        aux.append(String.format("  %1$s = %2$s %3$s %4$s, %5$s\n", s.getIrRef(),
                s.get_llvm_op_code(), tipoLlvm, s.getIzquierda().getIrRef(), s.getDerecha().getIrRef()));
        this.insertInToStringBuilder(aux.toString());
    }

    //OR con cortocircuito booleano
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

    //AND con cortocircuito booleano
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
            this.generarCodigoOperacionBinaria(and);    //OR o AND donde no estan en condiciones de estructuras de control. por ej una asignacion
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


    //esto quedo asi para no tener que modificar el visitor abstracto ni todos los que lo implementan
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

    // los visit(conversiones) se podrian haber juntado en un unico metodo que resulva la cuestion
    // lo unico que cambian son los tipos y la instruccion sitofp o fptosi

    public String visit(EnteroAFlotante eaf) throws ExcepcionDeAlcance {
        StringBuilder aux = new StringBuilder();
        aux.append(super.visit(eaf));   //genero codigo de la expresion
        eaf.setIrRef(this.newTempId());
        aux.append(String.format("  %1$s = sitofp i32 %2$s to float\n", eaf.getIrRef(), eaf.getExpresion().getIrRef()));
        this.insertInToStringBuilder(aux.toString());   //inicializaciones o resultado
        return "";
    }


    public String visit(FlotanteAEntero fae) throws ExcepcionDeAlcance {
        StringBuilder aux = new StringBuilder();
        aux.append(super.visit(fae));   //genero codigo de la expresion
        fae.setIrRef(this.newTempId());
        aux.append(String.format("  %1$s = fptosi float %2$s to i32\n", fae.getIrRef(), fae.getExpresion().getIrRef()));
        this.insertInToStringBuilder(aux.toString()); //inicializaciones o resultado
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
        this.insertInToStringBuilder(aux.toString()); //inicializaciones o resultado
        return "";
    }

    // invertir etiquetas de salto
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
            this.generarCodigoNot(not); //not para casos fuera de condiciones de estructuras de control
        }
        return "";
    }

    //instruccion xor que implementa el not
    public void generarCodigoNot(Not not) throws ExcepcionDeAlcance {
        StringBuilder aux = new StringBuilder();
        aux.append(super.visit(not));
        not.setIrRef(this.newTempId());
        aux.append(String.format("  %1$s = xor i1 %2$s, 1\n", not.getIrRef(), not.getExpresion().getIrRef()));
        this.insertInToStringBuilder(aux.toString());
    }


    /*
       if(cond){
          et_then: sentencias
          br et_fin
       } else {
          et_else: sentencias
          br et_fin
       }
       et_fin:
          sentencias despues del if
    */
    public void generarCodigoIfElse(If iF) throws ExcepcionDeAlcance {
        String etiquetaThen = this.newTempLabel();
        String etiquetaElse = this.newTempLabel();
        String etiquetaFin = this.newTempLabel();
        ArrayList<String> elementoPila = this.initElementoPila(etiquetaThen,etiquetaElse);  //creo el array como elemento de pila
        etiquetasSalto.push(elementoPila);
        resultado.append(iF.getCondicion().accept(this));   //genero codigo para la condicion
        elementoPila = etiquetasSalto.pop();    //elemento de pila con la condicion evaluada
        String etiquetaTrue = elementoPila.get(0);
        String etiquetaFalse = elementoPila.get(1);
        //salto condicional segun resultado de la condicion
        resultado.append(String.format("  br i1 %1$s, label %2$s, label %3$s\n\n", iF.getCondicion().getIrRef(),etiquetaTrue,etiquetaFalse));
        resultado.append(String.format("  %1$s\n",this.reFormatLabel(etiquetaThen)));   // etThen:
        resultado.append(iF.getBloqueThen().accept(this));  //genero codigo para el bloqueThen
        resultado.append(String.format("  br label %1$s\n\n",etiquetaFin));   // jump etFin
        resultado.append(String.format("  %1$s\n",this.reFormatLabel(etiquetaElse)));   //etElse:
        resultado.append(iF.getBloqueElse().accept(this));  //genero codigo para el bloqueElse
        resultado.append(String.format("  br label %1$s\n\n",etiquetaFin));   // jump etFin
        resultado.append(String.format("  %1$s\n",this.reFormatLabel(etiquetaFin)));   // etFin:
    }

    /*
       if(cond){
          et_then: sentencias
          br et_fin
       }
       et_fin:
          sentencias despues del if
    */

    public void generarCodigoIf(If iF) throws ExcepcionDeAlcance {
        String etiquetaThen = this.newTempLabel();
        String etiquetaFin = this.newTempLabel();
        ArrayList<String> elementoPila = this.initElementoPila(etiquetaThen,etiquetaFin);   //si es falso salta al fin
        etiquetasSalto.push(elementoPila);
        resultado.append(iF.getCondicion().accept(this));   //genero codigo para la condicion
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

    /*
       br et_cond       "para finalizar el bloque anterior al while"
       et_condicion:
            while(cond){
                et_true: sentencias
                br et_condicion
            }
            et_false:
                sentencias despues del while
    */

    @Override
    public String visit(While w) throws ExcepcionDeAlcance {
        String etiquetaCondicion = condicion = this.newTempLabel(); //atributo condicion necesario para el continue
        String etiquetaVerdadero = this.newTempLabel();
        String etiquetaFalso = this.newTempLabel();
        ArrayList<String> elementoPila = this.initElementoPila(etiquetaVerdadero,etiquetaFalso);
        etiquetasSalto.push(elementoPila);
        resultado.append(String.format("  br label %1$s\n\n",etiquetaCondicion));   //salto que "cierra" el bloque llvm anterior al while
        resultado.append(String.format("  %1$s\n",this.reFormatLabel(etiquetaCondicion))); //condicion:
        resultado.append(w.getCondicion().accept(this));    //codigo de la condicion
        String etiquetaTrue = etiquetasSalto.peek().get(0);
        String etiquetaFalse = etiquetasSalto.peek().get(1);
        etiquetasSalto.pop();
        //salto condicional segun resultado de la condicion
        resultado.append(String.format("  br i1 %1$s, label %2$s, label %3$s\n\n", w.getCondicion().getIrRef(),etiquetaTrue,etiquetaFalse));
        resultado.append(String.format("  %1$s\n",this.reFormatLabel(etiquetaTrue)));   // etTrue:
        resultado.append(w.getBloque().accept(this));   //codigo del bloque del while
        resultado.append(String.format("  br label %1$s\n\n",etiquetaCondicion));   // br etCondicion   "para volver a evaluar la condicion"
        resultado.append(String.format("  %1$s\n",this.reFormatLabel(etiquetaFalse)));   //etFalse:     "fin del while"
        return "";
    }

    @Override
    public String visit(Continue c){
        // salto a la condicion del while y "volver a consultar la condicion del while"
        resultado.append(String.format("  br label %1$s",condicion));
        return "";
    }

    @Override
    public String visit(Break b){
        //salto a la etiqueta false -> [true, false] "fin del while"
        resultado.append(String.format("  br label %1$s",etiquetasSalto.peek().get(1)));
        return "";
    }

    // hacer el store del resultado de la expresion en una determinada variable
    // quedo asi xq llame distinto los atributos IrName e IrRef en las distintas clases
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
        return "";
    }

    @Override
    public String visit(Write w) throws ExcepcionDeAlcance {
        if(w.getEsString()){
            this.generarCodigoWriteString(w);   //write(string)
        } else {
            this.generarCodigoWriteExp(w);  //write(expresion)
        }
        return "";
    }

    public void generarCodigoWriteString(Write w){
        w.setIrRefStr(this.getStrName());
        int longitud = w.getMensaje().getCadena().length() + 1; // por el \00
        String fin = "\\00\"\n";
        if(w.getEsLn()){    //agrego el salto de linea
            longitud +=1;   //por el \0A
            fin = "\\0A\\00\"\n";
        }
        str.append(String.format("%1$s = private constant [%2$s x i8] c\"%3$s%4$s",
                w.getIrRefStr(), String.valueOf(longitud),w.getMensaje().getCadena(),fin));
        //call
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
            // printf debe recibir un double en lugar de float para funcionar bien
            resultado.append(String.format("  %1$s = fpext float %2$s to double\n", temp_ref_to_print, ref_to_print)); //convierto a double
            ref_to_print = temp_ref_to_print;
            tipo_llvm = "double";
            if(w.getEsLn()){    //modificacion para el salto de linea
                variable_print = "@.floatn";
            } else {
                variable_print = "@.float";
            }
        } else {
            if(w.getEsLn()){    //modificacion para el salto de linea
                variable_print = "@.integern";
            } else {
                variable_print = "@.integer";
            }
            if(w.getExpresion().getTipo() == Tipo.BOOL){
                // 0 = false  1 = true
                ref_to_print = this.newTempId();
                tipo_llvm = "i32";
                resultado.append(String.format("  %s = zext i1 %s to i32\n", ref_to_print, w.getExpresion().getIrRef())); //casteo i1 a i32 para poder imprimir
            }
        }
        if(w.getEsLn()) {  //este if es culpa del [x x i8]
            resultado.append(String.format("  %1$s = call i32 (i8*, ...) @printf(i8* getelementptr([4 x i8], [4 x i8]* %2$s, i32 0, i32 0), %3$s %4$s)\n",
                    this.newTempId(), variable_print, tipo_llvm, ref_to_print));
        } else {
            resultado.append(String.format("  %1$s = call i32 (i8*, ...) @printf(i8* getelementptr([3 x i8], [3 x i8]* %2$s, i32 0, i32 0), %3$s %4$s)\n",
                    this.newTempId(), variable_print, tipo_llvm, ref_to_print));
        }
    }

    @Override
    public String visit(Read read){
        this.insertInToStringBuilder(this.generarCodigoRead(read)); //iniciazilacion o resultado
        return "";
    }

    public String generarCodigoRead(Read read){
        StringBuilder aux = new StringBuilder();
        String temp = this.newTempId();
        String tipoLlvm = this.LLVM_IR_TYPE_INFO.get(read.getTipo()).get(0);
        aux.append(String.format("  %1$s = alloca %2$s ; alloca = %1$s\n", temp, tipoLlvm));
        if(read.getTipo() == Tipo.INTEGER){ //esto es simplemente para que en la consola ante un read_integer() genere un mensajito que diga ingrese un integer:
            aux.append(String.format("  %1$s = call i32 (i8*, ...) @printf(i8* getelementptr([20 x i8], [20 x i8]* @.inputInteger, i32 0, i32 0))\n",this.newTempId())); //Ingrese un Integer:
            aux.append(String.format("  %1$s = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.int_read_format, i64 0, i64 0), %2$s* %3$s)\n", this.newTempId(),tipoLlvm,temp));
        }
        if(read.getTipo() == Tipo.BOOL){
            // si ingresa un numero > 1 tener en cuenta que almacena un unico bit (el ultimo) de su representacion asociado a su paridad,los pares los almacena como 0 y los impares como 1
            // ej 23 -> 1 o 4 -> 0
            // por eso en el input se recomienda usar 0 o 1. caso contrario estara haciendo un mal uso de la herramienta y puede obtener resultados confusos o inesperados
            aux.append(String.format("  %1$s = call i32 (i8*, ...) @printf(i8* getelementptr([26 x i8], [26 x i8]* @.inputBool, i32 0, i32 0))\n",this.newTempId())); //Ingrese un Bool(0-1):
            aux.append(String.format("  %1$s = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.bool_read_format, i32 0, i32 0), i1* %2$s)\n", this.newTempId(),temp));
        }
        if (read.getTipo() == Tipo.FLOAT){    // read_float()
            aux.append(String.format("  %1$s = call i32 (i8*, ...) @printf(i8* getelementptr([18 x i8], [18 x i8]* @.inputFloat, i32 0, i32 0))\n",this.newTempId())); //Ingrese un Float:
            aux.append(this.generarCodigoReadFloat(temp));
        }
        read.setIrRef(this.newTempId());
        aux.append(String.format("  %1$s = load %2$s, %2$s* %3$s ; %1$s = %4$s\n", read.getIrRef(), tipoLlvm, temp, temp)); //este load es xq al asignar el read a una variable necesito un valor, y no un puntero
        return aux.toString();
    }

    public String generarCodigoReadFloat(String temp){
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