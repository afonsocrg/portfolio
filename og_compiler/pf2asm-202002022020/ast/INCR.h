#ifndef __PF2ASM_AST_INCR_H__
#define __PF2ASM_AST_INCR_H__

#include <cdk/ast/basic_node.h>

namespace pf2asm {

  class INCR: public cdk::basic_node {
    int _value;

  public:
    INCR(int lineno, int value) :
        cdk::basic_node(lineno), _value(value) {
    }
    int value() const {
      return _value;
    }
    void accept(basic_ast_visitor *sp, int level) {
      sp->do_INCR(this, level);
    }
  };

} // pf2asm

#endif
