#pragma once

#include <iostream>
#include <format>
#include <string>

namespace Log
{
    template <typename... Args>
    void msg(const std::string region, std::format_string<Args...> fmt, Args &&...args)
    {
        std::cout << region << " => " << std::format(fmt, std::forward<Args>(args)...) << "\n";
    }
}