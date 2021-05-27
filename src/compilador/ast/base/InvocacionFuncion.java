package compilador.ast.base;

import compilador.ast.instrucciones.Sentencia;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

public class InvocacionFuncion extends Sentencia {

    private Identificador identificador;
    private List<Expresion> params = new ArrayList<Expresion>();

    public InvocacionFuncion(String nombre, Identificador identificador, List<Expresion> params) {
        super(nombre);
        this.identificador = identificador;
        this.params = params;
    }

    public InvocacionFuncion(Identificador identificador, List<Expresion> params) {
        this.identificador = identificador;
        this.params = params;
    }

    public InvocacionFuncion(String nombre, Identificador identificador) {
        super(nombre);
        this.identificador = identificador;
    }

    public InvocacionFuncion(Identificador identificador) {
        this.identificador = identificador;
    }

    public Identificador getIdentificador() {
        return identificador;
    }

    public void setIdentificador(Identificador identificador) {
        this.identificador = identificador;
    }

    public List<Expresion> getParams() {
        return params;
    }

    public void setParams(List<Expresion> params) {
        this.params = params;
    }

    @Override
    public <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance {
        return v.visit(this);   //invoca el visit(identificador) en visitor o el visit(identificador) de cualquier subclase de Visitor, va a depender de <T>
    }

}
