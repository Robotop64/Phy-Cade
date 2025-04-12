#include "option.hpp"

std::string OptionUtils::to_string(const OptionType type)
{
    switch (type)
    {
    case OptionType::BOOL:
        return "bool";
    case OptionType::INT:
        return "int";
    case OptionType::FLOAT:
        return "float";
    case OptionType::STRING:
        return "string";
    case OptionType::CHOICE:
        return "choice";
    case OptionType::RANGE:
        return "range";
    default:
        return "base";
    }
};

OptionType OptionUtils::from_string(const std::string &str)
{
    if (str == "bool")
        return OptionType::BOOL;
    else if (str == "int")
        return OptionType::INT;
    else if (str == "float")
        return OptionType::FLOAT;
    else if (str == "string")
        return OptionType::STRING;
    else if (str == "choice")
        return OptionType::CHOICE;
    else if (str == "range")
        return OptionType::RANGE;
    else
        return OptionType::BASE;
};

std::shared_ptr<Option> Option::from_json(const json &j)
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

    OptionType opt_type = OptionUtils::from_string(j["type"].get<std::string>());
    std::optional<OptionType> inner_opt_type = [&]() -> std::optional<OptionType>
    {
        if (j.contains("innerType"))
            return OptionUtils::from_string(j["innerType"].get<std::string>());
        return std::nullopt;
    }();

    switch (opt_type)
    {
    case OptionType::BOOL:
        return std::make_shared<OptionExtended<bool>>(name, j["value"].get<bool>(), visible, editable);
    case OptionType::INT:
        return std::make_shared<OptionExtended<int>>(name, j["value"].get<int>(), visible, editable);
    case OptionType::FLOAT:
        return std::make_shared<OptionExtended<float>>(name, j["value"].get<float>(), visible, editable);
    case OptionType::STRING:
        return std::make_shared<OptionExtended<std::string>>(name, j["value"].get<std::string>(), visible, editable);
    case OptionType::CHOICE:
    {
        assert(inner_opt_type.has_value() && "Inner type not found in JSON");
        assert(inner_opt_type != OptionType::CHOICE && "Inner type cannot be a choice");
        assert(inner_opt_type != OptionType::RANGE && "Inner type cannot be a range");

        switch (inner_opt_type.value())
        {
        case OptionType::BOOL:
            return std::make_shared<OptionChoice<bool>>(name, j["value"].get<bool>(), j["choices"].get<std::vector<bool>>(), visible, editable);
        case OptionType::INT:
            return std::make_shared<OptionChoice<int>>(name, j["value"].get<int>(), j["choices"].get<std::vector<int>>(), visible, editable);
        case OptionType::FLOAT:
            return std::make_shared<OptionChoice<float>>(name, j["value"].get<float>(), j["choices"].get<std::vector<float>>(), visible, editable);
        case OptionType::STRING:
            return std::make_shared<OptionChoice<std::string>>(name, j["value"].get<std::string>(), j["choices"].get<std::vector<std::string>>(), visible, editable);
        default:
            throw std::runtime_error("Invalid inner option type in JSON");
        }
    }
    case OptionType::RANGE:
    {
        assert(inner_opt_type.has_value() && "Inner type not found in JSON");
        assert(inner_opt_type != OptionType::CHOICE && "Inner type cannot be a choice");
        assert(inner_opt_type != OptionType::RANGE && "Inner type cannot be a range");

        switch (inner_opt_type.value())
        {
        case OptionType::INT:
            return std::make_shared<OptionRange<int>>(name, j["value"].get<int>(), j["min"].get<int>(), j["max"].get<int>(), visible, editable);
        case OptionType::FLOAT:
            return std::make_shared<OptionRange<float>>(name, j["value"].get<float>(), j["min"].get<float>(), j["max"].get<float>(), visible, editable);
        default:
            throw std::runtime_error("Invalid inner option type in JSON");
        }
    }
    default:
        throw std::runtime_error("Invalid option type in JSON");
    }
}

void Option::to_json(json &j) const
{
    j = json{};

    j["name"] = name;

    if (!visible)
        j["visible"] = visible;
    if (!editable)
        j["editable"] = editable;

    j["type"] = OptionUtils::to_string(type);
    if (innerType.has_value())
        j["innerType"] = OptionUtils::to_string(innerType.value());
}