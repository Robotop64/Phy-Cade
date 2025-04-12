#include "config.hpp"
#include "logging.hpp"

#include <iostream>
#include <fstream>

NodePtr ConfigUtil::gen_default()
{
    NodePtr root = Node::createNodeGroup("root");

    root->addNode(Node::createNodeGroup("display"));
    root->at("display")->addNode(Node::createOptionList("general"));
    root->at("display.general")->addOption(std::make_shared<OptionExtended<bool>>("fullscreen", false));
    root->at("display.general")->addOption(std::make_shared<OptionExtended<bool>>("borderless", false));
    root->at("display.general")->addOption(std::make_shared<OptionRange<int>>("fps", 60, 25, 165));
    root->at("display.general")->addOption(std::make_shared<OptionExtended<bool>>("show_fps", false));
    root->at("display.general")->addOption(std::make_shared<OptionChoice<std::string>>("resolution", "1920x1080", std::vector<std::string>{"1920x1080", "1280x720", "1024x768", "800x600", "640x480"}));
    root->at("display")->addNode(Node::createOptionList("advanced"));
    root->at("display.advanced")->addOption(std::make_shared<OptionExtended<bool>>("anti_aliasing", true));

    root->addNode(Node::createNodeGroup("debug"));
    root->at("debug")->addNode(Node::createOptionList("general"));
    root->at("debug.general")->addOption(std::make_shared<OptionExtended<bool>>("enabled", false));
    root->at("debug.general")->addOption(std::make_shared<OptionExtended<bool>>("logging", false));
    root->at("debug.general")->addOption(std::make_shared<OptionExtended<bool>>("display", false));
    root->at("debug")->addNode(Node::createOptionList("gameplay"));

    root->addNode(Node::createOptionList("audio"));

    return root;
};

void ConfigUtil::save(const std::string &filename, const NodePtr &config)
{
    json j;
    config->to_json(j);
    std::ofstream file(filename);
    file << j.dump(4);
    file.close();
};

NodePtr ConfigUtil::load(const std::string &filename)
{
    std::ifstream file(filename);
    std::string content((std::istreambuf_iterator<char>(file)), std::istreambuf_iterator<char>());
    json j = json::parse(content);

    return Node::from_json(j);
};

void Config::load(const std::string &filename)
{
    try
    {
        user_tree = ConfigUtil::load(filename);
    }
    catch (const std::exception &e)
    {
        user_tree = ConfigUtil::gen_default();
        Log::newline();
        Log::msg("Config", "Failed to load config file: {}", e.what());
        Log::bullet("Using default config.");
    }

    user_map = Map(user_tree.value());
};

void Config::save(const std::string &filename) const
{
    if (user_tree.has_value())
    {
        ConfigUtil::save(filename, user_tree.value());
    }
    else
    {
        ConfigUtil::save(filename, default_tree);
    }
};

void Config::init()
{
    Log::updated("Config", "Initializing...");

    load("userData\\config.json");

    Log::updated("Config", "Initialized!\n");
}

Config::Map &Config::map()
{
    return user_map.value();
};

NodePtr &Config::tree()
{
    return user_tree.value();
};

size_t Config::Map::hash(const std::string &key)
{
    return std::hash<std::string>{}(key) % 1000000;
};

Config::Map::Map(const NodePtr &root)
{
    map = std::map<size_t, OptionPtr>();
    std::vector<NodePtr> stack = {root};

    while (!stack.empty())
    {
        NodePtr current = stack.back();
        stack.pop_back();

        if (current->getType() == NodeType::Group)
        {
            for (const auto &child : current->getChildren())
            {
                stack.push_back(child);
            }
        }
        else if (current->getType() == NodeType::Leaf)
        {
            for (const auto &option : current->getOptions())
            {
                map[hash(current->path() + "-" + option->name)] = option;
            }
        }
    }

    Config::Map::validate();
};

std::optional<OptionPtr> Config::Map::get(const std::string &key) const
{
    auto it = map.find(hash(key));
    if (it != map.end())
    {
        return it->second;
    }
    Log::msg("Config", "Option: {} not found in map!", key);
    return std::nullopt;
};

void Config::Map::print() const
{
    for (const auto &[key, option] : map)
    {
        std::cout << "Key: " << key << ", Option: " << option->name << std::endl;
    }
};

void Config::Map::validate() const
{
    // check for key duplicates
    std::map<size_t, int> key_count;
    for (const auto &[key, option] : map)
    {
        key_count[key]++;
    }
    bool duplicates = false;
    for (const auto &[key, count] : key_count)
    {
        if (count > 1)
        {
            Log::msg("Config", "Key: {} has {} duplicates!", key, count);
            duplicates = true;
        }
    }
    if (duplicates)
    {
        Log::msg("Config", "Config map has duplicates!");
    }
};