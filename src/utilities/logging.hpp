#include <iostream>
#include <format>

namespace Log
{
    template <typename... Args>
    void msg(const char *region, std::format_string<Args...> fmt, Args &&...args)
    {
        std::cout << region << " => " << std::format(fmt, std::forward<Args>(args)...) << "\n";
    }
}