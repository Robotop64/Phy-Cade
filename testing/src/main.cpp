#include "Node.hpp"

int main(void)
{
    std::shared_ptr<Node> root = Node::createNodeGroup("root");

    std::shared_ptr<Node> display = Node::createNodeGroup("display");
    std::shared_ptr<Node> basic = Node::createOptionList("basic");
    std::shared_ptr<Node> advanced = Node::createNodeGroup("advanced");
    display->addNode(basic);
    display->addNode(advanced);

    std::shared_ptr<Node> secret = Node::createNodeGroup("secret");
    advanced->addNode(secret);

    std::shared_ptr<Node> audio = Node::createOptionList("audio");

    root->addNode(display);
    root->addNode(audio);

    std::shared_ptr<Option> master_volume = std::make_shared<OptionRange<int>>("master_volume", 0, 100, 50);
    audio->addOption(master_volume);


    root->printTree();

    std::shared_ptr<Node> secretX = root->at("display.advanced.secret");

    std::cout << "Path to secret: " << secretX->path() << "\n";
    
    return 0;
};