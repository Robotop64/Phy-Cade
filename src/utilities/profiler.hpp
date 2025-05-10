#pragma once

#include <chrono>
#include <vector>
#include <unordered_map>
#include <variant>
#include <optional>
#include <cassert>

#include "unordered_dense.h"

#include "logging.hpp"

namespace Profiler
{
#pragma region defs
    using Value = std::variant<int, float, bool>;
    using Id = int;
    using Link = std::variant<Id, std::string>; // Id: other id, std::string: name

    using Duration = std::chrono::duration<float>;
    using Time = std::chrono::high_resolution_clock::time_point;
#pragma endregion

    struct Tracker
    {
        std::vector<Id> ids;
        std::vector<std::string> names;
        ankerl::unordered_dense::map<std::string, Id> name_to_id;
        std::vector<Time> times;
        std::vector<std::optional<Link>> links;
        std::vector<std::optional<Value>> values;
        Id last_id;
        Id capacity;

        // grow until this pin is reached, reset/clear to the pinned index
        bool clear_pinned = false;
        int pin_idx = 0;

        Tracker();

        Id stamp(std::string_view name, std::optional<Link> link = std::nullopt, std::optional<Value> value = std::nullopt);

        Id getId(std::string_view name);

        Duration calculate(Id id_a, Id id_b);
        Duration calculate(std::string_view name_a, std::string_view name_b);

        Value getValue(Id id);
        Value getValue(std::string_view name);

        void clear();
        void pin();
        void unpin();
    };

    Tracker &Get();

    inline void Setup()
    {
        Get();
    }
    // inline void BeginLoop()
    // {
    //     Get().clear();
    // }
    // inline void EndLoop()
    // {
    //     Get().pin();
    // }
    // inline void Continue()
    // {
    //     Get().unpin();
    // }
    inline Id Stamp(std::string_view name, std::optional<Link> link = std::nullopt, std::optional<Value> value = std::nullopt)
    {
        return Get().stamp(name, link, value);
    }
    inline Id StampS(std::string_view name, std::optional<Link> link = std::nullopt, std::optional<Value> value = std::nullopt)
    {
        auto _name = std::string(name);
        return Get().stamp(_name + "-Start", link, value);
    }
    inline Id StampE(std::string_view name, std::optional<Link> link = std::nullopt, std::optional<Value> value = std::nullopt)
    {
        auto _name = std::string(name);
        return Get().stamp(_name + "-End", link, value);
    }
    inline Duration Eval(std::string_view nameA, std::string_view nameB)
    {
        return Get().calculate(nameA, nameB);
    }
    inline Duration Eval(Id idA, Id idB)
    {
        return Get().calculate(idA, idB);
    }
    inline Duration EvalScope(std::string_view name)
    {
        auto _name = std::string(name);
        return Get().calculate(_name + "-Start", _name + "-End");
    }
    inline Value GetValue(std::string_view name)
    {
        return Get().getValue(name);
    }
    inline Value GetValue(Id id)
    {
        return Get().getValue(id);
    }
}

// void example()
// {
//     assert(false && "This is an example of how to use the Tracker class. It should not be used!");

//     P_Setup();

//     while (true)
//     {
//         P_Begin();

//         P_Stamp("Start");
//         P_Stamp("Input-Start");
//         P_Stamp("Input-End");

//         P_Stamp("State-Start");
//         P_Stamp("State-End");

//         P_Stamp("Render-Start");
//         P_Stamp("Render-End");

//         P_Stamp("End");

//         P_End();

//         Duration t = P_Eval("Start", "End");
//     }
// }
