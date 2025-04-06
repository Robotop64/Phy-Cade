#include "config.hpp"

#include <iostream>

int main(void)
{
    NodePtr root = Config::gen_default_config();

    root->printTree();
    
    return 0;
};