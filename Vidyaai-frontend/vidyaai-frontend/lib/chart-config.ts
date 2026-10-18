export const CHART_COLORS = {
    accent: "#6366f1",
    accentMuted: "#4f46e5",
    grid: "#27272a",
    axis: "#71717a",
    text: "#a1a1aa",
    success: "#22c55e",
    warning: "#f59e0b",
    danger: "#ef4444",
} as const;

export const CHART_THEME = {
    stroke: CHART_COLORS.accent,
    fill: CHART_COLORS.accent,
    gridStroke: CHART_COLORS.grid,
    axisStroke: CHART_COLORS.axis,
    fontSize: 11,
} as const;