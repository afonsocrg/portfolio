#ifndef __OG_AST_NULLPTR_NODE_H__
#define __OG_AST_NULLPTR_NODE_H__

#include <cdk/ast/literal_node.h>

namespace og {

  /**
   * Class for describing syntactic tree leaves for holding null pointers.
   */
  class nullptr_node: public virtual cdk::literal_node<void*> {
  public:
    nullptr_node(int lineno) :
        cdk::literal_node<void*>(lineno, nullptr) {
    }

    /**
     * @param av basic AST visitor
     * @param level syntactic tree level
     */
    void accept(basic_ast_visitor *av, int level) {
      av->do_nullptr_node(this, level);
    }

  };

} // og

#endif
