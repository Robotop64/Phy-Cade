#include "config.hpp"
#include "logging.hpp"

Config::Config()
{
    Log::msg("Config", "Initializing.");

    defaultConfig = toml::parse_file("resources\\default.toml");

    try
    {
        userConfig = toml::parse_file("userdata\\config.toml");
        Log::msg("Config", "User-config loaded.");
    }
    catch (const std::exception &e)
    {
        Log::msg("Config", "Err: User-config not found!");
        Log::msg("Config", "     Using defaults.");
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

result Config::get(const Config::type type, const std::string key)
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
                Log::msg("Config", "Err: Config not found or invalid value in user-config!");
                Log::msg("Config", "Config: {}", key);
                Log::msg("Config", "     Using default!");
                return defaultConfig.at_path(key);
            }
        }

        try
        {
            return userConfig.at_path(key);
        }
        catch (const std::exception &e)
        {
            Log::msg("Config", "Err: Config not found or invalid value in user-config!");
            Log::msg("Config", "Config: {}", key);
            Log::msg("Config", "     Using default!");
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
    Log::msg("Config", "User-config saved.");
};

void Config::parseTree(Node &node, const toml::v3::table table)
{
    for (auto &&[k, v] : table)
    {
        if (v.is_table())
        {
            Node *child = node.addChild(Node(k.str().data())); // move child to parent
            parseTree(*child, *v.as_table());                  // parse tree content into child
        }
        else
        {
            std::any value = "ERROR";

            result node_res = Config::instance().get(Config::User, node.path() + "." + k.str().data());

            if (node_res.is_boolean())
            {
                value = node_res.value<bool>().value();
            }
            else if (node_res.is_integer())
            {
                value = node_res.value<int>().value();
            }
            else if (node_res.is_floating_point())
            {
                value = node_res.value<float>().value();
            }
            else if (node_res.is_string())
            {
                value = node_res.value<std::string>().value();
            }

            node.addChild(Node(k.str().data(), value));
        }
    }
};

Node Config::parseTree()
{
    Node root = Node("root");
    toml::v3::table source = *userConfig.as_table();

    parseTree(root, source);
    return root;
};