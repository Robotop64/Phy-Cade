#pragma once

#include "json.hpp"

#include <string>
#include <vector>
#include <memory>
#include <type_traits>

using json = nlohmann::ordered_json;

enum class OptionType
{
    BASE,
    BOOL,
    INT,
    FLOAT,
    STRING,
    CHOICE,
    RANGE
};

struct Option
{
    bool visible;
    bool editable;
    std::string name;
    OptionType type = OptionType::BASE;

    Option(bool visible, bool editable, std::string name, OptionType type) : visible(visible), editable(editable), name(std::move(name)), type(type){};

    virtual ~Option() = default;

    template <typename T>
    static std::shared_ptr<T> to(std::shared_ptr<Option> option)
    {
        return std::dynamic_pointer_cast<T>(option);
    }

    virtual void to_json(json &j) const
    {
        j = json{
            {"name", name},
            {"type", to_string(type)}};
        
        if (!visible) j["visible"] = visible;
        if (!editable) j["editable"] = editable;
    }
    
    static std::shared_ptr<Option> from_json(const json &j)
    {
        OptionType type = from_string(j["type"].get<std::string>());
    }

    static std::string to_string(OptionType type)
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
    }

    static OptionType from_string(const std::string &str)
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
    }

    template <typename T>
    static T createOption(OptionType type)
    {
        switch (type)
        {
        case OptionType::BOOL:
            return std::make_shared<OptionBool>("", false);
        case OptionType::INT:
            return std::make_shared<OptionInt>("", 0);
        case OptionType::FLOAT:
            return std::make_shared<OptionFloat>("", 0.0f);
        case OptionType::STRING:
            return std::make_shared<OptionString>("", "");
        case OptionType::CHOICE:
            return std::make_shared<OptionChoice<T>>("", T{}, {});
        case OptionType::RANGE:
            return std::make_shared<OptionRange<T>>("", T{}, T{}, T{});
        default:
            throw std::runtime_error("Invalid option type");
        }
    }
};

template <typename T>
constexpr OptionType inferOptionType()
{
    if constexpr (std::is_same_v<T, bool>)
        return OptionType::BOOL;
    else if constexpr (std::is_same_v<T, int>)
        return OptionType::INT;
    else if constexpr (std::is_same_v<T, float>)
        return OptionType::FLOAT;
    else if constexpr (std::is_same_v<T, std::string>)
        return OptionType::STRING;
    else
        return OptionType::BASE;
}

struct OptionBool : Option
{
    bool value;

    OptionBool(std::string name, bool value) : Option{true, true, name, OptionType::BOOL}, value(value){};
    OptionBool(std::string name, bool value, bool editable, bool visible) : Option{visible, editable, name, OptionType::BOOL}, value(value){};

    void to_json(json &j) const override
    {
        Option::to_json(j);
        j["value"] = value;
    }
};

struct OptionInt : Option
{
    int value;

    OptionInt(std::string name, int value) : Option{true, true, name, OptionType::INT}, value(value){};
    OptionInt(std::string name, int value, bool editable, bool visible) : Option{visible, editable, name, OptionType::INT}, value(value){};

    void to_json(json &j) const override
    {
        Option::to_json(j);
        j["value"] = value;
    }
};

struct OptionFloat : Option
{
    float value;

    OptionFloat(std::string name, float value) : Option{true, true, name, OptionType::FLOAT}, value(value){};
    OptionFloat(std::string name, float value, bool editable, bool visible) : Option{visible, editable, name, OptionType::FLOAT}, value(value){};

    void to_json(json &j) const override
    {
        Option::to_json(j);
        j["value"] = value;
    }
};

struct OptionString : Option
{
    std::string value;

    OptionString(std::string name, std::string value) : Option{true, true, name, OptionType::STRING}, value(value){};
    OptionString(std::string name, std::string value, bool editable, bool visible) : Option{visible, editable, name, OptionType::STRING}, value(value){};

    void to_json(json &j) const override
    {
        Option::to_json(j);
        j["value"] = value;
    }
};

template <typename T>
struct OptionChoice : Option
{
    std::vector<T> choices;
    T value;
    OptionType innerType;

    OptionChoice(std::string name, T value, std::vector<T> choices)
        : Option{true, true, name, OptionType::CHOICE}, value(value), choices(choices), innerType(inferOptionType<T>()){};
    OptionChoice(std::string name, T value, std::vector<T> choices, bool editable, bool visible)
        : Option{visible, editable, name, OptionType::CHOICE}, value(value), choices(choices), innerType(inferOptionType<T>()){};

    void to_json(json &j) const override
    {
        Option::to_json(j);
        j["value"] = value;
        j["choices"] = choices;
        j["innerType"] = to_string(innerType);
    }
};

template <typename T>
struct OptionRange : Option
{
    T min;
    T max;
    T value;
    OptionType innerType;

    OptionRange(std::string name, T value, T min, T max)
        : Option{true, true, name, OptionType::RANGE}, value(value), min(min), max(max), innerType(inferOptionType<T>()){};
    OptionRange(std::string name, T value, T min, T max, bool editable, bool visible)
        : Option{visible, editable, name, OptionType::RANGE}, value(value), min(min), max(max), innerType(inferOptionType<T>()){};

    void to_json(json &j) const override
    {
        Option::to_json(j);
        j["value"] = value;
        j["min"] = min;
        j["max"] = max;
        j["innerType"] = to_string(innerType);
    }
};