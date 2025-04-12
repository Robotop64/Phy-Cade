#include "config.hpp"

#include <iostream>
#include <typeinfo>

int main(void)
{
    Config &config = Config::instance();
    config.init();

    OptionPtr opt = config.tree()->at("display.general")->getOption("resolution");
    OptionPtr opt2 = config.map().get("display.general-resolution").value();

    std::shared_ptr<OptionChoice<std::string>> opt_choice = std::dynamic_pointer_cast<OptionChoice<std::string>>(opt);
    std::shared_ptr<OptionChoice<std::string>> opt_choice2 = std::dynamic_pointer_cast<OptionChoice<std::string>>(opt2);

    return 0;
};