#include "config.hpp"
#include <iostream>
#include <filesystem>

int main(void)
{
    // load config
    Config config = getConfig();
    std::cout << config["Display"]["target_fps"] << "\n";
    // manage screens

    return 0;
}