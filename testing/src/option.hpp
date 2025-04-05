#pragma once

#include <string>
#include <vector>

struct Option
{
    bool visible;
    bool editable;
    std::string name;
};

struct OptionBool : Option
{
    bool value;

    OptionBool(std::string name, bool value) : Option{true, true, name}, value(value){};
    OptionBool(std::string name, bool value, bool editable, bool visible) : Option{visible, editable, name}, value(value){};
};

struct OptionInt : Option
{
    int value;

    OptionInt(std::string name, int value) : Option{true, true, name}, value(value){};
    OptionInt(std::string name, int value, bool editable, bool visible) : Option{visible, editable, name}, value(value){};
};

struct OptionFloat : Option
{
    float value;

    OptionFloat(std::string name, float value) : Option{true, true, name}, value(value){};
    OptionFloat(std::string name, float value, bool editable, bool visible) : Option{visible, editable, name}, value(value){};
};

struct OptionString : Option
{
    std::string value;

    OptionString(std::string name, std::string value) : Option{true, true, name}, value(value){};
    OptionString(std::string name, std::string value, bool editable, bool visible) : Option{visible, editable, name}, value(value){};
};

template <typename T>
struct OptionChoice : Option
{
    std::vector<T> choices;
    T value;

    OptionChoice(std::string name, T value, std::vector<T> choices) : Option{true, true, name}, value(value), choices(choices){};
    OptionChoice(std::string name, T value, std::vector<T> choices, bool editable, bool visible) : Option{visible, editable, name}, value(value), choices(choices){};
};

template <typename T>
struct OptionRange : Option
{
    T min;
    T max;
    T value;

    OptionRange(std::string name, T value, T min, T max) : Option{true, true, name}, value(value), min(min), max(max){};
    OptionRange(std::string name, T value, T min, T max, bool editable, bool visible) : Option{visible, editable, name}, value(value), min(min), max(max){};
};