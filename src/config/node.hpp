#pragma once

#include <any>
#include <vector>
#include <optional>

class Node
{
public:
    enum type
    {
        Group,
        Leaf
    };

    Node(std::string name, std::any value = std::nullopt)
        : name(name), value(value), node_type(setType(value)) {}

    std::vector<Node> children;

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

    void addChild(Node child)
    {
        children.push_back(child);
        if (node_type == Leaf)
            node_type = Group;
    };

private:
    type setType(std::any value)
    {
        if (value.has_value())
            return Leaf;
        return Group;
    };
};