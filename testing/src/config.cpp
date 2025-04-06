#include "config.hpp"

NodePtr Config::gen_default_config(){
    NodePtr root = Node::createNodeGroup("root");

    root->addNode(Node::createNodeGroup("display"));
    root->at("display")->addNode(Node::createOptionList("general"));
    root->at("display.general")->addOption(std::make_shared<OptionBool>("fullscreen", false));
    root->at("display.general")->addOption(std::make_shared<OptionBool>("borderless", false));
    root->at("display.general")->addOption(std::make_shared<OptionRange<int>>("fps", 60, 25, 165));
    root->at("display.general")->addOption(std::make_shared<OptionBool>("show_fps", false));
    root->at("display.general")->addOption(std::make_shared<OptionChoice<std::string>>("resolution", "1920x1080", std::vector<std::string>{"1920x1080", "1280x720", "1024x768", "800x600", "640x480"}));
    root->at("display")->addNode(Node::createOptionList("advanced"));
    root->at("display.advanced")->addOption(std::make_shared<OptionBool>("anti_aliasing", true));

    root->addNode(Node::createNodeGroup("debug"));
    root->at("debug")->addNode(Node::createOptionList("general"));
    root->at("debug.general")->addOption(std::make_shared<OptionBool>("enabled", false));
    root->at("debug.general")->addOption(std::make_shared<OptionBool>("logging", false));
    root->at("debug.general")->addOption(std::make_shared<OptionBool>("display", false));
    root->at("debug")->addNode(Node::createOptionList("gameplay"));
    
    root->addNode(Node::createOptionList("audio"));

    return root;
};