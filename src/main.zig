const rl = @import("raylib");

const window = @import("window.zig");
const menu = @import("Menus/menu.zig");

pub fn main() anyerror!void {
    window.dimension = .{
        .width = 800,
        .height = 450,
    };

    rl.initWindow(window.dimension.width, window.dimension.height, "PacPhycade");
    defer rl.closeWindow();

    rl.setWindowState(rl.ConfigFlags{ .window_resizable = true });

    menu.selectMenu(.Main);
}
