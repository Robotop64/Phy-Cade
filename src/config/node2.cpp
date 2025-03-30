#include "node2.hpp"

const Node2 *Node2::getParent()
{
    return parent;
}

const std::vector<std::shared_ptr<Node2>> Node2::getChildren()
{
    return children;
}

void Node2::setValue(const std::any value)
{
    this->value = value;
    node_type = Leaf;
}

const std::optional<std::any> Node2::getValue()
{
    if (node_type == Group)
        return std::nullopt;
    return value;
}

const std::shared_ptr<Node2> Node2::addChild(const Node2 child)
{
    if (node_type == Leaf)
    {
        node_type = Group;
    }

    std::shared_ptr<Node2> child_ptr = std::make_shared<Node2>(child);

    children.push_back(child_ptr);
    child_ptr->parent = this;
    return child_ptr;
}

void Node2::printTree()
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

const std::string Node2::path()
{
    std::string path = "";

    Node2 *next = this;

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