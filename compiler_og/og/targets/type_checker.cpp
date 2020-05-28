#include <string>
#include <vector>
#include "targets/type_checker.h"
#include "ast/all.h"  // automatically generated
#include <cdk/types/primitive_type.h>
#include "og_parser.tab.h"

#define ASSERT_UNSPEC { if (node->type() != nullptr && !node->is_typed(cdk::TYPE_UNSPEC)) return; }

void og::type_checker::assert_not_global_initialization() {
    // throws error if initializing global var with expression
    if(_parent->in_global_initialization())
        throw std::string("Cannot initialize global variable with expression");
}

//---------------------------------------------------------------------------

void og::type_checker::do_sequence_node(cdk::sequence_node *const node, int lvl) {
    for (size_t i = 0; i < node->size(); i++) {
        node->node(i)->accept(this, lvl);
    }
}

void og::type_checker::do_block_node(og::block_node *const node, int lvl) {
    node->declarations()->accept(this,lvl+2);
    node->instructions()->accept(this,lvl+2);
}

void og::type_checker::do_tuple_node(og::tuple_node *const node, int lvl) {
    ASSERT_UNSPEC;
    
    if(node->size() == 0) { // empty tuple is void
        node->type(cdk::make_primitive_type(0, cdk::TYPE_VOID));
        return; 
    }

    node->nodes()->accept(this,lvl+2);
    if(node->size() == 1) {
        // 1 element tuple is same type of its element
        node->type(node->node(0)->type());
    } else {
        std::vector<std::shared_ptr<cdk::basic_type>> tuple_types;
        for(auto node : node->nodes()->nodes()) {
            tuple_types.push_back(((cdk::expression_node*)node)->type());
        }
        node->type(cdk::make_structured_type(tuple_types));
    }
}

//---------------------------------------------------------------------------

void og::type_checker::do_nil_node(cdk::nil_node *const node, int lvl) {
    // EMPTY
}
void og::type_checker::do_data_node(cdk::data_node *const node, int lvl) {
    // EMPTY
}

//---------------------------------------------------------------------------

void og::type_checker::do_integer_node(cdk::integer_node *const node, int lvl) {
    ASSERT_UNSPEC;
    node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));
}

void og::type_checker::do_double_node(cdk::double_node *const node, int lvl) {
    ASSERT_UNSPEC;
    node->type(cdk::make_primitive_type(8, cdk::TYPE_DOUBLE));
}

void og::type_checker::do_string_node(cdk::string_node *const node, int lvl) {
    ASSERT_UNSPEC;
    node->type(cdk::make_primitive_type(4, cdk::TYPE_STRING));
}

void og::type_checker::do_nullptr_node(og::nullptr_node *const node, int lvl) {
    ASSERT_UNSPEC;
    node->type(cdk::make_reference_type(4, cdk::make_primitive_type(0, cdk::TYPE_UNSPEC)));
}


//---------------------------------------------------------------------------

void og::type_checker::do_not_node(cdk::not_node *const node, int lvl) {
    ASSERT_UNSPEC;
    assert_not_global_initialization();

    node->argument()->accept(this, lvl + 2);

    if (!node->argument()->is_typed(cdk::TYPE_INT))
        throw std::string("wrong type in argument of unary expression");
    
    node->type(node->argument()->type());
}

void og::type_checker::do_neg_node(cdk::neg_node *const node, int lvl) {
    ASSERT_UNSPEC;
    assert_not_global_initialization();

    node->argument()->accept(this, lvl + 2);

    if (!(node->argument()->is_typed(cdk::TYPE_INT) 
        || node->argument()->is_typed(cdk::TYPE_DOUBLE))) 
        throw std::string("wrong type in argument of unary expression");
    
    node->type(node->argument()->type());
}

void og::type_checker::do_identity_node(og::identity_node *const node, int lvl) {
    ASSERT_UNSPEC;
    assert_not_global_initialization();

    node->argument()->accept(this, lvl + 2);

    if (!(node->argument()->is_typed(cdk::TYPE_INT) 
        || node->argument()->is_typed(cdk::TYPE_DOUBLE))) 
        throw std::string("wrong type in argument of unary expression");
    
    node->type(node->argument()->type());
}

void og::type_checker::do_sizeof_node(og::sizeof_node *const node, int lvl) {
    ASSERT_UNSPEC;
    assert_not_global_initialization();

    node->argument()->accept(this, lvl + 2);

    if(node->argument()->is_typed(cdk::TYPE_UNSPEC)) // read
        node->argument()->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

    node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));
}

void og::type_checker::do_alloc_node(og::alloc_node *const node, int lvl) {
    ASSERT_UNSPEC;
    assert_not_global_initialization();

    node->argument()->accept(this, lvl + 2);

    if(node->argument()->is_typed(cdk::TYPE_UNSPEC)) // read
        node->argument()->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

    if (!node->argument()->is_typed(cdk::TYPE_INT))
        throw std::string("wrong type in argument of unary expression");

    node->type(cdk::make_reference_type(4, cdk::make_primitive_type(0, cdk::TYPE_UNSPEC)));
}

//---------------------------------------------------------------------------

void og::type_checker::do_id_expression(cdk::binary_operation_node *const node, int lvl) {
    // used by mul and div
    ASSERT_UNSPEC;
    assert_not_global_initialization();

    node->left()->accept(this, lvl + 2);
    node->right()->accept(this, lvl + 2);

    // read can be double or integer. making it int
    if(node->left()->is_typed(cdk::TYPE_UNSPEC)) // read
        node->left()->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

    if(node->right()->is_typed(cdk::TYPE_UNSPEC)) // read
        node->right()->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

    // type possibilities
    if(node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_INT)) {
        node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

    } else if(node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        node->type(cdk::make_primitive_type(8, cdk::TYPE_DOUBLE));

    } else if((node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE))
            ||(node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT))) {
        node->type(cdk::make_primitive_type(8, cdk::TYPE_DOUBLE));

    } else
        throw std::string("wrong types in binary expression");
}

void og::type_checker::do_i_expression(cdk::binary_operation_node *const node, int lvl) {
    ASSERT_UNSPEC;
    assert_not_global_initialization();

    node->left()->accept(this, lvl + 2);
    node->right()->accept(this, lvl + 2);

    // read must be integer
    if(node->left()->is_typed(cdk::TYPE_UNSPEC)) // read
        node->left()->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

    if(node->right()->is_typed(cdk::TYPE_UNSPEC)) // read
        node->right()->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

    if(node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_INT)) {
        node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));
    } else 
        throw std::string("wrong types in binary expression");
}

void og::type_checker::do_comparison_expression(cdk::binary_operation_node *const node, int lvl) {
    ASSERT_UNSPEC;
    assert_not_global_initialization();

    node->left()->accept(this, lvl + 2);
    node->right()->accept(this, lvl + 2);

    // read can be double or integer. making it int
    if(node->left()->is_typed(cdk::TYPE_UNSPEC)) // read
        node->left()->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

    if(node->right()->is_typed(cdk::TYPE_UNSPEC)) // read
        node->right()->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

    if(node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_INT)) {
        node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));
    } else if (node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));
    } else if((node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE))
            ||(node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT))) {
        node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));         
    } else
        throw std::string("wrong types in binary expression");
}

void og::type_checker::do_equality_expression(cdk::binary_operation_node *const node, int lvl) {
    ASSERT_UNSPEC;
    assert_not_global_initialization();

    node->left()->accept(this, lvl + 2);
    node->right()->accept(this, lvl + 2);

    // read can be double or integer. making it int
    if(node->left()->is_typed(cdk::TYPE_UNSPEC)) // read
        node->left()->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

    if(node->right()->is_typed(cdk::TYPE_UNSPEC)) // read
        node->right()->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

    if(node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_INT)) {
        node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));
    } else if (node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));
    } else if (node->left()->is_typed(cdk::TYPE_POINTER) && node->right()->is_typed(cdk::TYPE_POINTER)) {
        auto lr = cdk::reference_type_cast(node->left()->type())->referenced();
        auto rr = cdk::reference_type_cast(node->left()->type())->referenced();
        if(!(lr == rr || lr->name() == cdk::TYPE_VOID || rr->name() == cdk::TYPE_VOID)) {
            throw std::string("Invalid pointer types in comparison");
        }
        node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));
    } else if((node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE))
            ||(node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT))) {
        node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));
    } else if((node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_POINTER))
            ||(node->left()->is_typed(cdk::TYPE_POINTER) && node->right()->is_typed(cdk::TYPE_INT))) {
        node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));
    } else
        throw std::string("wrong types in binary expression");
}

void og::type_checker::do_add_node(cdk::add_node *const node, int lvl) {
    ASSERT_UNSPEC;
    assert_not_global_initialization();

    node->left()->accept(this, lvl + 2);
    node->right()->accept(this, lvl + 2);

    // read can be double or integer. making it int
    if(node->left()->is_typed(cdk::TYPE_UNSPEC)) // read
        node->left()->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

    if(node->right()->is_typed(cdk::TYPE_UNSPEC)) // read
        node->right()->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

    // type possibilities
    if(node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_INT)) {
        node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));
    } else if(node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        node->type(cdk::make_primitive_type(8, cdk::TYPE_DOUBLE));
    } else if((node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE))
            ||(node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT))) {
        node->type(cdk::make_primitive_type(8, cdk::TYPE_DOUBLE));

    } else if(node->left()->is_typed(cdk::TYPE_POINTER) && node->right()->is_typed(cdk::TYPE_INT)) {
        node->type(node->left()->type());
    } else if(node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_POINTER)) {
        node->type(node->right()->type());

    } else
        throw std::string("wrong types in binary expression");
}

void og::type_checker::do_sub_node(cdk::sub_node *const node, int lvl) {
    ASSERT_UNSPEC;
    assert_not_global_initialization();

    node->left()->accept(this, lvl + 2);
    node->right()->accept(this, lvl + 2);

    // read can be double or integer. making it int
    if(node->left()->is_typed(cdk::TYPE_UNSPEC)) // read
        node->left()->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

    if(node->right()->is_typed(cdk::TYPE_UNSPEC)) // read
        node->right()->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

    // type possibilities
    if(node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_INT)) {
        node->type(cdk::make_primitive_type(4, cdk::TYPE_INT));
    } else if(node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        node->type(cdk::make_primitive_type(8, cdk::TYPE_DOUBLE));
    } else if((node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE))
            ||(node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT))) {
        node->type(cdk::make_primitive_type(8, cdk::TYPE_DOUBLE));
    } else if((node->left()->is_typed(cdk::TYPE_POINTER) && node->right()->is_typed(cdk::TYPE_INT))) {
        node->type(cdk::make_primitive_type(4, cdk::TYPE_POINTER));
    } else if(node->left()->is_typed(cdk::TYPE_POINTER) && node->right()->is_typed(cdk::TYPE_POINTER)) {
        node->type(cdk::make_primitive_type(4, cdk::TYPE_INT)); 
    } else
        throw std::string("wrong types in binary expression");
}

void og::type_checker::do_mul_node(cdk::mul_node *const node, int lvl) {
    do_id_expression(node, lvl);
}

void og::type_checker::do_div_node(cdk::div_node *const node, int lvl) {
    do_id_expression(node, lvl);
}

void og::type_checker::do_mod_node(cdk::mod_node *const node, int lvl) {
    do_i_expression(node, lvl + 2);
}

void og::type_checker::do_lt_node(cdk::lt_node *const node, int lvl) {
    do_comparison_expression(node, lvl);
}

void og::type_checker::do_le_node(cdk::le_node *const node, int lvl) {
    do_comparison_expression(node, lvl);
}

void og::type_checker::do_ge_node(cdk::ge_node *const node, int lvl) {
    do_comparison_expression(node, lvl);
}

void og::type_checker::do_gt_node(cdk::gt_node *const node, int lvl) {
    do_comparison_expression(node, lvl);
}

void og::type_checker::do_ne_node(cdk::ne_node *const node, int lvl) {
    do_equality_expression(node, lvl);
}
void og::type_checker::do_eq_node(cdk::eq_node *const node, int lvl) {
    do_equality_expression(node, lvl);
}

void og::type_checker::do_and_node(cdk::and_node *const node, int lvl) {
    do_i_expression(node, lvl + 2);
}

void og::type_checker::do_or_node(cdk::or_node *const node, int lvl) {
    do_i_expression(node, lvl + 2);
}

//---------------------------------------------------------------------------

void og::type_checker::do_variable_node(cdk::variable_node *const node, int lvl) {
  ASSERT_UNSPEC;
  const std::string &id = node->name();
  std::shared_ptr<og::symbol> symbol = _symtab.find(id);

  if (symbol != nullptr) {
      if(symbol->is_function()) {
          throw std::string("cannot use function as variable");
      }
      node->type(symbol->type());
  } else {
      throw id;
  }
}

void og::type_checker::do_variable_declaration_node(og::variable_declaration_node *const node, int lvl) {
    // WARNING: variable_declaration_nodes are associated with a non-unspec type.
    // assert unspec would not visit this node and thus it would not declare the variables
    // ASSERT_UNSPEC; // removed for now: this node is visited once.
    
    _parent->start_initializing_var();
    if(node->initializer()) node->initializer()->accept(this, lvl+2);
    _parent->stop_initializing_var();

    if(node->qualifier() != tPRIVATE) {
        if(_parent->in_function_args())
            throw std::string("Cannot use modifiers in function arguments");
        if(_parent->in_function_body()) {
            throw std::string("Cannot use modifiers in function body");
        }
    }

    if(_parent->in_function_args() && node->initializer())
        throw std::string("Cannot initialize function arguments");

    if(/*node->is_typed(cdk::TYPE_UNSPEC) || */node->is_typed(cdk::TYPE_STRUCT)) { // AUTO DECLARATION
        if(!node->initializer())
            throw std::string("auto variable must be initialized");


        if(node->size() == node->initializer()->size()) {
            // int a = 1;
            // auto a = 1;
            // auto a, b, c = 1, 2, 3
            // auto a = f();
            // each variable gets its correspondent initializer type

            for(size_t i = 0; i < node->size(); i++) {
                // read can be double or integer. making it int
                if(node->initializer()->node(i)->is_typed(cdk::TYPE_UNSPEC)) // read
                    node->initializer()->node(i)->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

                auto symbol = og::make_symbol(node->qualifier(), node->initializer()->node(i)->type(), node->name(i));
                if(!_symtab.insert(node->name(i), symbol)) {
                    throw std::string("id " + node->name(i) + "redeclared!");
                }
            }

        } else {
            // auto a = 1, 2, 3;
            // auto a, b, c = f();
            // one of the sides must have size 1
            if(node->size() > 1 && node->initializer()->size() > 1) {
                throw std::string("Invalid declaration");
            }
            if(node->size() == 1) {
                // auto a = 1, 2, 3
                // declare variable with tuple type
                auto symbol = og::make_symbol(node->qualifier(), node->initializer()->type(), node->name(0));
                if(!_symtab.insert(node->name(0), symbol)) {
                    throw std::string("id " + node->name(0) + "redeclared!");
                }
            } else {
                // auto a, b, c = f()
                // check if initializer size has one type for each variable
                if(!node->initializer()->is_typed(cdk::TYPE_STRUCT))
                    throw std::string("Invalid types for initialization");

                auto struct_type = cdk::structured_type_cast(node->initializer()->type());
                if(!(struct_type->length() == node->size()))
                    throw std::string("Invalid types for initialization");

                for(size_t i = 0; i < node->size(); i++) {
                    auto var_type = struct_type->component(i);
                    auto symbol = og::make_symbol(node->qualifier(), var_type, node->name(i));
                    if(!_symtab.insert(node->name(i), symbol)) {
                        throw std::string("id " + node->name(i) + "redeclared!");
                    }
                }
            }
        }

        node->type(node->initializer()->type());
    } else { // NON auto DECLARATION
        if(!(node->size() == 1))
            throw std::string("Cannot declare multiple non-auto variables");

        if(node->initializer()) {
            // initialized
            if(node->initializer()->node(0)->is_typed(cdk::TYPE_UNSPEC)) { // read node
                if(node->is_typed(cdk::TYPE_DOUBLE)
                || node->is_typed(cdk::TYPE_INT)) { // initializer has just one element
                    node->initializer()->type(node->type());
                    node->initializer()->node(0)->type(node->type());
                } else {
                    throw std::string("Cannot initialize " + cdk::to_string(node->type()) +  " with a read");
                }
            }
            if(!(
               (do_check_assignment_types(node->type(), node->initializer()->type()))
            || (node->is_typed(cdk::TYPE_DOUBLE) && node->initializer()->is_typed(cdk::TYPE_INT))
            ))
                throw std::string("initializer type does not match declared type");

        }

        auto symbol = og::make_symbol(node->qualifier(), node->type(), node->name(0));
        if(!_symtab.insert(node->name(0), symbol))
            throw std::string("id " + node->name(0) + " redeclared!");
    }
}


void og::type_checker::do_rvalue_node(cdk::rvalue_node *const node, int lvl) {
    ASSERT_UNSPEC;
    assert_not_global_initialization();

    node->lvalue()->accept(this, lvl);
    node->type(node->lvalue()->type());
}

void og::type_checker::do_assignment_node(cdk::assignment_node *const node, int lvl) {
    ASSERT_UNSPEC;
    assert_not_global_initialization();


    node->lvalue()->accept(this, lvl);
    node->rvalue()->accept(this, lvl);

    // keeping these verifications verbose and pedantic on purpose.
    // We know this is awful and hereby apologize for your eyes

    if(node->rvalue()->is_typed(cdk::TYPE_UNSPEC)) {
        if(node->lvalue()->is_typed(cdk::TYPE_DOUBLE)  // read case
        || node->lvalue()->is_typed(cdk::TYPE_INT)) {

            node->rvalue()->type(node->lvalue()->type());
        } else {
            throw new std::string("Cannot assign " + cdk::to_string(node->type()) +  " with a read");
        }
    }

    if(!do_check_assignment_types(node->lvalue()->type(), node->rvalue()->type())) {
        throw std::string("invalid types for assignment");
    }
    
    node->type(node->lvalue()->type());
}

bool og::type_checker::do_check_assignment_types(
        std::shared_ptr<cdk::basic_type> lv_type,
        std::shared_ptr<cdk::basic_type> rv_type
){

    // pointer types require special checking
    if(lv_type->name() == cdk::TYPE_POINTER && rv_type->name() == cdk::TYPE_POINTER) {

        auto left_pointer = cdk::reference_type_cast(lv_type);
        auto left_referenced = left_pointer->referenced();

        auto right_pointer = cdk::reference_type_cast(rv_type);
        auto right_referenced = right_pointer->referenced();

        // go deep down the pointer hole
        while(left_referenced->name() == cdk::TYPE_POINTER
          && right_referenced->name() == cdk::TYPE_POINTER) {
            left_pointer = cdk::reference_type_cast(left_referenced);
            left_referenced = left_pointer->referenced();

            right_pointer = cdk::reference_type_cast(right_referenced);
            right_referenced = right_pointer->referenced();
        }

        // invalid auto vs auto
        if(left_referenced->name() == cdk::TYPE_VOID
        && right_referenced->name() == cdk::TYPE_VOID){
            // throw std::string("Cannot have two auto types in assignment");
            return true;

        // auto is compatible with every type
        } else if(left_referenced->name() == cdk::TYPE_VOID
               || right_referenced->name() == cdk::TYPE_VOID){
            // one of the referenced types is auto
            return true;
        } else if(right_referenced->name() == cdk::TYPE_UNSPEC) {
            // alloc case: make it left value type
            *right_pointer = *cdk::make_reference_type(4, left_referenced);
            return true;
        } else {
            return left_referenced == right_referenced;
        }

    } else if(lv_type->name() == cdk::TYPE_STRUCT && rv_type->name() == cdk::TYPE_STRUCT) {
        auto left_struct = cdk::structured_type_cast(lv_type);
        auto right_struct = cdk::structured_type_cast(rv_type);
        if(left_struct->length() != right_struct->length()) {
            return false;
        } 

        for(size_t i = 0; i < left_struct->length(); i++) {
            if(!do_check_assignment_types(left_struct->component(i), right_struct->component(i)))
                return false;
        }
        return true;
    }

    return lv_type == rv_type
    || (lv_type->name() == cdk::TYPE_DOUBLE && rv_type->name() == cdk::TYPE_INT)
    || (lv_type->name() == cdk::TYPE_INT && rv_type->name() == cdk::TYPE_POINTER
            && cdk::reference_type_cast(rv_type)->referenced()->name() == cdk::TYPE_VOID);
}

//---------------------------------------------------------------------------


void og::type_checker::do_evaluation_node(og::evaluation_node *const node, int lvl) {
    assert_not_global_initialization();

    node->argument()->accept(this, lvl + 2);
}

void og::type_checker::do_write_node(og::write_node *const node, int lvl) {
    node->arguments()->accept(this, lvl + 2);

    for(auto arg : node->arguments()->nodes()) {
        cdk::expression_node* expr = (cdk::expression_node*)arg;

        if(expr->is_typed(cdk::TYPE_UNSPEC)) // read
            expr->type(cdk::make_primitive_type(4, cdk::TYPE_INT));

        if(expr->is_typed(cdk::TYPE_POINTER))
            throw std::string("Cannot print pointers");
    }
}

//---------------------------------------------------------------------------

// in function calls the calling types can be casted
void og::type_checker::do_check_function_call(
        std::vector<std::shared_ptr<cdk::basic_type>> declared_types,
        std::vector<std::shared_ptr<cdk::basic_type>> call_types
) {

    if(declared_types.size() != call_types.size())
        throw std::string("argument sizes do not match");

    for(size_t i = 0; i < declared_types.size(); i++) {
        if(call_types.at(i)->name() == cdk::TYPE_UNSPEC) {
            // read as an argument -> make it declared type
            if(declared_types.at(i)->name() == cdk::TYPE_INT
            || declared_types.at(i)->name() == cdk::TYPE_DOUBLE) {
                *(call_types.at(i)) = *(declared_types.at(i));
            } else {
                throw std::string("Cannot use read as argument for type " + cdk::to_string(declared_types.at(i)));
            }
        }

        if(!(declared_types.at(i) == call_types.at(i)
          || (declared_types.at(i)->name() == cdk::TYPE_DOUBLE && call_types.at(i)->name() == cdk::TYPE_INT)
        ))
            throw std::string("given argument type (" 
                    + cdk::to_string(call_types.at(i)) 
                    + ") does not match declared type ("
                    + cdk::to_string(declared_types.at(i)) + ")");
    }
}

void og::type_checker::do_check_function_args(
        std::vector<std::shared_ptr<cdk::basic_type>> declared_types,
        std::vector<std::shared_ptr<cdk::basic_type>> node_types
) {

    if(declared_types.size() != node_types.size())
        throw std::string("argument sizes do not match");

    for(size_t i = 0; i < declared_types.size(); i++) {
        if(!(node_types.at(i) == declared_types.at(i)))
            throw std::string("given argument type (" 
                    + cdk::to_string(node_types.at(i)) 
                    + ") does not match declared type ("
                    + cdk::to_string(declared_types.at(i)) + ")");
    }
}

void og::type_checker::do_function_declaration_node(og::function_declaration_node *const node, int lvl) {
    // ASSERT_UNSPEC; // do not do this: function declaration's type is its return type
    if(!_parent->in_global())
        throw std::string("Cannot declare function inside function body");

    if(node->is_typed(cdk::TYPE_UNSPEC))
        throw std::string("auto functions cannot be declared without a body");


    _parent->enter_function_args();
    _symtab.push(); // verify variables in safe environment
    node->args()->accept(this, lvl + 2);
    _symtab.pop(); // finish argument safe zone
    _parent->reset_place();

    const std::string id = node->name();
    auto symbol = _symtab.find(id);

    if(!symbol) { // new declaration
        symbol = og::make_symbol(node->qualifier(), node->type(), id, true);
        symbol->arg_types(node->arg_types()); // save argument types

        if(!_symtab.insert(id, symbol))
            throw std::string("error inserting in symbol table"); // should not happen
    } else {
        // existing declaration
        // check qualifier
        if(!(node->qualifier() == symbol->qualifier()))
            throw std::string("Conflicting qualifiers for function declaration");

        // check return type
        if(!(node->type() == symbol->type()))
            throw std::string("Conflicting return types for function declaration");

        // check args 
        auto declared_types = symbol->arg_types();
        auto node_types = node->arg_types();

        do_check_function_args(declared_types, node_types);
    }
}

void og::type_checker::do_function_definition_node(og::function_definition_node *const node, int lvl) {
    // ASSERT_UNSPEC; // do not do this: function declaration's type is its return type

    // programmers must write a return in the funciton if they want to return a value.
    // if that does not happen, unpredictable behaviour may occur

    if(!_parent->in_global())
        throw std::string("Cannot define function inside function body");

    const std::string id = node->name();
    auto symbol = _symtab.find(id);

    bool new_function = (symbol==nullptr);
    if(!new_function) { // previously declared
        if(!symbol->is_function())
            throw std::string("Function name conflicts with already existing variable name");

        if(symbol->is_defined())
            throw std::string("Function with name '" + node->name() + "' already defined");

        // check qualifier
        if(!(node->qualifier() == symbol->qualifier()))
            throw std::string("Conflicting types for " + node->name());

        if(symbol->qualifier() == tREQUIRE)
            throw std::string("Cannot define required function");

        // check return type
        if(!(node->type() == symbol->type()))
            throw std::string("Conflicting return types for function declaration");
    } else {
        symbol = og::make_symbol(node->qualifier(), node->type(), id, true); // set defined type (even auto)
        if(!_symtab.insert(id, symbol)) {
            std::cerr << "Error inserting new symbol" << std::endl; // SHOULD NOT HAPPEN
            exit(1);
        }
    }

    _parent->set_func_symbol(symbol);

    _symtab.push(); // HACK: verify variables in safe environment

    _parent->enter_function_args();
    node->args()->accept(this, lvl + 2);
    _parent->reset_place();

    if(new_function) {
        symbol->arg_types(node->arg_types()); // save argument types
    } else {
        auto declared_types = symbol->arg_types();
        auto node_types = node->arg_types();

        do_check_function_args(declared_types, node_types);
    }
    
    _symtab.push(); // HACK: verify variables in safe environment
    _parent->enter_function_body();
    node->body()->accept(this, lvl + 2);
    _parent->reset_place();
    _symtab.pop(); // finish argument safe zone

    _symtab.pop(); // finish argument safe zone

    _parent->reset_func_symbol();



    symbol->set_defined();
}

void og::type_checker::do_function_call_node(og::function_call_node *const node, int lvl) {
    ASSERT_UNSPEC;
    assert_not_global_initialization();

    // probably types will differ. It's possible to send a ptr<auto> to a declared ptr<int>

    node->args()->accept(this, lvl + 2);

    // check if function exists
    auto symbol = _symtab.find(node->name());
    if(!symbol)
        throw std::string("undefined reference to " + node->name());

    if(!symbol->is_function())
        throw std::string("called object " + node->name() + " is not a function");

    // check if args are compatible 
    auto declared_types = symbol->arg_types();
    auto node_types = node->arg_types();
    do_check_function_call(declared_types, node_types);

    // set node type
    node->type(symbol->type());
}

void og::type_checker::do_return_node(og::return_node *const node, int lvl) {

    node->returnValue()->accept(this, lvl+2);

    if(!_parent->in_function_body())
        throw std::string("Cannot return outside of a function");


    auto symbol = _parent->func_symbol();
    if(!symbol) {
        std::cerr << "No function symbol set" << std::endl;
        exit(1);
    }

    if(symbol->is_typed(cdk::TYPE_UNSPEC)) {
        // read nodes will be kept unspec here. They will be int by default
        // if some other return returns double, it will be casted appropriately
        // function declared as auto. Setting its return value
        symbol->type(duplicate_type(node->returnValue()->type()));
    } else {
        // already defined type: they must be the same
        if(!do_check_return_types(symbol->type(), node->returnValue()->type()))
            throw std::string("Return type is not compatible with previously defined type");

        if(symbol->is_typed(cdk::TYPE_STRUCT)) {
            *(symbol->type()) = *cdk::make_structured_type(cdk::structured_type_cast(symbol->type())->components());
        }
        if(node->returnValue()->is_typed(cdk::TYPE_STRUCT)) {
            *(node->returnValue()->type()) = *cdk::make_structured_type(cdk::structured_type_cast(node->returnValue()->type())->components());
        }
    }
}

bool og::type_checker::do_check_return_types(
        std::shared_ptr<cdk::basic_type> symbol_type,
        std::shared_ptr<cdk::basic_type> node_type
) {
    if(symbol_type->name() == cdk::TYPE_STRUCT
     && node_type->name()  == cdk::TYPE_STRUCT) {

        // DISCLAIMER!!
        // need to duplicate some types so that if the symbol
        // type changes, the node types do not change.
        // Shared pointers are very maroscated...

        auto symbol_struct = structured_type_cast(symbol_type);
        auto node_struct = structured_type_cast(node_type);

        if(symbol_struct->length() != node_struct->length()) return false;

        size_t len = symbol_struct->length();
        for(size_t i = 0; i < len; i++) {
            if(!do_check_return_types(symbol_struct->component(i), node_struct->component(i))) {

                // types do not match. May need to convert
                if(symbol_struct->component(i)->name() == cdk::TYPE_DOUBLE 
                 && node_struct->component(i)->name()   == cdk::TYPE_INT) {
                    // < ... , double , ... > with < ... , int , ... >
                    // DO NOTHING: VALID SITUATION

                } else if(symbol_struct->component(i)->name() == cdk::TYPE_INT
                 && node_struct->component(i)->name()   == cdk::TYPE_DOUBLE) {
                    // < ... , int , ... > with < ... , double , ... >
                    // function must return a double
                    *symbol_struct->component(i) = *duplicate_type(node_struct->component(i));
                } else {
                    // no conversion possible
                    return false;
                }
            }
            // refresh changed types in recursion call (recalculate sizes)
            *symbol_struct = *cdk::make_structured_type(symbol_struct->components());
            *node_struct = *cdk::make_structured_type(node_struct->components());
        }
        return true;

    } else if(symbol_type->name() == cdk::TYPE_POINTER
     && node_type->name()   == cdk::TYPE_POINTER) {

        auto symbol_pointer = cdk::reference_type_cast(symbol_type);
        auto symbol_referenced = symbol_pointer->referenced();

        auto node_pointer = cdk::reference_type_cast(node_type);
        auto node_referenced = node_pointer->referenced();

        // go deep down the pointer hole
        while(symbol_referenced->name() == cdk::TYPE_POINTER
          && node_referenced->name() == cdk::TYPE_POINTER) {

            symbol_pointer = cdk::reference_type_cast(symbol_referenced);
            symbol_referenced = symbol_pointer->referenced();

            node_pointer = cdk::reference_type_cast(node_referenced);
            node_referenced = node_pointer->referenced();
        }

        return (symbol_referenced == node_referenced
             || symbol_referenced->name() == cdk::TYPE_UNSPEC); // function returns ptr<auto>


    // no pointers, no structs
    } else if(symbol_type->name() == cdk::TYPE_UNSPEC) { // expecting read
        if(node_type->name() == cdk::TYPE_INT
        || node_type->name() == cdk::TYPE_DOUBLE) {
            *symbol_type = *duplicate_type(node_type); // setting it to already defined type
        } else {
            throw std::string("Incompatible return types");
        }
    } else if(node_type->name() == cdk::TYPE_UNSPEC) { // returning read
        if(symbol_type->name() == cdk::TYPE_INT
        || symbol_type->name() == cdk::TYPE_DOUBLE) {
            *node_type = *duplicate_type(symbol_type); // casting it to declared type
        } else {
            throw std::string("Incompatible return types");
        }
    }

    return (symbol_type == node_type
        || (symbol_type->name() == cdk::TYPE_DOUBLE && node_type->name() == cdk::TYPE_INT));
}


//---------------------------------------------------------------------------

void og::type_checker::do_read_node(og::read_node *const node, int lvl) {
    ASSERT_UNSPEC; // read node type will be set eventually. must not reset it
    assert_not_global_initialization();

    node->type(cdk::make_primitive_type(0, cdk::TYPE_UNSPEC));
}

void og::type_checker::do_address_of_node(og::address_of_node *const node, int lvl) {
    ASSERT_UNSPEC;
    assert_not_global_initialization();

    node->lvalue()->accept(this, lvl+2);
    node->type(cdk::make_reference_type(4, node->lvalue()->type()));
}

void og::type_checker::do_ptr_indexation_node(og::ptr_indexation_node *const node, int lvl) {
    ASSERT_UNSPEC;
    assert_not_global_initialization();


    node->base()->accept(this, lvl+2);
    node->offset()->accept(this, lvl+2);

    if(node->offset()->is_typed(cdk::TYPE_UNSPEC)) {
        node->offset()->type(cdk::make_primitive_type(4, cdk::TYPE_INT));
    }

    if(!(node->base()->is_typed(cdk::TYPE_POINTER)))
        throw std::string("pointer indexation base must be a reference");

    if(!(node->offset()->is_typed(cdk::TYPE_INT)))
        throw std::string("pointer index must be an integer");

    node->type(cdk::reference_type_cast(node->base()->type())->referenced());
}

void og::type_checker::do_tpl_indexation_node(og::tpl_indexation_node *const node, int lvl) {
    ASSERT_UNSPEC;
    assert_not_global_initialization();


    node->base()->accept(this, lvl+2);
    node->offset()->accept(this, lvl+2);


    if(!(node->base()->is_typed(cdk::TYPE_STRUCT)))
        throw std::string("tuple indexation base must be a tuple");
    
    tuple_node* base = (tuple_node*)node->base();

    if(!(node->offset()->is_typed(cdk::TYPE_INT)))
        throw std::string("tuple index must be an integer");

    size_t index = node->offset()->value();
    if(!(index > 0 && index <= base->size()))
        throw std::string("tuple index out of range");

    node->type(cdk::structured_type_cast(base->type())->component(index - 1));
}

//---------------------------------------------------------------------------

void og::type_checker::do_for_node(og::for_node *const node, int lvl) {
    _symtab.push(); // pushing state so variables are not redeclared
    node->initialization()->accept(this, lvl+2);
    node->condition()->accept(this, lvl+2);
    _symtab.pop();

    // increment will be type checked when generated
    // block     will be type checked when generated

    for(auto cond : node->condition()->nodes()) {
        cdk::expression_node* expr = (cdk::expression_node*)cond;
        if(!expr->is_typed(cdk::TYPE_INT))
            throw std::string("non integer expression in for condition");
    }
}


void og::type_checker::do_break_node(og::break_node *const node, int lvl) {
    if(!_parent->inside_for())
        throw std::string("Cannot break outside of a for loop");
}

void og::type_checker::do_continue_node(og::continue_node *const node, int lvl) {
    if(!_parent->inside_for())
        throw std::string("Cannot break outside of a for loop");
}


//---------------------------------------------------------------------------

void og::type_checker::do_if_node(og::if_node *const node, int lvl) {
    node->condition()->accept(this, lvl + 2);
    node->block()->accept(this, lvl + 2);

    if(!(node->condition()->is_typed(cdk::TYPE_INT)))
        throw std::string("condition must be integer");
}

void og::type_checker::do_if_else_node(og::if_else_node *const node, int lvl) {
    node->condition()->accept(this, lvl + 2);
    node->thenblock()->accept(this, lvl + 2);
    node->elseblock()->accept(this, lvl + 2);

    if(!(node->condition()->is_typed(cdk::TYPE_INT)))
        throw std::string("condition must be integer");
}
