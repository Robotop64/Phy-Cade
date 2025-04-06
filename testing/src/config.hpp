#pragma once

#include "node.hpp"

#include <fstream>

class Config
{
public:
    static NodePtr gen_default_config();

    static void save_config(const std::string &filename, const NodePtr &config)
    {
        json j;
        config->to_json(j);
        std::ofstream file(filename);
        file << j.dump(4);
        file.close();
    }
};