#include "config.hpp"
#include <iostream>
#include <filesystem>

int main(void)
{
    // load config
    Config config = getConfig();
    std::cout << "Config:\n"
              << config << "\n";
    // manage screens

    return 0;
}