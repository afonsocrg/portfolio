%{
//-- don't change *any* of these: if you do, you'll break the compiler.
#include <cdk/compiler.h>
#include "ast/all.h"
#define LINE               compiler->scanner()->lineno()
#define yylex()            compiler->scanner()->scan()
#define yyerror(s)         compiler->scanner()->error(s)
#define YYPARSE_PARAM_TYPE std::shared_ptr<cdk::compiler>
#define YYPARSE_PARAM      compiler
//-- don't change *any* of these --- END!
#define NIL (new cdk::nil_node(LINE))
%}



%union {
  int                   i;	/* integer value */
  double                d;    /* double value */           
  std::string          *s;	/* symbol name or string literal */
  cdk::basic_node      *node;	/* node pointer */
  cdk::sequence_node   *sequence;
  cdk::expression_node *expression; /* expression nodes */
  cdk::lvalue_node     *lvalue;
  cdk::integer_node    *integer;

  std::vector<std::string> *vs;

  cdk::primitive_type  *primitive;
  cdk::reference_type  *reference;   /* types */    
  og::block_node       *block;
  og::tuple_node       *tuple;     
 
};


%token tINT_TYPE tREAL_TYPE tSTRING_TYPE tPTR tAUTO tNULL
%token tPRIVATE tPUBLIC tREQUIRE tPROCEDURE



%token <i> tINTEGER
%token <d> tREAL
%token <s> tIDENTIFIER tSTRING 
%token tFOR tDO tIF tELIF tELSE tTHEN tINPUT tWRITE tWRITELN tBREAK tCONTINUE tRETURN

%nonassoc tTHEN
%nonassoc tELIF tELSE 
%token tAND tOR tSIZEOF

%right '='
%left tOR
%left tAND
%nonassoc '~'
%left tEQ tNE  
%left tGE tLE '>' '<'
%left '+' '-'
%left '*' '/' '%'
%nonassoc tUNARY '?' tSIZEOF '@' '[' 

//-- program
%type <node>  instruction cycle conditional else declaration
%type <sequence> var_decls instructions args file declarations for_args 
%type <expression> expr 
%type <lvalue> lval
%type <integer> integer
%type <primitive> primitive_type
%type <reference> reference_type

%type <vs> identifiers

%type <s>  string
%type <block> block
%type <tuple> exprs
// %type <node> variable function procedure var_decls
%type <node> auto_variable function simple_var procedure

%{
//-- The rules below will be included in yyparse, the main parsing function.
%}
%%

file            : declarations { compiler->ast($$ = $1);}
	            ;

declarations    :              declaration { $$ = new cdk::sequence_node(LINE, $1); }
	            | declarations declaration { $$ = new cdk::sequence_node(LINE, $2, $1); }
	            ;

declaration     : auto_variable ';'        { $$ = $1; }
                | simple_var    ';'        { $$ = $1; }
                | function                 { $$ = $1; }
                | procedure                { $$ = $1; }
                ;
                        

var_decls       : auto_variable ';'                                           { $$ = new cdk::sequence_node(LINE, $1); }
                | simple_var ';'                                              { $$ = new cdk::sequence_node(LINE, $1); }
                | var_decls auto_variable ';'                                 { $$ = new cdk::sequence_node(LINE, $2, $1); }
                | var_decls simple_var ';'                                    { $$ = new cdk::sequence_node(LINE, $2, $1); }   
                ;

auto_variable   : tPUBLIC  tAUTO identifiers '=' exprs                   {
                     $$ = new og::variable_declaration_node(LINE, tPUBLIC, std::make_shared<cdk::structured_type>(*(new std::vector<std::shared_ptr<cdk::basic_type>>())), *$3, $5);
                }

                |          tAUTO identifiers '=' exprs                   {
                     $$ = new og::variable_declaration_node(LINE, tPRIVATE, std::make_shared<cdk::structured_type>(*(new std::vector<std::shared_ptr<cdk::basic_type>>())), *$2, $4);
                }            
                ;

simple_var      : tPUBLIC  primitive_type tIDENTIFIER                    { 
                     $$ = new og::variable_declaration_node(LINE, tPUBLIC, std::make_shared<cdk::primitive_type>(*$2), *$3); 
                }

                | tPUBLIC  reference_type tIDENTIFIER                    { 
                     $$ = new og::variable_declaration_node(LINE, tPUBLIC, std::make_shared<cdk::reference_type>(*$2), *$3); 
                }

                | tPUBLIC  primitive_type tIDENTIFIER '=' expr           {
                     $$ = new og::variable_declaration_node(LINE, tPUBLIC, std::make_shared<cdk::primitive_type>(*$2), *$3, new og::tuple_node(LINE, $5));
                }

                | tPUBLIC  reference_type tIDENTIFIER '=' expr           {
                     $$ = new og::variable_declaration_node(LINE, tPUBLIC, std::make_shared<cdk::reference_type>(*$2), *$3, new og::tuple_node(LINE, $5));
                }

                | tREQUIRE primitive_type tIDENTIFIER                    { 
                     $$ = new og::variable_declaration_node(LINE, tREQUIRE, std::make_shared<cdk::primitive_type>(*$2), *$3); 
                }

                | tREQUIRE reference_type tIDENTIFIER                    { 
                     $$ = new og::variable_declaration_node(LINE, tREQUIRE, std::make_shared<cdk::reference_type>(*$2), *$3); 
                }

                |          primitive_type tIDENTIFIER                    { 
                     $$ = new og::variable_declaration_node(LINE, tPRIVATE, std::make_shared<cdk::primitive_type>(*$1), *$2); 
                }

                |          reference_type tIDENTIFIER                    { 
                     $$ = new og::variable_declaration_node(LINE, tPRIVATE, std::make_shared<cdk::reference_type>(*$1), *$2); 
                }

                |          primitive_type tIDENTIFIER '=' expr           {
                     $$ = new og::variable_declaration_node(LINE, tPRIVATE, std::make_shared<cdk::primitive_type>(*$1), *$2, new og::tuple_node(LINE, $4));
                }

                |          reference_type tIDENTIFIER '=' expr           {
                     $$ = new og::variable_declaration_node(LINE, tPRIVATE, std::make_shared<cdk::reference_type>(*$1), *$2, new og::tuple_node(LINE, $4));
                }
                ;

function        : tPUBLIC primitive_type  tIDENTIFIER '(' args ')'       {
                     $$ = new og::function_declaration_node(LINE, tPUBLIC, std::make_shared<cdk::primitive_type>(*$2), *$3, $5); 
                } 

                | tPUBLIC reference_type  tIDENTIFIER '(' args ')' {
                     $$ = new og::function_declaration_node(LINE, tPUBLIC, std::make_shared<cdk::reference_type>(*$2), *$3, $5); 
                } 



                | tPUBLIC tAUTO tIDENTIFIER '(' args ')'       {
                     $$ = new og::function_declaration_node(LINE, tPUBLIC, std::make_shared<cdk::primitive_type>(*(new cdk::primitive_type())), *$3, $5); 
                } 

                | tPUBLIC primitive_type  tIDENTIFIER '(' args ')' block {
                     $$ = new og::function_definition_node(LINE, tPUBLIC, std::make_shared<cdk::primitive_type>(*$2), *$3, $5, $7); 
                } 

                | tPUBLIC reference_type  tIDENTIFIER '(' args ')' block {
                     $$ = new og::function_definition_node(LINE, tPUBLIC, std::make_shared<cdk::reference_type>(*$2), *$3, $5, $7); 
                }

                | tPUBLIC tAUTO tIDENTIFIER '(' args ')' block  {
                     $$ = new og::function_definition_node(LINE, tPUBLIC, std::make_shared<cdk::primitive_type>(*(new cdk::primitive_type())), *$3, $5, $7); 
                }
 
                | tREQUIRE primitive_type tIDENTIFIER '(' args ')'    {
                     $$ = new og::function_declaration_node(LINE, tREQUIRE, std::make_shared<cdk::primitive_type>(*$2), *$3, $5); 
                } 

                | tREQUIRE reference_type tIDENTIFIER '(' args ')'       {
                     $$ = new og::function_declaration_node(LINE, tREQUIRE, std::make_shared<cdk::reference_type>(*$2), *$3, $5); 
                } 

                | tREQUIRE tAUTO tIDENTIFIER '(' args ')'      {
                     $$ = new og::function_declaration_node(LINE, tREQUIRE, std::make_shared<cdk::primitive_type>(*(new cdk::primitive_type())), *$3, $5); 
                }
          
                |          primitive_type tIDENTIFIER '(' args ')' {
                     $$ = new og::function_declaration_node(LINE, tPRIVATE, std::make_shared<cdk::primitive_type>(*$1), *$2, $4); 
                }

                |          reference_type tIDENTIFIER '(' args ')' {
                     $$ = new og::function_declaration_node(LINE, tPRIVATE, std::make_shared<cdk::reference_type>(*$1), *$2, $4); 
                }

                |          tAUTO tIDENTIFIER '(' args ')' {
                     $$ = new og::function_declaration_node(LINE, tPRIVATE, std::make_shared<cdk::primitive_type>(*(new cdk::primitive_type())), *$2, $4); 
                }

                |          primitive_type  tIDENTIFIER '(' args ')' block {
                     $$ = new og::function_definition_node(LINE, tPRIVATE, std::make_shared<cdk::primitive_type>(*$1), *$2, $4, $6); 
                }

                |          reference_type  tIDENTIFIER '(' args ')' block  {
                     $$ = new og::function_definition_node(LINE, tPRIVATE, std::make_shared<cdk::reference_type>(*$1), *$2, $4, $6); 
                }

                |          tAUTO tIDENTIFIER '(' args ')' block {
                     $$ = new og::function_definition_node(LINE, tPRIVATE, std::make_shared<cdk::primitive_type>(*(new cdk::primitive_type())), *$2, $4, $6); 
                }
                ;

procedure       : tPUBLIC  tPROCEDURE tIDENTIFIER '(' args ')'          { 
                     $$ = new og::function_declaration_node(LINE, tPUBLIC, std::make_shared<cdk::primitive_type>(*(new cdk::primitive_type(0, cdk::TYPE_VOID))), *$3, $5); 
                } 

                | tPUBLIC  tPROCEDURE tIDENTIFIER '(' args ')' block    {
                     $$ = new og::function_definition_node(LINE, tPUBLIC, std::make_shared<cdk::primitive_type>(*(new cdk::primitive_type(0, cdk::TYPE_VOID))), *$3, $5, $7);
                }

                | tREQUIRE tPROCEDURE tIDENTIFIER '(' args ')'         { 
                     $$ = new og::function_declaration_node(LINE, tREQUIRE, std::make_shared<cdk::primitive_type>(*(new cdk::primitive_type(0, cdk::TYPE_VOID))), *$3, $5);
                }

                |          tPROCEDURE tIDENTIFIER '(' args ')'         {
                     $$ = new og::function_declaration_node(LINE, tPRIVATE, std::make_shared<cdk::primitive_type>(*(new cdk::primitive_type(0, cdk::TYPE_VOID))), *$2, $4); 
                }

                |          tPROCEDURE tIDENTIFIER '(' args ')' block   {
                     $$ = new og::function_definition_node(LINE, tPRIVATE, std::make_shared<cdk::primitive_type>(*(new cdk::primitive_type(0, cdk::TYPE_VOID))), *$2, $4, $6);
                }
                ;

for_args        : auto_variable                      { $$ = new cdk::sequence_node(LINE, $1); }   
                | args                               { $$ = $1; }   
                ;

args            : simple_var                         { $$ = new cdk::sequence_node(LINE, $1); }
                | args ',' simple_var                { $$ = new cdk::sequence_node(LINE, $3, $1); }
                | /*empty*/                          { $$ = new cdk::sequence_node(LINE); }   
                ;
                
identifiers     : tIDENTIFIER                       { $$ = new std::vector<std::string>(); $$->push_back(*$1); }
                | identifiers ',' tIDENTIFIER       { $$ = $1; $$->push_back(*$3); }
                ;

exprs           : expr	                           { $$ = new og::tuple_node(LINE, $1); }
                | exprs ',' expr                     { $$ = new og::tuple_node(LINE, $3, $1); }
 	            ;


primitive_type  : tINT_TYPE                         { $$ = new cdk::primitive_type(4, cdk::typename_type::TYPE_INT); }
                | tSTRING_TYPE                      { $$ = new cdk::primitive_type(4, cdk::typename_type::TYPE_STRING); }
                | tREAL_TYPE                        { $$ = new cdk::primitive_type(8, cdk::typename_type::TYPE_DOUBLE); }
                ; 

reference_type  : tPTR '<' primitive_type '>'       { $$ = new cdk::reference_type(4, std::make_shared<cdk::primitive_type>(*$3)); }
                | tPTR '<' tAUTO '>'                { $$ = new cdk::reference_type(4, cdk::make_primitive_type(0, cdk::TYPE_VOID)); }
                | tPTR '<' reference_type '>'       {
                                                        if($3->referenced()->name() == cdk::TYPE_VOID) {
                                                            // ptr<ptr<...ptr<auto>...>> == ptr<auto>
                                                            $$ = $3;
                                                        } else {
                                                            $$ = new cdk::reference_type(4, std::make_shared<cdk::reference_type>(*$3));
                                                        }
                                                    }
                ;

block           : '{' var_decls instructions '}'    { $$ = new og::block_node(LINE, $2, $3); }
                | '{'           instructions '}'    { $$ = new og::block_node(LINE, nullptr, $2); }
                | '{' var_decls              '}'    { $$ = new og::block_node(LINE, $2, nullptr); }
                | '{'                        '}'    { $$ = new og::block_node(LINE, nullptr, nullptr); }
                ;

instructions    : instruction                       { $$ = new cdk::sequence_node(LINE, $1); }    
                | instructions instruction          { $$ = new cdk::sequence_node(LINE, $2, $1); }
                ;

instruction     : expr ';'                          { $$ = new og::evaluation_node(LINE, $1); }
                | tWRITE exprs ';'                  { $$ = new og::write_node(LINE, new cdk::sequence_node(LINE, $2)); }
                | tWRITELN exprs ';'                { $$ = new og::write_node(LINE, new cdk::sequence_node(LINE, $2), true); }
                | tBREAK                            { $$ = new og::break_node(LINE); }
                | tCONTINUE                         { $$ = new og::continue_node(LINE); }
                | tRETURN exprs ';'                 { $$ = new og::return_node(LINE, $2); }
                | conditional                       { $$ = $1; }
                | cycle                             { $$ = $1; }
                | block                             { $$ = $1; }
                ;

conditional     : tIF expr tTHEN instruction                             { $$ = new og::if_node(LINE, $2, $4); }
                | tIF expr tTHEN instruction else                        { $$ = new og::if_else_node(LINE, $2, $4, $5); }        
                ;

else            : tELSE instruction                                      { $$ = $2; }
                | tELIF expr tTHEN instruction                           { $$ = new og::if_node(LINE, $2, $4); }
                | tELIF expr tTHEN instruction else                      { $$ = new og::if_else_node(LINE, $2, $4, $5); }
                ;

cycle           : tFOR exprs ';' exprs ';' exprs tDO instruction         { $$ = new og::for_node(LINE, new cdk::sequence_node(LINE, $2), new cdk::sequence_node(LINE, $4), new cdk::sequence_node(LINE, $6), $8); }
                | tFOR for_args ';' exprs ';' exprs tDO instruction      { $$ = new og::for_node(LINE, $2, new cdk::sequence_node(LINE, $4), new cdk::sequence_node(LINE, $6), $8); }
                ;

expr            : integer                    { $$ = $1; }
                | tREAL                      { $$ = new cdk::double_node(LINE, $1); }
                | tINPUT                     { $$ = new og::read_node(LINE); }
                | tNULL                      { $$ = new og::nullptr_node(LINE); }
                | string                     { $$ = new cdk::string_node(LINE, $1); }
                | tIDENTIFIER '(' exprs')'   { $$ = new og::function_call_node(LINE, *$1, new cdk::sequence_node(LINE, $3));  delete $1; }
                | tIDENTIFIER '(' ')'        { $$ = new og::function_call_node(LINE, *$1); delete $1; }

                | '(' expr ')'               { $$ = $2; }
                | '[' expr ']'               { $$ = new og::alloc_node(LINE, $2); }

                | '-' expr %prec tUNARY      { $$ = new cdk::neg_node(LINE, $2); }
                | '+' expr %prec tUNARY      { $$ = new og::identity_node(LINE, $2); }
                | lval '?'                   { $$ = new og::address_of_node(LINE, $1); }
                | tSIZEOF '(' exprs ')'      { $$ = new og::sizeof_node(LINE, $3); }
                
                | expr '*' expr	             { $$ = new cdk::mul_node(LINE, $1, $3); }
                | expr '/' expr	             { $$ = new cdk::div_node(LINE, $1, $3); }
                | expr '%' expr	             { $$ = new cdk::mod_node(LINE, $1, $3); }

                | expr '+' expr	             { $$ = new cdk::add_node(LINE, $1, $3); }
                | expr '-' expr	             { $$ = new cdk::sub_node(LINE, $1, $3); }

                | expr '<' expr	             { $$ = new cdk::lt_node(LINE, $1, $3); }
                | expr '>' expr	             { $$ = new cdk::gt_node(LINE, $1, $3); }
                | expr tGE expr	             { $$ = new cdk::ge_node(LINE, $1, $3); }
                | expr tLE expr              { $$ = new cdk::le_node(LINE, $1, $3); }

                | expr tNE expr	             { $$ = new cdk::ne_node(LINE, $1, $3); }
                | expr tEQ expr	             { $$ = new cdk::eq_node(LINE, $1, $3); }

                | '~' expr                   { $$ = new cdk::not_node(LINE, $2); } 

                | expr tAND expr             { $$ = new cdk::and_node(LINE, $1, $3); }         

                | expr tOR expr              { $$ = new cdk::or_node(LINE, $1, $3); } 

                | lval                       { $$ = new cdk::rvalue_node(LINE, $1); }
                | lval '=' expr              { $$ = new cdk::assignment_node(LINE, $1, $3); }
                ;

lval            : tIDENTIFIER                { $$ = new cdk::variable_node(LINE, $1); }
                | expr '[' expr ']'          { $$ = new og::ptr_indexation_node(LINE, $1, $3 ); }
                | expr '@' integer           { $$ = new og::tpl_indexation_node(LINE, $1, $3); }
                ;

integer         : tINTEGER                   { $$ = new cdk::integer_node(LINE, $1); }     
                ;

string          : tSTRING                    { $$ = new std::string(*$1); delete $1; }
                | string tSTRING             { $$ = new std::string(*$1 + *$2); delete $1; delete $2; }
                ;

%%
