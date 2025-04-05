#pragma once

#include "option.hpp"
#include "logging.hpp"

#include <string>
#include <vector>
#include <memory>
#include <variant>
#include <cassert>
#include <iostream>

class Node;
using NodeList = std::vector<std::shared_ptr<Node>>;
using OptionList = std::vector<std::shared_ptr<Option>>;

enum NodeType
{
    Group,
    Leaf
};

class Node : public std::enable_shared_from_this<Node>
{
public:
    static std::shared_ptr<Node> createNodeGroup(const std::string name)
    {
        auto node = std::make_shared<Node>();
        node->type = NodeType::Group;
        node->name = name;
        node->children = NodeList{};
        return node;
    }

    static std::shared_ptr<Node> createOptionList(const std::string name)
    {
        auto node = std::make_shared<Node>();
        node->type = NodeType::Leaf;
        node->name = name;
        node->children = OptionList{};
        return node;
    }

    void addNode(std::shared_ptr<Node> child)
    {
        assert(type == NodeType::Group && "Cannot add node to a Leaf node.");
        
        NodeList &childrenNodes = std::get<NodeList>(children);

        childrenNodes.push_back(child);
        child->parent = shared_from_this();
    }

    const NodeList &getChildren() const
    {
        assert(type == NodeType::Group && "Cannot get children from a Leaf node.");
        
        return std::get<NodeList>(children);
    }

    const OptionList &getOptions() const
    {
        assert(type == NodeType::Leaf && "Cannot get options from a Group node.");
        
        return std::get<OptionList>(children);
    }

    void addOption(std::shared_ptr<Option> option)
    {
        assert(type == NodeType::Leaf && "Cannot add option to a Group node.");
        
        auto &childrenOptions = std::get<OptionList>(children);
        childrenOptions.push_back(option);
    }

    void printTree()
    {
        Log::msg("Node", "Name: {}, Parent: {}, Children: {}",
            name,
            parent.lock() ? parent.lock()->name : "null",
            [&]()
            {
                std::string names = "[";
                if (type == NodeType::Group)
                {
                    for (const auto &child : std::get<NodeList>(children))
                    {
                        names += child->name + ", ";
                    }
                }
                else if (type == NodeType::Leaf)
                {
                    for (const auto &option : std::get<OptionList>(children))
                    {
                        names += option->name + ", ";
                    }
                }
                names += "]";
                return names;
            }());

        if (type == NodeType::Leaf) return;

        for (const auto &child : std::get<NodeList>(children))
        {
            child->printTree();
        }
    }

    const std::string path() const
    {
        if (auto next = parent.lock())
        {
            if (next->name == "root")
                return name;

            return next->path() + "." + name;
        }

        return name;
    }

    std::shared_ptr<Node> getChild(const std::string &childname)
    {
        assert(type == NodeType::Group && "Cannot get child from a Leaf node.");
        
        for (const auto &child : getChildren())
        {
            if (child->name == childname) return child;
        }

        throw std::runtime_error("Node:" + childname + " not found in: " + name);
    }

    std::shared_ptr<Option> getOption(const std::string &optionname)
    {
        assert(type == NodeType::Leaf && "Cannot get option from a Group node.");

        for (const auto &option : getOptions())
        {
            if (option->name == optionname) return option;
        }

        throw std::runtime_error("Option:" + optionname + " not found in: " + name);
    }

    std::shared_ptr<Node> at(const std::string &path)
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

private:
    std::weak_ptr<Node> parent;
    NodeType type;
    std::string name;

    std::variant<
        NodeList,
        OptionList
    > children;
};