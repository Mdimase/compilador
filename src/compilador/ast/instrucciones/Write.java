package compilador.ast.instrucciones;

import compilador.ast.base.ExcepcionDeAlcance;
import compilador.ast.base.ExcepcionDeTipos;
import compilador.ast.base.Expresion;
import compilador.ast.base.Mensaje;
import compilador.visitor.Transformer;
import compilador.visitor.Visitor;

public class Write extends Sentencia{
    private Expresion expresion;
    private Boolean esString=false;
    private Boolean esLn=false;
    private Mensaje mensaje;
    private String IrRefStr;

    public Write(Expresion expresion) {
        super("Write");
        this.expresion = expresion;
    }

    public Write(Expresion expresion, Boolean esLn) {
        super("WriteLn");
        this.expresion = expresion;
        this.esLn = esLn;
    }

    public Write(Boolean esLn,String mensaje) {
        super("WriteLn String");
        this.esLn = esLn;
        this.mensaje = new Mensaje(mensaje);
        this.esString = true;
    }

    public Write(String mensaje) {
        super("Write String");
        this.mensaje = new Mensaje(mensaje);
        this.esString = true;
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

    public Mensaje getMensaje() {
        return mensaje;
    }

    public void setMensaje(Mensaje mensaje) {
        this.mensaje = mensaje;
    }

    public String getIrRefStr() {
        return IrRefStr;
    }

    public void setIrRefStr(String irRefStr) {
        IrRefStr = irRefStr;
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
