#include "config.hpp"

// void saveConfig(Config config)
// {
//     std::ofstream file("userdata\\config.toml");
//     file << config;
//     file.close();
//     std::cout << "User-config saved!\n";
// };

// Config getConfig()
// {
//     try
//     {
//         std::cout << "Loading user-config.\n";
//         Config config = toml::parse_file("userdata\\config.toml");
//         std::cout << "User-config loaded!\n";
//         return config;
//     }
//     catch (const std::exception &e)
//     {
//         std::cout << "User-config not found.\n";
//         std::cout << "Using default-config.\n";
//         Config defaultConfig = toml::parse_file("resources\\default.toml");
//         saveConfig(defaultConfig);
//         return getConfig();
//     };
// };

Config::Config()
{
    std::cout << "Initializing config.\n";

    defaultConfig = toml::parse_file("resources\\default.toml");

    try
    {
        userConfig = toml::parse_file("userdata\\config.toml");
        std::cout << "User-config loaded!\n";
    }
    catch (const std::exception &e)
    {
        std::cout << "Err: User-config not found!\n";
        std::cout << "     Using defaults!\n";
        userConfig = defaultConfig;
        save();
    };
    hot_swap_enabled = userConfig.at_path("Debug.hot_swap_enabled").value_or(false);
};

Config &Config::instance()
{
    static Config instance = Config();
    return instance;
}

result Config::get(Config::type type, std::string key)
{
    if (type == User)
    {
        if (hot_swap_enabled)
        {
            try
            {
                native temp_config = toml::parse_file("userdata\\config.toml");
                return temp_config.at_path(key);
            }
            catch (const std::exception &e)
            {
                std::cout << "Err: Config not found or invalid value in user-config!\n";
                std::cout << "Config: " << key << "\n";
                std::cout << "Using default!\n";
                return defaultConfig.at_path(key);
            }
        }

        try
        {
            return userConfig.at_path(key);
        }
        catch (const std::exception &e)
        {
            std::cout << "Err: Config not found or invalid value in user-config!\n";
            std::cout << "Config: " << key << "\n";
            std::cout << "Using default!\n";
            return defaultConfig.at_path(key);
        }
    }
    else
    {
        return defaultConfig.at_path(key);
    };
};

void Config::save()
{
    std::ofstream file("userdata\\config.toml");
    file << userConfig;
    file.close();
    std::cout << "User-config saved!\n";
};