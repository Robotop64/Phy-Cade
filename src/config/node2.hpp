#pragma once

#include "logging.hpp"

#include <string>
#include <vector>
#include <any>
#include <memory>
#include <optional>

class Node2
{
public:
    enum type
    {
        Group,
        Leaf
    };

    Node2(const std::string name) : name(name), value(std::nullopt), node_type(Group), parent() {}
    Node2(const std::string name, const std::any value) : name(name), value(value), node_type(Leaf), parent() {}

    const Node2 *getParent();
    const std::vector<std::shared_ptr<Node2>> getChildren();

    void setValue(const std::any value);
    const std::optional<std::any> getValue();

    const std::string getName() { return name; };

    const std::shared_ptr<Node2> addChild(const Node2 child);

    void printTree();
    const std::string path();

private:
    Node2 *parent;
    std::vector<std::shared_ptr<Node2>> children;

    type node_type;
    std::string name;
    std::optional<std::any> value;
};