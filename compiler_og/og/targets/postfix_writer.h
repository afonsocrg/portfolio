#ifndef __OG_TARGETS_POSTFIX_WRITER_H__
#define __OG_TARGETS_POSTFIX_WRITER_H__

#include "targets/basic_ast_visitor.h"

#include <sstream>
#include <set>
#include <cdk/emitters/basic_postfix_emitter.h>

namespace og {

  //!
  //! Traverse syntax tree and generate the corresponding assembly code.
  //!
  class postfix_writer: public basic_ast_visitor {
    cdk::symbol_table<og::symbol> &_symtab;
    cdk::basic_postfix_emitter &_pf;
    int _lbl;
    std::set<std::string> external_symbols;

  public:
    postfix_writer(std::shared_ptr<cdk::compiler> compiler, cdk::symbol_table<og::symbol> &symtab,
                   cdk::basic_postfix_emitter &pf) :
        basic_ast_visitor(compiler), _symtab(symtab), _pf(pf), _lbl(0) {
    }

  public:
    ~postfix_writer() {
      os().flush();
    }

  private:
    /** Method used to generate sequential labels. */
    inline std::string mklbl(int lbl) {
      std::ostringstream oss;
      if (lbl < 0)
        oss << ".L" << -lbl;
      else
        oss << "_L" << lbl;
      return oss.str();
    }

    int calculateFrameSize(cdk::basic_node* node);
    void do_initialize_variable(std::shared_ptr<cdk::basic_type> type, cdk::expression_node* initializer, int base_offset);
    void return_tuple(std::shared_ptr<cdk::basic_type> symbol_type, tuple_node* tuple, int prev_returned_bytes);
    void do_initialize_tuple_variable(
            int tpl_base_offset,
            std::shared_ptr<cdk::basic_type> tuple_type,
            int assigned_bytes
    );
    void do_load_tuple(std::shared_ptr<cdk::basic_type> tuple_type, int offset);
    void do_tuple_print(std::shared_ptr<cdk::basic_type> tuple_type);

    // external symbol control
    void require_symbol(std::string name) {
        external_symbols.insert(name);
    }

    void unrequire_symbol(std::string name) {
        external_symbols.erase(name);
    }

    void do_store_tuple(
            cdk::lvalue_node* const lval,
            std::shared_ptr<cdk::basic_type> tuple_type,
            int assigned_bytes
    );


  public:
  // do not edit these lines
#define __IN_VISITOR_HEADER__
#include "ast/visitor_decls.h"       // automatically generated
#undef __IN_VISITOR_HEADER__
  // do not edit these lines: end

    void include_symbols() {
        for(std::string sym : external_symbols) {
            _pf.EXTERN(sym);
        }
    }

  };

} // og

#endif
