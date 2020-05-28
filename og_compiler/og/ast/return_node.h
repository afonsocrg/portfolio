#ifndef __OG_AST_RETURN_NODE_H__
#define __OG_AST_RETURN_NODE_H__

#include <cdk/ast/basic_node.h>
#include "tuple_node.h"

namespace og {

  /**
   * Class for describing return nodes.
   */
  class return_node: public cdk::basic_node {
    cdk::expression_node *_returnValue;

  public:
    inline return_node(int lineno, og::tuple_node *returnValue) :
        cdk::basic_node(lineno) {
            if(returnValue->size() == 1) {
                _returnValue = returnValue->node(0);
            } else {
                _returnValue = (cdk::expression_node*)returnValue;
            }
    }

  public:
    inline cdk::expression_node *returnValue() {
      return _returnValue;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_return_node(this, level);
    }

  };

} // og

#endif
