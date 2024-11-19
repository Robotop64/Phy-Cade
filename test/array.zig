const std = @import("std");

pub fn main() anyerror!void {
    const rows = 6;
    const cols = 2;

    var gpa = std.heap.GeneralPurposeAllocator(.{}){};
    const allocator = gpa.allocator();

    var table = try createTable(allocator, rows, cols);

    editTable(&table, rows, cols);

    printTable(&table, rows, cols);
}

fn createTable(allocator: std.mem.Allocator, rows: usize, cols: usize) ![][]@Vector(4, u16) {
    const table: [][]@Vector(4, u16) = try allocator.alloc([]@Vector(4, u16), rows);
    for (0..rows) |row| {
        table[row] = try allocator.alloc(@Vector(4, u16), cols);
    }

    return table;
}

fn destroyTable(allocator: std.mem.Allocator, table: [][]@Vector(4, u16), rows: usize, cols: usize) void {
    for (0..rows) |row| {
        for (0..cols) |col| {
            allocator.free(table[row][col]);
        }
        allocator.free(table[row]);
    }
    allocator.free(table);
}

fn editTable(table: *[][]@Vector(4, u16), rows: usize, cols: usize) void {
    for (0..rows) |row| {
        for (0..cols) |col| {
            table.*[row][col] = .{ @intCast(row + 1), @intCast(col + 1), 0, 0 };
        }
    }
}

fn printTable(table: *const [][]@Vector(4, u16), rows: usize, cols: usize) void {
    for (0..rows) |row| {
        for (0..cols) |col| {
            const cell = table.*[row][col];
            std.debug.print("{d}, {d}, {d}, {d} | ", .{ cell[0], cell[1], cell[2], cell[3] });
        }
        std.debug.print("\n", .{});
    }
}
