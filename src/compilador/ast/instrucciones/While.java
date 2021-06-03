package compilador.ast.instrucciones;

import compilador.ast.base.Bloque;
import compilador.ast.base.ExcepcionDeAlcance;
import compilador.ast.base.ExcepcionDeTipos;
import compilador.ast.base.Expresion;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

public class While extends Sentencia{

    private Expresion condicion;
    private Bloque bloque;

    public While(Expresion condicion, Bloque bloque) {
        this.condicion = condicion;
        this.bloque = new Bloque(bloque.initSentencia(bloque),"BLOQUE_WHILE",false);
    }

    public While(String nombre, Expresion condicion, Bloque bloque) {
        super(nombre);
        this.condicion = condicion;
        this.bloque = new Bloque(bloque.initSentencia(bloque),"BLOQUE_WHILE",false);
        if(bloque.getSentencias().size() > 1){ //bloque es el del parametro(no el atributo), ademas solamente tendra mas de una sentencia en caso de la tranformacion
            Asignacion asignacion = (Asignacion) bloque.getSentencias().get(1); //esto ocurre solamente cuando instancio la transformacion de for a while
            this.bloque.getSentencias().add(asignacion);
        }

    }

    public Expresion getCondicion() {
        return condicion;
    }

    public void setCondicion(Expresion condicion) {
        this.condicion = condicion;
    }

    public Bloque getBloque() {
        return bloque;
    }

    public void setBloque(Bloque bloque) {
        this.bloque = bloque;
    }

    @Override
    public <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance {
        return v.visit(this);
    }

    @Override
    public While accept_transfomer(Transformer t) throws ExcepcionDeTipos {
        return t.transform(this);
    }
}
