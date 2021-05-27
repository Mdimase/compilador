package compilador.ast.operaciones.binarias;

import compilador.ast.base.Expresion;
import compilador.ast.base.Tipo;

public abstract class OperacionBinaria extends Expresion {

    //composite de expresiones
    private Expresion izquierda; //operando 1
    private Expresion derecha;  //operando 2

    //operacion sin tipo
    public OperacionBinaria(Expresion izquierda, Expresion derecha) {
        super(Tipo.UNKNOWN);
        this.izquierda = izquierda;
        this.derecha = derecha;
    }

    //sin tipo y con nombre
    public OperacionBinaria(Expresion izquierda, Expresion derecha, String nombre) {
        super(Tipo.UNKNOWN, nombre);
        this.izquierda = izquierda;
        this.derecha = derecha;
    }

    //operacion con tipo
    public OperacionBinaria(Expresion izquierda, Expresion derecha, Tipo tipo) {
        super(tipo);
        this.izquierda = izquierda;
        this.derecha = derecha;
    }

    // con tipo y nombre
    public OperacionBinaria(Expresion izquierda, Expresion derecha, Tipo tipo, String nombre) {
        super(tipo, nombre);
        this.izquierda = izquierda;
        this.derecha = derecha;
    }

    public void setIzquierda(Expresion izquierda) {
        this.izquierda = izquierda;
    }

    public void setDerecha(Expresion derecha) {
        this.derecha = derecha;
    }
       
    public Expresion getIzquierda(){
        return izquierda;
    }

    public Expresion getDerecha(){
        return derecha;
    }
    
    @Override
    public String getEtiqueta() {
        if(this.getTipo() != null){
            return String.format("%s %s", this.getNombre(), this.getTipo());
        }
        return String.format("%s", this.getNombre());
    }
    
}
