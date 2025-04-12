#pragma once

#include <iostream>
#include <format>
#include <string>

namespace Log
{
    static std::string last_prefix = "";

    template <typename... Args>
    void msg(const std::string region, std::format_string<Args...> fmt, Args &&...args)
    {
        std::string out = region + " => " + std::format(fmt, std::forward<Args>(args)...);
        std::cout << out << "\n";

        last_prefix = region;
    }

    template <typename... Args>
    void updated(const std::string region, std::format_string<Args...> fmt, Args &&...args, bool overwrite = false)
    {
        static int last_size = 0;
        std::cout << "\r" << std::string(last_size, ' ') << "\r";

        std::string out = region + " => " + std::format(fmt, std::forward<Args>(args)...);
        last_size = out.size();
        std::cout << out << std::flush;

        last_prefix = region;
    }

    template <typename... Args>
    void bullet(std::format_string<Args...> fmt, Args &&...args)
    {
        std::string out = std::string(last_prefix.size(), ' ') + " => " + std::format(fmt, std::forward<Args>(args)...);
        std::cout << out << "\n";
    }

    void newline();
}