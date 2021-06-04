package compilador.ast.instrucciones;

import compilador.ast.base.ExcepcionDeAlcance;
import compilador.ast.base.ExcepcionDeTipos;
import compilador.ast.base.Expresion;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

public class Write extends Sentencia{
    private Expresion expresion;
    private Boolean esString=false;
    private Boolean esLn=false;

    public Write(Expresion expresion) {
        super("Write");
        this.expresion = expresion;
    }

    public Write(Expresion expresion, Boolean esLn) {
        super("WriteLn");
        this.expresion = expresion;
        this.esLn = esLn;
    }

    public Write(Boolean esLn) {
        super("WriteLn String");
        this.esLn = esLn;
    }

    public Write() {
        super("Write String");
    }

    public Boolean getEsString() {
        return esString;
    }

    public void setEsString(Boolean esString) {
        this.esString = esString;
    }

    public Boolean getEsLn() {
        return esLn;
    }

    public void setEsLn(Boolean esLn) {
        this.esLn = esLn;
    }

    public Expresion getExpresion() {
        return expresion;
    }

    public void setExpresion(Expresion expresion) {
        this.expresion = expresion;
    }

    @Override
    public <T> T accept(Visitor<T> v) throws ExcepcionDeAlcance {
        return v.visit(this);
    }

    @Override
    public Write accept_transfomer(Transformer t) throws ExcepcionDeTipos {
        return t.transform(this);
    }

}
