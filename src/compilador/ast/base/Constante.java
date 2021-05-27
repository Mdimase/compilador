package compilador.ast.base;

import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

public class Constante extends Expresion {
    private final String valor;

    public Constante(String valor) {
        this.valor = valor;
    }

    public Constante(String valor, String nombre) {
        super(nombre);
        this.valor = valor;
    }

    public Constante(String valor, Tipo tipo, String nombre) {
        super(tipo, nombre);
        this.valor = valor;
    }

    public Constante(String valor, Tipo tipo) {
        super(tipo);
        this.valor = valor;
    }
        
    public Object getValor() {
        return valor;
    }

    @Override
    public String getEtiqueta() {
        return String.format(String.format("Const %s %s", getValor(), this.getTipo()));
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this); //invoca el visit(constante) de visitor o o el visit(constante) de cualquier subclase de Visitor, va a depender de <T>
    }

    /*
    @Override
    public Constante accept_transfomer(Transformer t) {
        return t.transform(this);
    }

     */
}
