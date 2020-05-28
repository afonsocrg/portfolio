#ifndef _OG_AST_FUNCTION_DEFINITION_NODE_H__
#define __OG_AST_FUNCTION_DEFINITION_NODE_H__

#include <cdk/ast/typed_node.h>
#include <string>

namespace og {
class function_definition_node : public cdk::typed_node {
    int _qualifier;
    std::string _name;
    cdk::sequence_node* _args;
    og::block_node* _body;

public:
    inline function_definition_node(int lineno, int qualifier, std::shared_ptr<cdk::basic_type> retType, std::string name, cdk::sequence_node* args, og::block_node* body) :
        cdk::typed_node(lineno), _qualifier(qualifier), _name(name), _args(args), _body(body) { 
            cdk::typed_node::type(retType);

    } 
    
    inline int qualifier() { return _qualifier; }

    inline std::string name() { return _name; }

    inline cdk::sequence_node* args() { return _args; }

    inline og::block_node* body() { return _body; }

    void accept(basic_ast_visitor *sp, int level) {
        sp->do_function_definition_node(this, level);
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
