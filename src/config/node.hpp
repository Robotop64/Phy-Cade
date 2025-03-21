#pragma once

#include "logging.hpp"

#include <any>
#include <vector>
#include <optional>
#include <string>
#include <numeric>

class Node
{
public:
    enum type
    {
        Group,
        Leaf
    };

    Node(std::string name) 
        : name(std::move(name)), value(std::nullopt), node_type(Group), parent(nullptr) {}
    Node(std::string name, std::any value)
        : name(std::move(name)), value(std::move(value)), node_type(setType(value)), parent(nullptr) {}
    Node(std::string name, std::any value, Node *parent)
        : name(std::move(name)), value(std::move(value)), node_type(setType(value)), parent(parent) {}

    std::vector<Node> children;
    Node *parent;

    type node_type;
    std::string name;
    std::optional<std::any> value;

    std::optional<std::vector<std::string>> getChildrenNames()
    {
        if (node_type == Leaf)
            return std::nullopt;

        std::vector<std::string> names = std::vector<std::string>(children.size());

        for (int i = 0; i < children.size(); i++)
        {
            names[i] = children[i].name;
        }

        return names;
    };

    std::optional<std::any> getValue()
    {
        if (node_type == Group)
            return std::nullopt;
        return value;
    };

    Node *addChild(Node child)
    {
        if (node_type == Leaf)
        {
            node_type = Group;
            // Log::msg("Node", "Converted Node: {} to Group", name);
        }

        children.push_back(std::move(child));
        Node *new_child = &children.back();
        new_child->parent = this;

        // Log::msg("Node", "Added Child: {} with parent {}", new_child->name, new_child->parent->name);
        return new_child;
    };

    void printTree()
    {
        Log::msg("Node", "Name: {}, Parent: {}, Children: {}",
                 name,
                 parent != nullptr ? parent->name : "undefined",
                 [&]()
                 {
                     std::string children = "{";
                     for (auto child : this->children)
                     {
                         children += child.name + ", ";
                     }
                     children += "}";
                     return children;
                 }());

        for (auto child : children)
        {
            // Log::msg("Node", "Observe Child: {}", child.name);
            if (child.node_type == Group)
            {
                // Log::msg("Node", "Attempt Print on Node: {}", child.name);
                child.printTree();
            }
        }
    };

    std::string path()
    {
        std::string path = "";

        Node* next = this;
        
        while (next->parent != nullptr)
        {
            if (path.empty()) 
                path = next->name;
            else
                path = next->name + "." + path;
            
            next = next->parent;
        }

        return path;
    };

    void setValue(std::any value)
    {
        this->value = value;
        node_type = Leaf;
    };

private:
    type setType(std::any value)
    {
        if (value.has_value())
            return Leaf;
        return Group;
    };
};