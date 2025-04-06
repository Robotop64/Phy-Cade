#include "config.hpp"

#include <iostream>

int main(void)
{
    NodePtr root = Config::gen_default_config();

    Config::save_config("config.json", root);
    
    return 0;
};