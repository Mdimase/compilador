package compilador.ast.base;

import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

public class SimboloCmp extends Nodo{
    //como el enum Comparador no puede extender de Nodo y lo quiero para poder graficar bien el when
    //creo esta clase que contiene el comparador y como nombre tendra el simbolo de cmp correspondiente
    private Comparador comparador;

    public SimboloCmp(String nombre, Comparador comparador) {
        super(nombre);
        this.comparador = comparador;
    }

    public Comparador getComparador() {
        return comparador;
    }

    public void setComparador(Comparador comparador) {
        this.comparador = comparador;
    }

    @Override
    public <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance {
       return v.visit(this);
    }

    @Override
    public SimboloCmp accept_transfomer(Transformer t) throws ExcepcionDeTipos {
        return t.transform(this);
    }
}
