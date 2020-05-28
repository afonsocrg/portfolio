#ifndef __OG_AST_VARIABLE_DECLARATION_H__
#define __OG_AST_VARIABLE_DECLARATION_H__

#include <vector>
#include <cdk/ast/typed_node.h>

namespace og {

  class variable_declaration_node: public cdk::typed_node {
    int _qualifier;
    std::vector<std::string> _names;
    og::tuple_node *_initializer;

  public:
    variable_declaration_node(int lineno, int qualifier, std::shared_ptr<cdk::basic_type> type, std::string name, og::tuple_node* initializer = nullptr) :
      cdk::typed_node(lineno), _qualifier(qualifier), _initializer(initializer) {
        cdk::typed_node::type(type);
        _names.push_back(name);
      }

    variable_declaration_node(int lineno, int qualifier,  std::shared_ptr<cdk::basic_type> type, std::vector<std::string> names, og::tuple_node* initializer) :
      cdk::typed_node(lineno), _qualifier(qualifier), _names(names), _initializer(initializer) {
        cdk::typed_node::type(type);
      }



    variable_declaration_node(int lineno, std::string name, variable_declaration_node* node = nullptr) :
        cdk::typed_node(lineno){
            if(node != nullptr) {
              cdk::typed_node::type(node->type());
              _qualifier = node->qualifier();
              _names = node->names();
            }
            _names.push_back(name);
    }

     variable_declaration_node(int lineno, variable_declaration_node* node, og::tuple_node* initializer) :
        cdk::typed_node(lineno), _initializer(initializer) {
          cdk::typed_node::type(node->type());
          _qualifier = node->qualifier();
          _names = node->names();
        }

    // // constructor for auto variables
    // variable_declaration_node(int lineno, int qualifier, std::string name, cdk::expression_node *initializer) :
    //     cdk::typed_node(lineno), _qualifier(qualifier), _name(name), _initializer(initializer) {
    //         // default unspec
    // }

  public:

    int qualifier() {
      return _qualifier;
    }

    std::string name(size_t i) {
      return _names[i];
    }

    const std::vector<std::string> names() const {
      return _names;
    }

    size_t size() {
      return _names.size();
    }

    og::tuple_node *initializer() {
      return _initializer;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_variable_declaration_node(this, level);
    }

  };

} // og

#endif
