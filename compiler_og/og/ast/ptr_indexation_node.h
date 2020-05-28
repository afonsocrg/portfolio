#ifndef __OG_AST_PTR_INDEXATION_NODE_H__
#define __OG_AST_PTR_INDEXATION_NODE_H__

#include <cdk/ast/lvalue_node.h>
#include <cdk/ast/expression_node.h>

namespace og {

  class ptr_indexation_node: public cdk::lvalue_node {
    cdk::expression_node *_base;
    cdk::expression_node *_offset;

  public:
    ptr_indexation_node(int lineno, cdk::expression_node *base, cdk::expression_node *offset) :
        cdk::lvalue_node(lineno), _base(base), _offset(offset) {
    }

  public:
    cdk::expression_node *base() {
      return _base;
    }

    cdk::expression_node *offset() {
        return _offset;
    }

  public:
    void accept(basic_ast_visitor *sp, int level) {
      sp->do_ptr_indexation_node(this, level);
    }

  };

} // og

#endif
