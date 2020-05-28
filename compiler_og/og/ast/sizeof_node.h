#ifndef __OG_AST_SIZEOF_NODE_H__
#define __OG_AST_SIZEOF_NODE_H__

#include <cdk/ast/unary_operation_node.h>

namespace og {

  /**
   * Class for describing the sizeof operator
   */
  class sizeof_node : public cdk::unary_operation_node {
  public:
    sizeof_node(int lineno, expression_node *arg) :
        cdk::unary_operation_node(lineno, arg) {
    }

    void accept(basic_ast_visitor *av, int level) {
      av->do_sizeof_node(this, level);
    }

  };

} // og

#endif
