#include "Node.hpp"

#include <iostream>

int main(void)
{
    using NodePtr = std::shared_ptr<Node>;
    using OptionPtr = std::shared_ptr<Option>;

    NodePtr root = Node::createNodeGroup("root");

    NodePtr display = Node::createNodeGroup("display");
    NodePtr basic = Node::createOptionList("basic");
    NodePtr advanced = Node::createNodeGroup("advanced");
    display->addNode(basic);
    display->addNode(advanced);

    NodePtr secret = Node::createNodeGroup("secret");
    advanced->addNode(secret);

    NodePtr audio = Node::createOptionList("audio");

    root->addNode(display);
    root->addNode(audio);

    OptionPtr master_volume = std::make_shared<OptionRange<int>>("master_volume", 50, 0, 100);
    audio->addOption(master_volume);
    OptionPtr music_volume = std::make_shared<OptionRange<int>>("music_volume", 50, 0, 100);
    audio->addOption(music_volume);
    OptionPtr sfx_volume = std::make_shared<OptionRange<int>>("sfx_volume", 69, 0, 100);
    audio->addOption(sfx_volume);

    root->printTree();

    //print sfx volume value
    auto sfx_volume_option_range = Option::to<OptionRange<int>>(root->at("audio")->getOption("sfx_volume"));

    std::cout << "sfx_volume: " << sfx_volume_option_range->value << "\n";
    
    return 0;
};