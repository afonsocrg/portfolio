#ifndef __PF2ASM_AST_ALLOC_H__
#define __PF2ASM_AST_ALLOC_H__

#include <cdk/ast/basic_node.h>

namespace pf2asm {

  class ALLOC: public cdk::basic_node {
  public:
    ALLOC(int lineno) :
        cdk::basic_node(lineno) {
    }
    void accept(basic_ast_visitor *sp, int level) {
      sp->do_ALLOC(this, level);
    }
  };

} // pf2asm

#endif
