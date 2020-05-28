#ifndef __PF2ASM_AST_ARITHMETIC_UMOD_H__
#define __PF2ASM_AST_ARITHMETIC_UMOD_H__

#include <cdk/ast/basic_node.h>

namespace pf2asm {

  class UMOD: public cdk::basic_node {
  public:
    UMOD(int lineno) :
        cdk::basic_node(lineno) {
    }
    void accept(basic_ast_visitor *sp, int level) {
      sp->do_UMOD(this, level);
    }
  };

} // pf2asm

#endif
