#ifndef _OG_AST_FUNCTION_CALL_NODE_H__
#define __OG_AST_FUNCTION_CALL_NODE_H__

#include <cdk/ast/expression_node.h>
#include "tuple_node.h"
#include <string>

namespace og {
class function_call_node : public cdk::expression_node {
    std::string _name;
    cdk::sequence_node* _args;

public:
    inline function_call_node(int lineno, std::string name, cdk::sequence_node* args = nullptr) :
        cdk::expression_node(lineno), _name(name), _args(args) {
            if(!args) {
                // create sequence node with empty tuple
                _args = new cdk::sequence_node(lineno, new tuple_node(lineno));
                
            }
    }
     
    inline std::string name() { return _name; }

    inline cdk::sequence_node* args() { return _args; }

    void accept(basic_ast_visitor *sp, int level) {
        sp->do_function_call_node(this, level);
    }

    std::vector<std::shared_ptr<cdk::basic_type>> arg_types() {
        std::vector<std::shared_ptr<cdk::basic_type>> res;

        if(_args->size() == 0) return res;

        tuple_node* args = (tuple_node*) _args->node(0);
        return args->arg_types();
    }

};
}  // namespace og

#endif
