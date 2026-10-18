"use client";

import {
    Bar,
    BarChart,
    CartesianGrid,
    ResponsiveContainer,
    Tooltip,
    XAxis,
    YAxis,
} from "recharts";

import { ChartShell } from "./chart-shell";
import { CHART_THEME } from "@/lib/chart-config";

interface WeeklyActivityChartProps {
    data: Array<{ day: string; count: number }>;
}

export function WeeklyActivityChart({ data }: WeeklyActivityChartProps) {
    return (
        <ChartShell
            title="Weekly activity"
            description="Videos processed each day this week"
            height={260}
        >
            <ResponsiveContainer width="100%" height="100%">
                <BarChart data={data} margin={{ top: 8, right: 8, left: -16, bottom: 0 }}>
                    <CartesianGrid
                        strokeDasharray="3 3"
                        stroke={CHART_THEME.gridStroke}
                        vertical={false}
                    />
                    <XAxis
                        dataKey="day"
                        stroke={CHART_THEME.axisStroke}
                        fontSize={CHART_THEME.fontSize}
                        tickLine={false}
                        axisLine={false}
                    />
                    <YAxis
                        stroke={CHART_THEME.axisStroke}
                        fontSize={CHART_THEME.fontSize}
                        tickLine={false}
                        axisLine={false}
                        allowDecimals={false}
                    />
                    <Tooltip
                        cursor={{ fill: "rgba(99, 102, 241, 0.08)" }}
                        contentStyle={{
                            background: "#141416",
                            border: "1px solid #27272a",
                            borderRadius: 6,
                            fontSize: 12,
                            color: "#fafafa",
                        }}
                        labelStyle={{ color: "#a1a1aa" }}
                    />
                    <Bar
                        dataKey="count"
                        fill={CHART_THEME.fill}
                        radius={[4, 4, 0, 0]}
                        maxBarSize={32}
                    />
                </BarChart>
            </ResponsiveContainer>
        </ChartShell>
    );
}