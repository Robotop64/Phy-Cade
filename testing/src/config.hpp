#pragma once

#include "node.hpp"

#include <fstream>

class Config
{
public:
    static NodePtr gen_default_config();

    static void save_config(const std::string &filename, const NodePtr &config);

    static NodePtr load_config(const std::string &filename);
};