const std = @import("std");
const rl = @import("raylib");

//=================================================================================================

pub const Dimension = struct {
    width: u16 = undefined,
    height: u16 = undefined,

    pub fn update(self: *Dimension) void {
        self.width = @intCast(rl.getScreenWidth());
        self.height = @intCast(rl.getScreenHeight());
    }
};
pub var dimension: Dimension = .{};

//=================================================================================================

pub const Transform = struct {
    ///The following functions  interact via @Vectors of 4 u16 (0-65535) values encoding the x-Pos, y-Pos, width, and height of a corner defined rectangle
    ///The base corner is the top left corner of the rectangle
    /// Returns a Vector
    /// Reduces the parent's size by the buffer size
    pub fn absBuffer(parent: *const @Vector(4, u16), buffers: *const @Vector(4, u16)) @Vector(4, u16) {
        const px = parent[0];
        const py = parent[1];
        const pw = parent[2];
        const ph = parent[3];

        const bLeft = buffers[0];
        const bRight = buffers[1];
        const bTop = buffers[2];
        const bBottom = buffers[3];

        const xShift: u16 = px + bLeft;
        const yShift: u16 = py + bTop;
        const rWidth: u16 = pw - bLeft - bRight;
        const rHeight: u16 = ph - bTop - bBottom;
        return .{ xShift, yShift, rWidth, rHeight };
    }

    /// Returns a Vector
    /// Reduces the parent's size by the buffer size, relative to the parent's size
    /// The buffer is a percentage of the parent's size
    pub fn relBuffer(parent: *const @Vector(4, u16), buffers: *const @Vector(4, f32)) @Vector(4, u16) {
        const px = parent[0];
        const py = parent[1];
        const pw = parent[2];
        const ph = parent[3];

        const bLeft = buffers[0];
        const bRight = buffers[1];
        const bTop = buffers[2];
        const bBottom = buffers[3];

        const xShift: u16 = px + @as(u16, @intFromFloat(@round(@as(f32, @floatFromInt(pw)) * bLeft)));
        const yShift: u16 = py + @as(u16, @intFromFloat(@round(@as(f32, @floatFromInt(ph)) * bTop)));
        const rWidth: u16 = pw - @as(u16, @intFromFloat(@round(@as(f32, @floatFromInt(pw)) * bLeft))) - @as(u16, @intFromFloat(@round(@as(f32, @floatFromInt(pw)) * bRight)));
        const rHeight: u16 = ph - @as(u16, @intFromFloat(@round(@as(f32, @floatFromInt(ph)) * bTop))) - @as(u16, @intFromFloat(@round(@as(f32, @floatFromInt(ph)) * bBottom)));
        return .{ xShift, yShift, rWidth, rHeight };
    }

    const Align = enum { TOP, BOTTOM, LEFT, RIGHT, CENTER, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT };

    /// Returns a Vector
    /// Aligns the child rectangle relative to the parent rectangle, based on the alignment provided
    pub fn relAlign(parent: *const @Vector(4, u16), child: *const @Vector(4, u16), aligment: Align) @Vector(4, u16) {
        const px = parent[0];
        const py = parent[1];
        const pw = parent[2];
        const ph = parent[3];

        const cw = child[2];
        const ch = child[3];

        switch (aligment) {
            .CENTER => {
                const xShift: u16 = px + (pw / 2) - (cw / 2);
                const yShift: u16 = py + (ph / 2) - (ch / 2);
                return .{ xShift, yShift, cw, ch };
            },
            .TOP => {
                const centered = relAlign(parent, child, Align.CENTER);
                return .{ centered[0], py, cw, ch };
            },
            .BOTTOM => {
                const centered = relAlign(parent, child, Align.CENTER);
                return .{ centered[0], py + ph - ch, cw, ch };
            },
            .LEFT => {
                const centered = relAlign(parent, child, Align.CENTER);
                return .{ px, centered[1], cw, ch };
            },
            .RIGHT => {
                const centered = relAlign(parent, child, Align.CENTER);
                return .{ px + pw - cw, centered[1], cw, ch };
            },
            .TOP_LEFT => {
                return .{ px, py, cw, ch };
            },
            .TOP_RIGHT => {
                return .{ px + pw - cw, py, cw, ch };
            },
            .BOTTOM_LEFT => {
                return .{ px, py + ph - ch, cw, ch };
            },
            .BOTTOM_RIGHT => {
                return .{ px + pw - cw, py + ph - ch, cw, ch };
            },
        }
    }

    // Constraint function, prevent over scaling

    /// Returns a Vector
    /// Tabelizes the parent rectangle into a grid of columns and rows with and optional buffer in between
    /// The columns and rows are given by an array of u16 values
    /// The buffer is an array of 2 u16 values, the first value is the buffer between columns, the second value is the buffer between rows
    pub fn tabelize(parent: *const @Vector(4, u16), col_row: []const u16, buffers: []const u16) []@Vector(4, u16) {
        const px = parent[0];
        const py = parent[1];
        const pw = parent[2];
        const ph = parent[3];

        const col: u16 = col_row[0];
        const row: u16 = col_row[1];

        const colBuffer: u16 = buffers[0];
        const rowBuffer: u16 = buffers[1];

        const colWidth = (pw - (col - 1) * colBuffer) / col;
        const rowHeight = (ph - (row - 1) * rowBuffer) / row;

        // create a table of columns and rows
        var table: []@Vector(4, u16) = std.heap.allocSlice(@Vector(4, u16), col * row);

        for (0..col) |c| {
            for (0..row) |r| {
                const xShift: u16 = px + c * (colWidth + colBuffer);
                const yShift: u16 = py + r * (rowHeight + rowBuffer);
                table[c * row + r] = .{ xShift, yShift, @as(u16, @intFromFloat(colWidth)), @as(u16, @intFromFloat(rowHeight)) };
            }
        }

        return table;
    }
};
