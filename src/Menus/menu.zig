const main_menu = @import("main_menu.zig");

pub const menus = enum { Main, Game, Ranking, Settings, Credits, Updates, Quit };

pub fn selectMenu(m: menus) void {
    switch (m) {
        .Main => {
            main_menu.init();
        },
        .Game => {},
        .Ranking => {},
        .Settings => {},
        .Credits => {},
        .Updates => {},
        .Quit => {},
    }
}
