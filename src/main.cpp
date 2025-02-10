#include "config.hpp"
#include <iostream>
#include <filesystem>

int main(void)
{
    // load config
    Config config = Config::instance();
    std::cout << "Hot swap enabled: " << config.get(Config::User, "Display.Basic.target_fps").value() << "\n";
    return 0;
}