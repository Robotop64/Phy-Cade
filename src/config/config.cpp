#include "config.hpp"

void saveConfig(Config config)
{
    std::ofstream file("userdata\\config.toml");
    file << config;
    file.close();
    std::cout << "User-config saved!\n";
};

Config getConfig()
{
    try 
    {
        std::cout << "Loading user-config.\n";
        Config config = toml::parse_file("userdata\\config.toml");
        std::cout << "User-config loaded!\n";
        return config;
    }
    catch (const std::exception& e)
    {
        std::cout << "User-config not found.\n";
        std::cout << "Using default-config.\n";
        Config defaultConfig = toml::parse_file("resources\\default.toml");
        saveConfig(defaultConfig);
        return getConfig();
    };
};