#include "Node.hpp"

std::shared_ptr<Node> Node::getParent() const { return parent.lock(); }

const std::vector<std::shared_ptr<Node>> &Node::getChildren() const { return children; }

void Node::setValue(const std::any &value)
{
    this->value = value;
    node_type = Leaf;
}

std::optional<std::any> Node::getValue() const
{
    if (node_type == Group)
        return std::nullopt;
    return value;
}

std::shared_ptr<Node> Node::addChild(std::shared_ptr<Node> child)
{
    if (node_type == Leaf)
    {
        node_type = Group;
    }

    child->parent = shared_from_this();
    children.push_back(child);
    return child;
}

void Node::printTree() const
{
    Log::msg("Node", "Name: {}, Parent: {}, Children: {}",
             name,
             parent.lock() ? parent.lock()->name : "null",
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

const std::string Node::path() const
{
    if (auto next = parent.lock())
    {
        return next->path() + "." + name;
    }

    return name;
}