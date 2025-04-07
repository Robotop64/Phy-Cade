#pragma once

#include "json.hpp"

#include "option.hpp"

#include <string>
#include <vector>
#include <memory>
#include <variant>

using json = nlohmann::ordered_json;

class Node;
using NodeList = std::vector<std::shared_ptr<Node>>;
using NodePtr = std::shared_ptr<Node>;

using OptionList = std::vector<std::shared_ptr<Option>>;
using OptionPtr = std::shared_ptr<Option>;

enum NodeType
{
    Group,
    Leaf
};

class Node : public std::enable_shared_from_this<Node>
{
public:
    static std::shared_ptr<Node> createNodeGroup(const std::string name);

    static std::shared_ptr<Node> createOptionList(const std::string name);

    void addNode(std::shared_ptr<Node> child);

    void addOption(std::shared_ptr<Option> option);

    const NodeList &getChildren() const;

    const OptionList &getOptions() const;

    const NodeType &getType() const;

    void printTree(bool path = false);

    //the root node is omitted from the path
    const std::string path() const;

    std::shared_ptr<Node> getChild(const std::string &childname);

    std::shared_ptr<Option> getOption(const std::string &optionname);

    std::shared_ptr<Node> at(const std::string &path);

    void to_json(json &j) const;

    static std::shared_ptr<Node> from_json(const json &j);

private:
    std::weak_ptr<Node> parent;
    NodeType type;
    std::string name;

    std::variant<
        NodeList,
        OptionList
    > children;
};