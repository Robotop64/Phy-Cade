#pragma once

#include "json.hpp"

#include "logging.hpp"

#include <string>
#include <vector>
#include <memory>
#include <type_traits>

using json = nlohmann::ordered_json;

#pragma region OptionType

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

namespace
{
    std::string to_string(const OptionType type)
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

    OptionType from_string(const std::string &str)
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

    // infer type to OptionType
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
}

#pragma endregion OptionType

struct Option
{
    std::string name;
    bool visible;
    bool editable;
    OptionType type = OptionType::BASE;

    Option(std::string name, bool visible = true, bool editable = true,
           OptionType type = OptionType::BASE)
        : name(std::move(name)), visible(visible), editable(editable), type(type) {};

    ~Option() = default;

    virtual void to_json(json &j) const;

    static std::shared_ptr<Option> from_json(const json &j);

    template <typename T>
    static std::shared_ptr<T> as(std::shared_ptr<Option> option)
    {
        return std::dynamic_pointer_cast<T>(option);
    }
};

#pragma region Derivative Classes

template <typename T>
struct OptionExtended : Option
{
    T value;

    OptionExtended(std::string name, T value, bool visible = true, bool editable = true,
                   OptionType type = inferOptionType<T>())
        : Option(std::move(name), visible, editable, type), value(value) {};

    void to_json(json &j) const override
    {
        Option::to_json(j);
        j["value"] = value;
    }
};

template <typename T>
struct OptionChoice : OptionExtended<T>
{
    std::vector<T> choices;
    OptionType innerType;

    OptionChoice(std::string name, T value, std::vector<T> choices, bool visible = true, bool editable = true)
        : OptionExtended<T>(std::move(name), value, visible, editable), choices(std::move(choices)), innerType(inferOptionType<T>())
    {
        this->type = OptionType::CHOICE;
    };

    void to_json(json &j) const override
    {
        OptionExtended<T>::to_json(j);
        j["innerType"] = to_string(innerType);
        j["choices"] = choices;
    }
};

template <typename T>
struct OptionRange : OptionExtended<T>
{
    T min;
    T max;
    OptionType innerType;

    OptionRange(std::string name, T value, T min, T max, bool visible = true, bool editable = true)
        : OptionExtended<T>(std::move(name), value, visible, editable), min(min), max(max), innerType(inferOptionType<T>())
    {
        this->type = OptionType::RANGE;
    };

    void to_json(json &j) const override
    {
        OptionExtended<T>::to_json(j);
        j["innerType"] = to_string(innerType);
        j["min"] = min;
        j["max"] = max;
    }
};

#pragma endregion Derivative Classes