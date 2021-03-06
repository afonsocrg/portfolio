#ifndef __PF2ASM_AST_RELATIONAL_GT_H__
#define __PF2ASM_AST_RELATIONAL_GT_H__

#include <cdk/ast/basic_node.h>

namespace pf2asm {

  class GT: public cdk::basic_node {
  public:
    GT(int lineno) :
        cdk::basic_node(lineno) {
    }
    void accept(basic_ast_visitor *sp, int level) {
      sp->do_GT(this, level);
    }
  };

} // pf2asm

#endif
