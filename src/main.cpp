#include <iostream>
#include <filesystem>

int main(int, char **)
{
    std::cout << "Hello, World!\n";
    std::cout << "C++ Standard: " << "asdasdasd" << "\n";
    std::cout << "Current working directory: " << std::filesystem::current_path() << "\n";
}
