#include "config.hpp"

#include <iostream>

NodePtr Config::gen_default_config()
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

void Config::save_config(const std::string &filename, const NodePtr &config)
{
    json j;
    config->to_json(j);
    std::ofstream file(filename);
    file << j.dump(4);
    file.close();
};

NodePtr Config::load_config(const std::string &filename)
{
    std::ifstream file(filename);
    std::string content((std::istreambuf_iterator<char>(file)), std::istreambuf_iterator<char>());
    json j = json::parse(content);

    return Node::from_json(j);
};