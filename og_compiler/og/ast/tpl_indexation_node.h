#ifndef __OG_AST_TPL_INDEXATION_NODE_H__
#define __OG_AST_TPL_INDEXATION_NODE_H__

#include <cdk/ast/lvalue_node.h>
#include <cdk/ast/integer_node.h>
#include <cdk/ast/expression_node.h>

namespace og {

  class tpl_indexation_node: public cdk::lvalue_node {
    cdk::expression_node *_base;
    cdk::integer_node *_offset;

  public:
    tpl_indexation_node(int lineno, cdk::expression_node *base, cdk::integer_node *offset) :
        cdk::lvalue_node(lineno), _base(base), _offset(offset) {
    }

  public:
    cdk::expression_node *base() {
      return _base;
    }

    cdk::integer_node *offset() {
        return _offset;
    }

  public:
    void accept(basic_ast_visitor *sp, int level) {
      sp->do_tpl_indexation_node(this, level);
    }

  };

} // og

#endif
