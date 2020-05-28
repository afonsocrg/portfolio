#ifndef __OG_TARGETS_SYMBOL_H__
#define __OG_TARGETS_SYMBOL_H__

#include <string>
#include <memory>
#include <cdk/types/basic_type.h>

namespace og {

    class symbol {
        int _qualifier;
        std::shared_ptr<cdk::basic_type> _type;
        std::string _name;

        // offset for variables. global variables have offset 0
        int _offset = 0;

        bool _isFunction;
        bool _isDefined = false;
        std::vector<std::shared_ptr<cdk::basic_type>> _argTypes;
        
    public:
        symbol(int qualifier,
                std::shared_ptr<cdk::basic_type> type,
                const std::string &name,
                bool isFunction = false) :
            _qualifier(qualifier), _type(type), _name(name), _isFunction(isFunction) {
        }
        
        virtual ~symbol() {
            // EMPTY
        }
        
        int qualifier() const { return _qualifier; }

        std::shared_ptr<cdk::basic_type> type() const { return _type; }
        bool is_typed(cdk::typename_type name) const { return _type->name() == name; }
        void type(std::shared_ptr<cdk::basic_type> type) {
            if(_type->name() != cdk::TYPE_UNSPEC)
                throw std::string("Symbol type is already defined");

            _type = type;
        }
        
        const std::string &name() const { return _name; }

        bool is_function() const { return _isFunction; }

        bool is_defined() const { return _isDefined; }
        void set_defined() { _isDefined = true; }

        int offset() { return _offset; }
        void set_offset(int offset) {
            _offset = offset;
        }

        auto arg_types() const { return _argTypes; }
        void arg_types(std::vector<std::shared_ptr<cdk::basic_type>> const types) { _argTypes = types; }
        // TODO: rename args_size -> args_len; rename args_byte_size -> args_size
        size_t args_size() const { return _argTypes.size(); }
        size_t args_byte_size() const  {
            size_t res = 0;
            for(auto arg : _argTypes) {
                res += arg->size();
            }
            return res;
        }
    
    };

    inline auto make_symbol(int qualifier,
            std::shared_ptr<cdk::basic_type> type,
            const std::string &name,
            bool isFunction = false) {
        return std::make_shared<symbol>(qualifier, type, name, isFunction);
    }

} // og

#endif
