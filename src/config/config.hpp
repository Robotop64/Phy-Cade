#pragma once

#include "toml.hpp"

#include "node.hpp"

#include <string>

using native = toml::parse_result;
using result = toml::node_view<toml::node>;

class Config
{
public:
    enum type
    {
        User,
        Default
    };
    static Config &instance();

    ~Config() {
        // save();
    };

    result get(const Config::type type, const std::string key);

    void save();

    Node parseTree();

private:
    Config();
    native userConfig;
    native defaultConfig;

    void parseTree(Node &node, const toml::v3::table table);

    bool hot_swap_enabled = false;
};