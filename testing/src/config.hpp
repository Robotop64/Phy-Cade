#pragma once

#include "node.hpp"

#include <fstream>
#include <map>

class Config
{
public:
    enum class ConfigOptions : uint8_t {};
    // Literal operator moved outside the class definition
    static constexpr Config::ConfigOptions config(std::string str)
    {
        return static_cast<Config::ConfigOptions>(std::hash<std::string>{}(str));
    }

    using ConfigMap = std::map<ConfigOptions, OptionPtr>;    

    static NodePtr gen_default_config();
    static ConfigMap gen_config_map(NodePtr &root);

    static void save_config(const std::string &filename, const NodePtr &config);

    static NodePtr load_config(const std::string &filename);   
};