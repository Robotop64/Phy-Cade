const std = @import("std");
const rl = @import("raylib");
const win = @import("../window.zig");
const menu = @import("menu.zig");

//=================================================================================================

const Layout = struct {
    title: @Vector(4, u16) = undefined,
    play: @Vector(4, u16) = undefined,
    ranking: @Vector(4, u16) = undefined,
    options: @Vector(4, u16) = undefined,
    credits: @Vector(4, u16) = undefined,
    updates: @Vector(4, u16) = undefined,
    quit: @Vector(4, u16) = undefined,
};
var layout: Layout = .{};

const State = struct {
    switchMenu: bool = false,
    nextMenu: menu.menus = .Quit,
};
var state: State = .{};

//=================================================================================================

pub fn init() void {
    rl.setTargetFPS(60);

    calcLayout();

    while (!rl.windowShouldClose()) {
        if (rl.isWindowResized() and !rl.isWindowFullscreen()) {
            win.dimension.update();
            calcLayout();
        }

        rl.beginDrawing();
        defer rl.endDrawing();

        rl.clearBackground(rl.Color.white);

        draw();
    }

    defer menu.selectMenu(state.nextMenu);
    state = .{};
}

fn draw() void {
    rl.drawRectangle(layout.title[0], layout.title[1], layout.title[2], layout.title[3], rl.Color.gray);
}

fn calcLayout() void {
    const window: @Vector(4, u16) = .{ 0, 0, win.dimension.width, win.dimension.height };
    const outerBorder: @Vector(4, u16) = win.Transform.absBuffer(&window, &.{ 10, 10, 10, 10 });
    layout.title = win.Transform.relBuffer(&outerBorder, &.{ 0.20, 0.20, 0.05, 0.85 });
    const buttonList: @Vector(4, u16) = win.Transform.relBuffer(&outerBorder, &.{ 0.0, 0.60, 0.25, 0.35 });
    const buttonListItems = win.Transform.tabelize(&buttonList, []u16{ 1, 6 }, []u16{ 0, 15 });
}
