#include "Node.hpp"

int main(void)
{
    std::shared_ptr<Node> root = Node::createGroup("root");
    std::shared_ptr<Node> display = Node::createGroup("display");
    std::shared_ptr<Node> debug = Node::createGroup("debug");

    return 0;
};