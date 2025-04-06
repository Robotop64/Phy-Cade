#include "node.hpp"
#include "logging.hpp"

#include <cassert>

std::shared_ptr<Node> Node::createNodeGroup(const std::string name)
{
    auto node = std::make_shared<Node>();
    node->type = NodeType::Group;
    node->name = name;
    node->children = NodeList{};
    return node;
}

std::shared_ptr<Node> Node::createOptionList(const std::string name)
{
    auto node = std::make_shared<Node>();
    node->type = NodeType::Leaf;
    node->name = name;
    node->children = OptionList{};
    return node;
}

void Node::addNode(std::shared_ptr<Node> child)
{
    assert(type == NodeType::Group && "Cannot add node to a Leaf node.");

    NodeList &childrenNodes = std::get<NodeList>(children);

    childrenNodes.push_back(child);
    child->parent = shared_from_this();
}

void Node::addOption(std::shared_ptr<Option> option)
{
    assert(type == NodeType::Leaf && "Cannot add option to a Group node.");

    auto &childrenOptions = std::get<OptionList>(children);
    childrenOptions.push_back(option);
}

const NodeList &Node::getChildren() const
{
    assert(type == NodeType::Group && "Cannot get children from a Leaf node.");

    return std::get<NodeList>(children);
}

const OptionList &Node::getOptions() const
{
    assert(type == NodeType::Leaf && "Cannot get options from a Group node.");

    return std::get<OptionList>(children);
}

void Node::printTree(bool path)
{
    std::string children = [&]()
    {
        std::string names = "[";
        if (type == NodeType::Group)
        {
            for (const auto &child : getChildren())
            {
                names += child->name;
                if (child != getChildren().back())
                {
                    names += ", ";
                }
            }
        }
        else if (type == NodeType::Leaf)
        {
            for (const auto &option : getOptions())
            {
                names += option->name;
                if (option != getOptions().back())
                {
                    names += ", ";
                }
            }
        }
        names += "]";
        return names;
    }();

    if (path)
    {
        Log::msg("Node", "Path: {}, Children: {}", Node::path(), children);
    }
    else
    {
        Log::msg(
            "Node", "Name: {}, Parent: {}, Children: {}",
            name,
            parent.lock() ? parent.lock()->name : "null",
            children);
    }

    if (type == NodeType::Leaf)
        return;

    for (const auto &child : getChildren())
    {
        child->printTree(path);
    }
}

const std::string Node::path() const
{
    if (auto next = parent.lock())
    {
        if (next->name == "root")
            return name;

        return next->path() + "." + name;
    }

    return name;
}

std::shared_ptr<Node> Node::getChild(const std::string &childname)
{
    assert(type == NodeType::Group && "Cannot get child from a Leaf node.");

    for (const auto &child : getChildren())
    {
        if (child->name == childname)
            return child;
    }

    throw std::runtime_error("Node:" + childname + " not found in: " + name);
}

std::shared_ptr<Option> Node::getOption(const std::string &optionname)
{
    assert(type == NodeType::Leaf && "Cannot get option from a Group node.");

    for (const auto &option : getOptions())
    {
        if (option->name == optionname)
            return option;
    }

    throw std::runtime_error("Option:" + optionname + " not found in: " + name);
}

std::shared_ptr<Node> Node::at(const std::string &path)
{
    std::string currentPath = path;
    std::string delimiter = ".";
    size_t pos = 0;

    std::shared_ptr<Node> currentNode = shared_from_this();

    while ((pos = currentPath.find(delimiter)) != std::string::npos)
    {
        std::string token = currentPath.substr(0, pos);
        currentNode = currentNode->getChild(token);
        currentPath.erase(0, pos + delimiter.length());
    }

    return currentNode->getChild(currentPath);
}

void Node::to_json(json &j) const

{
    j = json{};
    j["name"] = name;
    j["type"] = (type == NodeType::Group) ? "group" : "leaf";

    j["children"] = json::array();

    if (type == NodeType::Group)
    {
        for (const auto &child : getChildren())
        {
            json childJson;
            child->to_json(childJson);
            j["children"].push_back(childJson);
        }
    }
    else if (type == NodeType::Leaf)
    {
        for (const auto &option : getOptions())
        {
            json optionJson;
            option->to_json(optionJson);
            j["children"].push_back(optionJson);
        }
    }
};

std::shared_ptr<Node> Node::from_json(const json &j)
{
    std::shared_ptr<Node> node = [&]()
    {
        assert(j.contains("name") && "Node name is missing in JSON.");
        assert(j.contains("type") && "Node type is missing in JSON.");
        assert(j["type"] == "group" || j["type"] == "leaf" && "Invalid node type.");

        if (j["type"] == "group")
            return createNodeGroup(j["name"]);
        else if (j["type"] == "leaf")
            return createOptionList(j["name"]);

        throw std::runtime_error("Invalid node type in JSON");
    }();

    if (node->type == NodeType::Group)
    {
        for (const auto &child : j["children"])
        {
            node->addNode(Node::from_json(child));
        }
    }
    else if (node->type == NodeType::Leaf)
    {
        for (const auto &option : j["children"])
        {
            node->addOption(std::make_shared<Option>(Option::from_json(option)));
        }
    }

    return node;
}