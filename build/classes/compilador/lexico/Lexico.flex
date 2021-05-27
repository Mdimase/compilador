package compilador.lexico;

import compilador.sintactico.SintacticoSym;
import java_cup.runtime.Symbol;

%%

%public
%class Lexico
%cup
%char
%line
%column
%unicode

%eofval{
     return new Symbol(SintacticoSym.EOF);
%eofval}

FIN_DE_LINEA = \r | \n | \r\n

BLANCO       = {FIN_DE_LINEA} | [ \t\f]

IDENTIFICADOR = [_]* [\p{letter}] [\w]*

ENTERO = [\d]+

BOOL = \.[TF]\.

FLOAT = \d+\.\d*|\.\d+

%%

<YYINITIAL> {

    "integer"        { return new Symbol(SintacticoSym.TIPO_INT, yycolumn, yyline); }
    "boolean"        { return new Symbol(SintacticoSym.TIPO_BOOL, yycolumn, yyline); }
    "float"          { return new Symbol(SintacticoSym.TIPO_FLOAT, yycolumn, yyline); }
    ":"              { return new Symbol(SintacticoSym.COLON, yycolumn, yyline); }
    "}"              { return new Symbol(SintacticoSym.LLAVE_CIERRA, yycolumn, yyline); }
    "{"              { return new Symbol(SintacticoSym.LLAVE_ABRE, yycolumn, yyline); }  
    "vars"           { return new Symbol(SintacticoSym.VARS, yycolumn, yyline); }
    "+"              { return new Symbol(SintacticoSym.MAS, yycolumn, yyline); }
    "-"              { return new Symbol(SintacticoSym.MENOS, yycolumn, yyline); }
    "*"              { return new Symbol(SintacticoSym.ASTERISCO, yycolumn, yyline); }
    "/"              { return new Symbol(SintacticoSym.BARRA, yycolumn, yyline); }
    "="              { return new Symbol(SintacticoSym.IGUAL, yycolumn, yyline); }
    "("              { return new Symbol(SintacticoSym.PARENTESIS_ABRE, yycolumn, yyline); }
    ")"              { return new Symbol(SintacticoSym.PARENTESIS_CIERRA, yycolumn, yyline); }
    ","              { return new Symbol(SintacticoSym.COMA, yycolumn, yyline); }
    {ENTERO}         { return new Symbol(SintacticoSym.CONSTANTE_ENTERA, (int) yychar, yyline, yytext()); }
    {BOOL}           { return new Symbol(SintacticoSym.CONSTANTE_BOOL, (int) yychar, yyline, yytext()); }
    {FLOAT}          { return new Symbol(SintacticoSym.CONSTANTE_FLOAT, (int) yychar, yyline, yytext()); }

    {IDENTIFICADOR}  { return new Symbol(SintacticoSym.IDENTIFICADOR, (int) yychar, yyline, yytext()); }

    {BLANCO}         {}
}

[^]                  { throw new Error(String.format("Carácter no permitido: <%s> en línea %d, columna %d.", yytext(), yyline, yycolumn)); }
