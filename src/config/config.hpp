#pragma once

#include "node.hpp"

#include <map>
#include <optional>

namespace ConfigUtil
{
    NodePtr gen_default();

    void save(const std::string &filename, const NodePtr &config);

    NodePtr load(const std::string &filename);
};

class Config
{
public:
    Config()
        : user_tree(std::nullopt), user_map(std::nullopt) {};
    // Config(const Config &) = delete;
    ~Config() = default;

    class Map
    {
    public:
        Map(const NodePtr &root);

        std::optional<OptionPtr> get(const std::string &key) const;

        void print() const;

        void validate() const;

        static size_t hash(const std::string &key);

    private:
        std::map<size_t, OptionPtr> map;
    };

    static Config &instance()
    {
        static Config instance = []()
        {
            Config config = Config();
            config.init();
            return config;
        }();
        return instance;
    }

    void init();

    void load(const std::string &filename);
    void save(const std::string &filename) const;

    Map &map();

    NodePtr &tree();

private:
    const NodePtr default_tree = ConfigUtil::gen_default();
    std::optional<NodePtr> user_tree = std::nullopt;

    const Map default_map = Map(default_tree);
    std::optional<Map> user_map = std::nullopt;
};
