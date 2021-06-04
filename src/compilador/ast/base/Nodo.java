package compilador.ast.base;

import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

// concepto mas general, todas mis clases que formen parte del AST van a heredar de Nodo
public abstract class Nodo{
    private String nombre;

    public Nodo() {}

    public Nodo(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // etiqueta para cuando imprimimos el nodo, en el grafico
    public String getEtiqueta() {
        if (this.nombre != null) {
            return this.getNombre();
        }
        final String name = this.getClass().getName();
        final int pos = name.lastIndexOf('.') + 1;
        return name.substring(pos);
    }
    
    public abstract <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance;

    public abstract <R> R accept_transfomer(Transformer t) throws ExcepcionDeTipos;

}