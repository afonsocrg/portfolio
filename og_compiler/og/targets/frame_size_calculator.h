#ifndef __OG_TARGETS_FRAME_SIZE_CALCULATOR_H__
#define __OG_TARGETS_FRAME_SIZE_CALCULATOR_H__

#include "targets/basic_ast_visitor.h"
#include <cdk/ast/basic_node.h>

namespace og {

  /**
   * Calculates total declaration size
   */
  class frame_size_calculator: public basic_ast_visitor {
    int _result = 0;
    cdk::symbol_table<og::symbol> &_symtab;

  public:
    frame_size_calculator(std::shared_ptr<cdk::compiler> compiler, cdk::symbol_table<og::symbol> &symtab) :
        basic_ast_visitor(compiler), _symtab(symtab) {
            enter_function_body();
    }

  public:
    ~frame_size_calculator() {
        reset_place();
    }

    int result() { return _result; }

  public:
    // do not edit these lines
#define __IN_VISITOR_HEADER__
#include "ast/visitor_decls.h"       // automatically generated
#undef __IN_VISITOR_HEADER__
    // do not edit these lines: end

  };

} // og

#endif
