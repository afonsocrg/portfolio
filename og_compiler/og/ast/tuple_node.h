#ifndef __OG_AST_TUPLE_NODE_H__
#define __OG_AST_TUPLE_NODE_H__

#include <vector>
#include <cdk/ast/expression_node.h>

namespace og {

    class tuple_node: public cdk::expression_node {

        cdk::sequence_node* _nodes;
    
    public:
        tuple_node(int lineno) :
            cdk::expression_node(lineno) {
                _nodes = new cdk::sequence_node(lineno);
        }

        tuple_node(int lineno, cdk::expression_node *expr, tuple_node *tuple = nullptr) :
            cdk::expression_node(lineno) {
            if(!tuple)
                _nodes = new cdk::sequence_node(lineno, expr);
            else
                _nodes = new cdk::sequence_node(lineno, expr, tuple->nodes());
        }

        ~tuple_node() {
            if(_nodes) delete _nodes;
        }

        cdk::expression_node* node(size_t i) {
            // every node is definetly an expression node
            return (cdk::expression_node*) _nodes->node(i);
        }

        cdk::sequence_node* nodes() {
            return _nodes;
        }

        size_t size() {
            return _nodes->size();
        }

        size_t byteSize() {
            size_t res = 0;
            for(cdk::basic_node* node : _nodes->nodes()) {
                res += ((cdk::expression_node*) node)->type()->size();
            }
            return res;
        }

        void accept(basic_ast_visitor *sp, int level) {
            sp->do_tuple_node(this, level);
        }


        // return a vector of stored types
        std::vector<std::shared_ptr<cdk::basic_type>> arg_types() {
            std::vector<std::shared_ptr<cdk::basic_type>> res;

            if(_nodes->size() == 0) return res;

            for(auto arg : _nodes->nodes()) {
                cdk::expression_node* expr = (cdk::expression_node*)arg;
                res.push_back(expr->type());
            }
            return res;
        }

    };
} // og


#endif
