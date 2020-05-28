#ifndef __OG_TARGETS_TYPE_CHECKER_H__
#define __OG_TARGETS_TYPE_CHECKER_H__

#include "targets/basic_ast_visitor.h"

namespace og {

  /**
   * Print nodes as XML elements to the output stream.
   */
  class type_checker: public basic_ast_visitor {
    cdk::symbol_table<og::symbol> &_symtab;

    basic_ast_visitor *_parent;

  public:
    type_checker(std::shared_ptr<cdk::compiler> compiler, cdk::symbol_table<og::symbol> &symtab, basic_ast_visitor *parent) :
        basic_ast_visitor(compiler), _symtab(symtab), _parent(parent) {
    }

  public:
    ~type_checker() {
      os().flush();
    }

  protected:
    void do_id_expression(cdk::binary_operation_node *const node, int lvl);
    void do_i_expression(cdk::binary_operation_node *const node, int lvl);
    void do_comparison_expression(cdk::binary_operation_node *const node, int lvl);
    void do_equality_expression(cdk::binary_operation_node *const node, int lvl);
    void do_check_function_args(
        std::vector<std::shared_ptr<cdk::basic_type>> declared_types,
        std::vector<std::shared_ptr<cdk::basic_type>> node_types
    );
    bool do_check_return_types(
            std::shared_ptr<cdk::basic_type> symbol_type,
            std::shared_ptr<cdk::basic_type> node_type
    );
    void do_check_function_call(
        std::vector<std::shared_ptr<cdk::basic_type>> declared_types,
        std::vector<std::shared_ptr<cdk::basic_type>> call_types
    );
    bool do_check_assignment_types(
            std::shared_ptr<cdk::basic_type> lv_type,
            std::shared_ptr<cdk::basic_type> rv_type
    );
    void assert_not_global_initialization();
    template<typename T>
    void process_literal(cdk::literal_node<T> *const node, int lvl) {
    }

  public:
    // do not edit these lines
#define __IN_VISITOR_HEADER__
#include "ast/visitor_decls.h"       // automatically generated
#undef __IN_VISITOR_HEADER__
    // do not edit these lines: end

  };

} // og

//---------------------------------------------------------------------------
//     HELPER MACRO FOR TYPE CHECKING
//---------------------------------------------------------------------------

#define CHECK_TYPES(compiler, symtab, node) { \
  try { \
    og::type_checker checker(compiler, symtab, this); \
    (node)->accept(&checker, 0); \
  } \
  catch (const std::string &problem) { \
    std::cerr << (node)->lineno() << ": " << problem << std::endl; \
    return; \
  } \
}

#define ASSERT_SAFE_EXPRESSIONS CHECK_TYPES(_compiler, _symtab, node)

#endif
