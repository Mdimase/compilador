package compilador.ast.instrucciones;

import compilador.ast.base.Bloque;
import compilador.ast.base.Constante;
import compilador.ast.base.ExcepcionDeAlcance;
import compilador.ast.base.Identificador;
import compilador.visitor.Visitor;

public class For extends Sentencia{

    // QUEDO SIN USO, DADO QUE LA SENTENCIA FOR LA TRANSFORMAMOS EN UN WHILE,
    // DIRECTAMENTE INSTANCIAMOS UN WHILE EQUIVALENTE EN EL PARSING

    private Identificador identificador;
    private Bloque bloque;
    private Constante from;
    private Constante to;
    private Constante by;

    public For(Identificador identificador, Constante from, Constante to, Constante by, Bloque bloque) {
        this.identificador = identificador;
        this.bloque = bloque;
        this.from = from;
        this.to = to;
        this.by = by;
    }

    public For(Identificador identificador, Bloque bloque, Constante from, Constante to) {
        this.identificador = identificador;
        this.bloque = bloque;
        this.from = from;
        this.to = to;
    }

    public For(String nombre, Identificador identificador, Bloque bloque, Constante from, Constante to, Constante by) {
        super(nombre);
        this.identificador = identificador;
        this.bloque = new Bloque(bloque.initSentencia(bloque),"BLOQUE_FOR",false);
        this.from = from;
        this.to = to;
        this.by = by;
    }

    public For(String nombre, Identificador identificador, Bloque bloque, Constante from, Constante to) {
        super(nombre);
        this.identificador = identificador;
        this.bloque = new Bloque(bloque.initSentencia(bloque),"BLOQUE_FOR",false);
        this.from = from;
        this.to = to;
    }

    public Identificador getIdentificador() {
        return identificador;
    }

    public void setIdentificador(Identificador identificador) {
        this.identificador = identificador;
    }

    public Bloque getBloque() {
        return bloque;
    }

    public void setBloque(Bloque bloque) {
        this.bloque = bloque;
    }

    public Constante getFrom() {
        return from;
    }

    public void setFrom(Constante from) {
        this.from = from;
    }

    public Constante getTo() {
        return to;
    }

    public void setTo(Constante to) {
        this.to = to;
    }

    public Constante getBy() {
        return by;
    }

    public void setBy(Constante by) {
        this.by = by;
    }

    @Override
    public <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance {
        return v.visit(this);
    }
}
