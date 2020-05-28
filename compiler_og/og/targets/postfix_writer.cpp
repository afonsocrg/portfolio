#include <string>
#include <sstream>
#include "targets/type_checker.h"
#include "targets/postfix_writer.h"
#include "targets/frame_size_calculator.h"
#include "ast/all.h"  // all.h is automatically generated
#include "og_parser.tab.h"

int og::postfix_writer::calculateFrameSize(cdk::basic_node* node) {
    og::frame_size_calculator calculator(_compiler, _symtab);
    node->accept(&calculator, 0);
    return calculator.result();
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_nil_node(cdk::nil_node * const node, int lvl) {
    // EMPTY
}
void og::postfix_writer::do_data_node(cdk::data_node * const node, int lvl) {
    // EMPTY
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_sequence_node(cdk::sequence_node * const node, int lvl) {
    for (size_t i = 0; i < node->size(); i++) {
        node->node(i)->accept(this, lvl);
    }
}

void og::postfix_writer::do_block_node(og::block_node *const node, int lvl) {
    _symtab.push();
    node->declarations()->accept(this, lvl+2);
    node->instructions()->accept(this, lvl+2);
    _symtab.pop();
}

void og::postfix_writer::do_tuple_node(og::tuple_node *const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    node->nodes()->accept(this, lvl + 2);
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_integer_node(cdk::integer_node * const node, int lvl) {
    if(in_global_initialization()) {
        if(new_symbol() && new_symbol()->type()->name() == cdk::TYPE_DOUBLE) {
            _pf.SDOUBLE(node->value());
        } else {
            _pf.SINT(node->value());
        }
    } else {
        _pf.INT(node->value()); // push an integer
    }
}

void og::postfix_writer::do_double_node(cdk::double_node * const node, int lvl) {
    if(in_global_initialization()) {
        _pf.SDOUBLE(node->value());
    } else {
        _pf.DOUBLE(node->value());
    }
}

void og::postfix_writer::do_string_node(cdk::string_node * const node, int lvl) {
    int lbl1;

    /* generate the string */
    _pf.RODATA(); // strings are DATA readonly
    _pf.ALIGN(); // make sure we are aligned
    _pf.LABEL(mklbl(lbl1 = ++_lbl)); // give the string a name
    _pf.SSTRING(node->value()); // output string characters

    if(in_global_initialization()) {
        _pf.DATA();
        _pf.SADDR(mklbl(lbl1));
    } else {
        /* leave the address on the stack */
        _pf.TEXT(); // return to the TEXT segment
        _pf.ADDR(mklbl(lbl1)); // the string to be printed
    }
}

void og::postfix_writer::do_nullptr_node(og::nullptr_node *const node, int lvl) {
    if(in_global_initialization()) {
        _pf.SINT(0);
    } else {
        _pf.INT(0);
    }
}


//---------------------------------------------------------------------------

void og::postfix_writer::do_neg_node(cdk::neg_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    node->argument()->accept(this, lvl); // determine the value
    _pf.NEG(); // 2-complement
}

void og::postfix_writer::do_identity_node(og::identity_node *const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    node->argument()->accept(this, lvl);
}


void og::postfix_writer::do_sizeof_node(og::sizeof_node *const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    _pf.INT(node->argument()->type()->size());
}

void og::postfix_writer::do_alloc_node(og::alloc_node *const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    node->argument()->accept(this, lvl);

    auto referenced = cdk::reference_type_cast(node->type())->referenced();
    if(referenced->name() == cdk::TYPE_INT
    || referenced->name() == cdk::TYPE_STRING
    || referenced->name() == cdk::TYPE_STRUCT
    || referenced->name() == cdk::TYPE_POINTER
    ) {
        _pf.INT(4);
    } else if(referenced->name() == cdk::TYPE_DOUBLE) {
        _pf.INT(8);
    } else {
        std::cerr << "Could not allocate type" << std::endl;
        exit(1);
    }

    _pf.MUL();
    _pf.ALLOC();
    _pf.SP();
}

void og::postfix_writer::do_not_node(cdk::not_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    node->argument()->accept(this, lvl);
    _pf.INT(0);
    _pf.EQ();
}
//---------------------------------------------------------------------------

void og::postfix_writer::do_add_node(cdk::add_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    node->left()->accept(this, lvl);
    if(node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        // if int with double -> convert int to double
        _pf.I2D();
    } else if(node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_POINTER) ) {
        // if int with pointer -> convert int to pointed offset
        if(!(cdk::reference_type_cast(node->right()->type())->referenced()->name()==cdk::TYPE_VOID)) {
            // multiply int only if not void (if void, add number of bytes)
            auto referenced = cdk::reference_type_cast(node->right()->type())->referenced();
            _pf.INT(referenced->size());
            _pf.MUL();
        }
    }

    node->right()->accept(this, lvl);
    if(node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT)) {
        // if int with double -> convert int to double
        _pf.I2D();
    } else if(node->left()->is_typed(cdk::TYPE_POINTER) && node->right()->is_typed(cdk::TYPE_INT)) {
        // if int with pointer -> convert int to pointed offset
        if(!(cdk::reference_type_cast(node->left()->type())->referenced()->name()==cdk::TYPE_VOID)) {
            auto referenced = cdk::reference_type_cast(node->left()->type())->referenced();
            _pf.INT(referenced->size());
            _pf.MUL();
        }
    }

    if(node->is_typed(cdk::TYPE_INT)
    || node->is_typed(cdk::TYPE_POINTER)
    ) {
        _pf.ADD();
    } else if (node->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.DADD();
    } else {
        std::cerr << "SHOULD NOT HAPPEN!" << std::endl;
        exit(1);
    }
}
void og::postfix_writer::do_sub_node(cdk::sub_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    node->left()->accept(this, lvl);
    if(node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        // if int with double -> convert int to double
        _pf.I2D();
    } 

    node->right()->accept(this, lvl);
    if(node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT)) {
        // if int with double -> convert int to double
        _pf.I2D();
    }

    if(node->is_typed(cdk::TYPE_INT)) {
        _pf.SUB();
    } else if (node->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.DSUB();
    } else {
        std::cerr << "SHOULD NOT HAPPEN!" << std::endl;
        exit(1);
    }

    if(node->left()->is_typed(cdk::TYPE_POINTER) && node->right()->is_typed(cdk::TYPE_POINTER)) {
        // pointer difference yields number of pointed objects
        auto referenced = cdk::reference_type_cast(node->left()->type())->referenced();
        _pf.INT(referenced->size());
        _pf.DIV();
    }
}
void og::postfix_writer::do_mul_node(cdk::mul_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    node->left()->accept(this, lvl);
    if(node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        // if int with double -> convert int to double
        _pf.I2D();
    }

    node->right()->accept(this, lvl);
    if(node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT)) {
        // if int with double -> convert int to double
        _pf.I2D();
    }

    if(node->is_typed(cdk::TYPE_INT)) {
        _pf.MUL();
    } else if (node->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.DMUL();
    } else {
        std::cerr << "SHOULD NOT HAPPEN!" << std::endl;
        exit(1);
    }
}
void og::postfix_writer::do_div_node(cdk::div_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    node->left()->accept(this, lvl);
    if(node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        // if int with double -> convert int to double
        _pf.I2D();
    }
    node->right()->accept(this, lvl);
    if(node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT)) {
        // if int with double -> convert int to double
        _pf.I2D();
    }
    if(node->is_typed(cdk::TYPE_INT)) {
        _pf.DIV();
    } else if (node->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.DDIV();
    } else {
        std::cerr << "SHOULD NOT HAPPEN!" << std::endl;
        exit(1);
    }
}
void og::postfix_writer::do_mod_node(cdk::mod_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    node->left()->accept(this, lvl);
    node->right()->accept(this, lvl);
    _pf.MOD();
}
void og::postfix_writer::do_lt_node(cdk::lt_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    node->left()->accept(this, lvl);
    if(node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        // if int with double -> convert int to double
        _pf.I2D();
    }
    node->right()->accept(this, lvl);
    if(node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT)) {
        // if int with double -> convert int to double
        _pf.I2D();
    }

    if (node->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.DCMP();
        _pf.INT(0);
    }
    _pf.LT();
}
void og::postfix_writer::do_le_node(cdk::le_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    node->left()->accept(this, lvl);
    if(node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        // if int with double -> convert int to double
        _pf.I2D();
    }
    node->right()->accept(this, lvl);
    if(node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT)) {
        // if int with double -> convert int to double
        _pf.I2D();
    }

    if (node->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.DCMP();
        _pf.INT(0);
    }
    _pf.LE();
}
void og::postfix_writer::do_ge_node(cdk::ge_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    node->left()->accept(this, lvl);
    if(node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        // if int with double -> convert int to double
        _pf.I2D();
    }
    node->right()->accept(this, lvl);
    if(node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT)) {
        // if int with double -> convert int to double
        _pf.I2D();
    }

    if (node->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.DCMP();
        _pf.INT(0);
    }
    _pf.GE();
}
void og::postfix_writer::do_gt_node(cdk::gt_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    node->left()->accept(this, lvl);
    if(node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        // if int with double -> convert int to double
        _pf.I2D();
    }
    node->right()->accept(this, lvl);
    if(node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT)) {
        // if int with double -> convert int to double
        _pf.I2D();
    }

    if (node->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.DCMP();
        _pf.INT(0);
    }
    _pf.GT();
}
void og::postfix_writer::do_ne_node(cdk::ne_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    node->left()->accept(this, lvl);
    if(node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        // if int with double -> convert int to double
        _pf.I2D();
    }
    node->right()->accept(this, lvl);
    if(node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT)) {
        // if int with double -> convert int to double
        _pf.I2D();
    }

    if (node->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.DCMP();
        _pf.INT(0);
    }
    _pf.NE();
}
void og::postfix_writer::do_eq_node(cdk::eq_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    node->left()->accept(this, lvl);
    if(node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        // if int with double -> convert int to double
        _pf.I2D();
    }
    node->right()->accept(this, lvl);
    if(node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT)) {
        // if int with double -> convert int to double
        _pf.I2D();
    }

    if (node->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.DCMP();
        _pf.INT(0);
    }
    _pf.EQ();
}


void og::postfix_writer::do_and_node(cdk::and_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    int skip = ++_lbl;

    node->left()->accept(this, lvl);
    _pf.DUP32();                        // will be consumed by JZ
    _pf.JZ(mklbl(skip));                // if zero, yield 0

    _pf.TRASH(4);                       // remove last value (true)
    node->right()->accept(this, lvl);   // yield right value

    _pf.ALIGN();
    _pf.LABEL(mklbl(skip));
}
void og::postfix_writer::do_or_node(cdk::or_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    int skip = ++_lbl;

    node->left()->accept(this, lvl);
    _pf.DUP32();
    _pf.JNZ(mklbl(skip));

    _pf.TRASH(4);
    node->right()->accept(this, lvl);

    _pf.ALIGN();
    _pf.LABEL(mklbl(skip));
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_variable_node(cdk::variable_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    const std::string &id = node->name();
    auto symbol = _symtab.find(id);
    if(!symbol) {
        std::cerr << "variable symbol not found" << std::endl;
        exit(1);
    }

    if(symbol->offset() == 0) { // 0 offset means global
        _pf.ADDR(symbol->name());
    } else {
        _pf.LOCAL(symbol->offset());
    }
}

void og::postfix_writer::do_variable_declaration_node(og::variable_declaration_node *const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
        if(in_global()){
            for(size_t i = 0; i < node->names().size(); i++){
                auto symbol = _symtab.find(node->name(i));
                if(!symbol) { // should not happen
                    std::cerr << "variable not previously defined" << std::endl;
                    exit(1);
                }

                if(symbol->qualifier() == tREQUIRE) {
                    require_symbol(symbol->name());
                    return;
                }

                if(node->initializer()) {
                    _pf.DATA();
                } else {
                    _pf.BSS();
                }

                _pf.ALIGN();
                if(symbol->qualifier() == tPUBLIC) {
                    _pf.GLOBAL(symbol->name(), _pf.OBJ());
                }
                _pf.LABEL(symbol->name());

                if(node->initializer()) {
                    start_initializing_var();
                    set_new_symbol(symbol); // let integer node know if needs to store double

                    if(node->names().size() == 1 && node->initializer()->size() > 1) {
                        // auto a = 1, 2, 3
                        node->initializer()->nodes()->accept(this, lvl+2);
                    } else {
                        // auto a, b, c = 1, 2, 3
                        // all the other kids with the pumped up kicks and the...
                        node->initializer()->node(i)->accept(this, lvl+2);
                        // ... all the other kids...
                    }

                    reset_new_symbol();
                    stop_initializing_var();
                } else {
                    _pf.SALLOC(node->type()->size());
                }
            }
        } else if(in_function_args()) {
            // declare arg and get offset
            auto symbol = _symtab.find(node->name(0));
            if(!symbol) { // should not happen
                std::cerr << "variable not previously defined" << std::endl;
                exit(1);
            }
            
            int offset = declare_local_arg(symbol->type()->size());
            symbol->set_offset(offset);
        } else if(in_function_body()) {
            if(node->initializer()) {
                // place initializers in the stack (first stays on top)
                for(size_t i = node->initializer()->size(); i-- > 0;) {
                    node->initializer()->node(i)->accept(this, lvl+2);

                    if(node->names().size()-1 < i) continue;

                    // there's a name for this initializer
                    auto symbol = _symtab.find(node->name(i));
                    if(!symbol) { // should not happen
                        std::cerr << "variable not previously defined" << std::endl;
                        exit(1);
                    }

                    if(symbol->is_typed(cdk::TYPE_DOUBLE) // assigning int to double: cast
                    && node->initializer()->node(i)->is_typed(cdk::TYPE_INT)) {
                        _pf.I2D();
                    }

                }
            }

            for(size_t i = 0; i < node->names().size(); i++){
                // for each variable, get the topmost item in stack
                auto symbol = _symtab.find(node->name(i));
                if(!symbol) { // should not happen
                    std::cerr << "variable not previously defined" << std::endl;
                    exit(1);
                }

                int offset = declare_local_var(symbol->type()->size());
                symbol->set_offset(offset);

                if(node->initializer()){

                    if(symbol->is_typed(cdk::TYPE_INT)
                    || symbol->is_typed(cdk::TYPE_POINTER)
                    || symbol->is_typed(cdk::TYPE_STRING)
                    ) {
                        _pf.LOCAL(offset);
                        _pf.STINT();
                    } else if(symbol->is_typed(cdk::TYPE_DOUBLE)) {
                        // integer to double is already casted
                        _pf.LOCAL(offset);
                        _pf.STDOUBLE();
                    } else if(symbol->is_typed(cdk::TYPE_STRUCT)) {
                        do_initialize_tuple_variable(offset, cdk::structured_type_cast(symbol->type()), 0);
                    }
                }
            }

        } else {
            std::cerr << "variable not previously defined" << std::endl;
            exit(1);
        }
}

void og::postfix_writer::do_initialize_tuple_variable(
        int tpl_base_offset,
        std::shared_ptr<cdk::basic_type> tuple_type,
        int assigned_bytes
) {
    auto struct_type = cdk::structured_type_cast(tuple_type);
    for(size_t i = 0; i < struct_type->length(); i++) {
        if(struct_type->component(i)->name() == cdk::TYPE_STRUCT) {
            do_initialize_tuple_variable(
                tpl_base_offset,
                struct_type->component(i),
                assigned_bytes
            );
        } else {
            // optimization
            _pf.LOCAL(tpl_base_offset+assigned_bytes);
            // _pf.LOCAL(tpl_base_offset);
            // _pf.INT(assigned_bytes);
            // _pf.ADD();

            if(struct_type->component(i)->name() == cdk::TYPE_INT
            || struct_type->component(i)->name() == cdk::TYPE_POINTER
            || struct_type->component(i)->name() == cdk::TYPE_STRING) {

                _pf.STINT();

            } else if(struct_type->component(i)->name() == cdk::TYPE_DOUBLE) {
                // integer already casted if needed (when loading tuple)
                _pf.STDOUBLE();
            }
        }

        assigned_bytes += struct_type->component(i)->size();
    }
}

void og::postfix_writer::do_rvalue_node(cdk::rvalue_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    node->lvalue()->accept(this, lvl);

    if(node->is_typed(cdk::TYPE_INT)
    || node->is_typed(cdk::TYPE_POINTER)
    || node->is_typed(cdk::TYPE_STRING)
    ) {
        _pf.LDINT(); // depends on type size
    } else if (node->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.LDDOUBLE();
    } else if(node->is_typed(cdk::TYPE_STRUCT)) {
        // might seem redundant. size used for cumulative recursion
        if(is_indexating_tuple()) {
            // left value loads the address
            return;
        } else {
            do_load_tuple(node->type(), node->type()->size()); 
            _pf.TRASH(4);
        }
    } else {
        std::cerr << node->lineno() << ": Could not load type " + cdk::to_string(node->type()) << std::endl;
        exit(1);
    }
}

// LOADS TUPLE IN ADDRESS ON TOP OF STACK AND LEAVES ADDRESS ON TOP OF STACK
void og::postfix_writer::do_load_tuple(std::shared_ptr<cdk::basic_type> tuple_type, int offset) {
    // copy the tuple to the top of the stack. Assume tuple base on top of stack
    // place lower indices on top of stack

    auto struct_type = cdk::structured_type_cast(tuple_type);
    for(int i = struct_type->length()-1; i >= 0; i--) {
        auto element_type = struct_type->component(i);
        offset -= element_type->size();
        

        if(element_type->name() == cdk::TYPE_STRUCT) {
            do_load_tuple(element_type, offset + element_type->size());
            continue;
        }

        _pf.DUP32(); // copy tuple address
        if(element_type->name() == cdk::TYPE_DOUBLE) {
            _pf.DUP32(); // duplicate again for swap64
        }

        _pf.INT(offset);
        _pf.ADD();

        if(element_type->name() == cdk::TYPE_INT
        || element_type->name() == cdk::TYPE_POINTER
        || element_type->name() == cdk::TYPE_STRING
        ) {
            _pf.LDINT();
            _pf.SWAP32(); // swap tuple element with base address
        } else if (element_type->name() == cdk::TYPE_DOUBLE) {
            _pf.LDDOUBLE();
            _pf.SWAP64(); // swap tuple element with base address
            _pf.TRASH(4); // remove extra duplicate (see above)
        } else {
            std::cerr << "Invalid type!! ARRRRGGHH" << std::endl; // SHOULD NOT HAPPEN
            exit(1);
        }
    }
}

void og::postfix_writer::do_assignment_node(cdk::assignment_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    node->rvalue()->accept(this, lvl); // determine the new value

    if(node->is_typed(cdk::TYPE_DOUBLE) && node->rvalue()->is_typed(cdk::TYPE_INT)) {
        _pf.I2D();
    }

    if(node->is_typed(cdk::TYPE_INT)
    || node->is_typed(cdk::TYPE_POINTER)
    || node->is_typed(cdk::TYPE_STRING)
    ) {
        _pf.DUP32();
    } else if (node->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.DUP64();

    } else if(node->is_typed(cdk::TYPE_STRUCT)) {
        _pf.SP(); // duplicate tuple
        do_load_tuple(node->rvalue()->type(), node->rvalue()->type()->size());
        _pf.TRASH(4); // discard SP

    } else {
        std::cerr << "Could not load type " + cdk::to_string(node->type()) << std::endl;
        exit(1);
    }


    if(node->is_typed(cdk::TYPE_INT)
    || node->is_typed(cdk::TYPE_POINTER)
    || node->is_typed(cdk::TYPE_STRING)
    ) {
        node->lvalue()->accept(this, lvl); // where to store the value
        _pf.STINT();
    } else if (node->is_typed(cdk::TYPE_DOUBLE)) {
        node->lvalue()->accept(this, lvl); // where to store the value
        _pf.STDOUBLE();
    } else if (node->is_typed(cdk::TYPE_STRUCT)) {
        do_store_tuple(node->lvalue(), node->rvalue()->type(), 0);
    } else {
        std::cerr << "Could not load type " + cdk::to_string(node->type()) << std::endl;
        exit(1);
    }
}

void og::postfix_writer::do_store_tuple(
        cdk::lvalue_node* const lval,
        std::shared_ptr<cdk::basic_type> tuple_type,
        int assigned_bytes
) {
    auto right_type = cdk::structured_type_cast(tuple_type);
    auto left_type = cdk::structured_type_cast(lval->type());
    for(size_t i = 0; i < right_type->length(); i++) {
        if(left_type->component(i)->name() == cdk::TYPE_STRUCT) {
            do_store_tuple(lval, right_type->component(i), assigned_bytes);

        } else {
            if(left_type->component(i)->name() == cdk::TYPE_DOUBLE
            && right_type->component(i)->name() == cdk::TYPE_INT) {
                _pf.I2D();
            }
            lval->accept(this, 0); // need to reload writing address
            _pf.INT(assigned_bytes); // calculate tuple offset
            _pf.ADD();

            if(left_type->component(i)->name() == cdk::TYPE_INT
            || left_type->component(i)->name() == cdk::TYPE_POINTER
            || left_type->component(i)->name() == cdk::TYPE_STRING) {

                _pf.STINT();

            } else if(left_type->component(i)->name() == cdk::TYPE_DOUBLE) {
                _pf.STDOUBLE();
            }
        }
        assigned_bytes += left_type->component(i)->size();
    }
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_evaluation_node(og::evaluation_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    node->argument()->accept(this, lvl); // determine the value

    if(node->argument()->is_typed(cdk::TYPE_INT)
    || node->argument()->is_typed(cdk::TYPE_DOUBLE)
    || node->argument()->is_typed(cdk::TYPE_POINTER)
    || node->argument()->is_typed(cdk::TYPE_STRING)
    || node->argument()->is_typed(cdk::TYPE_STRUCT)
    ) {
        _pf.TRASH(node->argument()->type()->size());
    } else if (!(node->argument()->is_typed(cdk::TYPE_VOID)
              || node->argument()->is_typed(cdk::TYPE_UNSPEC))) {
        std::cerr << "Could not discard type " + cdk::to_string(node->argument()->type()) << std::endl;
        exit(1);
    }
}

void og::postfix_writer::do_write_node(og::write_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    // might fix this later: visit all the expressions in reverse order
    // visit the type (use recursive function) and pick the appropriate call

    // write a, b, ...
    for(auto arg : node->arguments()->nodes()) {
        cdk::expression_node* expr = (cdk::expression_node*) arg;

        expr->accept(this, lvl+2);

        if(expr->is_typed(cdk::TYPE_INT)) {
            require_symbol("printi");
            _pf.CALL("printi");
            _pf.TRASH(4);
        } else if (expr->is_typed(cdk::TYPE_DOUBLE)) {
            require_symbol("printd");
            _pf.CALL("printd");
            _pf.TRASH(8);
        } else if (expr->is_typed(cdk::TYPE_STRING)) {
            require_symbol("prints");
            _pf.CALL("prints");
            _pf.TRASH(4);
        } else if(expr->is_typed(cdk::TYPE_STRUCT)){
            do_tuple_print(expr->type());
        } else {
            std::cerr << "ERROR: CANNOT HAPPEN!" << std::endl;
            exit(1);
        }

        if(node->newline()) {
            require_symbol("println");
            _pf.CALL("println");
        }
    }
}

// print all elements of the tuple
void og::postfix_writer::do_tuple_print(std::shared_ptr<cdk::basic_type> tuple_type) {
    auto struct_type = cdk::structured_type_cast(tuple_type);
    for(auto child_type : struct_type->components()) {
        if(child_type->name() == cdk::TYPE_INT) {
            require_symbol("printi");
            _pf.CALL("printi");
            _pf.TRASH(4);
        } else if (child_type->name() == cdk::TYPE_DOUBLE) {
            require_symbol("printd");
            _pf.CALL("printd");
            _pf.TRASH(8);
        } else if (child_type->name() == cdk::TYPE_STRING) {
            require_symbol("prints");
            _pf.CALL("prints");
            _pf.TRASH(4);
        } else if (child_type->name() == cdk::TYPE_STRUCT) {
            do_tuple_print(child_type);
        }
    }

}

//---------------------------------------------------------------------------

void og::postfix_writer::do_function_declaration_node(og::function_declaration_node *const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    auto symbol = _symtab.find(node->name());
    if(!symbol || !symbol->is_function()) {
        std::cerr << "Function not found. Should not happen" << std::endl;
        exit(1);
    }
    if(!symbol->is_defined()) {
        require_symbol(symbol->name());
    }
}

void og::postfix_writer::do_function_call_node(og::function_call_node *const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    auto symbol = _symtab.find(node->name());
    if(!symbol) { // should not happen
        std::cerr << "function not previously defined" << std::endl;
        exit(1);
    }

    int totalSize = 0;

    auto argTypes = symbol->arg_types();

    // place args
    if(symbol->is_typed(cdk::TYPE_STRUCT)) {
        // allocate tuple
        _pf.INT(symbol->type()->size());
        _pf.ALLOC();
    }

    // function args are a sequence with a tuple in the first position
    // the tuple contains all the arguments
    og::tuple_node* args = (og::tuple_node*) node->args()->node(0);
    for(int i = argTypes.size()-1; i >= 0; i--) {
        cdk::expression_node* expr = (cdk::expression_node*) args->node(i);
        expr->accept(this, lvl+2);
        if(expr->is_typed(cdk::TYPE_INT) && argTypes.at(i)->name() == cdk::TYPE_DOUBLE) {
            _pf.I2D();
        }
        totalSize += expr->type()->size();
    }

    if(symbol->is_typed(cdk::TYPE_STRUCT)) {
        _pf.SP();
        if(symbol->args_byte_size() > 0) {
            // add only if greater than 0
            _pf.INT(symbol->args_byte_size());
            _pf.ADD();
        }
    }

    _pf.CALL(symbol->name());

    // trash arguments
    if(totalSize > 0 || symbol->is_typed(cdk::TYPE_STRUCT))
        _pf.TRASH(totalSize + (symbol->is_typed(cdk::TYPE_STRUCT) ? 4 : 0));

    // get return value
    if(symbol->type()->name() == cdk::TYPE_INT
    || symbol->type()->name() == cdk::TYPE_POINTER
    || symbol->type()->name() == cdk::TYPE_STRING
    ) {
        _pf.LDFVAL32();
    } else if(symbol->type()->name() == cdk::TYPE_DOUBLE) {
        _pf.LDFVAL64();
    }

}

void og::postfix_writer::do_function_definition_node(og::function_definition_node *const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    auto symbol = _symtab.find(node->name());
    if(!symbol) { // should not happen
        std::cerr << "function not previously defined" << std::endl;
        exit(1);
    }

    unrequire_symbol(symbol->name());

    // save symbol to future use.
    set_func_symbol(symbol);

    _symtab.push(); // function args context
    // declare arguments
    enter_function_args();
    node->args()->accept(this, lvl+2);
    reset_place();

    const std::string name = symbol->name() == "og" ? "_main" : symbol->name();

    _pf.TEXT();
    _pf.ALIGN();
    if(symbol->qualifier() == tPUBLIC) {
        _pf.GLOBAL(name, _pf.FUNC());
    }
    _pf.LABEL(name);

    enter_function_body(); // notify that visiting inside function body
    _symtab.push(); // frame size calculator needs to type check auto vars
    int size = calculateFrameSize(node->body());
    _symtab.pop();
    reset_place();

    if(size == 0) {
        _pf.START();
    } else {
        _pf.ENTER(size);
    }

    enter_function_body(); // re-entering to reset total declared bytes
    node->body()->accept(this, lvl + 2);
    reset_place();

    _pf.LEAVE();
    _pf.RET();

    _symtab.pop(); // function args context
    reset_func_symbol();
}

void og::postfix_writer::do_return_node(og::return_node *const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    auto symbol = func_symbol();
    if(!symbol) { // SHOULD NOT HAPPEN
        std::cerr << "No current function set" << std::endl;
        exit(1);
    }

    if(symbol->is_typed(cdk::TYPE_INT)
    || symbol->is_typed(cdk::TYPE_STRING)
    || symbol->is_typed(cdk::TYPE_POINTER)
    ) {
        node->returnValue()->accept(this, lvl+2);
        _pf.STFVAL32();
    } else if(symbol->type()->name() == cdk::TYPE_DOUBLE) {
        node->returnValue()->accept(this, lvl+2);
        if(node->returnValue()->is_typed(cdk::TYPE_INT)) {
            _pf.I2D();
        }
        _pf.STFVAL64();
    } else if(symbol->is_typed(cdk::TYPE_STRUCT)) {
        return_tuple(symbol->type(), (tuple_node*)node->returnValue(), 0);
        
        _pf.LOCAL(8); // get tuple ptr
        _pf.LDINT();
        _pf.STFVAL32();
    } else {
        std::cerr << "Could not return type" << std::endl;
        exit(1);
    }

    _pf.LEAVE();
    _pf.RET();
}

void og::postfix_writer::return_tuple(std::shared_ptr<cdk::basic_type> symbol_type, tuple_node* tuple, int prev_returned_bytes) {
    size_t returned_bytes = 0;
    auto return_type = cdk::structured_type_cast(symbol_type);
    auto tuple_type = cdk::structured_type_cast(tuple->type());
    for(size_t i = 0; i < tuple->size(); i++) {
        cdk::expression_node* element = (cdk::expression_node*)tuple->node(i);
        auto wanted_type = return_type->component(i);
        auto el_type = tuple_type->component(i);

        element->accept(this, 0);


        // store
        if(wanted_type->name() == cdk::TYPE_INT
        || wanted_type->name() == cdk::TYPE_STRING
        || wanted_type->name() == cdk::TYPE_POINTER
        ) {
            _pf.LOCAL(8);
            _pf.LDINT(); // load tuple address

            _pf.INT(prev_returned_bytes + returned_bytes);
            _pf.ADD();

            _pf.STINT();
        } else if(wanted_type->name() == cdk::TYPE_DOUBLE) {
            if(element->is_typed(cdk::TYPE_INT)) {
                _pf.I2D();
            }
            _pf.LOCAL(8);
            _pf.LDINT(); // load tuple address

            _pf.INT(prev_returned_bytes + returned_bytes);
            _pf.ADD();

            _pf.STDOUBLE();
        } else if(wanted_type->name() == cdk::TYPE_STRUCT) {
            return_tuple(wanted_type, (tuple_node*)element, prev_returned_bytes + returned_bytes);
        }

        returned_bytes += wanted_type->size();
    }
}


//---------------------------------------------------------------------------

void og::postfix_writer::do_read_node(og::read_node * const node, int lvl) {
    if(node->is_typed(cdk::TYPE_INT)) {
      require_symbol("readi");
      _pf.CALL("readi");
      _pf.LDFVAL32();
    } else {
      require_symbol("readd");
      _pf.CALL("readd");
      _pf.LDFVAL64();
    }
}

void og::postfix_writer::do_address_of_node(og::address_of_node *const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    node->lvalue()->accept(this, lvl+2);
}

void og::postfix_writer::do_ptr_indexation_node(og::ptr_indexation_node *const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    node->base()->accept(this, lvl + 2);
    node->offset()->accept(this, lvl + 2);

    _pf.INT(node->type()->size());
    _pf.MUL();
    _pf.ADD();
}

void og::postfix_writer::do_tpl_indexation_node(og::tpl_indexation_node *const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    start_indexating_tuple();
    node->base()->accept(this, lvl+2);
    stop_indexating_tuple();

    size_t desired_index = node->offset()->value();
    size_t offset = 0;

    for(size_t i = 1; i < desired_index; i++) {
        offset += cdk::structured_type_cast(node->base()->type())->component(i-1)->size();
    }

    _pf.INT(offset);
    _pf.ADD();
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_for_node(og::for_node *const node, int lvl) {
    // removed assert safe because every child that needs type checking will do it
    int lbl_cond = ++_lbl;
    int lbl_end  = ++_lbl;
    int lbl_incr = ++_lbl;



    // init
    enter_for(lbl_end, lbl_incr);
    _symtab.push();
    node->initialization()->accept(this, lvl+2);

    // cond
    _pf.ALIGN();
    _pf.LABEL(mklbl(lbl_cond));
    node->condition()->accept(this, lvl+2);
    _pf.JZ(mklbl(lbl_end));

    // block
    node->block()->accept(this, lvl+2);

    // incr
    _pf.ALIGN();
    _pf.LABEL(mklbl(lbl_incr));
    node->increment()->accept(this, lvl+2);

    // go back
    _pf.JMP(mklbl(lbl_cond));

    // end
    _pf.ALIGN();
    _pf.LABEL(mklbl(lbl_end));

    _symtab.pop();
    exit_for();
}


void og::postfix_writer::do_break_node(og::break_node *const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    _pf.JMP(mklbl(end_label()));
}

void og::postfix_writer::do_continue_node(og::continue_node *const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    _pf.JMP(mklbl(incr_label()));
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_if_node(og::if_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    int lbl1;
    node->condition()->accept(this, lvl);
    _pf.JZ(mklbl(lbl1 = ++_lbl));
    node->block()->accept(this, lvl + 2);
    _pf.LABEL(mklbl(lbl1));
}

//---------------------------------------------------------------------------

void og::postfix_writer::do_if_else_node(og::if_else_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    int lbl1, lbl2;
    node->condition()->accept(this, lvl);
    _pf.JZ(mklbl(lbl1 = ++_lbl));
    node->thenblock()->accept(this, lvl + 2);
    _pf.JMP(mklbl(lbl2 = ++_lbl));
    _pf.LABEL(mklbl(lbl1));
    node->elseblock()->accept(this, lvl + 2);
    _pf.LABEL(mklbl(lbl1 = lbl2));
}

