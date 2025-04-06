#include "config.hpp"

#include <iostream>

int main(void)
{
    NodePtr def = Config::gen_default_config();

    Config::save_config("config.json", def);
    
    NodePtr par = Config::load_config("config.json");
    par->printTree(true);

    return 0;
};