#include "Node.hpp"

const Node *Node::getParent()
{
    return parent;
}

const std::vector<std::shared_ptr<Node>> Node::getChildren()
{
    return children;
}

void Node::setValue(const std::any value)
{
    this->value = value;
    node_type = Leaf;
}

const std::optional<std::any> Node::getValue()
{
    if (node_type == Group)
        return std::nullopt;
    return value;
}

const std::shared_ptr<Node> Node::addChild(const Node child)
{
    if (node_type == Leaf)
    {
        node_type = Group;
    }

    std::shared_ptr<Node> child_ptr = std::make_shared<Node>(child);

    children.push_back(child_ptr);
    child_ptr->parent = this;
    return child_ptr;
}

void Node::printTree()
{
    Log::msg("Node", "Name: {}, Parent: {}, Children: {}",
             name,
             parent != nullptr ? parent->name : "null",
             [&]()
             {
            std::string names = "[";
            for (const auto &child : children)
            {
                names += child->name + ", ";
            }
            names += "]";
            return names; }());

    for (const auto &child : children)
    {
        child->printTree();
    }
}

const std::string Node::path()
{
    std::string path = "";

    Node *next = this;

    while (next->parent != nullptr)
    {
        if (path.empty())
            path = next->name;
        else
            path = next->name + "." + path;

        next = next->parent;
    }

    return path;
}