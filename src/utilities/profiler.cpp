#include "profiler.hpp"
#include "logging.hpp"

namespace Profiler
{
    Tracker::Tracker()
        : ids{}, names{}, name_to_id{}, times{}, links{}, values{}, last_id{0}, capacity{0}, clear_pinned{false}, pin_idx{0}
    {
    }

    Id Tracker::stamp(std::string_view name, std::optional<Link> link, std::optional<Value> value)
    {
        Id id = getId(name);
        if (id == -1)
        {
            id = last_id++;
            ids.push_back(id);
            names.push_back(std::string(name));
            name_to_id[std::string(name)] = id;
            times.push_back(std::chrono::high_resolution_clock::now());
            links.push_back(link);
            values.push_back(value);
            // Log::msg("Profiler", "Added id: {}, name: {}", id, name);
        }
        else
        {
            times[id] = std::chrono::high_resolution_clock::now();
            links[id] = link;
            values[id] = value;
            // Log::msg("Profiler", "Updated id: {}, name: {}", id, name);
        }
        return id;
    };

    Id Tracker::getId(std::string_view name)
    {
        auto it = name_to_id.find(std::string(name));
        if (it != name_to_id.end())
        {
            return it->second;
        }

        return -1;
    };

    Duration Tracker::calculate(Id id_a, Id id_b)
    {
        auto start = times[id_a];
        auto end = times[id_b];

        std::chrono::duration<float> duration = end - start;
        return duration;
    };

    Duration Tracker::calculate(std::string_view name_a, std::string_view name_b)
    {
        Id id_a = getId(name_a);
        Id id_b = getId(name_b);

        return calculate(id_a, id_b);
    };

    Value Tracker::getValue(Id id)
    {
        if (id > last_id)
            Log::msg("Profiler", "Accessing out of bounds id: {}", id);

        if (values[id].has_value())
            return *values[id];
        else
            throw std::runtime_error("Value not found for id: " + std::to_string(id));
    };

    Value Tracker::getValue(std::string_view name)
    {
        Id id = getId(name);
        return getValue(id);
    };

    // void Tracker::clear()
    // {
    //     if (!clear_pinned)
    //     {
    //         pin_idx = last_id;
    //     }
    //     last_id = pin_idx;
    // };

    // void Tracker::pin()
    // {
    //     clear_pinned = true;
    // };

    Tracker &Get()
    {
        static Tracker instance;
        return instance;
    }
}