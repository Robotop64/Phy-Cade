#pragma once

#include "logging.hpp"

#include <string>
#include <vector>
#include <any>
#include <memory>
#include <optional>

#include <iostream>
#include <vector>
#include <memory>
#include <any>
#include <optional>

class Node : public std::enable_shared_from_this<Node>
{
public:
    enum Type
    {
        Group,
        Leaf
    };

    Node(const std::string &name) : name(name), value(std::nullopt), node_type(Group) {}
    Node(const std::string &name, const std::any &value) : name(name), value(value), node_type(Leaf) {}

    std::shared_ptr<Node> getParent() const;
    const std::vector<std::shared_ptr<Node>> &getChildren() const;

    void setValue(const std::any &value);
    std::optional<std::any> getValue() const;

    std::string getName() const { return name; }
    Type getType() const { return node_type; }

    std::shared_ptr<Node> addChild(std::shared_ptr<Node> child);

    void printTree() const;

    const std::string path() const;

private:
    std::weak_ptr<Node> parent;
    std::vector<std::shared_ptr<Node>> children;

    Type node_type;
    std::string name;
    std::optional<std::any> value;
};
