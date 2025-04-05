#pragma once

#include "option.hpp"

#include <string>
#include <vector>
#include <memory>
#include <variant>
#include <stdexcept>

enum NodeType
{
    Group,
    OptionList
};

class Node : public std::enable_shared_from_this<Node>
{
public:
    
    static std::shared_ptr<Node> createGroup(const std::string name)
    {
        auto node = std::make_shared<Node>();
        node->type = NodeType::Group;
        node->children = std::make_pair(name, std::vector<std::shared_ptr<Node>>{});
        return node;
    }

    static std::shared_ptr<Node> createOptionList(const std::string name)
    {
        auto node = std::make_shared<Node>();
        node->type = NodeType::OptionList;
        node->children = std::vector<std::shared_ptr<Option>>{};
        return node;
    }

    void addChild(std::shared_ptr<Node> child)
    {
        if (type == NodeType::Group)
        {
            auto &childrenNodes = std::get<std::pair<std::string, std::vector<std::shared_ptr<Node>>>>(children).second;

            childrenNodes.push_back(child);
            child->parent = shared_from_this();
        } 
        else 
        {
            throw std::runtime_error("Cannot add child to OptionList node.");
        }
    }

private:
    std::weak_ptr<Node> parent;
    NodeType type;

    std::variant<
        std::pair<
            std::string, 
            std::vector<std::shared_ptr<Node>>
        >,
        std::vector<std::shared_ptr<Option>>
    > children;
};

int main(void)
{
    std::shared_ptr<Node> root = Node::createGroup("root");
    std::shared_ptr<Node> display = Node::createGroup("display");
    std::shared_ptr<Node> debug = Node::createGroup("debug");

    return 0;
};