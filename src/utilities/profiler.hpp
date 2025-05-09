#pragma once

#include <cstddef>
#include <chrono>
#include <vector>
#include <variant>
#include <optional>
#include <cassert>

#include "logging.hpp"

namespace Profiler
{
    using Value = std::variant<int, float, bool>;
    using Link = std::variant<size_t, std::string>; // size_t: other id, std::string: name

    using Duration = std::chrono::duration<float>;
    using Time = std::chrono::high_resolution_clock::time_point;

    struct Tracker
    {
        std::vector<size_t> ids = {};
        std::vector<std::string_view> names = {};
        std::vector<Time> times = {};
        std::vector<std::optional<Link>> links = {};
        std::vector<std::optional<Value>> values = {};
        size_t last_id = 0;
        size_t capacity = 0;

        bool is_init = false;

        size_t stamp(std::string_view name, std::optional<Link> link = std::nullopt, std::optional<Value> value = std::nullopt)
        {
            size_t next_id = last_id++;

            if (!is_init && last_id < capacity)
            {
                ids[next_id] = next_id;
                names[next_id] = name;
                times[next_id] = std::chrono::high_resolution_clock::now();
                links[next_id] = link;
                values[next_id] = value;
            }
            else if (!is_init)
            {
                ids.push_back(next_id);
                names.push_back(name);
                times.push_back(std::chrono::high_resolution_clock::now());
                links.push_back(link);
                values.push_back(value);
                capacity = last_id;
            }
            else
            {
                ids[next_id] = next_id;
                names[next_id] = name;
                times[next_id] = std::chrono::high_resolution_clock::now();
                links[next_id] = link;
                values[next_id] = value;
            }

            last_id = next_id;

            return next_id;
        };

        size_t getId(std::string_view name)
        {
            for (size_t i = 0; i <= last_id; i++)
            {
                if (names[i] == name)
                {
                    size_t id = ids[i];
                    if (id > last_id)
                        Log::msg("Profiler", "Accessing out of bounds id: {}, name: {}", id, name);
                    return ids[i];
                }
            }
            throw std::runtime_error("Event:" + std::string(name) + " not found");
        };

        Duration calculate(size_t id_a, size_t id_b)
        {
            auto start = times[id_a];
            auto end = times[id_b];

            std::chrono::duration<float> duration = end - start;
            return duration;
        };

        Duration calculate(std::string_view name_a, std::string_view name_b)
        {
            size_t id_a = getId(name_a);
            size_t id_b = getId(name_b);

            return calculate(id_a, id_b);
        };

        void clear()
        {
            last_id = 0;
        };

        void pin()
        {
            is_init = true;
        };
    };

    struct Telemetry
    {
    };
}

#define PROFILER Profiler::Tracker local_tracker;
#define BEGIN_PROFILER local_tracker.clear();
#define STAMP(name) local_tracker.stamp(name);
#define END_PROFILER local_tracker.pin();
#define EVAL(linkA, linkB) local_tracker.calculate(linkA, linkB);

void example()
{
    assert(false && "This is an example of how to use the Profiler::Tracker class. It should not be used!");

    PROFILER;

    while (true)
    {
        BEGIN_PROFILER;

        STAMP("Start");
        STAMP("Input-Start");
        STAMP("Input-End");

        STAMP("State-Start");
        STAMP("State-End");

        STAMP("Render-Start");
        STAMP("Render-End");

        STAMP("End");

        END_PROFILER;

        Profiler::Duration t = EVAL("Start", "End");
    }
}
