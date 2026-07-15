import {
    ResponsiveContainer,
    BarChart,
    Bar,
    XAxis,
    YAxis,
    Tooltip,
    CartesianGrid
} from 'recharts'

export default function DashboardRevenueChart({ monthlyRevenues }) {

    return (

        <div className="card bg-base-200 shadow-sm rounded-lg">

            <div className="card-body">

                <h2 className="card-title">
                    Chiffre d'affaires mensuel
                </h2>

                <ResponsiveContainer width="100%" height={300}>
                    <BarChart data={monthlyRevenues}>
                        <CartesianGrid strokeDasharray="3 3" />

                        <XAxis dataKey="month" />
                        <YAxis />

                        <Tooltip />

                        <Bar
                            dataKey="revenue"
                            fill="var(--color-chart-primary)"
                            radius={[8, 8, 0, 0]}
                        />
                    </BarChart>
                </ResponsiveContainer>

            </div>

        </div>

    )

}