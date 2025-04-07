#include "option.hpp"



Option Option::from_json(const json &j)
{
    std::string name = j["name"].get<std::string>();
    bool visible = [&]()
    {
        if (j.contains("visible"))
            return j["visible"].get<bool>();
        return true;
    }();
    bool editable = [&]()
    {
        if (j.contains("editable"))
            return j["editable"].get<bool>();
        return true;
    }();

    OptionType opt_type = from_string(j["type"].get<std::string>());

    switch (opt_type)
    {
    case OptionType::BOOL:
        return OptionExtended<bool>(name, j["value"].get<bool>(), visible, editable);
    case OptionType::INT:
        return OptionExtended<int>(name, j["value"].get<int>(), visible, editable);
    case OptionType::FLOAT:
        return OptionExtended<float>(name, j["value"].get<float>(), visible, editable);
    case OptionType::STRING:
        return OptionExtended<std::string>(name, j["value"].get<std::string>(), visible, editable);
    case OptionType::CHOICE:
    {
        assert(j.contains("innerType") && "Inner type not found in JSON");
        
        OptionType inner_opt_type = from_string(j["innerType"].get<std::string>());
        switch (inner_opt_type)
        {
        case OptionType::BOOL:
            return OptionChoice<bool>(name, j["value"].get<bool>(), j["choices"].get<std::vector<bool>>(), visible, editable);
        case OptionType::INT:
            return OptionChoice<int>(name, j["value"].get<int>(), j["choices"].get<std::vector<int>>(), visible, editable);
        case OptionType::FLOAT:
            return OptionChoice<float>(name, j["value"].get<float>(), j["choices"].get<std::vector<float>>(), visible, editable);
        case OptionType::STRING:
            return OptionChoice<std::string>(name, j["value"].get<std::string>(), j["choices"].get<std::vector<std::string>>(), visible, editable);
        default:
            throw std::runtime_error("Invalid inner option type in JSON");
        }
    }
    case OptionType::RANGE:
    {
        assert(j.contains("innerType") && "Inner type not found in JSON");
        
        OptionType inner_opt_type = from_string(j["innerType"].get<std::string>());
        switch (inner_opt_type)
        {
        case OptionType::INT:
            return OptionRange<int>(name, j["value"].get<int>(), j["min"].get<int>(), j["max"].get<int>(), visible, editable);
        case OptionType::FLOAT:
            return OptionRange<float>(name, j["value"].get<float>(), j["min"].get<float>(), j["max"].get<float>(), visible, editable);
        default:
            throw std::runtime_error("Invalid inner option type in JSON");
        }
    }
    default:
        throw std::runtime_error("Invalid option type in JSON");
    }
};

void Option::to_json(json &j) const
    {
        j = json{};

        j["name"] = name;

        if (!visible)
            j["visible"] = visible;
        if (!editable)
            j["editable"] = editable;

        j["type"] = to_string(type);
    }