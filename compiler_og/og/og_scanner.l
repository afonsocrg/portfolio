%option c++ prefix="og_scanner_" outfile="og_scanner.cpp"
%option stack noyywrap yylineno 8bit debug
%{ 
// make relevant includes before including the parser's tab file
#include <string>
#include <vector>
#include <sstream>
#include <stdexcept>
#include <cdk/ast/sequence_node.h>
#include <cdk/ast/expression_node.h>
#include <cdk/ast/lvalue_node.h>
#include "og_parser.tab.h"

static std::ostringstream strlit;

// don't change this
#define yyerror LexerError
%}
%x X_STRING X_COMMENT X_ESCAPE X_EATSTRING
%%
    /* DATA TYPES */

"int"                 return tINT_TYPE;
"real"                return tREAL_TYPE;
"string"              return tSTRING_TYPE;
"ptr"                 return tPTR;
"auto"                return tAUTO;
"nullptr"             return tNULL;    


    /* MODIFIERS */
"public"              return tPUBLIC;
"require"             return tREQUIRE;
"procedure"           return tPROCEDURE;  

    /* COMMENTS */

"//".*\n               ; /* ignore comments */
"/*"                   yy_push_state(X_COMMENT);
<X_COMMENT>"/*"        yy_push_state(X_COMMENT);
<X_COMMENT>"*/"        yy_pop_state();
<X_COMMENT>.|\n        ;  

    /* OPERATORS */

">="                   return tGE; 
"<="                   return tLE; 
"=="                   return tEQ; 
"!="                   return tNE;
"&&"                   return tAND;
"||"                   return tOR;
"sizeof"               return tSIZEOF;


    /* INSTRUCTIONS */

"for"                  return tFOR;
"do"                   return tDO;
"if"                   return tIF;
"then"                 return tTHEN;
"elif"                 return tELIF;
"else"                 return tELSE;
"write"                return tWRITE;
"writeln"              return tWRITELN;
"input"                return tINPUT;
"break"                return tBREAK;
"continue"             return tCONTINUE;
"return"               return tRETURN;




    /* IDENTIFIERS */
[A-Za-z][A-Za-z0-9_]*  yylval.s = new std::string(yytext); return tIDENTIFIER;


    /* LITERALS */
    /* Integers */
[0-9]+                 try {
                           yylval.i = std::stoi(yytext, nullptr, 10);
                           return tINTEGER;
                       } catch (const std::out_of_range& oor) {
                            yyerror("integer constant is too large for its type");
                       }


0x[0-9a-fA-F]+         try {
                           yylval.i = std::stoi(yytext, nullptr, 16);
                           return tINTEGER;
                       } catch (const std::out_of_range& oor) {
                            yyerror("integer constant is too large for its type");
                       }

    /* Doubles */
(((([0-9]+\.[0-9]*)|([0-9]*\.[0-9]+))([Ee][+-]?[0-9]+)?)|([0-9]+([Ee]([+-])?[0-9]+)))   {
            try {
                yylval.d = std::stod(yytext, nullptr);
                return tREAL;
            } catch (const std::out_of_range& err) {
                yyerror("double constant is too large for its type");
            } catch (const std::invalid_argument& err) {
                // should not happen. Keeping the safeguard
                yyerror("invalid float read");
            }
}

    /* Strings */
\"                     yy_push_state(X_STRING);
<X_STRING>\\           yy_push_state(X_ESCAPE);
<X_STRING>\"           { yylval.s = new std::string(strlit.str());
                         strlit.str("");
                         yy_pop_state();
                         return tSTRING;
                        }
<X_STRING>.            strlit << *yytext;
<X_STRING>\n           yyerror("newline in string");

<X_ESCAPE>n            strlit << '\n' ; yy_pop_state();
<X_ESCAPE>r            strlit << '\r' ; yy_pop_state();
<X_ESCAPE>t            strlit << '\t' ; yy_pop_state();
<X_ESCAPE>\"           strlit << '"'  ; yy_pop_state();
<X_ESCAPE>\\           strlit << '\\' ; yy_pop_state();
<X_ESCAPE>00?          yy_push_state(X_EATSTRING);

<X_ESCAPE>[0-9a-fA-F]{1,2} {
                                strlit << (char) strtoul(yytext, NULL, 16);
                                yy_pop_state();
                            }

<X_ESCAPE>.             strlit << *yytext; yy_pop_state();


<X_EATSTRING>\"         {// finished reading string. returning it
                         yy_pop_state(); // pop X_EATSTRING
                         yy_pop_state(); // pop X_ESCAPE
                         yy_pop_state(); // pop X_STRING

                        // get current string and reset strlit
                         yylval.s = new std::string(strlit.str());
                         strlit.str("");

                         return tSTRING;
                        }
<X_EATSTRING>\n         yyerror("newline in string");
<X_EATSTRING>\\.        ; // ignore any escaped character (including escaped quote)
<X_EATSTRING>.          ; // ignore any other character

[-~()<>=+*/%;{}@?\[\],]   return *yytext;


[ \n\t]+               ; /* ignore whitespace */

.                      yyerror("Unknown character");

%%
