#ifndef __PF2ASM_AST_JUMPS_JUGE_H__
#define __PF2ASM_AST_JUMPS_JUGE_H__

#include <cdk/ast/basic_node.h>

namespace pf2asm {

  class JUGE: public cdk::basic_node {
    std::string _label;

  public:
    JUGE(int lineno, std::string &label) :
        cdk::basic_node(lineno), _label(label) {
    }
    const std::string &label() const {
      return _label;
    }
    void accept(basic_ast_visitor *sp, int level) {
      sp->do_JUGE(this, level);
    }
  };

} // pf2asm

#endif
