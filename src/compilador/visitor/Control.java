package compilador.visitor;

import compilador.ast.base.*;
import compilador.ast.instrucciones.*;
import compilador.ast.operaciones.binarias.OperacionBinaria;

import java.util.List;

public class Control extends Visitor<Void>{

    public void procesar(Programa programa) throws ExcepcionDeAlcance {
        super.visit(programa);
    }

    @Override
    public Void visit(Return r) throws ExcepcionDeAlcance {
        if (!isEnFuncion()) {
            throw new ExcepcionDeAlcance("Se encontró return fuera de una función");
        }
        return super.visit(r);
    }

    @Override
    public Void visit(Break b) throws ExcepcionDeAlcance {
        if (!isEnBucle()) {
            throw new ExcepcionDeAlcance("Se encontró break fuera de un bucle");
        }
        return super.visit(b);
    }

    @Override
    public Void visit(Continue c) throws ExcepcionDeAlcance {
        if (!isEnBucle()) {
            throw new ExcepcionDeAlcance("Se encontró continue fuera de un bucle");
        }
        return super.visit(c);
    }

    @Override
    protected Void procesarWhenIs(WhenIs whenIs, Void simboloCpm, Void expresion, Void bloque) {
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
