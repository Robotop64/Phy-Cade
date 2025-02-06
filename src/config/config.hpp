#include "toml.hpp"
#include <iostream>
#include <string>

#define Config toml::parse_result

void saveConfig(Config config);

Config getConfig();

namespace config
{
    enum type
    {
        User,
        Default
    };
    void save();
    Config load(type config);
}