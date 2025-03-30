#pragma once

#include "logging.hpp"

#include <string>
#include <vector>
#include <any>
#include <memory>
#include <optional>

class Node
{
public:
    enum type
    {
        Group,
        Leaf
    };

    Node(const std::string name) : name(name), value(std::nullopt), node_type(Group), parent() {}
    Node(const std::string name, const std::any value) : name(name), value(value), node_type(Leaf), parent() {}

    const Node *getParent();
    const std::vector<std::shared_ptr<Node>> getChildren();

    void setValue(const std::any value);
    const std::optional<std::any> getValue();

    const std::string getName() { return name; };
    const type getType() { return node_type; };

    const std::shared_ptr<Node> addChild(const Node child);

    void printTree();
    const std::string path();

private:
    Node *parent;
    std::vector<std::shared_ptr<Node>> children;

    type node_type;
    std::string name;
    std::optional<std::any> value;
};