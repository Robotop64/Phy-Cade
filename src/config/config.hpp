#include "toml.hpp"
#include <iostream>

static int instance;

class Config
{
    auto instance;
};

auto loadConfig()
{
    try 
    {
        return toml::parse_file("userdata/config.toml");
    }
    catch (const std::exception& e)
    {
        std::cout << "User configuration not found\n";
        std::cout << "Using default\n";
        return toml::parse("resources/default.toml");
    };
};