#ifndef __OG_AST_ALLOC_NODE_H__
#define __OG_AST_ALLOC_NODE_H__

#include <cdk/ast/unary_operation_node.h>

namespace og {

  /**
   * Class for describing the allocation operator
   */
  class alloc_node : public cdk::unary_operation_node {
  public:
    alloc_node(int lineno, expression_node *arg) :
        cdk::unary_operation_node(lineno, arg) {
    }

    void accept(basic_ast_visitor *av, int level) {
      av->do_alloc_node(this, level);
    }

  };

} // og

#endif
