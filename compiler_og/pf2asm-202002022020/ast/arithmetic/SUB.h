#ifndef __PF2ASM_AST_ARITHMETIC_SUB_H__
#define __PF2ASM_AST_ARITHMETIC_SUB_H__

#include <cdk/ast/basic_node.h>

namespace pf2asm {

  class SUB: public cdk::basic_node {
  public:
    SUB(int lineno) :
        cdk::basic_node(lineno) {
    }
    void accept(basic_ast_visitor *sp, int level) {
      sp->do_SUB(this, level);
    }
  };

} // pf2asm

#endif
