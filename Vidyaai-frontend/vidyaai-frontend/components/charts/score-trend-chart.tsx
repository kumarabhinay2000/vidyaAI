"use client";

import {
    CartesianGrid,
    Line,
    LineChart,
    ResponsiveContainer,
    Tooltip,
    XAxis,
    YAxis,
} from "recharts";

import { ChartShell } from "./chart-shell";
import { CHART_THEME } from "@/lib/chart-config";

interface ScoreTrendChartProps {
    data: Array<{ label: string; score: number }>;
}

export function ScoreTrendChart({ data }: ScoreTrendChartProps) {
    return (
        <ChartShell
            title="Score trend"
            description="Your quiz scores over time"
            height={260}
        >
            <ResponsiveContainer width="100%" height="100%">
                <LineChart data={data} margin={{ top: 8, right: 8, left: -16, bottom: 0 }}>
                    <CartesianGrid
                        strokeDasharray="3 3"
                        stroke={CHART_THEME.gridStroke}
                        vertical={false}
                    />
                    <XAxis
                        dataKey="label"
                        stroke={CHART_THEME.axisStroke}
                        fontSize={CHART_THEME.fontSize}
                        tickLine={false}
                        axisLine={false}
                    />
                    <YAxis
                        domain={[0, 100]}
                        stroke={CHART_THEME.axisStroke}
                        fontSize={CHART_THEME.fontSize}
                        tickLine={false}
                        axisLine={false}
                    />
                    <Tooltip
                        contentStyle={{
                            background: "#141416",
                            border: "1px solid #27272a",
                            borderRadius: 6,
                            fontSize: 12,
                            color: "#fafafa",
                        }}
                        labelStyle={{ color: "#a1a1aa" }}
                    />
                    <Line
                        type="monotone"
                        dataKey="score"
                        stroke={CHART_THEME.stroke}
                        strokeWidth={2}
                        dot={{ r: 3, fill: CHART_THEME.stroke, strokeWidth: 0 }}
                        activeDot={{ r: 5 }}
                    />
                </LineChart>
            </ResponsiveContainer>
        </ChartShell>
    );
}