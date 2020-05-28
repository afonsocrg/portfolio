#ifndef _OG_AST_FUNCTION_DECLARATION_NODE_H__
#define __OG_AST_FUNCTION_DECLARATION_NODE_H__

#include <cdk/ast/typed_node.h>
#include <string>

namespace og {
class function_declaration_node : public cdk::typed_node {
    int _qualifier;
    std::string _name;
    cdk::sequence_node* _args;

public:

    // procedure: pass void basic_type
    inline function_declaration_node(int lineno, int qualifier, std::shared_ptr<cdk::basic_type> retType, std::string name, cdk::sequence_node* args) :
        cdk::typed_node(lineno), _qualifier(qualifier), _name(name), _args(args) {
            cdk::typed_node::type(retType);

    } 

    inline int qualifier() { return _qualifier; }
    
    inline std::string name() { return _name; }

    inline cdk::sequence_node* args() { return _args; }

    void accept(basic_ast_visitor *sp, int level) {
        sp->do_function_declaration_node(this, level);
    }

    std::vector<std::shared_ptr<cdk::basic_type>> arg_types() {
        std::vector<std::shared_ptr<cdk::basic_type>> res;

        for(auto arg : _args->nodes()) {
            cdk::expression_node* expr = (cdk::expression_node*)arg;
            res.push_back(expr->type());
        }
        return res;
    }
};
}  // namespace og

#endif
