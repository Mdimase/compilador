package compilador.visitor;

import compilador.ast.base.*;
import compilador.ast.instrucciones.*;
import compilador.ast.operaciones.binarias.OperacionBinaria;

import java.util.List;

public class GeneradorAlcanceGlobal extends Visitor<Void>{

    private Alcance alcance_global;

    public Alcance getAlcance_global() {
        return alcance_global;
    }

    public void procesar(Programa programa) throws ExcepcionDeAlcance{
        if(programa.getDeclaraciones() != null){
            this.visit(programa.getDeclaraciones());
        } else {
            alcance_global = new Alcance("global"); //lo va a devolver vacio
        }
    }

    // agregarSimbolo(nombre , declaracion)
    private Object agregarSimbolo(String nombre, Object s) throws ExcepcionDeAlcance {
        if(alcance_global.resolver(nombre) != null){    //retorna el repetido, si no esta -> null
            throw new ExcepcionDeAlcance(String.format("El nombre de %2$s %1$s fue utilizado previamente\"]\n",nombre,s.getClass().getSimpleName()));
        }
        return this.alcance_global.putIfAbsent(nombre, s);  //retorna lo que habia previamente, si no habia nada tira null
    }

    @Override
    public Void visit(Bloque bloque) throws ExcepcionDeAlcance {
        bloque.setAlcance(new Alcance("global"));
        this.alcance_global =  bloque.getAlcance();
        super.visit(bloque);
        return null;
    }

    @Override
    public Void visit(DeclaracionFuncion declaracionFuncion) throws ExcepcionDeAlcance {
        Object result = this.agregarSimbolo(declaracionFuncion.getIdentificador().getNombre(), declaracionFuncion);
        if(result!=null){   //repetido
            throw new ExcepcionDeAlcance(
                    String.format("El nombre de la funcion %1$s de tipo retorno %2$s fue utilizado previamente\"]\n",
                            declaracionFuncion.getIdentificador().getNombre(), declaracionFuncion.getTipoRetorno() ));
        }
        return null;
    }

    @Override
    public Void visit(DeclaracionVariable dv) throws ExcepcionDeAlcance{
        Object result = this.agregarSimbolo(dv.getId().getNombre(), dv);
        if(result!=null){   //repetido
            throw new ExcepcionDeAlcance(String.format("El nombre de la variable %1$s de tipo %2$s fue utilizado previamente\"]\n",
                    dv.getId().getNombre(), dv.getTipo() ));
        }
        super.visit(dv);
        return null;
    }

    @Override
    public Void visit(Identificador identificador) throws ExcepcionDeAlcance {
        if(this.alcance_global.resolver(identificador.getNombre()) == null){
            throw new ExcepcionDeAlcance(String.format("%1$s NO esta declarado previamente\"]\n",identificador.getNombre()));
        }
        return null;
    }

    @Override
    public Void visit(InvocacionFuncion invocacionFuncion) throws ExcepcionDeAlcance {
            throw new ExcepcionDeAlcance("No se puede invocar funciones en el bloque declaraciones");
    }

    @Override
    protected Void procesarWhenIs(WhenIs whenIs,Void simboloCpm, Void expresion, Void bloque) {
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
    protected Void procesarParametro(Parametro parametro, Void identificador, Void valor_defecto) {
        return null;
    }

    @Override
    protected Void procesarParametro(Parametro parametro, Void identificador) {
        return null;
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
