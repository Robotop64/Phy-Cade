#pragma once

#include <string>
#include <vector>
#include <memory>
#include <type_traits>

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
};

struct OptionInt : Option
{
    int value;

    OptionInt(std::string name, int value) : Option{true, true, name, OptionType::INT}, value(value){};
    OptionInt(std::string name, int value, bool editable, bool visible) : Option{visible, editable, name, OptionType::INT}, value(value){};
};

struct OptionFloat : Option
{
    float value;

    OptionFloat(std::string name, float value) : Option{true, true, name, OptionType::FLOAT}, value(value){};
    OptionFloat(std::string name, float value, bool editable, bool visible) : Option{visible, editable, name, OptionType::FLOAT}, value(value){};
};

struct OptionString : Option
{
    std::string value;

    OptionString(std::string name, std::string value) : Option{true, true, name, OptionType::STRING}, value(value){};
    OptionString(std::string name, std::string value, bool editable, bool visible) : Option{visible, editable, name, OptionType::STRING}, value(value){};
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
};