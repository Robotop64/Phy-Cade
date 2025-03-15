#pragma once

#include <any>
#include <vector>
#include <optional>
#include <map>
#include <string>

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
        
    std::map<std::string, Node> children;

    type node_type;
    std::string name;
    std::optional<std::any> value;

    std::optional<std::vector<std::string>> getChildrenNames()
    {
        if (node_type == Leaf) return std::nullopt;

        std::vector<std::string> names = std::vector<std::string>(children.size());

        //map names to vector and assign with index
        int i = 0;
        for (auto it = children.begin(); it != children.end(); it++, i++)
        {
            names[i] = it->first;
        }
    };
    
    std::optional<std::any> getValue()
    {
        if (node_type == Group) return std::nullopt;
        return value;
    };
private:
    type setType(std::any value)
    {
        if (value.has_value()) return Leaf;
        return Group;
    };
};