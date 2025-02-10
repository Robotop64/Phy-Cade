#include "toml.hpp"
#include <iostream>
#include <string>

using native = toml::parse_result;
using result = std::optional<toml::node_view<toml::node>>;

class Config
{
public:
    enum type
    {
        User,
        Default
    };
    static Config &instance();
    result get(Config::type type, std::string key);

    void save();

    ~Config()
    {
        save();
    };

private:
    Config();

    native defaultConfig;
    native userConfig;

    bool hot_swap_enabled = false;
};