#ifndef __OG_BASIC_AST_VISITOR_H__
#define __OG_BASIC_AST_VISITOR_H__

#include <string>
#include <stack>
#include <memory>
#include <iostream>
#include <cdk/compiler.h>
#include <cdk/symbol_table.h>
#include "targets/symbol.h"
#include <cdk/types/types.h>

/* do not edit -- include node forward declarations */
#define __NODE_DECLARATIONS_ONLY__
#include "ast/all.h"  // automatically generated
#undef __NODE_DECLARATIONS_ONLY__
/* do not edit -- end */

enum Place { DEFAULT, FUNCTION_BODY, FUNCTION_ARGS };


class basic_ast_visitor {
protected:
    // The owner compiler
    std::shared_ptr<cdk::compiler> _compiler;

private:
    // last symbol inserted in symbol table
    std::shared_ptr<og::symbol> _new_symbol;

    // current function symbol
    std::shared_ptr<og::symbol> _func_symbol;

    // declared bytes in function (local vars or args)
    int _totalDeclaredBytes = 0; 


    // used to check instruction surroundings
    Place _place = DEFAULT;


    // label stacks for nested fors
    std::stack<int> _endStack;
    std::stack<int> _incrStack;


    // flags
    bool _isInitializingVar = false;
    bool _isIndexatingTuple = false; // super martelated, like this englando

protected:
    basic_ast_visitor(std::shared_ptr<cdk::compiler> compiler) : _compiler(compiler) {}

    bool debug() { return _compiler->debug(); }
    std::ostream &os() { return *_compiler->ostream(); }

public:
    virtual ~basic_ast_visitor() {}

public:
    std::shared_ptr<og::symbol> new_symbol() { return _new_symbol; }
    void set_new_symbol(std::shared_ptr<og::symbol> symbol) { _new_symbol = symbol; }
    void reset_new_symbol() { _new_symbol = nullptr; }

    // exchange function symbols between visitors
    std::shared_ptr<og::symbol> func_symbol() const { return _func_symbol; }
    void set_func_symbol(std::shared_ptr<og::symbol> new_symbol) { _func_symbol = new_symbol; }
    void reset_func_symbol() { _func_symbol = nullptr; }

    // 'for' operations
    bool inside_for() { return _incrStack.size() > 0; }
    void enter_for(int lblend, int lblincr) {
        _endStack.push(lblend);
        _incrStack.push(lblincr);
    }
    void exit_for() {
        if(!inside_for()){
            std::cerr << "Cannot exit for loop: None exists" << std::endl;
            exit(1);
        }
        _endStack.pop();
        _incrStack.pop();
    }

    // get for loop end label (break)
    int end_label()  { return _endStack.top(); }

    // get for loop increment label (continue)
    int incr_label() { return _incrStack.top(); }

    // local variable offset calculation
    int declare_local_var(size_t bytes) {
        _totalDeclaredBytes += bytes;
        return -_totalDeclaredBytes;
    }

    int declare_local_arg(size_t bytes) {
        // save previous
        int prevDeclaredBytes = _totalDeclaredBytes;

        // update declared bytes
        _totalDeclaredBytes += bytes;

        // return offset

        // return 8 + prevDeclaredBytes;  // 8 = EBP + return_ptr
        if(_func_symbol->is_typed(cdk::TYPE_STRUCT)) {
            return 12 + prevDeclaredBytes; // 8 + tuple_ptr
        } else {
            return 8 + prevDeclaredBytes;  // 8 = EBP + return_ptr
        }
    }

    // flags

    // used to check instruction surroundings
    bool in_function_body()         { return _place == FUNCTION_BODY; }
    bool in_function_args()         { return _place == FUNCTION_ARGS; }
    bool in_global()                { return _place == DEFAULT; }

    void enter_function_body()      {
        _totalDeclaredBytes = 0;
        _place = FUNCTION_BODY;
    }

    void enter_function_args() {
        _totalDeclaredBytes = 0;
        _place = FUNCTION_ARGS;
    }

    void reset_place()              {
        _totalDeclaredBytes = 0;
        _place = DEFAULT;
    }

    // bool is_initializing_var()   { return  _isInitializingVar; }
    bool in_global_initialization() { return _isInitializingVar && in_global(); }
    void start_initializing_var()   { _isInitializingVar = true; }
    void stop_initializing_var()    { _isInitializingVar = false; }

    bool is_indexating_tuple()      { return _isIndexatingTuple;  }
    void start_indexating_tuple()   { _isIndexatingTuple = true;  }
    void stop_indexating_tuple()    { _isIndexatingTuple = false; }


    // tools
    void print_complex_type(std::shared_ptr<cdk::basic_type> t, bool first = true) {
        std::cout << "[";
        if(t->name() == cdk::TYPE_STRUCT) {
            auto struct_type = cdk::structured_type_cast(t);
            std::cout << "<";
            for(size_t i = 0; i < struct_type->length()-1; i++) {
                print_complex_type(struct_type->component(i), false);
                std::cout << ", ";
            }
            print_complex_type(struct_type->component(struct_type->length()-1), false);
            std::cout << ">";
        } else {
            std::cout << cdk::to_string(t);
        }
        std::cout << ", " << t->size() << "]";

        // print new line when finished
        if(first) std::cout << std::endl;
    }

    std::shared_ptr<cdk::basic_type> duplicate_type(std::shared_ptr<cdk::basic_type> t) {
        if(t->name() == cdk::TYPE_STRUCT) {
            std::vector<std::shared_ptr<cdk::basic_type>> vec;
            auto struct_type = cdk::structured_type_cast(t);
            for(auto element : struct_type->components()) {
                vec.push_back(duplicate_type(element));
            }
            return cdk::make_structured_type(vec);
        } else if(t->name() == cdk::TYPE_POINTER) {
            auto ptr = cdk::reference_type_cast(t);
            return cdk::make_reference_type(4, duplicate_type(ptr->referenced()));
        } else {
            return cdk::make_primitive_type(t->size(), t->name());
        }

    }

public:
    // do not edit these lines
#define __IN_VISITOR_HEADER__
#define __PURE_VIRTUAL_DECLARATIONS_ONLY__
#include "ast/visitor_decls.h"       // automatically generated
#undef __PURE_VIRTUAL_DECLARATIONS_ONLY__
#undef __IN_VISITOR_HEADER__
    // do not edit these lines: end

};

#endif
